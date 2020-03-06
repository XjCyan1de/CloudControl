package com.github.xjcyan1de.cloudcontrol.launcher

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

private fun setupEnvironmentVariables() {
    System.setProperty("file.encoding", "UTF-8")
    System.setProperty("io.netty.noPreferDirect", "true")
    System.setProperty("client.encoding.override", "UTF-8")
}

private fun rootWarning() {
    val user = System.getProperty("user.name")
    if (user.equals("root", true) || user.equals("administrator", true)) {
        System.err.println("=======================================================================")
        System.err.println("ВЫ ИСПОЛЬЗУЕТЕ ПРАВА АДМИНИСТРАТОРА ИЛИ ROOT АККАУНТ. ЭТО НЕ БЕЗОПАСНО!")
        System.err.println("ВЫ ПОДВЕРГАЕТЕ СЕБЯ ПОТЕНЦИАЛЬНОМУ РИСКУ!")
        System.err.println("ВИРУСЫ, ВЗЛОМАННЫЕ ПЛАГИНЫ И ХАКЕРЫ МОГУТ ПОЛУЧИТЬ ДОСТУП К ВАШЕЙ СЕРВЕРНОЙ МАШИНЕ!")
        System.err.println("НАСТОЯТЕЛЬНО РЕКОМЕНДУЕМ ЗАПУСКАТЬ ЭТО ПРИЛОЖЕНИЕ С ОГРАНИЧЕННЫМИ ПРАВАМИ!")
        System.err.println("=======================================================================")
    }
}

val LAUNCHER_CONFIG = System.getProperty("cloudcontrol.launcher.config", "launcher.conf")
val LAUNCHER_DIR = System.getProperty("cloudcontrol.launcher.dir", "launcher")
val FALLBACK_VERSION = CloudControlLauncher::class.java.`package`.specificationVersion
const val CLOUDCONTROL_REPOSITORY_AUTO_UPDATE = "cloudcontrol.auto-update"
const val CLOUDCONTROL_REPOSITORY = "cloudcontrol.repository"

fun main(args: Array<String>) {
    setupEnvironmentVariables()
    rootWarning()
    CloudControlLauncher.run(args)
}

lateinit var config: Config

object CloudControlLauncher {
    fun run(args: Array<String>) {
        println("Запускаем CloudControl Launcher ${javaClass.`package`.implementationVersion}...")
        val configPath = Paths.get(LAUNCHER_CONFIG)
        val launcherDirectory = Paths.get(LAUNCHER_DIR)

        File(launcherDirectory.toFile(), "versions").mkdirs()
        File(launcherDirectory.toFile(), "libs").mkdirs()

        config = ConfigFactory.parseFile(configPath.toFile())
        if (config.hasPath(CLOUDCONTROL_REPOSITORY_AUTO_UPDATE)
            && config.getBoolean(CLOUDCONTROL_REPOSITORY_AUTO_UPDATE)
            && config.hasPath(CLOUDCONTROL_REPOSITORY)
        ) {

        }
    }
}

private fun checkAutoUpdate(launcherDirectory: Path, url: String) {

}

