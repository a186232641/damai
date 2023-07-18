package com.example.designmode.strategy;

/**
 * @program: cook-frame
 * @description:
 * @author: 星哥
 * @create: 2023-06-09
 **/
public class AppleStrategy implements FruitStrategy{
    @Override
    public void eat() {
        System.out.println("吃到了苹果");
    }
}
