package com.quest.evrouting.configuration.utils.secrets

import com.quest.evrouting.secrets.domain.PropertiesServicePort
import com.quest.evrouting.secrets.infrastructure.LocalPropertiesServiceAdapter

object SecretsConfig {
    val propertiesService: PropertiesServicePort by lazy {
        LocalPropertiesServiceAdapter(
            filePath = "secrets.properties",
            defaultFilePath = "defaults.properties"
        )
    }
}