package studio.forface.either

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import arrow.core.right
import studio.forface.either.domain.ApiError
import studio.forface.either.domain.DecryptionError
import studio.forface.either.domain.Error
import studio.forface.either.domain.ValidationError
import studio.forface.either.domain.model.ClearContact
import studio.forface.either.domain.model.ClearMessage
import studio.forface.either.domain.usecase.GetMessages
import studio.forface.either.ui.theme.EitherTheme

class MainActivity : ComponentActivity() {

    private val getMessages = GetMessages()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EitherTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    Home(getMessages)
                }
            }
        }
    }
}

@Composable
fun Home(getMessages: GetMessages) {
    Scaffold(
        topBar = {
            TopAppBar(contentPadding = PaddingValues(4.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text(fontSize = 24.sp, style = TextStyle(fontWeight = FontWeight.Bold), text = "Inbox")
                }
            }
        }
    ) {
        MessagesContainer(getMessages)
    }
}

@Composable
fun MessagesContainer(getMessages: GetMessages) {
    val list by getMessages().collectAsState(initial = emptyList<ClearMessage>().right())
    list.fold(
        ifLeft = { Error(error = it) },
        ifRight = { Messages(messages = it) }
    )
}

@Composable
fun Messages(messages: List<ClearMessage>) {
    LazyColumn(Modifier.padding(8.dp)) {
        items(messages) { message ->
            Message(message = message)
        }
    }
}

@Composable
fun Message(message: ClearMessage) {
    Row(Modifier.padding(8.dp)) {
        Column {

            val firstLineString = buildAnnotatedString {
                append("from: ")
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                append(message.from.name)
                pop()
                pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                append(" <${message.from.email}>")
            }
            Text(text = firstLineString)

            val secondLineString = buildAnnotatedString {
                pushStyle(SpanStyle(fontSize = 18.sp))
                append("subject: ")
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                append(message.subject)
            }
            Text(text = secondLineString)
        }
    }
}

@Composable
fun Error(error: Error) {
    val text = when (error) {
        is ApiError -> "Network error!"
        is DecryptionError -> "Decryption error!"
        is ValidationError -> "Validation error!"
    }
    Text(text = "Error $text ${error.message}")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    EitherTheme {
        Scaffold(
            topBar = {
                TopAppBar(contentPadding = PaddingValues(4.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        Text(fontSize = 24.sp, style = TextStyle(fontWeight = FontWeight.Bold), text = "Inbox")
                    }
                }
            }
        ) {
            Messages(
                listOf(
                    ClearMessage(
                        "Something super cool is coming!!",
                        ClearContact("Proton", "proton@email.com")
                    ),
                    ClearMessage(
                        "Good morning",
                        ClearContact("Davide", "davide@email.com")
                    ),
                )
            )
        }
    }
}

