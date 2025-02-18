package com.demo.override.jackson;

import com.demo.override.meta.MetaList;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDelegatingDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer;
import com.fasterxml.jackson.databind.util.Converter;
import com.fasterxml.jackson.databind.util.StdConverter;

import java.util.ArrayList;
import java.util.Collection;

public class ListDeser {
    public static class Serializer<T> extends StdDelegatingSerializer {
        public Serializer(MetaList<T> metaList) {
            super(new StdConverter<T, Collection<?>>() {
                @Override
                public Collection<?> convert(T value) {
                    return metaList.toJava(value);
                }
            });
        }

        @Override
        protected StdDelegatingSerializer withDelegate(Converter<Object, ?> c, JavaType t, JsonSerializer<?> deser) {
            return new StdDelegatingSerializer(c, t, deser);
        }
    }

    public static class Deserializer<T> extends StdDelegatingDeserializer<T> {
        public Deserializer(MetaList<T> metaList) {
            super(new StdConverter<Collection<?>, T>() {
                @Override
                public T convert(Collection<?> value) {
                    return metaList.fromJava(new ArrayList<>(value));
                }
            });
        }

        @Override
        public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
            var elementType = property.getType().getBindings().getBoundType(0);
            var delegateType = ctxt.getTypeFactory().constructCollectionLikeType(Collection.class, elementType);
            return withDelegate(_converter, delegateType, ctxt.findContextualValueDeserializer(delegateType, property));
        }

        @Override
        protected StdDelegatingDeserializer<T> withDelegate(Converter<Object, T> c, JavaType t, JsonDeserializer<?> deser) {
            return new StdDelegatingDeserializer<>(c, t, deser);
        }
    }
}
