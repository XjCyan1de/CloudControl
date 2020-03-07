package com.github.xjcyan1de.cloudcontrol.template

import com.github.xjcyan1de.cloudcontrol.api.service.ServiceStorage
import com.github.xjcyan1de.cloudcontrol.api.template.TemplateStorage


fun getStorage(storage: ServiceStorage): TemplateStorage = when (storage) {
    ServiceStorage.LOCAL -> LocalTemplateStorage
}