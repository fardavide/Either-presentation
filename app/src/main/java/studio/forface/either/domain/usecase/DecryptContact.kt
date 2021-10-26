package studio.forface.either.domain.usecase

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.left
import arrow.core.right
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import studio.forface.either.domain.ARMOR
import studio.forface.either.domain.Error
import studio.forface.either.domain.model.ClearContact
import studio.forface.either.domain.model.EncryptedContact

class DecryptContact(
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    suspend operator fun invoke(encrypted: Either<Error, EncryptedContact>): Either<Error, ClearContact> =
        either {

            val encryptedContact = encrypted.bind()

            val name = decrypt(encryptedContact.name)
                .mapLeft { error -> Error("Name: ${error.message}") }
                .bind()

            val email = decrypt(encryptedContact.email)
                .mapLeft { error -> Error("Email: ${error.message}") }
                .bind()

            ClearContact(name = name, email = email)
        }

    private suspend fun decrypt(string: String): Either<Error, String> {
        return withContext(dispatcher) {
            when {
                string.startsWith(ARMOR) && string.endsWith(ARMOR) -> string.removeSurrounding(ARMOR).right()
                string.isBlank() -> Error(BLANK).left()
                else -> Error(GENERIC).left()
            }
        }
    }

    companion object ErrorMessages {

        const val BLANK = "String is blank"
        const val GENERIC = "Cannot decrypt"
    }
}
