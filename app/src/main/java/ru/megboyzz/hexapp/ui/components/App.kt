package ru.megboyzz.hexapp.ui.components

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.megboyzz.hexapp.ui.theme.*
import ru.megboyzz.hexapp.util.runningIntentValue
import ru.megboyzz.hexapp.util.statusIntentParam
import ru.megboyzz.hexapp.util.statusResultAction
import ru.megboyzz.hexapp.viewmodel.MainViewModel
import ru.megboyzz.hexapp.viewmodel.MainViewModelFactory
import ru.megboyzz.hexapp.viewmodel.ServiceStatus


//Главный компонент приложения
@Composable
fun App() {

    val mainActivity = LocalContext.current as Activity

    val viewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory(mainActivity.application),
    )

    val status by viewModel.serviceStatus.collectAsState()

    val isRunEnabled by viewModel.isRunServiceButtonEnabled.collectAsState()
    val isReadEnabled by viewModel.isReadButtonEnabled.collectAsState()

    val hexList by viewModel.hexList.collectAsState()

    MainScaffold {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Статус сервиса: $status",
                style = text
            )

            MainButton(
                title = if(status != ServiceStatus.STOPPED) "Выключить сервис" else "Запустить сервис",
                enabled = isRunEnabled
            ) {
                if(status == ServiceStatus.STARTED) viewModel.stopService()
                else if(status == ServiceStatus.STOPPED) viewModel.startService()
            }

            MainButton(
                title = if(status == ServiceStatus.READING_MODE) "Выключить режим чтения" else "Включить режим чтения",
                enabled = isReadEnabled
            ) {
                if(status == ServiceStatus.STARTED) viewModel.toggleIntoReadingMode()
                else if(status == ServiceStatus.READING_MODE) viewModel.leaveFormReadingMode()
            }

            Text(
                text = "HEX Коды",
                style = text
            )

            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(10.dp)
            ){
                items(hexList){ HexCard(hexString = it) }
            }

        }

    }

}

@Composable
fun HexCard(
    hexString: String
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, blue),
        modifier = Modifier.size(300.dp, 60.dp)
    ) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            Text(
                text = hexString,
                style = text
            )
        }

    }
}

@Composable
fun MainButton(
    title: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = blue,
            disabledBackgroundColor = disabledBlue
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.width(250.dp),
        enabled = enabled
    ) {
        Text(
            text = title,
            style = buttonText
        )
    }
}

@Composable
fun MainScaffold(
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "HexApp",
                        style = navbar
                    )
                },
                backgroundColor = blue
            )
        }
    ) {
        it.hashCode()
        content.invoke()
    }
}