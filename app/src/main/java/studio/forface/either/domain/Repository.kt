package studio.forface.either.domain

import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import studio.forface.either.data.RepositoryImpl
import studio.forface.either.domain.model.ClearContact
import studio.forface.either.domain.model.ClearMessage
import studio.forface.either.domain.model.EncryptedMessage

interface Repository {

    fun getContacts(): Flow<Either<Error, List<ClearContact>>>

    fun getMessages(): Flow<Either<Error, List<ClearMessage>>>
}

fun Repository() = RepositoryImpl()
