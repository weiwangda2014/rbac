package com.wandun;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.codehaus.groovy.control.CompilerConfiguration;

import java.util.HashMap;
import java.util.Map;

public class GroovyShellTest {

    public static void main(String[] args) {
        Binding binding = new Binding();
        binding.setVariable("p", 3.1415926);
        binding.setVariable("r", 5);

        GroovyShell shell = new GroovyShell(binding);

        Object result = shell.evaluate("p*r*r");

        System.out.println(result);


        result = shell.evaluate("Math.round(p*r*r)");

        System.out.println(result);


    }
}
