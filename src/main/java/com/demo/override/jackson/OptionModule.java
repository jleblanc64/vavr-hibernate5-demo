package com.demo.override.jackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import com.fasterxml.jackson.datatype.jdk8.PackageVersion;
import io.github.jleblanc64.libcustom.meta.MetaOption;
import lombok.AllArgsConstructor;

// https://github.com/FasterXML/jackson-modules-java8/blob/2.13/datatypes/src/main/java/com/fasterxml/jackson/datatype/jdk8/Jdk8Module.java
@AllArgsConstructor
public class OptionModule extends Module {
    MetaOption<?> metaOption;

    @Override
    public void setupModule(SetupContext context) {
        var ser = new SimpleSerializers();
        ser.addSerializer(metaOption.monadClass(), new OptionSer<>(metaOption));
        context.addSerializers(ser);

        context.addDeserializers(metaOption.deserBase());
        context.addTypeModifier(metaOption.typeModifier());
    }

    @Override
    public String getModuleName() {
        return "";
    }

    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }
}
