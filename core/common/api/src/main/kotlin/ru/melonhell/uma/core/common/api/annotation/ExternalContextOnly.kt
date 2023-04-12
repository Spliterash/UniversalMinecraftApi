package ru.melonhell.uma.core.common.api.annotation

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@ConditionalOnMissingBean(Marker::class)
annotation class ExternalContextOnly