package com.demo.lib_override.sub;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import java.util.List;

import static com.demo.lib_override.FieldMocked.getRefl;
import static com.demo.lib_override.OverrideLibs.mSelf;

public class SpringType {
    public static void override() {
        mSelf(SimpleValue.class, "getType", argsS -> {
            var self = argsS.self;
            return getType((SimpleValue) self);
        });
    }


    public static Type getType(SimpleValue s) {
        var table = (Table) getRefl(s, "table");
        var columns = (List<Column>) getRefl(s, "columns");
        if (table.getName().equals("customers") && columns.get(0).getName().equals("name"))
            return new StringType();

        return null;
    }
}
