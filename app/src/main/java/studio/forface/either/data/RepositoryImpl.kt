package studio.forface.either.data

import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import studio.forface.either.data.local.ContactDao
import studio.forface.either.data.local.MessageDao
import studio.forface.either.data.remote.ContactApi
import studio.forface.either.data.remote.MessageApi
import studio.forface.either.domain.Error
import studio.forface.either.domain.Repository
import studio.forface.either.domain.model.ClearContact
import studio.forface.either.domain.model.ClearMessage

class RepositoryImpl(
    private val contactDao: ContactDao = ContactDao(),
    private val messageDao: MessageDao = MessageDao(),
    private val contactApi: ContactApi = ContactApi(),
    private val messageApi: MessageApi = MessageApi()
) : Repository {

    override fun getContacts(): Flow<Either<Error, List<ClearContact>>> {
        TODO("Not yet implemented")
    }

    override fun getMessages(): Flow<Either<Error, List<ClearMessage>>> {
        TODO("Not yet implemented")
    }
}
