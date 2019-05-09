package com.activiti6.service;

/**
 * @author JW
 * @Service activiti常用功能整理
 */
public interface ProcessService {
    /**
     * 发布 流程
     * @param processPath
     */
    void startProcess(String processPath);
}
