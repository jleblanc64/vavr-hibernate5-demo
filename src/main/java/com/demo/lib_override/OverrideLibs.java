package com.demo.lib_override;

import com.demo.functional.Functor;
import com.demo.functional.ListF;
import com.demo.lib_override.sub.Jackson;
import com.demo.lib_override.sub.SpringGet;
import com.demo.lib_override.sub.SpringSet;
import com.demo.lib_override.sub.SpringType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.NamedElement;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;

import static com.demo.functional.ListF.empty;
import static com.demo.functional.ListF.f;
import static net.bytebuddy.agent.builder.AgentBuilder.InitializationStrategy.NoOp.INSTANCE;
import static net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy.RETRANSFORMATION;
import static net.bytebuddy.agent.builder.AgentBuilder.TypeStrategy.Default.REDEFINE;
import static net.bytebuddy.matcher.ElementMatcher.Junction;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.none;

public class OverrideLibs {
    public static Map<String, Function<Object[], Object>> nameToMethod;
    public static Map<String, Function<ArgsSelf, Object>> nameToMethodSelf;
    public static Map<String, Function<Object, Object>> nameToMethodExit;
    public static Map<String, Function<ArgsReturned, Object>> nameToMethodExitArgs;
    public static Map<String, MethodArgIdx> nameToMethodArgsMod;
    public static Map<String, MethodArgIdxSelf> nameToMethodArgsModSelf;
    private static final ListF<MethodDesc> methods = empty();
    private static final ListF<MethodDescSelf> methodsSelf = empty();
    private static final ListF<MethodDescExit> methodsExit = empty();
    private static final ListF<MethodDescExitArgs> methodsExitArgs = empty();
    private static final ListF<MethodDescArgsMod> methodsArgsMod = empty();
    private static final ListF<MethodDescArgsModSelf> methodsArgsModSelf = empty();

    interface MethodMeta {
        String getName();

        Class<?> getClazz();
    }

    @AllArgsConstructor
    @Getter
    static class MethodDesc implements MethodMeta {
        String name;
        Function<Object[], Object> method;
        Class<?> clazz;
    }

    @AllArgsConstructor
    @Getter
    static class MethodDescSelf implements MethodMeta {
        String name;
        Function<ArgsSelf, Object> method;
        Class<?> clazz;
    }

    @AllArgsConstructor
    @Getter
    static class MethodDescExit implements MethodMeta {
        String name;
        Function<Object, Object> method;
        Class<?> clazz;
    }

    @AllArgsConstructor
    @Getter
    static class MethodDescExitArgs implements MethodMeta {
        String name;
        Function<ArgsReturned, Object> method;
        Class<?> clazz;
    }

    @AllArgsConstructor
    @Getter
    static class MethodDescArgsMod implements MethodMeta {
        String name;
        MethodArgIdx method;
        Class<?> clazz;
    }

    @AllArgsConstructor
    @Getter
    static class MethodDescArgsModSelf implements MethodMeta {
        String name;
        MethodArgIdxSelf method;
        Class<?> clazz;
    }

    @AllArgsConstructor
    @Getter
    public static class ArgsReturned {
        public Object[] args;
        public Object returned;
    }

    @AllArgsConstructor
    @Getter
    public static class ArgsSelf {
        public Object[] args;
        public Object self;
    }

    @AllArgsConstructor
    @Getter
    public static class MethodArgIdx {
        public int argIdx;
        public Function<Object[], Object> method;
    }

    @AllArgsConstructor
    @Getter
    public static class MethodArgIdxSelf {
        public int argIdx;
        public Function<ArgsSelf, Object> method;
    }

    public static void m(Class<?> clazz, String name, Function<Object[], Object> method) {
        methods.add(new MethodDesc(name, method, clazz));
    }

    public static void mSelf(Class<?> clazz, String name, Functor.ThrowingFunction<ArgsSelf, Object> method) {
        methodsSelf.add(new MethodDescSelf(name, method, clazz));
    }

    public static void mExit(Class<?> clazz, String name, Function<Object, Object> method) {
        methodsExit.add(new MethodDescExit(name, method, clazz));
    }

    public static void mExitArgs(Class<?> clazz, String name, Function<ArgsReturned, Object> method) {
        methodsExitArgs.add(new MethodDescExitArgs(name, method, clazz));
    }

    public static void mArgsMod(Class<?> clazz, String name, int argIdx, Function<Object[], Object> method) {
        methodsArgsMod.add(new MethodDescArgsMod(name, new MethodArgIdx(argIdx, method), clazz));
    }

    public static void mArgsModSelf(Class<?> clazz, String name, int argIdx, Function<ArgsSelf, Object> method) {
        methodsArgsModSelf.add(new MethodDescArgsModSelf(name, new MethodArgIdxSelf(argIdx, method), clazz));
    }

    private static volatile boolean overridden = false;

    @SneakyThrows
    public static void override() {
        synchronized (OverrideLibs.class) {
            if (overridden)
                return;
            else
                overridden = true;
        }

        SpringGet.override();
        SpringSet.override();
        SpringType.override();
        Jackson.override();

        // fill nameToMethod
        nameToMethod = methods.toMap(m -> m.name, m -> m.method);
        nameToMethodExit = methodsExit.toMap(m -> m.name, m -> m.method);
        nameToMethodExitArgs = methodsExitArgs.toMap(m -> m.name, m -> m.method);
        nameToMethodArgsMod = methodsArgsMod.toMap(m -> m.name, m -> m.method);

        nameToMethodSelf = methodsSelf.toMap(m -> m.name, m -> m.method);
        nameToMethodArgsModSelf = methodsArgsModSelf.toMap(m -> m.name, m -> m.method);

        ByteBuddyAgent.install();

        var methodMetas = new ArrayList<MethodMeta>(methods);
        methodMetas.addAll(methodsExit);
        methodMetas.addAll(methodsExitArgs);
        methodMetas.addAll(methodsArgsMod);
        var classToMethods = f(methodMetas).groupBy(MethodMeta::getClazz, MethodMeta::getName);
        classToMethods.forEach((c, m) -> agent(c, m, AdviceGeneric.class));

        // self
        var methodMetasSelf = new ArrayList<MethodMeta>(methodsSelf);
        methodMetasSelf.addAll(methodsArgsModSelf);
        var classToMethodsSelf = f(methodMetasSelf).groupBy(MethodMeta::getClazz, MethodMeta::getName);
        classToMethodsSelf.forEach((c, m) -> agent(c, m, AdviceGenericSelf.class));
    }

    private static void agent(Class<?> clazz, ListF<String> methods, Class<?> adviceClass) {
        Junction<NamedElement> named = methods.fold(none(), (acc, m) -> acc.or(named(m)));
        new AgentBuilder.Default()
                .disableClassFormatChanges()
                .with(RETRANSFORMATION)
                .with(INSTANCE)
                .with(REDEFINE)
                .type(named(clazz.getName()))
                .transform((b, type, classLoader, module, x) -> b.visit(Advice.to(adviceClass).on(named)))
                .installOnByteBuddyAgent();
    }
}
