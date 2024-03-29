package studio.forface.either.domain.usecase

import arrow.core.left
import arrow.core.right
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test
import studio.forface.either.domain.ARMOR
import studio.forface.either.domain.DecryptionError
import studio.forface.either.domain.model.ClearContact
import studio.forface.either.domain.model.EncryptedContact

@ExperimentalCoroutinesApi
class DecryptContactTest {

    private val decryptContact = DecryptContact(DecryptString(TestCoroutineDispatcher()))

    @Test
    fun `blank name`() = runBlockingTest {
        // given
        val input = EncryptedContact(name = "", email = "${ARMOR}hi@email.com$ARMOR")
        val expected = DecryptionError("Name: ${DecryptString.ErrorMessages.BLANK}").left()

        // when
        val result = decryptContact(input)

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `blank email`() = runBlockingTest {
        // given
        val input = EncryptedContact(name = "${ARMOR}name$ARMOR", email = "")
        val expected = DecryptionError("Email: ${DecryptString.ErrorMessages.BLANK}").left()

        // when
        val result = decryptContact(input)

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `not encrypted name`() = runBlockingTest {
        // given
        val input = EncryptedContact(name = "name", email = "${ARMOR}hi@email.com$ARMOR")
        val expected = DecryptionError("Name: ${DecryptString.ErrorMessages.GENERIC}").left()

        // when
        val result = decryptContact(input)

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `not encrypted email`() = runBlockingTest {
        // given
        val input = EncryptedContact(name = "${ARMOR}name$ARMOR", email = "hi@email.com")
        val expected = DecryptionError("Email: ${DecryptString.ErrorMessages.GENERIC}").left()

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
