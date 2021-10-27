package studio.forface.either.domain.usecase

import arrow.core.Either
import arrow.core.computations.either
import studio.forface.either.domain.DecryptionError
import studio.forface.either.domain.model.ClearMessage
import studio.forface.either.domain.model.EncryptedMessage

class DecryptMessages(
    private val decryptMessage: DecryptMessage = DecryptMessage()
) {

    suspend operator fun invoke(encrypted: Collection<EncryptedMessage>): Either<DecryptionError, List<ClearMessage>> =
        either {
            encrypted.map { message ->
                decryptMessage(message).bind()
            }
        }
}
