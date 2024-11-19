package com.example.domainservice.product.application.service

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.library.Architectures.onionArchitecture
import org.junit.jupiter.api.Test

internal class ProductModuleOnionArchitectureTest {
    @Test
    fun `module should be following valid onion architecture`() {
        val rule = onionArchitecture()
            .withOptionalLayers(true)
            .domainModels(DOMAIN)
            .applicationServices(APPLICATION)
            .adapter("mongo", MONGO_ADAPTER)
            .adapter("nats", NATS_ADAPTER)
            .adapter("redis", REDIS_ADAPTER)

        rule.check(importedClasses)
    }

    @Test
    fun `no outward dependencies from domain layer`() {
        val rule = noClasses()
            .that().resideInAPackage(DOMAIN)
            .should().dependOnClassesThat().resideInAPackage(APPLICATION)
            .orShould().dependOnClassesThat().resideInAPackage(INFRASTRUCTURE)

        rule.check(importedClasses)
    }

    @Test
    fun `no outward dependencies from application layer`() {
        val rule = noClasses()
            .that().resideInAPackage(APPLICATION)
            .should().dependOnClassesThat().resideInAPackage(INFRASTRUCTURE)

        rule.check(importedClasses)
    }

    companion object {
        private val importedClasses: JavaClasses = ClassFileImporter()
            .withImportOption(ImportOption.DoNotIncludeTests())
            .importPackages("com.example.domainservice.product")

        private const val DOMAIN = "..domain.."
        private const val APPLICATION = "..application.."
        private const val INFRASTRUCTURE = "..infrastructure.."
        private const val MONGO_ADAPTER = "..infrastructure.mongo.."
        private const val NATS_ADAPTER = "..infrastructure.nats.."
        private const val REDIS_ADAPTER = "..infrastructure.redis.."
    }
}
