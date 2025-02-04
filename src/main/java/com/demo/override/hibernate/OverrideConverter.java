package com.demo.override.hibernate;

import com.demo.override.FieldMocked;
import com.demo.override.duplicate.JavaXProperty;
import com.demo.override.duplicate.ParameterizedTypeImpl;
import com.demo.override.meta.MetaList;
import com.demo.override.meta.MetaOption;
import com.fasterxml.classmate.TypeBindings;
import com.fasterxml.classmate.types.ResolvedObjectType;
import io.github.jleblanc64.libcustom.LibCustom;
import lombok.SneakyThrows;
import org.hibernate.annotations.common.reflection.ReflectionManager;
import org.hibernate.annotations.common.reflection.java.JavaXMember;
import org.hibernate.boot.internal.ClassmateContext;
import org.hibernate.boot.model.convert.internal.AbstractConverterDescriptor;
import org.hibernate.boot.model.convert.internal.ClassBasedConverterDescriptor;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.cfg.*;
import org.hibernate.resource.beans.internal.ManagedBeanRegistryImpl;
import org.hibernate.resource.beans.spi.ManagedBean;
import org.hibernate.resource.beans.spi.ProvidedInstanceManagedBeanImpl;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static com.demo.override.FieldMocked.getRefl;
import static com.demo.override.FieldMocked.typeArgIOptionF;
import static io.github.jleblanc64.libcustom.Reflection.mockAnnotation;
import static io.github.jleblanc64.libcustom.functional.ListF.f;

public class OverrideConverter {
    public static void overrideConverter(MetaOption metaOption, MetaList metaList) {
        LibCustom.modifyArg(AnnotationBinder.class, "processElementAnnotations", 2, args -> {
            var pid = (PropertyInferredData) args[2];
            var p = pid.getProperty();
            var type = (Type) getRefl(p, "type");
            var at = (AccessType) getRefl(pid, "defaultAccess");
            var rm = (ReflectionManager) getRefl(pid, "reflectionManager");
            var j = JavaXProperty.of((JavaXMember) p, type, metaOption, metaList);

            // handle simple type
            var typeArg = extractManyToOneIOptionF(p.getAnnotations(), type, metaOption);
            if (typeArg != null)
                return handleSimpleType(pid, typeArg, j, at, rm, metaOption, metaList);

            // handle param type
            typeArg = typeArgIOptionF(type, metaOption);
            if (typeArg != null)
                return handleParamType(pid, typeArg, j, at, rm, metaOption, metaList);

            // handle annotations
            if (type instanceof ParameterizedType) {
                var param = (ParameterizedType) type;
                if (metaOption.isSuperClassOf(param.getRawType())) {
                    var subType = param.getActualTypeArguments()[0];
                    var converter = converter((Class<?>) subType, metaOption);
                    if (converter != null)
                        return handleAnnotations(pid, converter.getClass(), j, at, rm);
                }
            }

            return LibCustom.ORIGINAL;
        });

        LibCustom.overrideWithSelf(ClassBasedConverterDescriptor.class, "createManagedBean", argsSelf -> {
            var self = (ClassBasedConverterDescriptor) argsSelf.self;

            if (isOptionConverter(self.getAttributeConverterClass())) {
                var jdbcType = getRefl(self, "jdbcType");
                var erasedType = (Class) getRefl(jdbcType, "_erasedType");

                var converter = converter(erasedType, metaOption);
                if (converter != null)
                    return new ProvidedInstanceManagedBeanImpl(converter);
            }

            return LibCustom.ORIGINAL;
        });

        LibCustom.overrideWithSelf(AbstractPropertyHolder.class, "makeAttributeConverterDescriptor", x -> {
            var conversion = (AttributeConversionInfo) x.args[0];
            var source = conversion.getSource();
            if (source instanceof JavaXProperty) {

                var self = (AbstractPropertyHolder) x.self;
                var context = (MetadataBuildingContext) getRefl(self, "context");

                return new ConverterDescriptorTyped(conversion.getConverterClass(), false,
                        context.getBootstrapContext().getClassmateContext(), (JavaXProperty) source);
            }

            return LibCustom.ORIGINAL;
        });

        LibCustom.overrideWithSelf(AbstractConverterDescriptor.class, "getDomainValueResolvedType", x -> {
            var converterClass = (Class) getRefl(x.self, "converterClass");
            if (isOptionConverter(converterClass))
                return ResolvedObjectType.create(metaOption.monadClass(), TypeBindings.emptyBindings(), null, null);

            return LibCustom.ORIGINAL;
        });

        LibCustom.overrideWithSelf(AbstractConverterDescriptor.class, "getRelationalValueResolvedType", x -> {
            if (x.self instanceof ConverterDescriptorTyped) {
                var typed = (ConverterDescriptorTyped) x.self;

                if (!isOptionConverter(typed.getAttributeConverterClass()))
                    return LibCustom.ORIGINAL;

                var paramTypeS = typed.prop.getJavaType().toString();
                var paramType = FieldMocked.paramClass(paramTypeS);
                return ResolvedObjectType.create(paramType, TypeBindings.emptyBindings(), null, null);
            }

            return LibCustom.ORIGINAL;
        });

        LibCustom.override(ManagedBeanRegistryImpl.class, "getBean", args -> {
            if (!(args[0] instanceof Class))
                return LibCustom.ORIGINAL;

            var clazz = (Class) args[0];
            if (isOptionConverter(clazz)) {
                var converter = metaOption.attributeConverter();
                return new ManagedBean() {

                    @Override
                    public Class getBeanClass() {
                        return converter.getClass();
                    }

                    @Override
                    public Object getBeanInstance() {
                        return converter;
                    }
                };
            }

            return LibCustom.ORIGINAL;
        });
    }

    static boolean isOptionConverter(Class c) {
        return c.getName().contains(".MetaOption$");
    }

    static AttributeConverter converter(Class<?> clazz, MetaOption metaOption) {
        return !isEntity(clazz.getDeclaredAnnotations()) ? metaOption.attributeConverter() : null;
    }

    // returns type arg
    public static Type extractManyToOneIOptionF(Annotation[] annotations, Type type, MetaOption metaOption) {
        if (!(type instanceof ParameterizedType))
            return null;

        var typeP = (ParameterizedType) type;
        var isManyToOne = isManyToOne(annotations);
        if (!isManyToOne || !metaOption.isSuperClassOf(typeP.getRawType()))
            return null;

        return typeP.getActualTypeArguments()[0];
    }

    public static boolean isManyToOne(Annotation[] annotations) {
        return f(annotations).stream().anyMatch(a -> a instanceof javax.persistence.ManyToOne);
    }

    public static boolean isEntity(Annotation[] annotations) {
        return f(annotations).stream().anyMatch(a -> a instanceof javax.persistence.Entity);
    }

    @SneakyThrows
    public static PropertyInferredData handleAnnotations(PropertyInferredData pid, Class<? extends AttributeConverter> converterClass,
                                                         JavaXProperty j, AccessType at, ReflectionManager rm) {

        var convert = mockAnnotation(Convert.class, Map.of("converter", converterClass));
        j.addAnnotation(convert);
        return new PropertyInferredData(pid.getDeclaringClass(), j, at.getType(), rm);
    }

    @SneakyThrows
    public static PropertyInferredData handleParamType(PropertyInferredData pid, Type typeArg,
                                                       JavaXProperty j, AccessType at, ReflectionManager rm,
                                                       MetaOption metaOption, MetaList metaList) {
        var fOrig = (Field) j.getMember();
        var f = FieldMocked.getIOptionF(fOrig, metaOption);
        var parameterized = ParameterizedTypeImpl.of(metaOption.monadClass(), typeArg, null);
        var jOver = JavaXProperty.of(f, parameterized, j, metaOption, metaList);

        // replace OneToOne with OneToMany
        var oneToOne = fOrig.getAnnotation(OneToOne.class);
        var methods = OneToOne.class.getDeclaredMethods();
        var memberValues = new HashMap<String, Object>();

        for (var method : methods) {
            var name = method.getName();
            var value = method.invoke(oneToOne);
            memberValues.put(name, value);
        }

        jOver.removeAnnotation(OneToOne.class);
        jOver.addAnnotation(mockAnnotation(OneToMany.class, memberValues));

        return new PropertyInferredData(pid.getDeclaringClass(), jOver, at.getType(), rm);
    }

    @SneakyThrows
    public static PropertyInferredData handleSimpleType(PropertyInferredData pid, Type typeArg,
                                                        JavaXProperty j, AccessType at, ReflectionManager rm,
                                                        MetaOption metaOption, MetaList metaList) {
        var f = FieldMocked.getSimple((Field) j.getMember(), typeArg);
        var jOver = JavaXProperty.of(f, typeArg, j, metaOption, metaList);
        return new PropertyInferredData(pid.getDeclaringClass(), jOver, at.getType(), rm);
    }

    private static class ConverterDescriptorTyped extends ClassBasedConverterDescriptor {
        public JavaXProperty prop;

        public ConverterDescriptorTyped(
                Class<? extends AttributeConverter> converterClass,
                Boolean forceAutoApply,
                ClassmateContext classmateContext,
                JavaXProperty prop) {
            super(converterClass, forceAutoApply, classmateContext);
            this.prop = prop;
        }
    }
}
