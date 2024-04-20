package com.zlh.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zlh.shortlink.project.dao.entity.ShortLinkDO;
import com.zlh.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.zlh.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.zlh.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.zlh.shortlink.project.dto.resp.ShortLinkPageRespDTO;

/**
 * 短链接口层
 */
public interface ShortLinkService extends IService<ShortLinkDO> {
    /**
     * 创建短链接
     */
    ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam);

    IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam);
}

