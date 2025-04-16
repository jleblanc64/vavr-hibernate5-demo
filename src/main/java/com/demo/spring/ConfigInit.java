package com.demo.spring;

import io.github.jleblanc64.hibernate5.hibernate.VavrHibernate5;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class ConfigInit implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext ctx) {
        VavrHibernate5.override();
    }
}
