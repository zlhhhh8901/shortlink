package com.zlh.shortlink.project.service.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zlh.shortlink.project.common.convention.exception.ServiceException;
import com.zlh.shortlink.project.dao.entity.ShortLinkDO;
import com.zlh.shortlink.project.dao.mapper.ShortLinkMapper;
import com.zlh.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.zlh.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.zlh.shortlink.project.service.ShortLinkService;
import com.zlh.shortlink.project.toolkit.HashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * 短链接口层实现层
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {
    private final RBloomFilter<String> shortLinkCreateCachePenetrationBloomFilter;

    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        String shortLinkSuffix = generateSuffix(requestParam);
        String fullShortLink = requestParam.getDomain() + "/" + shortLinkSuffix;
        //请求信息转为短链接实体
        ShortLinkDO shortLinkDO = BeanUtil.toBean(requestParam, ShortLinkDO.class);
        shortLinkDO.setFullShortUrl(fullShortLink);
        shortLinkDO.setShortUri(shortLinkSuffix);
        shortLinkDO.setEnableStatus(0);
        try {
            //shortUri加了唯一索引，在高并发场景下有可能冲突
            baseMapper.insert(shortLinkDO);
        } catch (Exception e) {
            log.warn("{}重复入库", fullShortLink);
            throw new ServiceException("短链接重复入库");
        }
        shortLinkCreateCachePenetrationBloomFilter.add(fullShortLink);
        return ShortLinkCreateRespDTO.builder()
                .fullShortUrl(shortLinkDO.getFullShortUrl())
                .gid(shortLinkDO.getGid())
                .originUrl(shortLinkDO.getOriginUrl())
                .build();
    }

    //生成短链接后缀
    private String generateSuffix(ShortLinkCreateReqDTO requestParam){
        int generateCount = 0; //短链接重复生成次数
        String shortLinkSuffix;
        //短链重复就重新生成
        while(true){
            if(generateCount > 10){
                throw new ServiceException("短链接生成频繁，请稍后再试！");
            }
            //加随机数降低重复率，以应对毫秒级高并发同一个originUrl的情况
            //但这样一个originUrl就可以有多个短链接了
            shortLinkSuffix = HashUtil.hashToBase62(requestParam.getOriginUrl() + UUID.randomUUID().toString());
            //由布隆过滤器判断完整短链是否存在于数据库，减小DB压力
            if(!shortLinkCreateCachePenetrationBloomFilter.contains(requestParam.getDomain() + "/" + shortLinkSuffix))
                break;
            generateCount++;
        }
        return shortLinkSuffix;
    }
}
