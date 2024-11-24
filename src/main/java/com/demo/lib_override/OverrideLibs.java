package com.demo.lib_override;

import com.demo.lib_override.sub.HibernateGet;
import com.demo.lib_override.sub.HibernateSet;
import com.demo.lib_override.sub.HibernateType;
import com.demo.lib_override.sub.Jackson;
import io.github.jleblanc64.libcustom.LibCustom;
import lombok.SneakyThrows;

public class OverrideLibs {
    private static volatile boolean overridden = false;

    @SneakyThrows
    public static void override() {
        synchronized (OverrideLibs.class) {
            if (overridden)
                return;
            else
                overridden = true;
        }

        HibernateGet.override();
        HibernateSet.override();
        HibernateType.override();
        Jackson.override();

        LibCustom.load();
    }
}
