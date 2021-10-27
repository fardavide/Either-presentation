package studio.forface.either.domain.usecase

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import studio.forface.either.domain.ARMOR
import studio.forface.either.domain.DecryptionError

class DecryptString(
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    suspend operator fun invoke(string: String): Either<DecryptionError, String> =
        withContext(dispatcher) {
            when {
                string.startsWith(ARMOR) && string.endsWith(ARMOR) -> string.removeSurrounding(ARMOR).right()
                string.isBlank() -> DecryptionError(BLANK).left()
                else -> DecryptionError(GENERIC).left()
            }
        }

    companion object ErrorMessages {

        const val BLANK = "String is blank"
        const val GENERIC = "Cannot decrypt"
    }
}
