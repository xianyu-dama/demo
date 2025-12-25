package com.xianyu.architecture;

import com.tngtech.archunit.base.DescribedPredicate;
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
                        // 忽略 Jimmer 生成的类
                        .or(new DescribedPredicate<>("not Jimmer generated classes") {
                            @Override
                            public boolean test(JavaClass javaClass) {
                                return javaClass.isAnnotatedWith(org.babyfish.jimmer.internal.GeneratedBy.class);
                            }
                        })
        );

        modules.verify();
    }
}