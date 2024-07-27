package com.demo.lib_override;

import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class FieldMocked {
    @SneakyThrows
    static Field getSimple(Field f, Type typeArg) {
        var mock = mock(Field.class);

        doReturn(typeArg).when(mock).getGenericType();

        mockSimple(mock, f, typeArg);
        return mock;
    }

    private static void mockSimple(Field mock, Field f, Type typeArg) {
        doReturn(typeArg).when(mock).getType();
        doReturn(f.getName()).when(mock).getName();
        doReturn(f.getDeclaringClass()).when(mock).getDeclaringClass();
    }

    @SneakyThrows
    public static Object getRefl(Object o, String field) {
        try {
            return getRefl(o, field, o.getClass());
        } catch (Exception ignored) {
            return getRefl(o, field, o.getClass().getSuperclass());
        }
    }

    private static Object getRefl(Object o, String field, Class clazz) throws NoSuchFieldException, IllegalAccessException {
        var f = clazz.getDeclaredField(field);
        f.setAccessible(true);
        return f.get(o);
    }
}
