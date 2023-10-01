package com.example.handler;

import com.example.config.ApiStatProperties;
import com.example.rel.MethodDataStackHolder;
import com.example.rel.operate.MethodDataOperate;
import com.example.rel.operate.MethodHierarchyTransferOperate;
import com.example.rel.operate.MethodQueueOperate;
import com.example.structure.MethodData;
import com.example.structure.MethodHierarchyTransfer;
import com.example.service.ApiStatInvokedQueue;
import com.example.util.MethodStackHolder;
import lombok.AllArgsConstructor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @program: cook-frame
 * @description:
 * @author: k
 * @create: 2023-09-27
 **/
@AllArgsConstructor
public class ApiStatRunTimeHandler implements MethodInterceptor {
    
    private final ApiStatProperties apiStatProperties;
    
    private final ApiStatInvokedQueue apiStatInvokedQueue;
    
    private final MethodDataOperate methodDataOperate;
    
    private final MethodDataStackHolder methodDataStackHolder;
    
    private final MethodHierarchyTransferOperate methodHierarchyTransferOperate;
    
    private final MethodQueueOperate methodQueueOperate;
    
    @Override
    public Object invoke(final MethodInvocation methodInvocation) throws Throwable {
        Object obj = null;
        long start = System.nanoTime();
        MethodData parentMethodData = methodDataOperate.getParentMethodData();
        methodDataStackHolder.putMethodData(methodInvocation);
        MethodHierarchyTransfer methodHierarchyTransfer = new MethodHierarchyTransfer();
        boolean exceptionFlag = false;
        try {
            obj = methodInvocation.proceed();
        } catch (Throwable t) {
            exceptionFlag = true;
            throw t;
        } finally {
            long runTime = System.nanoTime() - start;
            BigDecimal runTimeBigDecimal = new BigDecimal(String.valueOf(runTime)).divide(new BigDecimal(1000000), 2, RoundingMode.HALF_UP);
            methodHierarchyTransfer = methodHierarchyTransferOperate.getMethodHierarchyTransfer(methodInvocation,parentMethodData,runTimeBigDecimal,exceptionFlag);
            methodQueueOperate.add(methodHierarchyTransfer);
            MethodStackHolder.clear();
        }
        return obj;
    }
}
