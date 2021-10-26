package studio.forface.either.data.mapper

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.rightIfNotNull
import studio.forface.either.data.model.ContactDataModel
import studio.forface.either.data.model.MessageDataModel
import studio.forface.either.domain.Error
import studio.forface.either.domain.model.EncryptedContact
import studio.forface.either.domain.model.EncryptedMessage

class MessageMapper(
    private val contactMapper: ContactMapper = ContactMapper()
) {

    suspend fun toDomainModel(
        messageDataModel: Either<Error, MessageDataModel>,
        contactDataModel: Either<Error, ContactDataModel>
    ): Either<Error, EncryptedMessage> =
        either {
            val messageModel = messageDataModel.bind()
            val encryptedContact = contactMapper.toDomainModel(contactDataModel).bind()
            EncryptedMessage(subject = messageModel.subject, from = encryptedContact)
        }

    suspend fun toDomainModels(
        messageDataModels: Either<Error, Collection<MessageDataModel>>,
        contactDataModels: Either<Error, Collection<ContactDataModel>>
    ): Either<Error, List<EncryptedMessage>> =
        either {
            val messageModels = messageDataModels.bind()
            val contactModels = contactDataModels.bind()

            val cachedContacts = mutableMapOf<Int, EncryptedContact>()
            messageModels.map { messageDataModel ->
                val contactId = messageDataModel.fromId
                val encryptedContact = cachedContacts.getOrPut(contactId) {
                    val contactModel = contactModels.find { it.id == contactId }
                        .rightIfNotNull { Error("$CONTACT_NOT_FOUND_ERROR_MESSAGE: $contactId") }
                    contactMapper.toDomainModel(contactModel).bind()
                }

                EncryptedMessage(messageDataModel.subject, encryptedContact)
            }
        }

    companion object {
        const val CONTACT_NOT_FOUND_ERROR_MESSAGE = "Cannot find contact with id"
    }
}
