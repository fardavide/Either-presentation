package studio.forface.either.data.mapper

import arrow.core.left
import arrow.core.right
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test
import studio.forface.either.data.model.ContactDataModel
import studio.forface.either.data.model.MessageDataModel
import studio.forface.either.domain.ValidationError
import studio.forface.either.domain.model.EncryptedContact
import studio.forface.either.domain.model.EncryptedMessage

@ExperimentalCoroutinesApi
class MessageMapperTest {

    private val contactMapper: ContactMapper = mockk {
        coEvery { toDomainModels(any()) } returns listOf(DavideEncryptedContact, AnotherEncryptedContact).right()
    }
    private val mapper = MessageMapper(contactMapper = contactMapper)

    @Test
    fun `maps correctly single message`() = runBlockingTest {
        // given
        val input = MessageDataModel(subject = HelloSubject, id = 0, fromId = 0)
        val expected = EncryptedMessage(subject = HelloSubject, from = DavideEncryptedContact).right()

        coEvery { contactMapper.toDomainModel(any()) } returns DavideEncryptedContact.right()

        // when
        val result = mapper.toDomainModel(input, buildRandomContactDataModel())

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `maps correctly a collection of messages`() = runBlockingTest {
        // given
        val contactDataModel = buildRandomContactDataModel()
        val input = listOf(
            MessageDataModel(subject = HelloSubject, id = 0, fromId = contactDataModel.id),
            MessageDataModel(subject = GoodMorningSubject, id = 1, fromId = contactDataModel.id)
        )
        val expected = listOf(
            EncryptedMessage(subject = HelloSubject, from = DavideEncryptedContact),
            EncryptedMessage(subject = GoodMorningSubject, from = DavideEncryptedContact)
        ).right()

        coEvery { contactMapper.toDomainModel(any()) } returns DavideEncryptedContact.right()

        // when
        val result = mapper.toDomainModels(input, listOf(contactDataModel))

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `maps message without a relative contact`() = runBlockingTest {
        // given
        val contactId = 15
        val input = listOf(MessageDataModel(subject = HelloSubject, id = 0, fromId = contactId))
        val error = ValidationError("${MessageMapper.CONTACT_NOT_FOUND_ERROR_MESSAGE}: $contactId").left()

        // when
        val result = mapper.toDomainModels(input, emptyList())

        // then
        assertEquals(error, result)
        coVerify(exactly = 0) { contactMapper.toDomainModel(any()) }
    }

    private companion object TestData {
        const val HelloSubject = "Hello"
        const val GoodMorningSubject = "Good morning"

        const val DavideName = "Davide"
        const val DavideEmail = "davide@email.com"
        const val AnotherName = "Another"
        const val AnotherEmail = "another@email.com"

        val DavideEncryptedContact = EncryptedContact(DavideName, DavideEmail)
        val AnotherEncryptedContact = EncryptedContact(AnotherName, AnotherEmail)

        fun buildRandomContactDataModel() = ContactDataModel(
            id = 987,
            name = "Random",
            email = "random@email.com"
        )
        fun buildAnotherContactDataModel() = ContactDataModel(
            id = 975,
            name = "Another",
            email = "another@email.com"
        )
    }
}
