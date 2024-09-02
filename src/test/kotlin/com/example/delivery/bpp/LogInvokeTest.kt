package com.example.delivery.bpp

import com.example.delivery.annotaion.LogInvoke
import com.example.delivery.config.LogInvokeAnnotationBeanPostProcessor
import io.mockk.mockkStatic
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.slf4j.LoggerFactory
import org.springframework.aop.support.AopUtils

@ExtendWith(MockitoExtension::class)
class LogInvokeTest {

    private val beanPostProcessor = LogInvokeAnnotationBeanPostProcessor()

    @BeforeEach
    fun setUp() {
        mockkStatic(LoggerFactory::class)
    }

    companion object {
        const val FOO_MESSAGE = "I AM ANNOTATED FOO WITH NO ARGUMENTS"
        const val BAR_MESSAGE = "I AM NOT ANNOTATED BAR WITH ARGUMENTS"
        const val QUX_MESSAGE = "I AM QUX IN NOT ANNOTATED CLASS"
    }

    open class AnnotatedClass {
        @LogInvoke
        fun foo(): String {
            return FOO_MESSAGE
        }

        fun bar(i: Int, s: String): String {
            return BAR_MESSAGE
        }
    }

    open class NotAnnotatedClass {
        fun qux(): String {
            return QUX_MESSAGE
        }
    }

    @Test
    fun `should create proxy on class with annotations`() {
        //GIVEN
        val bean = AnnotatedClass()
        val beanName = "annotatedClass"
        beanPostProcessor.postProcessBeforeInitialization(bean, beanName)
        //WHEN
        val proxy = beanPostProcessor.postProcessAfterInitialization(bean, beanName) as AnnotatedClass
        //THEN
        assert(AopUtils.isAopProxy(proxy))
    }

    @Test
    fun `should not create proxy on class without annotations`() {
        //GIVEN
        val bean = NotAnnotatedClass()
        val beanName = "notAnnotatedClass"
        beanPostProcessor.postProcessBeforeInitialization(bean, beanName)

        //WHEN
        val proxy = beanPostProcessor.postProcessAfterInitialization(bean, beanName)

        //THEN
        assert(!AopUtils.isAopProxy(proxy))
    }
}
