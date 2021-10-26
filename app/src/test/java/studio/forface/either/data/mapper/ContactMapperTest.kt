package studio.forface.either.data.mapper

import arrow.core.left
import arrow.core.right
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Test
import studio.forface.either.data.DataSet
import studio.forface.either.data.model.ContactDataModel
import studio.forface.either.domain.Error
import studio.forface.either.domain.model.EncryptedContact

@ExperimentalCoroutinesApi
class ContactMapperTest {

    private val mapper = ContactMapper()

    @Test
    fun `maps correctly single contact`() = runBlockingTest {
        // given
        val input = ContactDataModel(
            id = 0,
            name = DavideName,
            email = DavideEmail
        ).right()
        val expected = EncryptedContact(name = DavideName, email = DavideEmail).right()

        // when
        val result = mapper.toDomainModel(input)

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `maps correctly a collection of contacts`() = runBlockingTest {
        // given
        val input = listOf(
            ContactDataModel(
                id = 0,
                name = DavideName,
                email = DavideEmail
            ),
            ContactDataModel(
                id = 1,
                name = AnotherName,
                email = AnotherEmail
            )
        ).right()
        val expected = listOf(
            EncryptedContact(name = DavideName, email = DavideEmail),
            EncryptedContact(name = AnotherName, email = AnotherEmail),
        ).right()

        // when
        val result = mapper.toDomainModels(input)

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `maps single contact with invalid email`() = runBlockingTest {
        // given
        val input = ContactDataModel(
            id = 0,
            name = DavideName,
            email = InvalidEmail
        ).right()
        val expected = Error(ContactMapper.INVALID_EMAIL_FORMAT_ERROR_MESSAGE).left()

        // when
        val result = mapper.toDomainModel(input)

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `maps collection of contacts with invalid email`() = runBlockingTest {
        // given
        val input = listOf(
            ContactDataModel(
                id = 0,
                name = DavideName,
                email = InvalidEmail
            ),
            buildRandomContactDataModel()
        ).right()
        val expected = Error(ContactMapper.INVALID_EMAIL_FORMAT_ERROR_MESSAGE).left()

        // when
        val result = mapper.toDomainModels(input)

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `maps single contact from Left`() = runBlockingTest {
        // given
        val input = Error("something happened").left()

        // when
        val result = mapper.toDomainModel(input)

        // then
        assertEquals(input, result)
    }

    @Test
    fun `maps collection of contacts from Left`() = runBlockingTest {
        // given
        val input = Error("something happened").left()

        // when
        val result = mapper.toDomainModels(input)

        // then
        assertEquals(input, result)
    }

    private companion object TestData {
        const val DavideName = "Davide"
        const val DavideEmail = "davide@email.com"
        const val AnotherName = "Another"
        const val AnotherEmail = "another@email.com"
        const val InvalidEmail = "invalid"

        fun buildRandomContactDataModel() = ContactDataModel(
            id = 987,
            name = "Random",
            email = "random@email.com"
        )
    }
}
