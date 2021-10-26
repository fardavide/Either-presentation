package studio.forface.either.data.remote

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import studio.forface.either.data.DataSet
import studio.forface.either.data.model.ContactDataModel
import studio.forface.either.data.model.MessageDataModel

class MessageApi(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun getAllMessages(): List<MessageDataModel> = withContext(dispatcher) {
        DataSet.Messages.all
    }
}
