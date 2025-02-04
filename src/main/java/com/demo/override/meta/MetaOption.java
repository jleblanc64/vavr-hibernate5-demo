package com.demo.override.meta;

import com.vladmihalcea.hibernate.type.array.internal.ListArrayTypeDescriptor;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.BasicJavaDescriptor;
import org.hibernate.type.descriptor.spi.JdbcRecommendedSqlTypeMappingContext;
import org.hibernate.type.descriptor.sql.JdbcTypeJavaClassMappings;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

import javax.persistence.AttributeConverter;
import java.util.ArrayList;
import java.util.List;

public interface MetaOption<T> extends WithClass<T> {
    T fromValue(Object v);

    Object getOrNull(Object o);

    default T fromList(List l) {
        if (isSuperClassOf(l))
            return (T) l;

        var v = l == null || l.isEmpty() ? null : l.get(0);
        return fromValue(v);
    }

    default List asList(Object t) {
        if (t instanceof List)
            return (List) t;

        var v = getOrNull(t);
        var l = v != null ? List.of(v) : List.of();
        return new ArrayList(l);
    }

    default <U> AttributeConverter<T, U> attributeConverter() {
        return new AttributeConverter<>() {

            @Override
            public U convertToDatabaseColumn(T attribute) {
                return (U) getOrNull(attribute);
            }

            @Override
            public T convertToEntityAttribute(U dbData) {
                return fromValue(dbData);
            }
        };
    }

    default BasicJavaDescriptor<T> hibernateDescriptor() {
        var descriptor = new ListArrayTypeDescriptor();

        return new BasicJavaDescriptor<T>() {

            @Override
            public Class<T> getJavaTypeClass() {
                return monadClass();
            }

            @Override
            public T fromString(String string) {
                var l = (List) descriptor.fromString(string);
                return (T) fromList(l);
            }

            @Override
            public <X> X unwrap(T value, Class<X> type, WrapperOptions options) {
                return (X) descriptor.unwrap(asList(value), type, options);
            }

            @Override
            public <X> T wrap(X value, WrapperOptions options) {
                var l = (List) descriptor.wrap(value, options);
                return (T) fromList(l);
            }

            @Override
            public SqlTypeDescriptor getJdbcRecommendedSqlType(JdbcRecommendedSqlTypeMappingContext context) {
                // match legacy behavior
                return context.getTypeConfiguration().getSqlTypeDescriptorRegistry().getDescriptor(
                        JdbcTypeJavaClassMappings.INSTANCE.determineJdbcTypeCodeForJavaClass(List.class)
                );
            }
        };
    }
}