package com.mo.process.service.impl;

import com.mo.model.process.ProcessType;
import com.mo.process.mapper.OaProcessTypeMapper;
import com.mo.process.service.OaProcessTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 审批类型 服务实现类
 * </p>
 *
 * @author mo
 * @since 2023-10-10
 */
@Service
public class OaProcessTypeServiceImpl extends ServiceImpl<OaProcessTypeMapper, ProcessType> implements OaProcessTypeService {

}
