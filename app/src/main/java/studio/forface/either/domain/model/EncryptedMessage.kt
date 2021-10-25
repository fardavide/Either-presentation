package studio.forface.either.domain.model

data class EncryptedMessage(
    val subject: String,
    val from: EncryptedContact
)
