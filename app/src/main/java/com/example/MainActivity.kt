package com.example

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import com.example.ui.AppNavigation
import com.example.ui.theme.MyApplicationTheme

class MainActivity : FragmentActivity() {
  private var isDarkThemeOverride by mutableStateOf<Boolean?>(null)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val systemDarkTheme = isSystemInDarkTheme()
      val isDarkTheme = isDarkThemeOverride ?: systemDarkTheme

      MyApplicationTheme(darkTheme = isDarkTheme) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          AppNavigation(
              isDarkTheme = isDarkTheme,
              onDarkThemeChange = { isDarkThemeOverride = it }
          )
        }
      }
    }
  }
}
