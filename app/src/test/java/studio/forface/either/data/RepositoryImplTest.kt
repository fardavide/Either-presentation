package studio.forface.either.data

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test
import studio.forface.either.data.local.ContactDao
import studio.forface.either.data.model.ContactDataModel
import studio.forface.either.data.remote.ContactApi
import studio.forface.either.domain.Error
import studio.forface.either.domain.model.ClearContact
import studio.forface.either.domain.model.EncryptedContact
import studio.forface.either.domain.usecase.DecryptContacts

@ExperimentalCoroutinesApi
class RepositoryImplTest {

    private val contactApi: ContactApi = mockk {
        coEvery { getAllContacts() } returns emptyList()
    }
    private val contactDao: ContactDao = mockk(relaxUnitFun = true) {
        every { findAllContacts() } returns flowOf(emptyList())
    }
    private val decryptContacts: DecryptContacts = mockk {
        coEvery { this@mockk.invoke(any()) } answers {
            firstArg<Either<Error, Collection<EncryptedContact>>>().map { collection ->
                collection.map { contact ->
                    ClearContact(name = contact.name, email = contact.email)
                }
            }
        }
    }
    private val repository = RepositoryImpl(
        contactApi = contactApi,
        contactDao = contactDao,
        decryptContacts = decryptContacts

    )

    @Test
    fun `getContacts correctly`() = runBlockingTest {
        // given
        every { contactDao.findAllContacts() } returns flowOf(
            listOf(
                ContactDataModel(id = 0, name = DavideName, email = DavideEmail),
                ContactDataModel(id = 1, name = AnotherName, email = AnotherEmail)
            )
        )
        val expected = listOf(
            ClearContact(name = DavideName, email = DavideEmail),
            ClearContact(name = AnotherName, email = AnotherEmail)
        ).right()

        // when
        val result = repository.getContacts().first()

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `getContacts emits Left if Api throw an exception`() = runBlockingTest {
        // given
        val errorMessage = "Ouch!"
        coEvery { contactApi.getAllContacts() } answers {
            throw IllegalStateException(errorMessage)
        }
        val expected = Error("${RepositoryImpl.FETCH_CONTACTS_ERROR_MESSAGE}: $errorMessage").left()

        // when
        val result = repository.getContacts().first()

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `getContacts emits Left from dependency`() = runBlockingTest {
        // given
        val errorMessage = "Ouch!"
        val error = Error(errorMessage).left()
        coEvery { decryptContacts(any()) } returns error

        // when
        val result = repository.getContacts().first()

        // then
        assertEquals(error, result)
    }

    private companion object TestData {
        const val DavideName = "Davide"
        const val DavideEmail = "davide@email.com"
        const val AnotherName = "Another"
        const val AnotherEmail = "another@email.com"
    }
}
