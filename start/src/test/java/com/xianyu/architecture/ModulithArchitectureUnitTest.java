package com.xianyu.architecture;

import com.tngtech.archunit.core.domain.JavaClass;
import com.xianyu.Application;
import org.junit.jupiter.api.Test;

import org.springframework.modulith.core.ApplicationModules;

class ModulithArchitectureUnitTest {

    @Test
    void verifyModularity() {
        var modules = ApplicationModules.of(
                Application.class,
                JavaClass.Predicates.resideInAPackage("com.xianyu.component..")
                        .or(JavaClass.Predicates.resideInAnyPackage("com.xianyu.config.."))
        );

        modules.verify();
    }

}
