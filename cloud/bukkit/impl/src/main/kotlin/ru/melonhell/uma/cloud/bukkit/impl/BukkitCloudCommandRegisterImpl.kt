package ru.melonhell.uma.cloud.bukkit.impl

import cloud.commandframework.annotations.AnnotationParser
import cloud.commandframework.arguments.parser.ParserParameters
import cloud.commandframework.arguments.parser.StandardParameters
import cloud.commandframework.bukkit.CloudBukkitCapabilities
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator
import cloud.commandframework.meta.CommandMeta
import cloud.commandframework.paper.PaperCommandManager
import org.bukkit.plugin.java.JavaPlugin
import org.springframework.stereotype.Component
import ru.melonhell.uma.cloud.bukkit.api.external.BukkitCloudCommandRegister
import ru.melonhell.uma.core.bukkit.api.wrappers.BukkitCommandSender.Companion.unwrap
import ru.melonhell.uma.core.bukkit.api.wrappers.BukkitCommandSender.Companion.wrap
import ru.melonhell.uma.core.common.api.wrappers.UmaCommandSender
import java.util.function.Function

@Component
class BukkitCloudCommandRegisterImpl : BukkitCloudCommandRegister {
    private val plugins = hashMapOf<JavaPlugin, BukkitCloudData>()

    private val parser = Function<ParserParameters, CommandMeta> { p: ParserParameters ->
        CommandMeta
            .simple()
            .with(CommandMeta.DESCRIPTION, p.get(StandardParameters.DESCRIPTION, "No description"))
            .build()
    }

    override fun manager(plugin: JavaPlugin): PaperCommandManager<UmaCommandSender> =
        plugins.computeIfAbsent(plugin, ::createManager).manager

    override fun registerCommand(plugin: JavaPlugin, obj: Any) {
        val cloudData = plugins.computeIfAbsent(plugin, ::createManager)

        cloudData.annotationParser.parse(obj)
    }

    private fun createManager(plugin: JavaPlugin): BukkitCloudData {
        val executionCoordinatorFunction = AsynchronousCommandExecutionCoordinator.builder<UmaCommandSender>().build()
        val manager = PaperCommandManager(
            plugin,
            executionCoordinatorFunction,
            { it.wrap() },
            { it.unwrap() }
        )

        if (manager.hasCapability(CloudBukkitCapabilities.BRIGADIER)) manager.registerBrigadier()
        if (manager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) manager.registerAsynchronousCompletions()
        val annotationParser = AnnotationParser(manager, UmaCommandSender::class.java, parser)

        return BukkitCloudData(manager, annotationParser)
    }


    private class BukkitCloudData(
        val manager: PaperCommandManager<UmaCommandSender>,
        val annotationParser: AnnotationParser<UmaCommandSender>
    )
}