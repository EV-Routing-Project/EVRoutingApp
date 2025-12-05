package com.quest.evrouting.configuration.secrets.model

import java.io.File
import java.io.FileInputStream
import java.net.URLDecoder
import java.util.Properties
import kotlin.io.path.exists

class Property(
    private val filePath: String,
    private val defaultFilePath: String = "defaults.properties"
) {
    private val properties: Properties by lazy {
        loadProperties(filePath, defaultFilePath)
    }

    private fun loadProperties(primaryPath: String, fallbackPath: String): Properties {
        val pros = Properties()
        val projectRoot = findProjectRoot() ?: return pros

        val primaryFile = File(projectRoot, primaryPath)
        loadFromFile(primaryFile)
            .onSuccess { pros.putAll(it) }
            .onFailure {
                fallbackPath.let {
                    val defaultFile = File(projectRoot, it)
                    loadFromFile(defaultFile)
                        .onSuccess { pros.putAll(it) }
                }
            }
        return pros
    }

    private fun loadFromFile(file: File): Result<Properties> {
        return runCatching {
            if(!file.toPath().exists()){
                throw IllegalStateException("File not found: ${file.toPath()}")
            }
            Properties().apply {
                FileInputStream(file).use {
                    load(it)
                }
            }
        }
    }

    fun getProperty(key: String, defaultValue: String = ""): String {
        return properties.getProperty(key, defaultValue)
    }

    fun getAllPropertyNames(): Set<String> {
        return properties.stringPropertyNames()
    }

    fun findProjectRoot(): File? {
        val classUrl = this::class.java.protectionDomain.codeSource.location
        var currDir = try {
            File(URLDecoder.decode(classUrl.path, "UTF-8"))
        } catch (e: Exception){
            File(classUrl.path)
        }
        repeat(30) {
            val dir = File(currDir, filePath)
            val defaultDir = File(currDir, defaultFilePath)
            if (dir.isFile || defaultDir.isFile) {
                return currDir
            }
            currDir = currDir.parentFile ?: return null
        }
        return null
    }
}