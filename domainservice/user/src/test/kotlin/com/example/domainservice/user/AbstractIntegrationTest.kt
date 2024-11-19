package com.example.domainservice.user

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.ActiveProfiles

@ComponentScan(
    "com.example.domainservice.user",
    "com.example.domainservice.core",
    "com.example.core",
)
@SpringBootTest
@ActiveProfiles("test")
abstract class AbstractIntegrationTest
