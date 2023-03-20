package ru.megboyzz.hexapp.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val Typography = Typography(
    body1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )
    /* Other default text styles to override
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)

val navbar = TextStyle(
    fontSize = 20.sp,
    fontWeight = FontWeight.Normal,
    color = Color.White
)

val text = TextStyle(
    fontSize = 15.sp,
    fontWeight = FontWeight.Medium,
    color = Color.Black,
    textAlign = TextAlign.Center
)

val buttonText = TextStyle(
    fontSize = 15.sp,
    fontWeight = FontWeight.Medium,
    color = Color.White
)