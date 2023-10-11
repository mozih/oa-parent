package com.mo.process.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mo.model.process.ProcessTemplate;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 审批模板 服务类
 * </p>
 *
 * @author mo
 * @since 2023-10-10
 */
public interface OaProcessTemplateService extends IService<ProcessTemplate> {
    //查询审批模板及审批类型名称
    IPage<ProcessTemplate> selectPageProcessTempate(Page<ProcessTemplate> pageParam);
    //发布
    void publish(Long id);
}
