package com.mo.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mo.model.system.SysMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 菜单表 Mapper 接口
 * </p>
 *
 * @author mo
 * @since 2023-10-07
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {
    //根据userId三表关联查询出用户可操作的菜单列表
    List<SysMenu> findMenuListByUserId(@Param("userId") Long userId);
}
