package com.demo.override;

import com.demo.override.duplicate.ParameterizedTypeImpl;
import com.demo.override.meta.MetaOption;
import io.github.jleblanc64.libcustom.functional.ListF;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.regex.Pattern;

import static com.demo.override.Utils.isEntity;
import static io.github.jleblanc64.libcustom.functional.ListF.f;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.reflections.ReflectionUtils.getAllFields;

public class FieldMocked {
    @SneakyThrows
    public static Field getIOptionF(Field f, MetaOption metaOption) {
        var mock = mock(Field.class);

        var typeArgs = typeArgsIOptionF(f, metaOption);
        var type = ParameterizedTypeImpl.of(metaOption.monadClass(), typeArgs, null);
        doReturn(type).when(mock).getGenericType();

        mockSimple(mock, f, metaOption.monadClass());
        return mock;
    }

    @SneakyThrows
    public static Field getSimple(Field f, Type typeArg) {
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

    // return null if no match
    public static Type typeArgIOptionF(Type t, MetaOption metaOption) {
        if (!(t instanceof ParameterizedType))
            return null;

        var p = (ParameterizedType) t;
        if (!metaOption.isSuperClassOf(p.getRawType()))
            return null;

        var c = (Class<?>) p.getActualTypeArguments()[0];
        return !isEntity(c.getDeclaredAnnotations()) ? null : p.getActualTypeArguments()[0];
    }

    public static Type typeArgsIOptionF(Field f, MetaOption metaOption) {
        return typeArgIOptionF(f.getGenericType(), metaOption);
    }

    @SneakyThrows
    public static Object getRefl(Object o, String field) {
        try {
            return getRefl(o, field, o.getClass());
        } catch (Exception ignored) {
            try {
                return getRefl(o, field, o.getClass().getSuperclass());
            } catch (Exception ignored2) {
                return getRefl(o, field, o.getClass().getSuperclass().getSuperclass());
            }
        }
    }

    private static Object getRefl(Object o, String field, Class clazz) throws NoSuchFieldException, IllegalAccessException {
        var f = clazz.getDeclaredField(field);
        f.setAccessible(true);
        return f.get(o);
    }

    @SneakyThrows
    public static Object getRefl(Object o, Field f) {
        f.setAccessible(true);
        return f.get(o);
    }

    @SneakyThrows
    public static void setRefl(Object o, Field f, Object value) {
        f.setAccessible(true);
        f.set(o, value);
    }

    public static ListF<Field> fields(Object o) {
        return f(getAllFields(o.getClass()));
    }

    @SneakyThrows
    public static Class<?> paramClass(String clazz) {
        return Class.forName(regex0(clazz, "(?<=\\<).*(?=\\>)"));
    }

    private static String regex0(String s, String pattern) {
        var matcher = Pattern.compile(pattern).matcher(s);
        matcher.find();
        return matcher.group(0);
    }
}
