package com.github.faah

enum class ErrorType(val displayName: String, val pattern: String) {
    EXCEPTION("Exception", """Exception"""),
    ERROR("Error", """\bError\b"""),
    FATAL("Fatal / FATAL", """FATAL"""),
    CAUSED_BY("Caused by", """Caused by:"""),
    STACK_FRAME("Stack frames (at …)", """^\s+at\s+[\w.${'$'}]+\("""),
}
