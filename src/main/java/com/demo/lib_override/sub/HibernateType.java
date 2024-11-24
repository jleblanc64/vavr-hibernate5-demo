package com.demo.lib_override.sub;

import io.github.jleblanc64.libcustom.LibCustom;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.type.StringType;

import static com.demo.lib_override.FieldMocked.getRefl;
import static com.demo.lib_override.FieldMocked.getReflL;

public class HibernateType {
    public static void override() {
        LibCustom.overrideWithSelf(SimpleValue.class, "getType", argsS -> {
            var self = argsS.self;
            return getType((SimpleValue) self);
        });
    }


    public static Object getType(SimpleValue s) {
        var table = getRefl(s, "table", Table.class);
        var columns = getReflL(s, "columns", Column.class);
        if (table.getName().equals("customers") && columns.get(0).getName().equals("name"))
            return new StringType();

        return LibCustom.ORIGINAL;
    }
}
