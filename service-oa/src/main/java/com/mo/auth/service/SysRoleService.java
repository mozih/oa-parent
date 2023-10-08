package com.mo.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mo.model.system.SysRole;
import com.mo.vo.system.AssginRoleVo;

import java.util.Map;

/**
 * author mozihao
 * create 2023-09-28 16:17
 * Description
 */
public interface SysRoleService extends IService<SysRole> {
    //查询所有角色和当前用户所属角色
    Map<String,Object> findRoleDataByUserId(Long userId);
    //为用户分配角色
    void doAssign(AssginRoleVo assginRoleVo);
}
