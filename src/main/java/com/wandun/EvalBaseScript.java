package com.wandun;


import groovy.lang.Script;

import java.lang.reflect.Method;
import java.math.BigDecimal;

public class EvalBaseScript extends Script {

    @Override
    public Object run() {
        Method[] methods = EvalBaseScript.class.getDeclaredMethods();
        StringBuilder sb = new StringBuilder();
        for (Method method : methods) {
            sb.append(method);
        }

        return sb.substring(0, sb.length() - 1);
    }

    public static double round(String value, int scale) {
        BigDecimal bd = new BigDecimal(value);
        return bd.setScale(scale, 4).doubleValue();
    }

    public static double round(double value, int scale) {
        BigDecimal bd = new BigDecimal(value);
        return bd.setScale(scale, 4).doubleValue();
    }
}
