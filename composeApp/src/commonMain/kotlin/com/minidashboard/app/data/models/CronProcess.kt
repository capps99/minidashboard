package com.minidashboard.app.data.models

// Define a sealed interface for cron processes
sealed interface CronProcess {
    val cronCommmon: CronCommmon
    val setup: SetupConfig
    fun execute()
}

data class CronCommmon(
    val title: String,
    val description: String,
    val active: Boolean
)

// Represent different configurations for each process type
sealed interface SetupConfig

data class HttpSetupConfig(val url: String, val parameters: Map<String, String> = emptyMap()) : SetupConfig
data class WebSocketSetupConfig(val endpoint: String, val protocols: List<String> = emptyList()) : SetupConfig
data class PythonSetupConfig(val scriptPath: String, val args: List<String> = emptyList()) : SetupConfig
data class JVMSetupConfig(val scriptPath: String, val args: List<String> = emptyList()) : SetupConfig


// Implement different cron processes with specific configurations
data class HttpCronProcess(
    override val cronCommmon: CronCommmon,
    override val setup: HttpSetupConfig,
) : CronProcess {
    override fun execute() {
        println("Executing HTTP request to ${setup.url}")
        // Implement the HTTP logic here (e.g., using Ktor or OkHttp)
    }
}

data class WebSocketCronProcess(
    override val cronCommmon: CronCommmon,
    override val setup: WebSocketSetupConfig
) : CronProcess {
    override fun execute() {
        println("Connecting to WebSocket ${setup.endpoint}")
        // Implement WebSocket connection logic here (e.g., using a WebSocket library)
    }
}

data class PythonCronProcess(
    override val cronCommmon: CronCommmon,
    override val setup: PythonSetupConfig
) : CronProcess {
    override fun execute() {
        println("Executing Python script at ${setup.scriptPath} with args ${setup.args}")
    // Execute the Python process (e.g., using ProcessBuilder)
    }
}

data class JVMronProcess(
    override val cronCommmon: CronCommmon,
    override val setup: PythonSetupConfig
) : CronProcess {
    override fun execute() {
        println("Executing JVM script at ${setup.scriptPath} with args ${setup.args}")
    // Execute the Python process (e.g., using ProcessBuilder)
    }
}

// Function to run all cron processes
fun runCronProcesses(cronProcesses: List<CronProcess>) {
    cronProcesses.forEach { process ->
        process.execute()  // Each process will execute its specific logic
    }
}