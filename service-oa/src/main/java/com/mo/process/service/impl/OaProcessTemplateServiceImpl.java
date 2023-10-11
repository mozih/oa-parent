package com.mo.process.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mo.model.process.ProcessTemplate;
import com.mo.model.process.ProcessType;
import com.mo.process.mapper.OaProcessTemplateMapper;
import com.mo.process.service.OaProcessTemplateService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mo.process.service.OaProcessTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 审批模板 服务实现类
 * </p>
 *
 * @author mo
 * @since 2023-10-10
 */
@Service
public class OaProcessTemplateServiceImpl extends ServiceImpl<OaProcessTemplateMapper, ProcessTemplate> implements OaProcessTemplateService {
    @Autowired
    private OaProcessTypeService processTypeService;

    //查询审批模板及审批类型名称
    @Override
    public IPage<ProcessTemplate> selectPageProcessTempate(Page<ProcessTemplate> pageParam) {
        //分页查询
        Page<ProcessTemplate> processTemplatePage = baseMapper.selectPage(pageParam, null);
        List<ProcessTemplate> processTemplateList = processTemplatePage.getRecords();
        //获取审批类型id，获取类型名称封装到对象中
        processTemplateList.stream().forEach(processTemplate -> {
            Long processTypeId = processTemplate.getProcessTypeId();
            ProcessType processType = processTypeService.getOne(new LambdaQueryWrapper<ProcessType>().eq(ProcessType::getId, processTypeId));
            if (processType != null) {
                processTemplate.setProcessTypeName(processType.getName());
            }
        });
        return processTemplatePage;
    }

    @Override
    public void publish(Long id) {
        //修改模板发布状态1 已经发布
        ProcessTemplate processTemplate = baseMapper.selectById(id);
        processTemplate.setStatus(1);
        baseMapper.updateById(processTemplate);

        //TODO 流程定义部署

    }
}
