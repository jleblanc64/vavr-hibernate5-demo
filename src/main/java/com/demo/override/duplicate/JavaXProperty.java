package com.demo.override.duplicate;

import com.demo.override.FieldMocked;
import com.demo.override.meta.MetaList;
import com.demo.override.meta.MetaOption;
import io.github.jleblanc64.libcustom.functional.ListF;
import lombok.SneakyThrows;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.annotations.common.reflection.XProperty;
import org.hibernate.annotations.common.reflection.java.JavaReflectionManager;
import org.hibernate.annotations.common.reflection.java.JavaXMember;
import org.hibernate.annotations.common.reflection.java.generics.TypeEnvironment;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.List;

import static com.demo.override.FieldMocked.getRefl;
import static com.demo.override.hibernate.OverrideConverter.isEntity;
import static io.github.jleblanc64.libcustom.functional.ListF.f;

// https://github.com/hibernate/hibernate-commons-annotations/blob/5.1/src/main/java/org/hibernate/annotations/common/reflection/java/JavaXProperty.java
public class JavaXProperty extends JavaXMember implements XProperty {
    public final TypeEnvironment env;
    public final JavaReflectionManager factory;
    public final ListF<Annotation> annotations;
    private final JavaXMember javaXProperty;
    private boolean isCollection;
    private Class collectionClass;
    private XClass elementClass;

    @SneakyThrows
    public static JavaXProperty of(JavaXMember m, Type type, MetaOption metaOption, MetaList metaList) {
        return of(m, type, f(m.getAnnotations()), metaOption, metaList);
    }

    @SneakyThrows
    public static JavaXProperty of(Field f, Type type, JavaXProperty j,
                                   MetaOption metaOption, MetaList metaList) {
        return new JavaXProperty(f, type, j.env, j.factory, j.annotations, metaOption, metaList);
    }

    @SneakyThrows
    private static JavaXProperty of(JavaXMember m, Type type, List<Annotation> annotations,
                                    MetaOption metaOption, MetaList metaList) {
        var env = (TypeEnvironment) getRefl(m, "env");
        return new JavaXProperty(m.getMember(), type, env, new JavaReflectionManager(), f(annotations),
                metaOption, metaList);
    }

    @SneakyThrows
    private JavaXProperty(Member member, Type type, TypeEnvironment env, JavaReflectionManager factory, ListF<Annotation> annotations,
                          MetaOption metaOption, MetaList metaList) {
        super(member, type, env, factory, factory.toXType(env, typeOf(member, env)));

        this.env = env;
        this.factory = factory;
        this.annotations = annotations;

        var clazz = Class.forName("org.hibernate.annotations.common.reflection.java.JavaXProperty");
        var clazzJavaXType = Class.forName("org.hibernate.annotations.common.reflection.java.JavaXType");

        var constructor = clazz.getDeclaredConstructor(Member.class, Type.class, TypeEnvironment.class,
                JavaReflectionManager.class, clazzJavaXType);
        constructor.setAccessible(true);
        javaXProperty = (JavaXMember) constructor.newInstance(member, type, env, factory, factory.toXType(env, typeOf(member, env)));

        // elementClass
        var typeS = getRefl(this, "type").toString();
        if (typeS.startsWith(metaOption.monadClass().getName() + "<")) {

            var paramClass = FieldMocked.paramClass(typeS);
            var clazzJavaXClass = Class.forName("org.hibernate.annotations.common.reflection.java.JavaXClass");
            constructor = clazzJavaXClass.getDeclaredConstructor(Class.class, TypeEnvironment.class, JavaReflectionManager.class);
            constructor.setAccessible(true);

            isCollection = isEntity(paramClass.getDeclaredAnnotations());
            collectionClass = metaOption.monadClass();
            elementClass = (XClass) constructor.newInstance(paramClass, env, factory);
        } else if (typeS.startsWith(metaList.monadClass().getName() + "<")) {
            var paramClass = FieldMocked.paramClass(typeS);
            var clazzJavaXClass = Class.forName("org.hibernate.annotations.common.reflection.java.JavaXClass");
            constructor = clazzJavaXClass.getDeclaredConstructor(Class.class, TypeEnvironment.class, JavaReflectionManager.class);
            constructor.setAccessible(true);

            isCollection = isEntity(paramClass.getDeclaredAnnotations());
            collectionClass = metaList.monadClass();
            elementClass = (XClass) constructor.newInstance(paramClass, env, factory);
        }
    }

    @Override
    public boolean isCollection() {
        return isCollection;
    }

    @Override
    public Class getCollectionClass() {
        return collectionClass;
    }

    @Override
    public XClass getElementClass() {
        return elementClass;
    }

    public void addAnnotation(Annotation a) {
        annotations.add(a);
    }

    public void removeAnnotation(Class<?> clazz) {
        var idx = -1;
        for (var i = 0; i < annotations.size(); i++) {
            var annotation = annotations.get(i);
            var annotationString = "@" + clazz.getName() + "(";

            if (annotation.toString().contains(annotationString))
                idx = i;
        }

        annotations.remove(idx);
    }

    @Override
    public String getName() {
        return javaXProperty.getName();
    }

    @Override
    public Object invoke(Object target) {
        return javaXProperty.invoke(target);
    }

    @Override
    public Object invoke(Object target, Object... parameters) {
        return javaXProperty.invoke(target, parameters);
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        return (T) annotations.findSafe(a -> annotationType.isAssignableFrom(a.getClass()));
    }

    @Override
    public <T extends Annotation> boolean isAnnotationPresent(Class<T> annotationType) {
        return annotations.stream().anyMatch(a -> annotationType.isAssignableFrom(a.getClass()));
    }

    @Override
    public Annotation[] getAnnotations() {
        return annotations.toArray(new Annotation[0]);
    }
}
