package ru.melonhell.uma.core.common.api.annotation

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@ConditionalOnBean(Marker::class)
annotation class UmaContextOnly {
}