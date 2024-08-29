package com.example.delivery.config

import com.example.delivery.annotaion.InvokeLog
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.cglib.proxy.Enhancer
import org.springframework.cglib.proxy.MethodInterceptor
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.lang.invoke.MethodHandles
import java.lang.reflect.Method
import kotlin.reflect.KParameter
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.jvmErasure

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class LogInvokeAnnotationBeanPostProcessor : BeanPostProcessor {

    private val beans = HashMap<String, AnnotationTarget>()

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {
        val beanClass = bean.javaClass
        when {
            beanClass.isAnnotationPresent(InvokeLog::class.java) -> {
                beans[beanName] = AnnotationTarget.CLASS
            }

            beanClass.declaredMethods.any { it.isAnnotationPresent(InvokeLog::class.java) } -> {
                beans[beanName] = AnnotationTarget.FUNCTION
            }
        }
        return bean
    }

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any {
        return if (beans.containsKey(beanName)) {
            createProxy(bean, beanName)
        } else {
            bean
        }
    }

    private fun createProxy(bean: Any, beanName: String): Any {
        bean::class.memberProperties.forEach {
            it.isAccessible = true
        }

        val constructorParams: List<KParameter> = bean::class.primaryConstructor!!.parameters
        val argumentTypes: Array<Class<*>> = constructorParams.map { it.type.jvmErasure.java }.toTypedArray()
        val arguments: Array<Any> = constructorParams.map { extractValue(it, bean) }.toTypedArray()

        return Enhancer().apply {
            setSuperclass(bean.javaClass)
            setCallback(createInterceptor(beans.getValue(beanName)))
        }.create(argumentTypes, arguments)
    }

    private fun extractValue(param: KParameter, bean: Any): Any {
        return bean::class.memberProperties.first { it.name == param.name }.call(bean)!!
    }

    private fun createInterceptor(target: AnnotationTarget): MethodInterceptor {
        return MethodInterceptor { obj, method, args, proxy ->
            if (target == AnnotationTarget.CLASS || method.isAnnotationPresent(InvokeLog::class.java)) {
                logMethod(method, args)
            }
            proxy.invokeSuper(obj, args)
        }
    }

    private fun logMethod(method: Method, args: Array<out Any>) {
        val className = method.declaringClass.name.substringAfterLast(".")
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
        private val log: Logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())
    }
}
