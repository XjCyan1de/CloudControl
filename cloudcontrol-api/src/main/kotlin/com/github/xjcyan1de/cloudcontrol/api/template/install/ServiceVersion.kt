package com.github.xjcyan1de.cloudcontrol.api.template.install

import com.google.gson.JsonObject
import java.net.URL

data class ServiceVersion(
    val name: String,
    val url: URL,
    val deprecated: Boolean = false,
    val properties: JsonObject = JsonObject(),
    val additionalDownloads: Map<String, String> = hashMapOf()
) {
    val isLatest: Boolean = name.equals("latest", true)
}