package studio.forface.either.data

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
import studio.forface.either.data.local.MessageDao
import studio.forface.either.data.mapper.MessageMapper
import studio.forface.either.data.model.ContactDataModel
import studio.forface.either.data.model.MessageDataModel
import studio.forface.either.data.remote.ContactApi
import studio.forface.either.data.remote.MessageApi
import studio.forface.either.domain.ApiError
import studio.forface.either.domain.DecryptionError
import studio.forface.either.domain.ValidationError
import studio.forface.either.domain.model.ClearContact
import studio.forface.either.domain.model.ClearMessage
import studio.forface.either.domain.usecase.DecryptContact
import studio.forface.either.domain.usecase.DecryptContacts
import studio.forface.either.domain.usecase.DecryptMessage
import studio.forface.either.domain.usecase.DecryptMessages
import studio.forface.either.domain.usecase.DecryptString

@ExperimentalCoroutinesApi
class RepositoryImplTest {

    private val contactApi: ContactApi = mockk {
        coEvery { getAllContacts() } returns emptyList()
    }
    private val messageApi: MessageApi = mockk {
        coEvery { getAllMessages() } returns emptyList()
    }
    private val contactDao: ContactDao = mockk(relaxUnitFun = true) {
        every { findAllContacts() } returns flowOf(emptyList())
    }
    private val messageDao: MessageDao = mockk(relaxUnitFun = true) {
        every { findAllMessages() } returns flowOf(emptyList())
    }
    private val decryptString: DecryptString = mockk {
        coEvery { this@mockk.invoke(any()) } answers {
            firstArg<String>().right()
        }
    }
    private val decryptContact = DecryptContact(decryptString)
    private val repository = RepositoryImpl(
        contactApi = contactApi,
        messageApi = messageApi,
        contactDao = contactDao,
        messageDao = messageDao,
        decryptContacts = DecryptContacts(decryptContact),
        decryptMessages = DecryptMessages(DecryptMessage(decryptString, decryptContact))
    )

    @Test
    fun `getContacts correctly`() {
        runBlockingTest {
            // given
            every { contactDao.findAllContacts() } returns flowOf(
                listOf(DavideContactDataModel, AnotherContactDataModel)
            )
            val expected = listOf(DavideClearContact, AnotherClearContact).right()

            // when
            val result = repository.getContacts().first()

            // then
            assertEquals(expected, result)
        }
    }

    @Test
    fun `getContacts emits Left if Api throw an exception`() = runBlockingTest {
        // given
        val errorMessage = "Ouch!"
        coEvery { contactApi.getAllContacts() } answers {
            throw IllegalStateException(errorMessage)
        }
        val expected = ApiError("${RepositoryImpl.FETCH_CONTACTS_ERROR_MESSAGE}: $errorMessage").left()

        // when
        val result = repository.getContacts().first()

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `getContacts emits Left from dependency`() = runBlockingTest {
        // given
        val errorMessage = "Ouch!"
        val error = DecryptionError(errorMessage).left()
        coEvery { decryptString(any()) } returns error
        every { contactDao.findAllContacts() } returns flowOf(
            listOf(DavideContactDataModel, AnotherContactDataModel)
        )
        val expected = DecryptionError("Name: $errorMessage").left()

        // when
        val result = repository.getContacts().first()

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `getMessages correctly`() = runBlockingTest {
        // given
        every { messageDao.findAllMessages() } returns flowOf(
            listOf(HelloMessageDataModel, GoodMorningMessageDataModel)
        )
        every { contactDao.findAllContacts() } returns flowOf(
            listOf(DavideContactDataModel, AnotherContactDataModel)
        )
        val expected = listOf(
            ClearMessage(subject = HelloSubject, from = DavideClearContact),
            ClearMessage(subject = GoodMorningSubject, from = AnotherClearContact)
        ).right()

        // when
        val result = repository.getMessages().first()

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `getMessages emits Left if Api throw an exception`() = runBlockingTest {
        // given
        val errorMessage = "Ouch!"
        coEvery { messageApi.getAllMessages() } answers {
            throw IllegalStateException(errorMessage)
        }
        val expected = ApiError("${RepositoryImpl.FETCH_MESSAGES_ERROR_MESSAGE}: $errorMessage").left()

        // when
        val result = repository.getMessages().first()

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `getMessages emits Left from dependency`() = runBlockingTest {
        // given
        val errorMessage = "Ouch!"
        val error = DecryptionError(errorMessage).left()
        coEvery { decryptString(any()) } returns error
        every { messageDao.findAllMessages() } returns flowOf(
            listOf(HelloMessageDataModel, GoodMorningMessageDataModel)
        )
        every { contactDao.findAllContacts() } returns flowOf(
            listOf(DavideContactDataModel, AnotherContactDataModel)
        )
        val expected = DecryptionError("Subject: $errorMessage").left()

        // when
        val result = repository.getMessages().first()

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `getMessages emits Left if no matching contact`() = runBlockingTest {
        // given
        val contactId = 0
        every { messageDao.findAllMessages() } returns flowOf(
            listOf(HelloMessageDataModel.copy(fromId = contactId))
        )
        val expected = ValidationError("${MessageMapper.CONTACT_NOT_FOUND_ERROR_MESSAGE}: $contactId").left()

        // when
        val result = repository.getMessages().first()

        // then
        assertEquals(expected, result)
    }

    private companion object TestData {
        const val DavideName = "Davide"
        const val DavideEmail = "davide@email.com"
        const val AnotherName = "Another"
        const val AnotherEmail = "another@email.com"

        val DavideClearContact = ClearContact(name = DavideName, email = DavideEmail)
        val AnotherClearContact = ClearContact(name = AnotherName, email = AnotherEmail)

        val DavideContactDataModel = ContactDataModel(id = 0, name = DavideName, email = DavideEmail)
        val AnotherContactDataModel = ContactDataModel(id = 1, name = AnotherName, email = AnotherEmail)

        const val HelloSubject = "Hello"
        const val GoodMorningSubject = "Good morning!"

        val HelloMessageDataModel = MessageDataModel(id = 0, subject = HelloSubject, fromId = 0)
        val GoodMorningMessageDataModel = MessageDataModel(id = 1, subject = GoodMorningSubject, fromId = 1)
    }
}
