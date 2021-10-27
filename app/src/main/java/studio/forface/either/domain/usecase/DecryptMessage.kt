package studio.forface.either.domain.usecase

import arrow.core.Either
import arrow.core.computations.either
import studio.forface.either.domain.DecryptionError
import studio.forface.either.domain.model.ClearMessage
import studio.forface.either.domain.model.EncryptedMessage

class DecryptMessage(
    private val decryptString: DecryptString = DecryptString(),
    private val decryptContact: DecryptContact = DecryptContact(decryptString)
) {

    suspend operator fun invoke(encrypted: EncryptedMessage): Either<DecryptionError, ClearMessage> =
        either {

            val subject = decryptString(encrypted.subject)
                .mapLeft { error -> DecryptionError("Subject: ${error.message}") }
                .bind()

            val contact = decryptContact(encrypted.from)
                .mapLeft { error -> DecryptionError("Contact: ${error.message}") }
                .bind()

            ClearMessage(subject = subject, from = contact)
        }
}
