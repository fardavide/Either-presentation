package studio.forface.either.domain.usecase

import arrow.core.Either
import arrow.core.computations.either
import studio.forface.either.domain.Error
import studio.forface.either.domain.model.ClearContact
import studio.forface.either.domain.model.EncryptedContact

class DecryptContact(
    private val decryptString: DecryptString = DecryptString()
) {

    suspend operator fun invoke(encrypted: Either<Error, EncryptedContact>): Either<Error, ClearContact> =
        either {

            val encryptedContact = encrypted.bind()

            val name = decryptString(encryptedContact.name)
                .mapLeft { error -> Error("Name: ${error.message}") }
                .bind()

            val email = decryptString(encryptedContact.email)
                .mapLeft { error -> Error("Email: ${error.message}") }
                .bind()

            ClearContact(name = name, email = email)
        }
}
