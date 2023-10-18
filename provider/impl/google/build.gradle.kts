/*
 * Copyright (C) 2023 Miłosz Moczkowski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    alias(libs.plugins.jb.compose)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.chart.jvm)
    alias(libs.plugins.chart.maven)
    alias(libs.plugins.chart.signing)
}

dependencies {
    implementation(projects.provider.api)
    implementation(libs.compose.desktop)
    implementation(libs.kotlinx.serialization.json)
    implementation(platform(libs.ktor.bom))
    implementation(libs.ktor.client.auth)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.serialization.json)
}
