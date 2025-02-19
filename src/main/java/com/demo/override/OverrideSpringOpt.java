package com.demo.override;

import com.sympheny.app.hibernate.override.duplicate.ParameterizedTypeImpl;
import io.github.jleblanc64.libcustom.LibCustom;
import io.github.jleblanc64.libcustom.meta.MetaOption;
import lombok.SneakyThrows;
import org.springframework.core.MethodParameter;
import org.springframework.data.projection.DefaultMethodInvokingMethodInterceptor;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

import static com.demo.override.Utils.getRefl;
import static io.github.jleblanc64.libcustom.functional.ListF.f;

public class OverrideSpringOpt {
    @SneakyThrows
    public static void override(MetaOption metaOption) {
        var clazz = Class.forName("org.springframework.data.util.TypeDiscoverer");
        LibCustom.modifyArg(clazz, "createInfo", 0, args -> {
            var type = args[0].toString();
            if (type.startsWith(metaOption.monadClass().getName() + "<")) {
                var paramClass = Utils.paramClass(type);
                var isEntity = Utils.isEntity(paramClass.getDeclaredAnnotations());
                if (isEntity)
                    return ParameterizedTypeImpl.of(Optional.class, paramClass, null);
            }

            return LibCustom.ORIGINAL;
        });

        LibCustom.modifyReturn(MethodParameter.class, "getGenericParameterType", argsR -> {
            var returned = argsR.returned.toString();
            if (returned.startsWith(metaOption.monadClass().getName() + "<")) {
                var paramClass = Utils.paramClass(returned);
                var isEntity = Utils.isEntity(paramClass.getDeclaredAnnotations());
                if (isEntity)
                    return ParameterizedTypeImpl.of(Optional.class, paramClass, null);
            }

            return LibCustom.ORIGINAL;
        });

        LibCustom.modifyReturn(DefaultMethodInvokingMethodInterceptor.class, "invoke", argsR -> {
            var returned = argsR.returned;
            var invocation = argsR.args[0];

            if (invocation.toString().contains(metaOption.monadClass().getName())) {
                if (metaOption.isSuperClassOf(returned))
                    return returned;

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
