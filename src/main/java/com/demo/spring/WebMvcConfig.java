package com.demo.spring;

import com.demo.deser.SetDeser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.github.jleblanc64.hibernate5.hibernate.Utils;
import io.github.jleblanc64.hibernate5.impl.MetaListImpl;
import io.github.jleblanc64.hibernate5.impl.MetaOptionImpl;
import io.github.jleblanc64.hibernate5.jackson.deser.ListDeser;
import io.github.jleblanc64.hibernate5.jackson.deser.OptionModule;
import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    ObjectMapper om;

    @SneakyThrows
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        var metaOption = new MetaOptionImpl();
        var metaList = new MetaListImpl();

        om.registerModule(new OptionModule(metaOption));

        var simpleModule = new SimpleModule()
                .addDeserializer(metaList.monadClass(), new ListDeser.Deserializer(metaList))
                .addSerializer(metaList.monadClass(), new ListDeser.Serializer(metaList));
        om.registerModule(simpleModule);

        Function<Collection<?>, Set> fromJava = HashSet::ofAll;
        Function<Set, Collection<?>> toJava = Set::toJavaSet;

        simpleModule = new SimpleModule()
                .addDeserializer(Set.class, new SetDeser.Deserializer(fromJava))
                .addSerializer(Set.class, new SetDeser.Serializer(toJava));
        om.registerModule(simpleModule);

        var msgConverterClass = Class.forName("org.springframework.http.converter.json.MappingJackson2HttpMessageConverter");
        io.vavr.collection.List.ofAll(converters).filter(c -> msgConverterClass.isAssignableFrom(c.getClass()))
                .forEach(c -> setObjectMapper(c, om));
    }

    @SneakyThrows
    static void setObjectMapper(Object msgConverter, ObjectMapper om) {
        Utils.invoke(msgConverter, "setObjectMapper", om);
    }
}
