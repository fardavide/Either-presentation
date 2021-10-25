package studio.forface.either.domain

import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import studio.forface.either.domain.model.EncryptedMessage

interface Repository {

    fun getMessages(): Flow<Either<String, List<EncryptedMessage>>>
}
