package com.demo.spring;

import com.demo.implem.MetaListImpl;
import com.demo.implem.MetaOptionImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jleblanc64.hibernate5.jackson.deser.UpdateOM;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    ObjectMapper om;

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        var metaOption = new MetaOptionImpl();
        var metaList = new MetaListImpl();
        UpdateOM.update(om, converters, metaOption, metaList);
    }
}
