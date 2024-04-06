package com.zlh.shortlink.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zlh.shortlink.project.dao.entity.ShortLinkDO;
import com.zlh.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.zlh.shortlink.project.dto.resp.ShortLinkCreateRespDTO;

/**
 * 短链接口层
 */
public interface ShortLinkService extends IService<ShortLinkDO> {
    /**
     * 创建短链接
     */
    ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam);
}

