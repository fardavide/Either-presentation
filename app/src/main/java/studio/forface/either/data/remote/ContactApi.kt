package studio.forface.either.data.remote

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import studio.forface.either.data.DataSet
import studio.forface.either.data.model.ContactDataModel

class ContactApi(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun getAllContacts(): List<ContactDataModel> = withContext(dispatcher) {
        DataSet.Contacts.all
    }
}
