package com.demo.spring;

import io.github.jleblanc64.libcustom.LibCustom;
import lombok.SneakyThrows;

public class OverrideContentType {
    @SneakyThrows
    public static void override() {
        // accept text/plain content-type as json
        var httpHeadersClass = Class.forName("org.springframework.http.HttpHeaders");
        var mediaTypeClass = Class.forName("org.springframework.http.MediaType");
        LibCustom.modifyReturn(httpHeadersClass, "getContentType", argsR -> {
            var mediaType = argsR.returned;
            if (mediaType != null && mediaType.toString().toLowerCase().startsWith("text/plain"))
                return mediaTypeClass.getMethod("parseMediaType", String.class).invoke(null, "application/json");

            return mediaType;
        });
    }
}
