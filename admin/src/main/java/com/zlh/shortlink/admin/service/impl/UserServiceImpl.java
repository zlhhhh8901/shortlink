package com.zlh.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
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
import com.zlh.shortlink.admin.dto.req.UserLoginReqDto;
import com.zlh.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.zlh.shortlink.admin.dto.req.UserUpdateReqDTO;
import com.zlh.shortlink.admin.dto.resp.UserLoginRespDTO;
import com.zlh.shortlink.admin.dto.resp.UserRespDTO;
import com.zlh.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
    private final StringRedisTemplate stringRedisTemplate;

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

    @Override
    public UserLoginRespDTO login(UserLoginReqDto requestDto) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, requestDto.getUsername())
                .eq(UserDO::getPassword, requestDto.getPassword())
                .eq(UserDO::getDelFlag, 0);
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        if(userDO == null){
            throw new ClientException("用户不存在");
        }
        Boolean hasKey = stringRedisTemplate.hasKey("login_" + requestDto.getUsername());
        if(hasKey != null && hasKey){
            throw new ClientException("用户已登陆");
        }
        /*
         * Hash
         * key：login_用户名
         * value
         *  key：token
         *  value：JSON格式用户信息
         */
        String uuid = UUID.randomUUID().toString();
        stringRedisTemplate.opsForHash().put("login_" + requestDto.getUsername(), uuid, JSON.toJSONString(userDO));
        stringRedisTemplate.expire("login_" + requestDto.getUsername(), 25L, TimeUnit.DAYS); //图个方便暂改为25天
        return new UserLoginRespDTO(uuid);
    }

    @Override
    public Boolean checkLogin(String username, String token) {
        return stringRedisTemplate.opsForHash().get("login_" + username, token) != null;
    }

    @Override
    public void logOut(String username, String token) {
        if(checkLogin(username, token)){
            stringRedisTemplate.delete("login_" + username);
            return;
        }
        throw new ClientException("用户未登录或token不存在");
    }
}
