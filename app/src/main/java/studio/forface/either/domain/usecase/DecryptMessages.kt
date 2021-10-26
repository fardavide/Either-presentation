package studio.forface.either.domain.usecase

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.right
import studio.forface.either.domain.Error
import studio.forface.either.domain.model.ClearMessage
import studio.forface.either.domain.model.EncryptedMessage

class DecryptMessages(
    private val decryptMessage: DecryptMessage = DecryptMessage()
) {

    suspend operator fun invoke(
        encrypted: Either<Error, Collection<EncryptedMessage>>
    ): Either<Error, List<ClearMessage>> =
        either {
            val encryptedMessages = encrypted.bind()
            encryptedMessages.map { message ->
                decryptMessage(message.right()).bind()
            }
        }
}
