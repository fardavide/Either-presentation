package studio.forface.either.domain.usecase

import arrow.core.Either
import arrow.core.computations.either
import studio.forface.either.domain.DecryptionError
import studio.forface.either.domain.model.ClearContact
import studio.forface.either.domain.model.EncryptedContact

class DecryptContacts(
    private val decryptContact: DecryptContact = DecryptContact()
) {

    suspend operator fun invoke(encrypted: Collection<EncryptedContact>): Either<DecryptionError, List<ClearContact>> =
        either {
            encrypted.map { contact ->
                decryptContact(contact).bind()
            }
        }
}
