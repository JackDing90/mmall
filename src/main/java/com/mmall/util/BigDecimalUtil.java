package com.mmall.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by Administrator on 2017/5/18.
 */
public class BigDecimalUtil {

    public static BigDecimal add(double d1,double d2){
        BigDecimal b1 = new BigDecimal(Double.toString(d1));
        BigDecimal b2 = new BigDecimal(Double.toString(d2));
        return b1.add(b2);
    }

    public static BigDecimal subtract(double d1,double d2){
        BigDecimal b1 = new BigDecimal(Double.toString(d1));
        BigDecimal b2 = new BigDecimal(Double.toString(d2));
        return b1.subtract(b2);
    }

    public static BigDecimal multiply(double d1,double d2){
        BigDecimal b1 = new BigDecimal(Double.toString(d1));
        BigDecimal b2 = new BigDecimal(Double.toString(d2));
        return b1.multiply(b2);
    }

    public static BigDecimal divide(double d1,double d2){
        BigDecimal b1 = new BigDecimal((Double.toString(d1)));
        BigDecimal b2 = new BigDecimal(Double.toString(d2));
        return b1.divide(b2,2, RoundingMode.HALF_UP);
    }

}
