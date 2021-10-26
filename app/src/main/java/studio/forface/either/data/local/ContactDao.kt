package studio.forface.either.data.local

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import studio.forface.either.data.model.ContactDataModel

class ContactDao(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    private val cache = MutableStateFlow(emptyList<ContactDataModel>())

    fun findAllContacts(): Flow<List<ContactDataModel>> =
        cache

    suspend fun saveContacts(contacts: List<ContactDataModel>) {
        withContext(dispatcher) {
            cache.value = (cache.value + contacts).distinct()
        }
    }

}

