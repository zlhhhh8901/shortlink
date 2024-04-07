package com.zlh.shortlink.project.service.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zlh.shortlink.project.dao.entity.ShortLinkDO;
import com.zlh.shortlink.project.dao.mapper.ShortLinkMapper;
import com.zlh.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.zlh.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.zlh.shortlink.project.service.ShortLinkService;
import com.zlh.shortlink.project.toolkit.HashUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 短链接口层实现层
 */
@Slf4j
@Service
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {
    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        //生成短链接后缀
        String shortLinkSuffix = HashUtil.hashToBase62(requestParam.getOriginUrl());
        //请求信息转为短链接实体
        ShortLinkDO shortLinkDO = BeanUtil.toBean(requestParam, ShortLinkDO.class);
        shortLinkDO.setFullShortUrl(requestParam.getDomain() + "/" + shortLinkSuffix);
        shortLinkDO.setShortUri(shortLinkSuffix);
        shortLinkDO.setEnableStatus(0);
        baseMapper.insert(shortLinkDO);
        return ShortLinkCreateRespDTO.builder()
                .fullShortUrl(shortLinkDO.getFullShortUrl())
                .gid(shortLinkDO.getGid())
                .originUrl(shortLinkDO.getOriginUrl())
                .build();
    }
}
