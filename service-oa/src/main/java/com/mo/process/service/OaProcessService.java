package com.mo.process.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mo.model.process.Process;
import com.mo.vo.process.ApprovalVo;
import com.mo.vo.process.ProcessFormVo;
import com.mo.vo.process.ProcessQueryVo;
import com.mo.vo.process.ProcessVo;
import java.util.Map;

/**
 * <p>
 * 审批类型 服务类
 * </p>
 *
 * @author mo
 * @since 2023-10-11
 */
public interface OaProcessService extends IService<Process> {
    //审批管理列表
    IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam, ProcessQueryVo processQueryVo);
    //部署流程定义
    void deployByZip(String deployPath);
    //启动流程实例
    void startUp(ProcessFormVo processFormVo);
    //查询待处理任务列表
    IPage<ProcessVo> findPending(Page<Process> pageParam);
    //查询审批详情信息
    Map<String,Object> show(Long id);
    //审批任务
    void approve(ApprovalVo approvalVo);
    //查询已处理任务
    IPage<ProcessVo> findProcessed(Page<Process> pageParam);
    //查询已发起任务
    IPage<ProcessVo> findStarted(Page<ProcessVo> pageParam);
}
