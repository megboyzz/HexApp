package ru.megboyzz.hexapp.ui.components

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.megboyzz.hexapp.MainActivity
import ru.megboyzz.hexapp.ui.theme.text
import ru.megboyzz.hexapp.ui.viewmodel.MainViewModel
import ru.megboyzz.hexapp.ui.viewmodel.MainViewModelFactory

@Preview
@Composable
fun ScaffoldWithServiceState() {

    val mainActivity = LocalContext.current as Activity

    val viewModel: MainViewModel = viewModel(factory = MainViewModelFactory(mainActivity.application))

    val status by viewModel.serviceStatus.collectAsState()

    val isRunEnabled by viewModel.isRunServiceButtonEnabled.collectAsState()
    val isReadEnabled by viewModel.isReadButtonEnabled.collectAsState()

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
                title = "Запустить сервис",
                enabled = isRunEnabled
            ) {

            }

            MainButton(
                title = "Включить Режим чтения",
                enabled = isReadEnabled
            ) {

            }

            Text(
                text = "HEX Коды",
                style = text
            )

            MainButton(title = "Toggle Mode") {
                viewModel.nextStatus()
                viewModel.calculateButtonsState()
            }
        }

    }
}