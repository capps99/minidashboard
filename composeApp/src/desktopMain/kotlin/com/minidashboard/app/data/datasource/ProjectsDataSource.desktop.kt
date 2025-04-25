package com.minidashboard.app.data.datasource

import com.minidashboard.app.data.models.ProjectModel
import com.minidashboard.app.domain.persistence.DriverFactory
import com.russhwolf.settings.Settings
import io.github.aakira.napier.Napier
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

actual class ProjectsDataSource actual constructor(
    private val driverFactory: DriverFactory,
    private val settings: Settings
) {

    private val KEY = "PROJECTS"

    actual fun delete() {
        Napier.d { "JVM - delete" }
    }

    actual fun find(uuid: String): ProjectModel? {
        Napier.d { "JVM - find" }
        return list().find { it.uuid == uuid }
    }

    actual fun insert(data: ProjectModel) {
        Napier.d { "JVM - insert" }
        val list = list()
        val result = list + data
        settings.putString(
            key = KEY,
            value = Json.encodeToString(result)
        )
    }

    actual fun update(data: ProjectModel) {
        Napier.d { "JVM - update" }
        when (val memory = find(uuid = data.uuid)) {
            null -> {
                // Create a new proccess if not exist.
                insert(data = data)
            }
            else -> {
                // Update proccess.
                updateCrons(current = data, memory = memory)
            }
        }

    }

    actual fun list(): List<ProjectModel> {
        Napier.d { "JVM - list" }
        return try {
            val listString = settings.getString(KEY, "")
            return Json.decodeFromString<List<ProjectModel>>(listString)
        } catch (e: SerializationException) {
            // Handle parsing errors by returning an empty list
            emptyList()
        } catch (e: Exception) {
            // Handle other potential exceptions (like IO or runtime exceptions)
            emptyList()
        }
    }

    actual fun clearAllData() {
        settings.putString(
            key = KEY,
            value = ""
        )
    }

    private fun updateCrons(current: ProjectModel, memory: ProjectModel){
        val previousMap = memory.crons.associateBy { it.uuid }
        val currentMap = current.crons.associateBy { it.uuid }

        // New items in current list that weren't in previous
        val added = current.crons.filter { it.uuid !in previousMap }

        // Removed items in previous list that aren't in current
        val removed = memory.crons.filter { it.uuid !in currentMap }

        // Modified items: same id but different content
        val modified = current.crons.filter { currentItem ->
            val previousItem = previousMap[currentItem.uuid]
            previousItem != null && previousItem != currentItem
        }

        // Unchanged items:  Nothing change.
        val unchanged = current.crons.filter { currentItem ->
            val previousItem = previousMap[currentItem.uuid]
            previousItem != null && previousItem == currentItem
        }

        Napier.d { "added: $added" }
        Napier.d { "removed: $removed" }
        Napier.d { "modified: $modified" }
        Napier.d { "unchanged: $unchanged" }

        val cronToStore = unchanged + modified + added
        val projects = list().toMutableList()
        val index = projects.indexOfFirst { it.uuid == current.uuid }
        if(index != -1){
            projects[index] = current
        }
        val results = projects

        // store projects
        settings.putString(
            key = KEY,
            value = Json.encodeToString(results)
        )
    }

    actual fun importData() {
        // TODO
    }

    actual fun exportData() {
        // TODO
    }

}