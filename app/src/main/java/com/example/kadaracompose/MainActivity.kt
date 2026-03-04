package com.example.kadaracompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.kadaracompose.navigation.presentation.AppNavHost
import com.example.kadaracompose.ui.theme.KadaracomposeTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * The one and only Activity in the app.
 *
 * Its only job is to mount the NavHost. It knows nothing about
 * individual screens — all navigation logic lives in AppNavHost.
 *
 * IMPORTANT: Remove the old localStorage MainActivity from your
 * AndroidManifest.xml and make this the single launcher activity:
 *
 *   <activity android:name=".MainActivity">
 *       <intent-filter>
 *           <action android:name="android.intent.action.MAIN" />
 *           <category android:name="android.intent.category.LAUNCHER" />
 *       </intent-filter>
 *   </activity>
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KadaracomposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavHost(navController = navController)
                }
            }
        }
    }
}
