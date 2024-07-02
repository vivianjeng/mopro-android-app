package com.example.moproandroidapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.moproandroidapp.ui.theme.MoproAndroidAppTheme
import kotlinx.coroutines.launch
import uniffi.mopro.generateCircomProof
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoproAndroidAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(this)
                }
            }
        }
    }
}

@Composable
fun MainScreen(context: Context) {
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(onClick = {
            coroutineScope.launch {
                val assetFilePath = copyAssetToInternalStorage(context, "multiplier2_final.zkey")
                assetFilePath?.let { path ->
                    val inputs = mutableMapOf<String, List<String>>()
                    inputs["a"] = listOf("3")
                    inputs["b"] = listOf("5")
                    val res = generateCircomProof(path, inputs)
                    println(res)
                }
            }
        }) {
            Text(text = "Generate Proof")
        }
    }
}

private fun copyAssetToInternalStorage(context: Context, assetFileName: String): String? {
    val file = File(context.filesDir, assetFileName)
    return try {
        context.assets.open(assetFileName).use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                val buffer = ByteArray(1024)
                var length: Int
                while (inputStream.read(buffer).also { length = it } > 0) {
                    outputStream.write(buffer, 0, length)
                }
                outputStream.flush()
            }
        }
        file.absolutePath
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}
