package com.demo.deser;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDelegatingDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer;
import com.fasterxml.jackson.databind.util.Converter;
import com.fasterxml.jackson.databind.util.StdConverter;

import java.util.Collection;
import java.util.function.Function;

public class SetDeser {
    public static class Serializer<T> extends StdDelegatingSerializer {
        public Serializer(Function<T, Collection<?>> toJava) {
            super(new StdConverter<T, Collection<?>>() {
                @Override
                public Collection<?> convert(T value) {
                    return toJava.apply(value);
                }
            });
        }

        @Override
        protected StdDelegatingSerializer withDelegate(Converter<Object, ?> c, JavaType t, JsonSerializer<?> deser) {
            return new StdDelegatingSerializer(c, t, deser);
        }
    }

    public static class Deserializer<T> extends StdDelegatingDeserializer<T> {
        public Deserializer(Function<Collection<?>, T> fromJava) {
            super(new StdConverter<Collection<?>, T>() {
                @Override
                public T convert(Collection<?> value) {
                    var r = fromJava.apply(value);
                    return r;
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

