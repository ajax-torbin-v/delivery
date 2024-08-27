package com.example.delivery.config

import com.example.delivery.annotaion.InvokeLog
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.cglib.proxy.Enhancer
import org.springframework.cglib.proxy.MethodInterceptor
import org.springframework.cglib.proxy.MethodProxy
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.lang.reflect.Method
import kotlin.reflect.KParameter
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.jvmErasure

@Component
//For testing purposes
@Order(Ordered.LOWEST_PRECEDENCE)
class LogInvokeAnnotationBeanPostProcessor : BeanPostProcessor {

    private val beans = HashMap<String, AnnotationTarget>()

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {
        if (bean.javaClass.isAnnotationPresent(InvokeLog::class.java)) {
            beans[beanName] = AnnotationTarget.CLASS
            return bean
        }
        val methods = bean.javaClass.declaredMethods
        for (method in methods) {
            if (method.isAnnotationPresent(InvokeLog::class.java)) {
                beans[beanName] = AnnotationTarget.FUNCTION
                return bean
            }
        }
        return bean
    }

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any {
        if (beans.containsKey(beanName)) {
            return createProxy(bean, beanName)
        }
        return bean
    }

    private fun createProxy(bean: Any, beanName: String): Any {
        val enhancer = Enhancer()
        enhancer.setSuperclass(bean.javaClass)

        bean::class.memberProperties.forEach {
            it.isAccessible = true
        }

        val constructorParams: List<KParameter> = bean::class.primaryConstructor!!.parameters
        val argumentTypes: Array<Class<*>> = constructorParams.map { it.type.jvmErasure.java }.toTypedArray()
        val arguments: Array<Any> = constructorParams.map { extractValue(it, bean) }.toTypedArray()

        enhancer.setCallback(createInterceptor(beans[beanName]!!))

        return enhancer.create(argumentTypes, arguments)
    }

    private fun extractValue(param: KParameter, bean: Any): Any {
        return bean::class.memberProperties.find { it.name == param.name }!!.call(bean)!!
    }

    private fun createInterceptor(target: AnnotationTarget): MethodInterceptor {
        return object : MethodInterceptor {
            override fun intercept(obj: Any?, method: Method?, args: Array<out Any>?, proxy: MethodProxy?): Any? {
                if (!method!!.isAnnotationPresent(InvokeLog::class.java) && target != AnnotationTarget.CLASS) {
                    return proxy?.invokeSuper(obj, args)
                } else {
                    logMethod(method, args)
                    return proxy?.invokeSuper(obj, args)
                }
            }
        }
    }

    private fun logMethod(method: Method, args: Array<out Any>?) {
        val className = method.declaringClass.name.substringAfterLast(".")
        val sb = StringBuilder("Method called: ${className}.${method.name}").append("(")
        if (args != null) {
            for (i in args.indices) {
                sb.append(method.parameters[i].name).append(":").append(args[i]).append(", ")
            }
        }
        sb.append(")")
        log.info(sb.toString().replace(", )", ")"))
    }

    companion object {
        val log: Logger = LoggerFactory.getLogger(LogInvokeAnnotationBeanPostProcessor::class.java)
    }
}
