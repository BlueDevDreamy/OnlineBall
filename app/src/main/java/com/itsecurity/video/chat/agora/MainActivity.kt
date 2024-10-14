package com.itsecurity.video.chat.agora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import com.itsecurity.video.chat.agora.model.UiStore
import com.itsecurity.video.chat.agora.ui.theme.OnlineBallTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OnlineBallTheme {
                NavGraph()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    OnlineBallTheme {
        NavGraph()
    }
}