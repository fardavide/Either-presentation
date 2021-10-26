package studio.forface.either.data.local

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import studio.forface.either.data.model.MessageDataModel

class MessageDao(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    private val cache = MutableStateFlow(emptyList<MessageDataModel>())

    fun findAllMessages(): Flow<List<MessageDataModel>> =
        cache

    suspend fun saveMessages(messages: List<MessageDataModel>) {
        withContext(dispatcher) {
            cache.value = (cache.value + messages).distinct()
        }
    }
}

