package com.demo.spring;

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

    @SneakyThrows
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        UpdateOM.update(om, converters);
    }
}
