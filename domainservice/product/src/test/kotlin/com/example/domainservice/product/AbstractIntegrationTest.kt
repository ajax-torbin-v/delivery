package com.example.domainservice.product

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.ActiveProfiles

@ComponentScan(
    "com.example.domainservice",
    "com.example.domainservice.product",
    "com.example.core"
)
@SpringBootTest
@ActiveProfiles("test")
abstract class AbstractIntegrationTest
