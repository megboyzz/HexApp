package ru.megboyzz.hexapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.megboyzz.hexapp.ui.theme.*


//Главный компонент приложения
@Composable
fun App() {

    MainScaffold {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            
           Spacer(modifier = Modifier.height(10.dp))
            
            Text(
                text = "Статус сервиса: сервис работает",
                style = text
            )
            
            MainButton(title = "Запустить сервис") {
                
            }

            MainButton(title = "Включить Режим чтения") {

            }

            Text(
                text = "HEX Коды",
                style = text
            )
            
            HexCard(hexString = "1A2B3C4D5E6F7A8B9C0D1E2")
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