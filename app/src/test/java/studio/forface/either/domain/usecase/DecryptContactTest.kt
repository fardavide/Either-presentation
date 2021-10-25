package studio.forface.either.domain.usecase

import arrow.core.left
import arrow.core.right
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test
import studio.forface.either.domain.ARMOR
import studio.forface.either.domain.Error
import studio.forface.either.domain.model.ClearContact
import studio.forface.either.domain.model.EncryptedContact
import kotlin.math.exp

@ExperimentalCoroutinesApi
class DecryptContactTest {

    private val decryptContact = DecryptContact(TestCoroutineDispatcher())

    @Test
    fun `blank name`() = runBlockingTest {
        // given
        val input = EncryptedContact(name = "", email = "${ARMOR}hi@email.com$ARMOR")
        val expected = Error("Name: ${DecryptContact.ErrorMessages.BLANK}").left()

        // when
        val result = decryptContact(input)

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `blank email`() = runBlockingTest {
        // given
        val input = EncryptedContact(name = "${ARMOR}name$ARMOR", email = "")
        val expected = Error("Email: ${DecryptContact.ErrorMessages.BLANK}").left()

        // when
        val result = decryptContact(input)

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `not encrypted name`() = runBlockingTest {
        // given
        val input = EncryptedContact(name = "name", email = "${ARMOR}hi@email.com$ARMOR")
        val expected = Error("Name: ${DecryptContact.ErrorMessages.GENERIC}").left()

        // when
        val result = decryptContact(input)

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `not encrypted email`() = runBlockingTest {
        // given
        val input = EncryptedContact(name = "${ARMOR}name$ARMOR", email = "hi@email.com")
        val expected = Error("Email: ${DecryptContact.ErrorMessages.GENERIC}").left()

        // when
        val result = decryptContact(input)

        // then
        assertEquals(expected, result)
    }

    @Test
    fun success() = runBlockingTest {
        // given
        val name = "name"
        val email = "hi@email.com"
        val input = EncryptedContact(name = "$ARMOR$name$ARMOR", email = "$ARMOR$email$ARMOR")
        val expected = ClearContact(name = name, email = email).right()

        // when
        val result = decryptContact(input)

        // then
        assertEquals(expected, result)
    }
}
