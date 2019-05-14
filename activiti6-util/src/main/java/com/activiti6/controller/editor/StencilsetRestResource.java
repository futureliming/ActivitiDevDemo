package com.activiti6.controller.editor;

import org.activiti.engine.ActivitiException;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
/** activiti-model 6.0  bpmn编译器 版本（来自github activiti官方）
 * 获取编辑器组件及配置项信息
 * liuzhize 2019年3月7日下午3:33:28
 */
@RestController
@RequestMapping("service")
public class StencilsetRestResource {
  
/**
 * 获取流程json文件
 * @return
 */
  @RequestMapping(value="/editor/stencilset", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
  @ResponseBody
  public String getStencilset() {
    InputStream stencilsetStream = this.getClass().getClassLoader().getResourceAsStream("stencilset.json");
    try {
      return IOUtils.toString(stencilsetStream, "utf-8");
    } catch (Exception e) {
      throw new ActivitiException("Error while loading stencil set", e);
    }
  }
}
