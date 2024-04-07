package com.zlh.shortlink.project.controller;

import com.zlh.shortlink.project.common.convention.result.Result;
import com.zlh.shortlink.project.common.convention.result.Results;
import com.zlh.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.zlh.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.zlh.shortlink.project.service.ShortLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短链接控制层
 */
@RestController
@RequiredArgsConstructor
public class ShortLinkController {
    private final ShortLinkService shortLinkService;

    /**
     * 创建短链接
     */
    @PostMapping("/api/short-link/project/v1/create")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam){
        ShortLinkCreateRespDTO shortLink = shortLinkService.createShortLink(requestParam);
        return Results.success(shortLink);
    }
}
