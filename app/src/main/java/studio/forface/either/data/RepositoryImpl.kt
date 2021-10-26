package studio.forface.either.data

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.left
import arrow.core.right
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import studio.forface.either.data.local.ContactDao
import studio.forface.either.data.local.MessageDao
import studio.forface.either.data.mapper.ContactMapper
import studio.forface.either.data.mapper.MessageMapper
import studio.forface.either.data.model.ContactDataModel
import studio.forface.either.data.model.MessageDataModel
import studio.forface.either.data.remote.ContactApi
import studio.forface.either.data.remote.MessageApi
import studio.forface.either.domain.Error
import studio.forface.either.domain.Repository
import studio.forface.either.domain.model.ClearContact
import studio.forface.either.domain.model.ClearMessage
import studio.forface.either.domain.usecase.DecryptContacts
import studio.forface.either.domain.usecase.DecryptMessages

class RepositoryImpl(
    private val contactDao: ContactDao = ContactDao(),
    private val messageDao: MessageDao = MessageDao(),
    private val contactApi: ContactApi = ContactApi(),
    private val messageApi: MessageApi = MessageApi(),
    private val contactMapper: ContactMapper = ContactMapper(),
    private val messageMapper: MessageMapper = MessageMapper(),
    private val decryptContacts: DecryptContacts = DecryptContacts(),
    private val decryptMessages: DecryptMessages = DecryptMessages()
) : Repository {

    override fun getContacts(): Flow<Either<Error, List<ClearContact>>> =
        contactDao.findAllContacts()
            .map<List<ContactDataModel>, Either<Error, List<ClearContact>>> { dataModels ->
                either {
                    val encryptedContacts = contactMapper.toDomainModels(dataModels.right())
                    decryptContacts(encryptedContacts).bind()
                }
            }
            .onStart {
                fetchAndSaveContacts()
            }

    override fun getMessages(): Flow<Either<Error, List<ClearMessage>>> =
        combine<List<MessageDataModel>, List<ContactDataModel>, Either<Error, List<ClearMessage>>>(
            messageDao.findAllMessages(),
            contactDao.findAllContacts()
        ) { messagesDataModels, contactsDataModels ->
            either {
                val encryptedMessages = messageMapper.toDomainModels(
                    messagesDataModels.right(),
                    contactsDataModels.right()
                )
                decryptMessages(encryptedMessages).bind()
            }
        }
            .onStart {
                fetchAndSaveMessages()
                fetchAndSaveContacts()
            }

    private suspend fun <T> FlowCollector<Either<Error, List<T>>>.fetchAndSaveContacts() {
        val fromApi = Either.catch { contactApi.getAllContacts() }
            .mapLeft { Error("$FETCH_CONTACTS_ERROR_MESSAGE: ${it.message}") }

        fromApi.fold(
            ifLeft = { emit(it.left()) },
            ifRight = { contactDao.saveContacts(it) }
        )
    }

    private suspend fun <T> FlowCollector<Either<Error, List<T>>>.fetchAndSaveMessages() {
        val messagesFromApi = Either.catch { messageApi.getAllMessages() }
            .mapLeft { Error("$FETCH_MESSAGES_ERROR_MESSAGE: ${it.message}") }

        messagesFromApi.fold(
            ifLeft = { emit(it.left()) },
            ifRight = { messageDao.saveMessages(it) }
        )
    }

    companion object {
        const val FETCH_CONTACTS_ERROR_MESSAGE = "Cannot fetch contacts"
        const val FETCH_MESSAGES_ERROR_MESSAGE = "Cannot fetch messages"
    }
}
