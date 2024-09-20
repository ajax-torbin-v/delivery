package com.example.delivery.config

import com.example.delivery.annotaion.LogInvoke
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.aop.MethodBeforeAdvice
import org.springframework.aop.framework.ProxyFactory
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component
import java.lang.invoke.MethodHandles
import java.lang.reflect.Method

@Component
class LogInvokeAnnotationBeanPostProcessor : BeanPostProcessor {
    private val beans = HashMap<String, List<Method>>()

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {
        val annotatedMethods = bean.javaClass
            .declaredMethods
            .filter { it.isAnnotationPresent(annotation) }
        if (annotatedMethods.isNotEmpty()) {
            beans[beanName] = annotatedMethods
        }
        return bean
    }

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any {
        return if (beans.contains(beanName)) {
            createProxy(bean, beans.getValue(beanName))
        } else {
            bean
        }
    }

    fun Method.signatureEqual(method: Method): Boolean {
        return this.name == method.name &&
            this.parameters.contentEquals(method.parameters) &&
            this.returnType == method.returnType
    }

    private fun createProxy(bean: Any, annotatedMethods: List<Method>): Any {
        val proxyFactory = ProxyFactory()
        proxyFactory.setTarget(bean)
        proxyFactory.addAdvice(object : MethodBeforeAdvice {
            override fun before(method: Method, args: Array<Any?>, target: Any?) {
                if (annotatedMethods.map { it.signatureEqual(method) }.first()) {
                    logMethod(bean::class.java.simpleName, method, args)
                }
            }
        })
        return proxyFactory.proxy
    }

    private fun logMethod(className: String, method: Method, args: Array<Any?>) {
        val arguments = method.parameters
            .withIndex()
            .joinToString(separator = ",") { (index, param) -> "${param.name} : ${args[index]}" }

        log.atInfo()
            .setMessage("Method called: {}.{}({})")
            .addArgument { className }
            .addArgument { method.name }
            .addArgument { arguments }
            .log()
    }

    companion object {
        private val annotation = LogInvoke::class.java
        private val log: Logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())
    }
}
