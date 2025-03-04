# Vavr + Spring + Hibernate 5 demo 

### 1) Add Maven dependency to pom.xml
```xml
<dependency>
    <groupId>io.github.jleblanc64</groupId>
    <artifactId>vavr-hibernate5</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2) Load custom Hibernate code in early Spring function
```java
@Bean
public DataSource getDataSource() {
    var metaList = new MetaListImpl();
    var metaOption = new MetaOptionImpl();
    
    VavrHibernate5.override(metaList);
    VavrSpring.override(metaList);
    VavrJackson.override(metaList);
    
    VavrHibernate5.override(metaOption);
    VavrSpring.override(metaOption);
    VavrJackson.override(metaOption);

    (...)
```

### 3) Load custom Jackson converters
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

### 5) Run tests showing that Hibernate 5 + Vavr code works

https://github.com/jleblanc64/hibernate5/blob/main/src/test/java/com/demo/ApplicationTests.java