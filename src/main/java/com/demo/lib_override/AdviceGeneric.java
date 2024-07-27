package com.demo.lib_override;

import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;

import static com.demo.functional.OptionF.o;
import static com.demo.lib_override.OverrideLibs.*;
import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.DYNAMIC;

public class AdviceGeneric {
    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    public static Object enter(@Advice.AllArguments(readOnly = false, typing = DYNAMIC) Object[] args,
                               @Advice.Origin Method method) {
        var name = method.getName();
        var f = nameToMethod.get(name);
        if (f != null)
            return f.apply(args);

        var methodArgIdx = nameToMethodArgsMod.get(name);
        if (methodArgIdx != null) {
            var argsMod = methodArgIdx.method.apply(args);
            if (argsMod != null)
                args = modArgs(args, methodArgIdx.argIdx, argsMod);
        }

        return null;
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    public static void exit(@Advice.Enter Object enter, @Advice.AllArguments Object[] args,
                            @Advice.Return(readOnly = false, typing = DYNAMIC) Object returned,
                            @Advice.Origin Method method) {

        var returnedOverride = returnedOverride(args, returned, method);
        if (returnedOverride != null)
            returned = returnedOverride;
        else if (enter != null)
            returned = enter;
    }

    public static Object returnedOverride(Object[] args, Object returned, Method method) {
        var name = method.getName();
        var fArgs = nameToMethodExitArgs.get(name);
        if (fArgs != null)
            return fArgs.apply(new ArgsReturned(args, returned));

        var fOpt = o(nameToMethodExit.get(method.getName()));
        return fOpt.flatMap(f -> o(f.apply(returned))).get();
    }

    public static Object[] modArgs(Object[] args, int idx, Object updated) {
        var argsCloned = args.clone();
        argsCloned[idx] = updated;
        return argsCloned;
    }
}
