package com.demo.spring;

import io.github.jleblanc64.hibernate5.hibernate.Utils;
import io.github.jleblanc64.hibernate5.hibernate.VavrHibernate5;
import io.github.jleblanc64.libcustom.LibCustom;
import io.vavr.collection.List;
import lombok.SneakyThrows;
import org.springframework.beans.TypeConverterSupport;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;

import java.lang.reflect.Method;

import static io.github.jleblanc64.libcustom.functional.ListF.f;

public class ConfigInit implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext ctx) {
        LibCustom.overrideWithSelf(TypeConverterSupport.class, "convertIfNecessary", x -> {

            var args = x.args;
            if (args.length != 3 || !(args[2] instanceof TypeDescriptor))
                return LibCustom.ORIGINAL;

            var value = args[0];
            var requiredType = (Class) args[1];

            if (requiredType != List.class)
                return LibCustom.ORIGINAL;

            var typeConverterDelegate = Utils.getRefl(x.self, "typeConverterDelegate");
            var resolvableType = ResolvableType.forClassWithGenerics(java.util.List.class, String.class);
            var typeDescriptor = new TypeDescriptor(resolvableType, null, null);

            var l = (java.util.List) invoke(typeConverterDelegate, "convertIfNecessary", null, null,
                    value, java.util.List.class, typeDescriptor);
            return List.ofAll(l);
        });

        VavrHibernate5.override();
    }

    @SneakyThrows
    public static Object invoke(Object o, String methodName, Object... args) {
        var m = findMethod(o, methodName, args);
        m.setAccessible(true);
        return m.invoke(o, args);
    }

    private static Method findMethod(Object o, String methodName, Object... args) {
        var currentClass = o.getClass();
        while (currentClass != null) {
            var match = f(currentClass.getDeclaredMethods()).findSafe(m -> matches(m, methodName, args));
            if (match != null)
                return match;

            currentClass = currentClass.getSuperclass();
        }

        return null;
    }

    private static boolean matches(Method m, String methodName, Object... args) {
        if (!m.getName().equals(methodName))
            return false;

        if (args.length != m.getParameterTypes().length)
            return false;

        for (var i = 0; i < m.getParameterTypes().length; i++)
            if (!matches(m.getParameterTypes()[i], args[i]))
                return false;

        return true;
    }

    private static boolean matches(Class<?> c, Object arg) {
        return arg == null || c.isAssignableFrom(arg.getClass());
    }
}
