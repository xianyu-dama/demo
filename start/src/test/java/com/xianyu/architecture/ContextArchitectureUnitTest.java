package com.xianyu.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.babyfish.jimmer.internal.GeneratedBy;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;
import static com.tngtech.archunit.core.domain.properties.CanBeAnnotated.Predicates.annotatedWith;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * 分层检测，不做业务依赖检测
 */
@AnalyzeClasses(
        packages = "com.xianyu",
        importOptions = ImportOption.DoNotIncludeTests.class
)
class ContextArchitectureUnitTest {

    @ArchTest
    static final ArchRule 非INFRA层不得依赖Jimmer生成类 =
            com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses()
                    .that().resideInAnyPackage(
                            "com.xianyu.(*).context.(*).adapter..",
                            "com.xianyu.(*).context.(*).app..",
                            "com.xianyu.(*).context.(*).domain.."
                    )
                    .should().dependOnClassesThat(annotatedWith(GeneratedBy.class));
    @ArchTest
    static final ArchRule 业务模块无环 = slices()
            .matching("com.xianyu.(*).context.(*)..")
            .should().beFreeOfCycles()
            .ignoreDependency(
                    annotatedWith(GeneratedBy.class),
                    resideInAnyPackage("..")
            )
            .ignoreDependency(
                    resideInAnyPackage(".."),
                    annotatedWith(GeneratedBy.class)
            );
    @ArchTest
    static final ArchRule 引用模块无环 = slices()
            .matching("com.xianyu.(*).context.reference.(*)..")
            .should().beFreeOfCycles()
            .ignoreDependency(
                    annotatedWith(GeneratedBy.class),
                    resideInAnyPackage("..")
            )
            .ignoreDependency(
                    resideInAnyPackage(".."),
                    annotatedWith(GeneratedBy.class)
            );
    // 定义各层常量
    private static final String ADAPTER_LAYER = "ADAPTER_LAYER";     // 适配层
    private static final String APP_LAYER = "APP_LAYER";            // 应用层
    private static final String DOMAIN_LAYER = "DOMAIN_LAYER";      // 领域层
    private static final String INFRA_LAYER = "INFRA_LAYER"; // 基础设施层
    @ArchTest
    static final ArchRule 分层依赖 = layeredArchitecture()
            .consideringAllDependencies()
            // (*)表示一级业务模块
            .layer(ADAPTER_LAYER).definedBy("com.xianyu.(*).context.(*).adapter..")
            .layer(APP_LAYER).definedBy("com.xianyu.(*).context.(*).app..")
            .layer(DOMAIN_LAYER).definedBy("com.xianyu.(*).context.(*).domain..")
            .layer(INFRA_LAYER).definedBy("com.xianyu.(*).context.(*).infr..")

            .whereLayer(ADAPTER_LAYER).mayNotBeAccessedByAnyLayer()
            .whereLayer(APP_LAYER).mayOnlyBeAccessedByLayers(ADAPTER_LAYER, INFRA_LAYER)
            .whereLayer(DOMAIN_LAYER).mayOnlyBeAccessedByLayers(ADAPTER_LAYER, APP_LAYER, INFRA_LAYER)
            .whereLayer(INFRA_LAYER).mayNotBeAccessedByAnyLayer()

            // 忽略所有与APT生成类相关的依赖（Jimmer）
            .ignoreDependency(
                    annotatedWith(GeneratedBy.class),
                    resideInAnyPackage("..")
            )
            .ignoreDependency(
                    resideInAnyPackage(".."),
                    annotatedWith(GeneratedBy.class)
            )

            .because("""
                    * 适配层 依赖 应用层、领域层
                    * 基础设施层 依赖 应用层、领域层
                    """);

}