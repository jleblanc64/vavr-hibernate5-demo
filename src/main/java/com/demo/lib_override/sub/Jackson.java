package com.demo.lib_override.sub;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonParser;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static com.demo.functional.Functor.minus;
import static com.demo.functional.ListF.f;
import static com.demo.lib_override.OverrideLibs.mSelf;
import static java.nio.charset.StandardCharsets.UTF_8;

public class Jackson {
    public static void override() {
        mSelf(ObjectMapper.class, "readValue", argsS -> {
            var args = argsS.args;
            var self = argsS.self;

            if (args == null || args.length != 2)
                return null;

            if (!(args[1] instanceof JavaType))
                return null;

            InputStream in;
            if (args[0] instanceof InputStream)
                in = (InputStream) args[0];
            else if (args[0] instanceof Reader)
                in = IOUtils.toInputStream(IOUtils.toString((Reader) args[0]), UTF_8);
            else
                return null;

            var jt = (JavaType) args[1];
            var clazz = jt.getRawClass();
            if (List.class.isAssignableFrom(clazz))
                return null;

            var s = fillMissingFields(IOUtils.toString(in, UTF_8), clazz);
            return ((ObjectMapper) self).readValue(s, jt);
        });
    }

    public static String fillMissingFields(String s, Class<?> clazz) {
        var jo = JsonParser.parseString(s).getAsJsonObject();

        var fields = f(clazz.getDeclaredFields()).filter(f -> Optional.class.equals(f.getType())).mapS(Field::getName);
        var fieldsJo = jo.keySet();
        var missingFields = minus(fields, fieldsJo);

        missingFields.forEach(f -> jo.addProperty(f, (String) null));
        return jo.toString();
    }
}
