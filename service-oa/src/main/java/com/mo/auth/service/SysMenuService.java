package com.mo.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mo.model.system.SysMenu;
import com.mo.vo.system.AssginMenuVo;
import com.mo.vo.system.AssginRoleVo;

import java.util.List;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author mo
 * @since 2023-10-07
 */
public interface SysMenuService extends IService<SysMenu> {
    //菜单列表接口
    List<SysMenu> findNodes();
    //删除菜单
    void removeMenuById(Long id);
    //查询所有菜单和角色分配的菜单
    List<SysMenu> findMenuById(Long roleId);
    //角色分配菜单
    void doAssign(AssginMenuVo assginMenuVo);
}
