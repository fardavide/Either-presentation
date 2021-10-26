package studio.forface.either.domain.usecase

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.right
import studio.forface.either.domain.Error
import studio.forface.either.domain.model.ClearContact
import studio.forface.either.domain.model.EncryptedContact

class DecryptContacts(
    private val decryptContact: DecryptContact = DecryptContact()
) {

    suspend operator fun invoke(
        encrypted: Either<Error, Collection<EncryptedContact>>
    ): Either<Error, List<ClearContact>> =
        either {
            val encryptedContacts = encrypted.bind()
            encryptedContacts.map { contact ->
                decryptContact(contact.right()).bind()
            }
        }
}
