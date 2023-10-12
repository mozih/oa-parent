package com.mo.process.service;

import com.mo.model.process.ProcessType;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 审批类型 服务类
 * </p>
 *
 * @author mo
 * @since 2023-10-10
 */
public interface OaProcessTypeService extends IService<ProcessType> {
    //查询所有审批分类和模板
    List<ProcessType> findProcessType();
}
