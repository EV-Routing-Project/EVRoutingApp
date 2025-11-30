package com.quest.evrounting.secrets

import java.io.File
import java.io.FileInputStream
import java.net.URLDecoder
import java.util.Properties

class Property(
    private val filePath: String,
    private val defaultFilePath: String? = null
) {
    private val properties = Properties()
    init {
        val projectRoot = findProjectRoot("secrets")
        if(projectRoot != null) {
            val primaryFile = File(projectRoot, filePath)
            val defaultFile = defaultFilePath?.let { File(projectRoot, it) }
            if (primaryFile.exists()) {
                try {
                    FileInputStream(primaryFile).use { inputStream ->
                        properties.load(inputStream)
                    }
                } catch (e: Exception) {
                    println("Không thể tải tệp properties tại: $filePath")
                    e.printStackTrace()
                    if (defaultFile != null && defaultFile.exists()) {
                        try {
                            FileInputStream(defaultFile).use { inputStream ->
                                properties.load(inputStream)
                            }
                        } catch (e: Exception) {
                            println("Không thể tải tệp properties tại: $defaultFilePath")
                            e.printStackTrace()
                        }
                    }
                }
            } else if (defaultFile != null && defaultFile.exists()) {
                try {
                    FileInputStream(defaultFile).use { inputStream ->
                        properties.load(inputStream)
                    }
                } catch (e: Exception) {
                    println("Không thể tải tệp properties tại: $defaultFilePath")
                    e.printStackTrace()
                }
            }
        }
    }

    fun getProperty(key: String, defaultValue: String? = null): String? {
        return properties.getProperty(key, defaultValue)
    }

    fun getAllPropertyNames(): Set<String> {
        return properties.stringPropertyNames()
    }

    private fun findProjectRoot(rootDirName: String): File? {
        val classUrl = this::class.java.getResource("${this::class.simpleName}.class") ?: return null
        var currentFile = try {
            File(URLDecoder.decode(classUrl.path, "UTF-8"))
        } catch (e: Exception) {
            File(classUrl.path)
        }
        repeat(15) {
            if (currentFile.name == rootDirName) {
                return currentFile
            }
            currentFile = currentFile.parentFile ?: return null // Nếu không còn thư mục cha, dừng lại
        }
        return null
    }
}