package studio.forface.either.domain.usecase

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.right
import studio.forface.either.domain.Error
import studio.forface.either.domain.model.ClearMessage
import studio.forface.either.domain.model.EncryptedMessage

class DecryptMessage(
    private val decryptString: DecryptString = DecryptString(),
    private val decryptContact: DecryptContact = DecryptContact(decryptString)
) {

    suspend operator fun invoke(encrypted: Either<Error, EncryptedMessage>): Either<Error, ClearMessage> =
        either {

            val encryptedMessage = encrypted.bind()

            val subject = decryptString(encryptedMessage.subject)
                .mapLeft { error -> Error("Subject: ${error.message}") }
                .bind()

            val contact = decryptContact(encryptedMessage.from.right())
                .mapLeft { error -> Error("Contact: ${error.message}") }
                .bind()

            ClearMessage(subject = subject, from = contact)
        }
}
