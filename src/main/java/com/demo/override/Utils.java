package com.demo.override;

import com.demo.override.duplicate.MyPersistentBag;
import com.demo.override.meta.WithClass;
import lombok.SneakyThrows;
import org.hibernate.persister.collection.AbstractCollectionPersister;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import static io.github.jleblanc64.libcustom.functional.ListF.f;

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
        return f(annotations).stream().anyMatch(a -> a instanceof javax.persistence.Entity);
    }
}
