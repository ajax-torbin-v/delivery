package com.example.delivery.bpp

import com.example.delivery.annotaion.LogInvoke
import com.example.delivery.config.LogInvokeAnnotationBeanPostProcessor
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.aop.support.AopUtils

@ExtendWith(MockitoExtension::class)
class LogInvokeTest {

    private val beanPostProcessor = LogInvokeAnnotationBeanPostProcessor()

    companion object {
        const val FOO_MESSAGE = "I AM ANNOTATED FOO WITH NO ARGUMENTS"
        const val QUX_MESSAGE = "I AM QUX IN NOT ANNOTATED CLASS"
    }

    open class AnnotatedClass {
        @LogInvoke
        fun foo(): String {
            return FOO_MESSAGE
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
        val proxy = beanPostProcessor.postProcessAfterInitialization(bean, beanName)

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
