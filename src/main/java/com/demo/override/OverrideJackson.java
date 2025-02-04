package com.demo.override;

import com.demo.override.meta.MetaOption;
import io.github.jleblanc64.libcustom.LibCustom;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;

import static com.demo.override.FieldMocked.*;

public class OverrideJackson {
    public static void override(MetaOption metaOption) {
        LibCustom.modifyReturn(AbstractJackson2HttpMessageConverter.class, "readJavaType", argsR -> {
            var returned = argsR.returned;
            if (returned == null)
                return returned;

            fields(returned).forEach(f -> {
                if (!metaOption.isSuperClassOf(f.getType()))
                    return;

                var opt = getRefl(returned, f);
                if (opt == null)
                    setRefl(returned, f, metaOption.fromValue(null));
            });

            return returned;
        });
    }
}
