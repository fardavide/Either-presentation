package studio.forface.either.data.mapper

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.left
import arrow.core.right
import studio.forface.either.data.model.ContactDataModel
import studio.forface.either.domain.ValidationError
import studio.forface.either.domain.model.EncryptedContact

class ContactMapper {

    suspend fun toDomainModel(dataModel: ContactDataModel): Either<ValidationError, EncryptedContact> =
        either {
            val validatedEmail = validateEmail(dataModel.email).bind()
            EncryptedContact(name = dataModel.name, email = validatedEmail)
        }

    suspend fun toDomainModels(
        dataModels: Collection<ContactDataModel>
    ): Either<ValidationError, List<EncryptedContact>> =
        either {
            dataModels.map { model ->
                toDomainModel(model).bind()
            }
        }

    private fun validateEmail(input: String): Either<ValidationError, String> =
        if ("@" in input && "." in input) input.right()
        else ValidationError(INVALID_EMAIL_FORMAT_ERROR_MESSAGE).left()

    companion object {
        const val INVALID_EMAIL_FORMAT_ERROR_MESSAGE = "Invalid email format"
    }
}
