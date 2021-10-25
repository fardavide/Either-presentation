package studio.forface.either.domain.model

data class ClearMessage(
    val subject: String,
    val from: ClearContact
)
