package com.minidashboard.app.data.models

import com.minidashboard.app.domain.command.Command
import io.github.aakira.napier.Napier
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kotlin.random.Random

// Define a sealed interface for cron processes
@Serializable
sealed interface CronProcess {
    val common: CronCommmon
    val schedule: CronSchedule
    val setup: SetupConfig
    val rules: List<Rule>

    val uuid: String
        get() = common.uuid

    suspend fun execute(result: (ProccessResult) -> Unit)
    fun validate(): Boolean
}

@Serializable
data class CronSchedule(
    val time: Int,
    val interval: String
){

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

        return when(interval){
            MINUTES -> minutesToMillis(time.toLong())
            HOURS -> hoursToMillis(time.toLong())
            DAYS -> daysToMillis(time.toLong())
            else -> minutesToMillis(time.toLong())
        }
    }

}

@Serializable
data class CronCommmon(
    val uuid: String = Random.nextInt(1, 99999).toString(),
    val title: String,
    val description: String,
    val active: Boolean,
)

// Represent different configurations for each process type
@Serializable
sealed interface SetupConfig

@Serializable
@SerialName("HttpSetup")
data class HttpSetupConfig(val url: String, val parameters: Map<String, String> = emptyMap()) : SetupConfig

@Serializable
@SerialName("WebSocketSetup")
data class WebSocketSetupConfig(val endpoint: String, val protocols: List<String> = emptyList()) : SetupConfig

@Serializable
@SerialName("PythonSetup")
data class PythonSetupConfig(val scriptPath: String, val args: List<String> = emptyList()) : SetupConfig

@Serializable
@SerialName("CommandSetup")
data class CommandSetupConfig(val command: String) : SetupConfig

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
): Rule {
    override fun isValid(result: ProccessResult): Boolean {
        return when(result){
            is HttpResult -> result.code == expected
            is CommandResult -> result.code == expected
        }
    }
}


// Implement different cron processes with specific configurations
@Serializable
@SerialName("HttpCronProcess")
data class HttpCronProcess(
    override val common: CronCommmon,
    override val schedule: CronSchedule,
    override val setup: HttpSetupConfig,
    override val rules: List<Rule>
) : CronProcess {

    private var result: ProccessResult ? = null

    override suspend fun execute(result: (ProccessResult) -> Unit) {
        Napier.d { "Launching proccess ${common.uuid}" }
        Napier.d { "Executing HTTP request to ${setup.url}" }
        // Implement the HTTP logic here (e.g., using Ktor or OkHttp)
        val client = HttpClient(){
            engine {
            }
        }
        val response = client.get(setup.url)
        val content = response.bodyAsText()

        HttpResult(
            proccess = this,
            code = response.status.value,
            message = content
        ).also{
            this.result = it
            result(it)
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
            if(!rule.isValid(proccessResult)) return false
        }

        return true
    }
}

@Serializable
@SerialName("WebSocketCronProcess")
data class WebSocketCronProcess(
    override val common: CronCommmon,
    override val schedule: CronSchedule,
    override val setup: WebSocketSetupConfig,
    override val rules: List<Rule>,
) : CronProcess {
    override suspend fun execute(result: (ProccessResult) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun validate(): Boolean {
        TODO("Not yet implemented")
    }
}

@Serializable
@SerialName("PythonCronProcess")
data class PythonCronProcess(
    override val common: CronCommmon,
    override val schedule: CronSchedule,
    override val setup: PythonSetupConfig,
    override val rules: List<Rule>,
) : CronProcess {
    override suspend fun execute(result: (ProccessResult) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun validate(): Boolean {
        TODO("Not yet implemented")
    }
}

@Serializable
@SerialName("CommandCronProcess")
data class CommandCronProcess(
    override val common: CronCommmon,
    override val schedule: CronSchedule,
    override val setup: CommandSetupConfig,
    override val rules: List<Rule>,
) : CronProcess {

    private var result: ProccessResult ? = null

    override suspend fun execute(result: (ProccessResult) -> Unit) {
        //kotlinc Main.kt -include-runtime -d Main.jar
        val commandString = setup.command

        val command = Command()

        command.execute(
            command = commandString,
            onResult = {
                val (code, message) = it
            }
        )

        command.execute(commandString){ (code, message) ->
            Napier.d {"Command result with code: $code, message: $message"}
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
            if(!rule.isValid(proccessResult)) return false
        }

        return true
    }
}

@OptIn(InternalSerializationApi::class)
fun CronProcess.toJson(): String {
    val module = SerializersModule {
        polymorphic(CronProcess::class){
            subclass(HttpCronProcess::class)
            subclass(WebSocketCronProcess::class)
        }
    }

    val format = Json { serializersModule = module }
    return format.encodeToString(CronProcess::class.serializer(), this)
}

fun String.toCronProcess(): CronProcess {
    val module = SerializersModule {
        polymorphic(CronProcess::class){
            subclass(HttpCronProcess::class)
            subclass(WebSocketCronProcess::class)
        }
    }

    val format = Json { serializersModule = module }


    // Deserialize the object
    val deserialized: CronProcess = format.decodeFromString(CronProcess.serializer(), this)
    println("Deserialized Object: $deserialized")
    return deserialized
}