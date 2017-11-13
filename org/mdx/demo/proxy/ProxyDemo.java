package org.mdx.demo.proxy;

import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

interface Printer {
    void print();
}

class GreetingPrinter implements Printer {
    @After(greetingTo = "Ma Dexuan.")
    @Override
    public void print() {
        System.out.println("Hello,");
    }
}

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@interface After {
    String greetingTo() default "Everyone.";
}

public class ProxyDemo {
    private static <T> Printer getInstanceOf(Class<T> cls) throws IllegalAccessException, InstantiationException {
        T obj = cls.newInstance();
        Object proxy = Proxy.newProxyInstance(cls.getClassLoader(), obj.getClass().getInterfaces(), new InvocationHandler(obj));
        return (Printer) proxy;
    }

    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        Printer pt = getInstanceOf(GreetingPrinter.class);
        pt.print();
    }
}

class InvocationHandler implements java.lang.reflect.InvocationHandler {
    private Object obj;
    InvocationHandler(Object obj) {
        this.obj = obj;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        method.invoke(obj, args);
        Method m =obj.getClass().getMethod(method.getName());
        After after = m.getAnnotation(After.class);
        System.out.println(after.greetingTo());
        return null;
    }
}