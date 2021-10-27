package studio.forface.either.domain.usecase

import arrow.core.Either
import arrow.core.computations.either
import studio.forface.either.domain.DecryptionError
import studio.forface.either.domain.model.ClearContact
import studio.forface.either.domain.model.EncryptedContact

class DecryptContact(
    private val decryptString: DecryptString = DecryptString()
) {

    suspend operator fun invoke(encrypted: EncryptedContact): Either<DecryptionError, ClearContact> =
        either {

            val name = decryptString(encrypted.name)
                .mapLeft { error -> DecryptionError("Name: ${error.message}") }
                .bind()

            val email = decryptString(encrypted.email)
                .mapLeft { error -> DecryptionError("Email: ${error.message}") }
                .bind()

            ClearContact(name = name, email = email)
        }
}
