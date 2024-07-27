package com.demo.lib_override;

import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;

import static com.demo.lib_override.AdviceGeneric.modArgs;
import static com.demo.lib_override.OverrideLibs.nameToMethodArgsModSelf;
import static com.demo.lib_override.OverrideLibs.nameToMethodSelf;
import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.DYNAMIC;

public class AdviceGenericSelf {
    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    public static Object enter(@Advice.This Object self, @Advice.AllArguments(readOnly = false, typing = DYNAMIC) Object[] args,
                               @Advice.Origin Method method) {
        var name = method.getName();

        var f = nameToMethodSelf.get(name);
        if (f != null)
            return f.apply(new OverrideLibs.ArgsSelf(args, self));

        var methodArgIdxSelf = nameToMethodArgsModSelf.get(name);
        if (methodArgIdxSelf != null) {
            var argsMod = methodArgIdxSelf.method.apply(new OverrideLibs.ArgsSelf(args, self));
            if (argsMod != null)
                args = modArgs(args, methodArgIdxSelf.argIdx, argsMod);
        }

        return null;
    }

    @Advice.OnMethodExit
    public static void exit(@Advice.Enter Object enter, @Advice.Return(readOnly = false, typing = DYNAMIC) Object returned) {
        if (enter instanceof ValueWrapper)
            returned = ((ValueWrapper) enter).value;
        else if (enter != null)
            returned = enter;
    }
}
