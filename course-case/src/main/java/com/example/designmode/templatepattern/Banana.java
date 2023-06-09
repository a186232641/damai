package com.example.designmode.templatepattern;

/**
 * @program: toolkit
 * @description:
 * @author: k
 * @create: 2023-06-09
 **/
public class Banana extends Fruit{
    @Override
    public String getFruitName() {
        return "香蕉";
    }
    
    @Override
    public void processFruit() {
        System.out.println("将香蕉的皮用手削掉");
    }
}
