package com.tool.servicelock.util;

/**
 * @program: redis-tool
 * @description: 无返回值的任务
 * @author: kuan
 * @create: 2023-05-28
 **/
@FunctionalInterface
public interface TaskRun {

    void run();
}
