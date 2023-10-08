package com.mo.auth.utils;

import com.mo.model.system.SysMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * author mozihao
 * create 2023-10-07 17:15
 * Description
 */
public class MenuHelper {
    //使用递归来构建
    public static List<SysMenu> buildTree(List<SysMenu> sysMenuList){
        List<SysMenu> trees = new ArrayList<>();

        for (SysMenu sysMenu : sysMenuList) {
            if (sysMenu.getParentId().longValue()==0) {
                trees.add(getChildren(sysMenu,sysMenuList));
            }
        }

        return trees;
    }

    private static SysMenu getChildren(SysMenu sysMenu,List<SysMenu> sysMenuList){
        sysMenu.setChildren(new ArrayList<>());
        //遍历所有菜单数据，判断id和parentId对应关系
        for (SysMenu menus : sysMenuList) {
            if (sysMenu.getId().longValue() == menus.getParentId().longValue()){
//                if (sysMenu.getChildren() == null) {
//                    sysMenu.setChildren(new ArrayList<>());
//                }
                sysMenu.getChildren().add(getChildren(menus,sysMenuList));
            }
        }
        return sysMenu;
    }

}
