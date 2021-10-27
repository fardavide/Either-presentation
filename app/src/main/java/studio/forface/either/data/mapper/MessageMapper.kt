package studio.forface.either.data.mapper

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.rightIfNotNull
import studio.forface.either.data.model.ContactDataModel
import studio.forface.either.data.model.MessageDataModel
import studio.forface.either.domain.ValidationError
import studio.forface.either.domain.model.EncryptedContact
import studio.forface.either.domain.model.EncryptedMessage

class MessageMapper(
    private val contactMapper: ContactMapper = ContactMapper()
) {

    suspend fun toDomainModel(
        messageDataModel: MessageDataModel,
        contactDataModel: ContactDataModel
    ): Either<ValidationError, EncryptedMessage> =
        either {
            val encryptedContact = contactMapper.toDomainModel(contactDataModel).bind()
            EncryptedMessage(subject = messageDataModel.subject, from = encryptedContact)
        }

    suspend fun toDomainModels(
        messageDataModels: Collection<MessageDataModel>,
        contactDataModels: Collection<ContactDataModel>
    ): Either<ValidationError, List<EncryptedMessage>> =
        either {
            val cachedContacts = mutableMapOf<Int, EncryptedContact>()
            messageDataModels.map { messageDataModel ->
                val contactId = messageDataModel.fromId
                val encryptedContact = cachedContacts.getOrPut(contactId) {
                    val contactModel = contactDataModels.find { it.id == contactId }
                        .rightIfNotNull { ValidationError("$CONTACT_NOT_FOUND_ERROR_MESSAGE: $contactId") }
                        .bind()
                    contactMapper.toDomainModel(contactModel).bind()
                }

                EncryptedMessage(messageDataModel.subject, encryptedContact)
            }
        }

    companion object {
        const val CONTACT_NOT_FOUND_ERROR_MESSAGE = "Cannot find contact with id"
    }
}
