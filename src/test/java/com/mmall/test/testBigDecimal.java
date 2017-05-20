package com.mmall.test;

import org.junit.Test;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2017/5/18.
 */
public class testBigDecimal {

    @Test
    public void test1(){
        float i= 0.05f;
        float j =0.01f;
        double m =0.12345678901234567890;
        float k= i+j;
        BigDecimal b1 = new BigDecimal("0.05");
        BigDecimal b2 = new BigDecimal("0.01");
        System.out.println(m);
        System.out.println(i);
        System.out.println(i+j);
        System.out.println(k);
        System.out.println(b1.add(b2));
    }
}
