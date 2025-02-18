package com.demo.override;

import com.demo.override.meta.WithClass;
import com.sympheny.app.hibernate.override.duplicate.MyPersistentBag;
import io.github.jleblanc64.libcustom.functional.ListF;
import lombok.SneakyThrows;
import org.hibernate.persister.collection.AbstractCollectionPersister;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.regex.Pattern;

import static io.github.jleblanc64.libcustom.functional.ListF.f;
import static org.reflections.ReflectionUtils.getAllFields;

public class Utils {
    private static Class PERSISTENT_COLLECTION_CLASS = MyPersistentBag.class;

    @SneakyThrows
    private static Field roleToField(String role) {
        var i = role.lastIndexOf(".");
        var className = role.substring(0, i);
        var fieldName = role.substring(i + 1);

        return Class.forName(className).getDeclaredField(fieldName);
    }

    public static boolean isOfType(AbstractCollectionPersister pers, WithClass w) {
        var path = pers.getNavigableRole().getFullPath();
        var field = roleToField(path);
        return w.isSuperClassOf(field.getType());
    }

    @SneakyThrows
    static Object checkPersistentBag(Object o) {
        if (o == null)
            return o;

        if (!PERSISTENT_COLLECTION_CLASS.isAssignableFrom(o.getClass()))
            throw new RuntimeException("Output of IBagProvider implem must extend " + PERSISTENT_COLLECTION_CLASS.getName());

        return o;
    }

    public static boolean isEntity(Annotation[] annotations) {
        return Arrays.stream(annotations).anyMatch(a -> a instanceof javax.persistence.Entity);
    }

    public static Object getRefl(Object o, String field) {
        var f = findField(o.getClass(), field);
        return getRefl(o, f);
    }

    private static Field findField(Class<?> clazz, String field) {
        var currentClass = clazz;
        while (currentClass != null) {
            var found = f(currentClass.getDeclaredFields()).findSafe(f -> f.getName().equals(field));
            if (found != null)
                return found;

            currentClass = currentClass.getSuperclass();
        }

        return null;
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
