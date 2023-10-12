package com.mo.process.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mo.common.result.Result;
import com.mo.model.process.ProcessTemplate;
import com.mo.process.service.OaProcessTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 审批模板 前端控制器
 * </p>
 *
 * @author mo
 * @since 2023-10-10
 */
@Api(value = "审批模板管理", tags = "审批模板管理")
@RestController
@RequestMapping(value = "/admin/process/processTemplate")
public class OaProcessTemplateController {

    @Autowired
    private OaProcessTemplateService processTemplateService;

    //上传流程定义
//    @PreAuthorize("hasAuthority('bnt.processTemplate.templateSet')")
    @ApiOperation(value = "上传流程定义")
    @PostMapping("/uploadProcessDefinition")
    public Result uploadProcessDefinition(MultipartFile file) throws FileNotFoundException {
        //获取classes目录位置
        String path = new File(ResourceUtils.getURL("classpath:").getPath()).getAbsolutePath();
        //设置上传文件夹
        File tempFile = new File(path + "/processes/");
        if (!tempFile.exists()){
            tempFile.mkdirs();
        }
        //创建空文件，写入文件
        String filename = file.getOriginalFilename();
        File zipFile = new File(path + "/processes/"+filename);
        try {
            file.transferTo(zipFile);
        } catch (IOException e) {
            return Result.fail();
        }
        Map<String, Object> map = new HashMap<>();
        map.put("processDefinitionPath","processes/"+filename);
        map.put("processDefinitionKey",filename.substring(0,filename.lastIndexOf(".")));
        return Result.ok(map);
    }

    //分页查询模板
    @ApiOperation("获取分页审批模板数据")
    @GetMapping("{page}/{limit}")
    public Result index(@PathVariable Long page,@PathVariable Long limit){
        Page<ProcessTemplate> pageParam = new Page<ProcessTemplate>(page,limit);
        IPage<ProcessTemplate> pageModel = processTemplateService.selectPageProcessTempate(pageParam);
        return Result.ok(pageModel);
    }

    //@PreAuthorize("hasAuthority('bnt.processTemplate.list')")
    @ApiOperation(value = "获取")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        ProcessTemplate processTemplate = processTemplateService.getById(id);
        return Result.ok(processTemplate);
    }

    //@PreAuthorize("hasAuthority('bnt.processTemplate.templateSet')")
    @ApiOperation(value = "新增")
    @PostMapping("save")
    public Result save(@RequestBody ProcessTemplate processTemplate) {
        processTemplateService.save(processTemplate);
        return Result.ok();
    }

    //@PreAuthorize("hasAuthority('bnt.processTemplate.templateSet')")
    @ApiOperation(value = "修改")
    @PutMapping("update")
    public Result updateById(@RequestBody ProcessTemplate processTemplate) {
        processTemplateService.updateById(processTemplate);
        return Result.ok();
    }

    //@PreAuthorize("hasAuthority('bnt.processTemplate.remove')")
    @ApiOperation(value = "删除")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        processTemplateService.removeById(id);
        return Result.ok();
    }

    //部署流程定义（发布）
    @ApiOperation("发布")
    @GetMapping("/publish/{id}")
    public Result publish(@PathVariable Long id){
        processTemplateService.publish(id);
        return Result.ok();
    }

    public static void main(String[] args) {
        String path = null;
        try {
            path = new File(ResourceUtils.getURL("classpath:").getPath()).getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println(path);
    }
}

