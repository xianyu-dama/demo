package com.xianyu.architecture;

import com.xianyu.Application;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModulithArchitectureUnitTest {

    @Test
    void verifyModularity() {
        var modules = ApplicationModules.of(Application.class);
        modules.verify();
    }
}
