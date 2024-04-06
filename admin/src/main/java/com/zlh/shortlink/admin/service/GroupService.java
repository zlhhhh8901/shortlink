package com.zlh.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zlh.shortlink.admin.dao.entity.GroupDO;
import com.zlh.shortlink.admin.dto.req.ShortLinkGroupSortReqDTO;
import com.zlh.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import com.zlh.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;

import java.util.List;

/**
 * 短链接分组接口层
 */
public interface GroupService extends IService<GroupDO> {
    /**
     * 新增短链分组
     */
    void saveGroup(String groupName);

    /**
     * 查询用户短链接分组集合
     */
    List<ShortLinkGroupRespDTO> listGroup();

    /**
     * 修改短链接分组
     *
     * @param requestParam 修改链接分组参数
     */
    void updateGroup(ShortLinkGroupUpdateReqDTO requestParam);

    /**
     * 删除短链分组
     */
    void deleteGroup(String gid);

    /**
     * 短链接分组排序功能
     */
    void sortGroup(List<ShortLinkGroupSortReqDTO> sortReqDTO);
}
