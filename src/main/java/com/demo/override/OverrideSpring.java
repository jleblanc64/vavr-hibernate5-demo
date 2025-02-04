package com.demo.override;

import com.demo.override.duplicate.ParameterizedTypeImpl;
import com.demo.override.hibernate.OverrideConverter;
import com.demo.override.meta.MetaList;
import com.demo.override.meta.MetaOption;
import io.github.jleblanc64.libcustom.LibCustom;
import lombok.SneakyThrows;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.data.projection.DefaultMethodInvokingMethodInterceptor;
import org.springframework.data.repository.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.demo.override.FieldMocked.getRefl;
import static io.github.jleblanc64.libcustom.functional.ListF.f;

public class OverrideSpring {
    @SneakyThrows
    public static void override(MetaOption metaOption, MetaList metaList) {
        LibCustom.override(GenericConversionService.class, "convert", args -> {
            if (args == null || args.length != 3)
                return LibCustom.ORIGINAL;

            if (!(args[2] instanceof TypeDescriptor))
                return LibCustom.ORIGINAL;

            var targetType = (TypeDescriptor) args[2];
            if (!metaList.isSuperClassOf(targetType.getObjectType()))
                return LibCustom.ORIGINAL;

            var source = args[0];
            if (!(source instanceof Collection))
                return LibCustom.ORIGINAL;

            return metaList.fromJava(new ArrayList<>((Collection) source));
        });

        var clazz = Class.forName("org.springframework.data.util.TypeDiscoverer");
        LibCustom.modifyArg(clazz, "createInfo", 0, args -> {
            var type = args[0].toString();
            if (type.startsWith(metaOption.monadClass().getName() + "<")) {
                var paramClass = FieldMocked.paramClass(type);
                var isEntity = OverrideConverter.isEntity(paramClass.getDeclaredAnnotations());
                if (isEntity)
                    return ParameterizedTypeImpl.of(Optional.class, paramClass, null);
            }

            return LibCustom.ORIGINAL;
        });

        LibCustom.modifyReturn(MethodParameter.class, "getGenericParameterType", argsR -> {
            var returned = argsR.returned.toString();
            if (returned.startsWith(metaOption.monadClass().getName() + "<")) {
                var paramClass = FieldMocked.paramClass(returned);
                var isEntity = OverrideConverter.isEntity(paramClass.getDeclaredAnnotations());
                if (isEntity)
                    return ParameterizedTypeImpl.of(Optional.class, paramClass, null);
            }

            return LibCustom.ORIGINAL;
        });

        LibCustom.modifyReturn(DefaultMethodInvokingMethodInterceptor.class, "invoke", argsR -> {
            var returned = argsR.returned;
            var invocation = argsR.args[0];

            if (invocation.toString().contains(metaOption.monadClass().getName())) {
                var o = (Optional<?>) returned;
                var v = o.isEmpty() ? null : o.get();
                return metaOption.fromValue(v);
            }

            return returned;
        });

        clazz = Class.forName("org.springframework.aop.framework.JdkDynamicAopProxy");
        LibCustom.modifyArgWithSelf(clazz, "invoke", 2, argsS -> {
            var args = argsS.args;
            var self = argsS.self;

            var proxiedInterfaces = f((Class[]) getRefl(self, "proxiedInterfaces"));
            if (!proxiedInterfaces.contains(Repository.class))
                return args[2];

            if (args[2] == null)
                return args[2];

            var argsL = f((Object[]) args[2]);
            return argsL.map(arg -> {
                if (!metaOption.isSuperClassOf(arg) || arg instanceof List)
                    return arg;

                return metaOption.getOrNull(arg);
            }).toArray();
        });
    }
}
