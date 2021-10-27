package studio.forface.either.domain

sealed class Error {

    abstract val message: String
}

data class ApiError(override val message: String) : Error()

data class DecryptionError(override val message: String) : Error()

data class ValidationError(override val message: String) : Error()
