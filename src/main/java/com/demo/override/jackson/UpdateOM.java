package com.demo.override.jackson;

import com.demo.override.meta.MetaList;
import com.demo.override.meta.MetaOption;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.SneakyThrows;
import org.springframework.http.converter.HttpMessageConverter;

import java.util.List;

public class UpdateOM {
    @SneakyThrows
    public static void update(ObjectMapper om, List<HttpMessageConverter<?>> converters, MetaOption metaOption, MetaList metaList) {
        om.registerModule(new OptionModule(metaOption));

        var simpleModule = new SimpleModule()
                .addDeserializer(metaList.monadClass(), new ListDeser.Deserializer(metaList))
                .addSerializer(metaList.monadClass(), new ListDeser.Serializer(metaList));
        om.registerModule(simpleModule);

        var msgConverterClass = Class.forName("org.springframework.http.converter.json.MappingJackson2HttpMessageConverter");
        io.vavr.collection.List.ofAll(converters).filter(c -> msgConverterClass.isAssignableFrom(c.getClass()))
                .forEach(c -> setObjectMapper(c, om, msgConverterClass));
    }

    @SneakyThrows
    static void setObjectMapper(Object msgConverter, ObjectMapper om, Class<?> msgConverterClass) {
        msgConverterClass.getMethod("setObjectMapper", ObjectMapper.class).invoke(msgConverter, om);
    }
}
