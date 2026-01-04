package com.quest.evrouting.phone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.quest.evrouting.phone.ui.AppNavigation
import com.quest.evrouting.phone.ui.screens.MapScreen
import com.quest.evrouting.phone.ui.theme.EVRountingAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EVRountingAppTheme {
//                MapScreen()
                AppNavigation()
            }
        }
    }
}