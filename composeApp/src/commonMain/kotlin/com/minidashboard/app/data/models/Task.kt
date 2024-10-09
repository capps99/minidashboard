package com.minidashboard.app.data.models

import com.minidashboard.app.domain.command.Command
import io.github.aakira.napier.Napier
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.datetime.*
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kotlin.random.Random

private val dateFormat = LocalDate.Format {
    monthNumber(padding = Padding.SPACE)
    char('/')
    dayOfMonth()
    char(' ')
    year()
}

// Define a sealed interface for cron processes
@Serializable
sealed interface Task {
    val common: TaskCommmon
    val schedule: TaskSchedule
    val setup: TaskConfig
    val rules: List<Rule>

    val uuid: String
        get() = common.uuid

    val lastUpdate: String

    suspend fun execute(result: (ProccessResult) -> Unit)
    fun validate(): Boolean
}

@Serializable
data class TaskSchedule(
    val time: Int,
    val interval: String
) {

    companion object {
        const val MINUTES = "Minutes"
        const val HOURS = "Hours"
        const val DAYS = "Days"
        const val WEEK = "Week"
    }

    fun nextLaunch(): Long {
        fun minutesToMillis(minutes: Long): Long = minutes * 60 * 1000
        fun hoursToMillis(hours: Long): Long = hours * 60 * 60 * 1000
        fun daysToMillis(days: Long): Long = days * 24 * 60 * 60 * 1000

        return when (interval) {
            MINUTES -> minutesToMillis(time.toLong())
            HOURS -> hoursToMillis(time.toLong())
            DAYS -> daysToMillis(time.toLong())
            else -> minutesToMillis(time.toLong())
        }
    }

}

@Serializable
data class TaskCommmon(
    val uuid: String = Random.nextInt(1, 99999).toString(),
    val title: String,
    val description: String,
    val active: Boolean,
)

// Represent different configurations for each process type
@Serializable
sealed interface TaskConfig

@Serializable
@SerialName("HttpSetup")
data class HttpSetupConfig(val url: String, val parameters: Map<String, String> = emptyMap()) : TaskConfig

@Serializable
@SerialName("WebSocketSetup")
data class WebSocketSetupConfig(val endpoint: String, val protocols: List<String> = emptyList()) : TaskConfig

@Serializable
@SerialName("CommandSetup")
data class CommandSetupConfig(val command: String) : TaskConfig

@Serializable
sealed interface Rule {
    fun isValid(result: ProccessResult): Boolean
}

/**
 * Matches the result with the expected int
 */
@Serializable
@SerialName("MatchIntRule")
data class MatchIntRule(
    val expected: Int,
) : Rule {
    override fun isValid(result: ProccessResult): Boolean {
        return when (result) {
            is HttpResult -> result.code == expected
            is CommandResult -> result.code == expected
        }
    }
}


// Implement different cron processes with specific configurations
@Serializable
@SerialName("HttpCronProcess")
data class HttpTask(
    override val common: TaskCommmon,
    override val schedule: TaskSchedule,
    override val setup: HttpSetupConfig,
    override val rules: List<Rule>
) : Task {

    private var result: ProccessResult? = null
    private var _lastUpdate: Instant? = null
    override val lastUpdate: String
        get() = _lastUpdate?.toLocalDateTime(TimeZone.currentSystemDefault())?.time.toString().take(5)

    
    override suspend fun execute(result: (ProccessResult) -> Unit) {
        Napier.d { "Launching proccess ${common.uuid}" }
        Napier.d { "Executing HTTP request to ${setup.url}" }
        // Implement the HTTP logic here (e.g., using Ktor or OkHttp)
        val client = HttpClient() {
            install(HttpTimeout) {
                requestTimeoutMillis = 24 * 60 * 60 * 1000
                connectTimeoutMillis = 24 * 60 * 60 * 1000
                socketTimeoutMillis = 24 * 60 * 60 * 1000
            }
            engine {
            }
        }
        try {
            _lastUpdate = Clock.System.now()
            val response = client.get(setup.url)
            val content = response.bodyAsText()

            HttpResult(
                proccess = this,
                code = response.status.value,
                message = content
            ).also {
                this.result = it
                result(it)
            }
        } catch (e: HttpRequestTimeoutException) {
            Napier.w { "Request timed out!" }
            Napier.e(e.message ?: "exception on request", e.cause)
        } finally {
            client.close()
        }
    }

    override fun validate(): Boolean {
        Napier.d { "in validate()" }
        val proccessResult = result ?: run {
            Napier.w { "Result is null" }
            return false
        }

        rules.forEach { rule ->
            Napier.d { "validating: $rule with: ${rule.isValid(proccessResult)}" }
            if (!rule.isValid(proccessResult)) return false
        }

        return true
    }
}

@Serializable
@SerialName("WebSocketCronProcess")
data class WebSocketTask(
    override val common: TaskCommmon,
    override val schedule: TaskSchedule,
    override val setup: WebSocketSetupConfig,
    override val rules: List<Rule>,
) : Task {
    private var _lastUpdate: Instant? = null
    override val lastUpdate: String
        get() = _lastUpdate?.toLocalDateTime(TimeZone.currentSystemDefault())?.time.toString().take(5)


    override suspend fun execute(result: (ProccessResult) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun validate(): Boolean {
        TODO("Not yet implemented")
    }
}

@Serializable
@SerialName("CommandCronProcess")
data class CommandTask(
    override val common: TaskCommmon,
    override val schedule: TaskSchedule,
    override val setup: CommandSetupConfig,
    override val rules: List<Rule>,
) : Task {

    private var result: ProccessResult? = null

    private var _lastUpdate: Instant? = null
    override val lastUpdate: String
        get() = _lastUpdate?.toLocalDateTime(TimeZone.currentSystemDefault())?.time.toString().take(5)

    override suspend fun execute(result: (ProccessResult) -> Unit) {
        //kotlinc Main.kt -include-runtime -d Main.jar
        val commandString = setup.command

        val command = Command()
        _lastUpdate = Clock.System.now()
        command.execute(commandString) { (code, message) ->
            Napier.d { "Command result with code: $code, message: $message" }
            result(
                CommandResult(
                    proccess = this,
                    code = code,
                    message = message
                ).also {
                    this.result = it
                }
            )
        }
    }

    override fun validate(): Boolean {
        Napier.d { "in command validate()" }
        val proccessResult = result ?: run {
            Napier.w { "Result is null" }
            return false
        }

        rules.forEach { rule ->
            Napier.d { "validating: $rule with: ${rule.isValid(proccessResult)}" }
            if (!rule.isValid(proccessResult)) return false
        }

        return true
    }
}

@OptIn(InternalSerializationApi::class)
fun Task.toJson(): String {
    val module = SerializersModule {
        polymorphic(Task::class) {
            subclass(HttpTask::class)
            subclass(WebSocketTask::class)
        }
    }

    val format = Json { serializersModule = module }
    return format.encodeToString(Task::class.serializer(), this)
}

fun String.toCronProcess(): Task {
    val module = SerializersModule {
        polymorphic(Task::class) {
            subclass(HttpTask::class)
            subclass(WebSocketTask::class)
        }
    }

    val format = Json { serializersModule = module }


    // Deserialize the object
    val deserialized: Task = format.decodeFromString(Task.serializer(), this)
    println("Deserialized Object: $deserialized")
    return deserialized
}