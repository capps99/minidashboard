package com.minidashboard.app.data.datasource


import com.minidashboard.app.data.datasource.CronDataSource.companion.KEY
import com.minidashboard.app.data.models.CronModel
import com.minidashboard.app.domain.persistence.DriverFactory
import com.russhwolf.settings.Settings
import io.github.aakira.napier.Napier
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

actual class CronDataSource actual constructor(
    driverFactory: DriverFactory,
    private val settings: Settings
) {
    object companion {
        const val KEY = "CRONS"
    }

    actual fun insert(uuid: String, setup: String, active: Boolean) {
        Napier.d { "JVM - insert" }
        val list = list()

        val result = list + CronModel(
            uuid = uuid,
            setup = setup,
            status = true
        )

        settings.putString(
            key = KEY,
            value = Json.encodeToString(result)
        )

    }

    actual fun list(): List<CronModel> {
        Napier.d { "JVM - list" }
        return try {
            val listString = settings.getString(KEY, "")
            return Json.decodeFromString<List<CronModel>>(listString)
        } catch (e: SerializationException) {
            // Handle parsing errors by returning an empty list
            emptyList()
        } catch (e: Exception) {
            // Handle other potential exceptions (like IO or runtime exceptions)
            emptyList()
        }
    }

    actual fun delete(uuid: String) {
        Napier.d { "JVM - list" }
        try {
            val listString = settings.getString(KEY, "")
            val list =  Json.decodeFromString<List<CronModel>>(listString)
            val result = list.filter { it.uuid != uuid}

            settings.putString(
                key = KEY,
                value = Json.encodeToString(result)
            )

        } catch (e: SerializationException) {
            // Handle parsing errors by returning an empty list
            Napier.w { "Error al eliminar objeto de la lista. ${e.localizedMessage}" }
        } catch (e: Exception) {
            // Handle other potential exceptions (like IO or runtime exceptions)
            Napier.w { "Error al eliminar objeto de la lista. ${e.localizedMessage}" }
        }
    }

}