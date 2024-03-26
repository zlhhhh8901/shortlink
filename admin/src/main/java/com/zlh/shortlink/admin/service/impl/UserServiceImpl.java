package com.zlh.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zlh.shortlink.admin.common.constant.RedisCacheConstant;
import com.zlh.shortlink.admin.common.convention.exception.ClientException;
import com.zlh.shortlink.admin.common.convention.exception.ServiceException;
import com.zlh.shortlink.admin.common.enums.UserErrorCodeEnum;
import com.zlh.shortlink.admin.dao.entity.UserDO;
import com.zlh.shortlink.admin.dao.mapper.UserMapper;
import com.zlh.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.zlh.shortlink.admin.dto.req.UserUpdateReqDTO;
import com.zlh.shortlink.admin.dto.resp.UserRespDTO;
import com.zlh.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import static com.zlh.shortlink.admin.common.enums.UserErrorCodeEnum.USER_NAME_EXIST;
import static com.zlh.shortlink.admin.common.enums.UserErrorCodeEnum.USER_SAVE_ERROR;

/**
 * 用户接口实现层
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    private final RedissonClient redissonClient;

    @Override
    public UserRespDTO getUserByUsername(String username) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username);
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        if (userDO == null) {
            throw new ServiceException(UserErrorCodeEnum.USER_NULL);
        }
        UserRespDTO result = new UserRespDTO();
        BeanUtils.copyProperties(userDO, result);
        return result;
    }

    @Override
    public Boolean hasUserName(String username) {
        //LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
        //        .eq(UserDO::getUsername, username);
        //UserDO userDO = baseMapper.selectOne(queryWrapper);
        //return userDO != null;
        return !userRegisterCachePenetrationBloomFilter.contains(username); //存在则不可用返回false
    }

    @Override
    public void register(UserRegisterReqDTO requestParam) {
        //如果有名字则无法注册
        if(!hasUserName(requestParam.getUsername())){
            throw new ClientException(USER_NAME_EXIST);
        }
        RLock lock = redissonClient.getLock(RedisCacheConstant.LOCK_USER_REGISTER_kEY + requestParam.getUsername());
        try {
            if(lock.tryLock()){
                int insert = baseMapper.insert(BeanUtil.toBean(requestParam, UserDO.class));
                if(insert < 1){
                    throw new ClientException(USER_SAVE_ERROR);
                }
                userRegisterCachePenetrationBloomFilter.add(requestParam.getUsername());
            }else {
                throw new ClientException(USER_NAME_EXIST);
            }
        }finally {
            lock.unlock();
        }

    }

    @Override
    public void update(UserUpdateReqDTO updateReqDTO) {
        //TODO 登录校验
        LambdaUpdateWrapper<UserDO> updateWrapper = Wrappers.lambdaUpdate(UserDO.class)
                .eq(UserDO::getUsername, updateReqDTO.getUsername());
        baseMapper.update(BeanUtil.toBean(updateReqDTO, UserDO.class), updateWrapper);
    }
}
