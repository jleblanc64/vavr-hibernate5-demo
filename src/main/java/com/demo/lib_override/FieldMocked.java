package com.demo.lib_override;

import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

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
    public static <T> T getRefl(Object o, String field, Class<T> c) {
        try {
            return getRefl(o, field, o.getClass(), c);
        } catch (Exception ignored) {
            return getRefl(o, field, o.getClass().getSuperclass(), c);
        }
    }

    @SneakyThrows
    public static <T> List<T> getReflL(Object o, String field, Class<T> c) {
        try {
            return getReflL(o, field, o.getClass(), c);
        } catch (Exception ignored) {
            return getReflL(o, field, o.getClass().getSuperclass(), c);
        }
    }

    private static <T> T getRefl(Object o, String field, Class clazz, Class<T> c) throws NoSuchFieldException, IllegalAccessException {
        var f = clazz.getDeclaredField(field);
        f.setAccessible(true);
        return (T) f.get(o);
    }

    private static <T> List<T> getReflL(Object o, String field, Class clazz, Class<T> c) throws NoSuchFieldException, IllegalAccessException {
        var f = clazz.getDeclaredField(field);
        f.setAccessible(true);
        return (List<T>) f.get(o);
    }
}
