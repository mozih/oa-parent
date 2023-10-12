package com.mo.process.service.impl;

import com.mo.auth.service.SysUserService;
import com.mo.model.process.ProcessRecord;
import com.mo.model.system.SysUser;
import com.mo.process.mapper.OaProcessRecordMapper;
import com.mo.process.service.OaProcessRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mo.security.custom.LoginUserInfoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 审批记录 服务实现类
 * </p>
 *
 * @author mo
 * @since 2023-10-12
 */
@Service
public class OaProcessRecordServiceImpl extends ServiceImpl<OaProcessRecordMapper, ProcessRecord> implements OaProcessRecordService {
    @Autowired
    private SysUserService sysUserService;

    @Override
    public void record(Long processId, Integer status, String description) {
        //查询用户信息
        SysUser sysUser = sysUserService.getById(LoginUserInfoHelper.getUserId());

        ProcessRecord processRecord = new ProcessRecord();
        processRecord.setProcessId(processId);
        processRecord.setStatus(status);
        processRecord.setDescription(description);
        processRecord.setOperateUser(sysUser.getName());
        processRecord.setOperateUserId(sysUser.getId());
        baseMapper.insert(processRecord);
    }
}
