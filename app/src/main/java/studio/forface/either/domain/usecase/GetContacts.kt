package studio.forface.either.domain.usecase

import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import studio.forface.either.data.RepositoryImpl
import studio.forface.either.domain.Error
import studio.forface.either.domain.Repository
import studio.forface.either.domain.model.ClearContact

@Suppress("unused")
class GetContacts(
    private val repository: Repository = RepositoryImpl()
) {

    operator fun invoke(): Flow<Either<Error, List<ClearContact>>> =
        repository.getContacts()
}
