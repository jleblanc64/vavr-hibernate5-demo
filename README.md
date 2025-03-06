# Vavr + Spring boot 2 + Hibernate 5 demo 

### 1) Add dependency to pom.xml
```xml
<dependency>
    <groupId>io.github.jleblanc64</groupId>
    <artifactId>vavr-hibernate5</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2) Add class ConfigInit
```java
import io.github.jleblanc64.hibernate5.hibernate.VavrHibernate5;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class ConfigInit implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext ctx) {
        VavrHibernate5.override();
    }
}
```

and register the class it in your `application.properties` file (replace `com.demo.spring` with your own package) :
```
context.initializer.classes=com.demo.spring.ConfigInit
```

### 3) Add class WebMvcConfig
```java
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
```

### 4) Write some Hibernate entities code using Vavr
```java
import io.vavr.collection.List;
import io.vavr.control.Option;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Option<String> name;

    private Option<Integer> number;

    private Option<String> city;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Order> orders;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "membership_id")
    private Option<Membership> membership;
}
```

### 5) Run Spring boot tests showing that Hibernate 5 + Vavr works

https://github.com/jleblanc64/hibernate5/blob/main/src/test/java/com/demo/ApplicationTests.java