package studio.forface.either.data.mapper

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import studio.forface.either.data.model.ContactDataModel
import studio.forface.either.domain.Error
import studio.forface.either.domain.model.EncryptedContact

class ContactMapper {

    suspend fun toDomainModel(
        dataModel: Either<Error, ContactDataModel>
    ): Either<Error, EncryptedContact> =
        either {
            val model = dataModel.bind()
            val validatedEmail = validateEmail(model.email).bind()
            EncryptedContact(name = model.name, email = validatedEmail)
        }

    suspend fun toDomainModels(
        dataModels: Either<Error, Collection<ContactDataModel>>
    ): Either<Error, List<EncryptedContact>> =
        either {
            val collection = dataModels.bind()
            collection.map { model ->
                toDomainModel(model.right()).bind()
            }
        }

    private fun validateEmail(input: String): Either<Error, String> =
        if ("@" in input && "." in input) input.right()
        else Error(INVALID_EMAIL_FORMAT_ERROR_MESSAGE).left()

    companion object {
        const val INVALID_EMAIL_FORMAT_ERROR_MESSAGE = "Invalid email format"
    }
}
