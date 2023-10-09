package com.mo.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mo.model.system.SysUser;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author mo
 * @since 2023-10-05
 */
public interface SysUserService extends IService<SysUser> {
    //根据用户id更新状态
    void updateStatus(Long id,Integer status);
    //根据用户名查询对象
    SysUser getUserByUserName(String username);
}
