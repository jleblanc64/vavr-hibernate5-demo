package com.demo.spring;

import com.demo.override.OverrideHibernate;
import com.demo.override.OverrideSpring;
import com.demo.override.meta.MetaListF;
import com.demo.override.meta.MetaOptionF;
import com.zaxxer.hikari.HikariDataSource;
import io.github.jleblanc64.libcustom.LibCustom;
import io.github.jleblanc64.libcustom.custom.spring.VavrSpring6;
import io.github.jleblanc64.libcustom.custom.utils.FieldCustomType;
import io.github.jleblanc64.libcustom.custom.utils.TypeImpl;
import lombok.SneakyThrows;
import org.flywaydb.core.Flyway;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.Entity;
import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static io.github.jleblanc64.libcustom.FieldMocked.getRefl;
import static io.github.jleblanc64.libcustom.functional.ListF.f;

@Configuration
public class DataSourceConfig {
    @Value("${spring.datasource.url}")
    String url;

    @Value("${spring.datasource.username}")
    String username;

    @Value("${spring.datasource.password}")
    String password;

    @Bean
    public DataSource getDataSource() {
        // list models
        var models = f(new Reflections("com.demo.model").getTypesAnnotatedWith(Entity.class));
        if (models.isEmpty())
            throw new RuntimeException("models cannot be empty");

        // meta
        var metaOption = new MetaOptionF();
        var metaList = new MetaListF();

        // override
        overrideHibernateOpt(metaOption);
        VavrSpring6.override();
        OverrideHibernate.override(metaList, metaOption, models);
        OverrideSpring.override(metaList);

        LibCustom.load();

        // Hikari
        var ds = new HikariDataSource();
        ds.setJdbcUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);

        // Flyway migration
        var config = Flyway.configure().dataSource(url, username, password);
        config.load().migrate();

        return ds;
    }

    @SneakyThrows
    static void overrideHibernateOpt(MetaOptionF metaOption) {
        var setterFieldImplClass = Class.forName("org.hibernate.property.access.spi.SetterFieldImpl");
        var getterFieldImplClass = Class.forName("org.hibernate.property.access.spi.GetterFieldImpl");


        LibCustom.modifyArgWithSelf(setterFieldImplClass, "set", 1, argsSelf -> {
            var args = argsSelf.args;
            var value = args[1];
            var self = argsSelf.self;
            var field = (Field) getRefl(self, setterFieldImplClass.getDeclaredField("field"));

            if (metaOption.isSuperClassOf(field.getType()) && !metaOption.isSuperClassOf(value))
                return metaOption.fromValue(value);

            return LibCustom.ORIGINAL;
        });

        LibCustom.modifyReturn(getterFieldImplClass, "get", x -> {
            var ret = x.returned;
            if (metaOption.isSuperClassOf(ret))
                return metaOption.getOrNull(ret);

            return ret;
        });

        LibCustom.modifyArg(Class.forName("org.hibernate.annotations.common.reflection.java.JavaXProperty"), "create", 0, args -> {
            var member = args[0];
            if (member instanceof Field) {
                var field = (Field) member;
                if (!(field.getGenericType() instanceof ParameterizedType))
                    return LibCustom.ORIGINAL;

                var type = (ParameterizedType) field.getGenericType();
                var typeRaw = type.getRawType();
                var typeParam = type.getActualTypeArguments()[0];
                var ownerType = ((ParameterizedType) field.getGenericType()).getOwnerType();

                if (metaOption.isSuperClassOf(typeRaw))
                    return FieldCustomType.create(field, new TypeImpl((Class<?>) typeParam, new Type[]{}, ownerType));
            }

            return LibCustom.ORIGINAL;
        });
    }
}
