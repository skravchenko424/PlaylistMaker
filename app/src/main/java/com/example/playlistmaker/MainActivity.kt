package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // anon class for the first button
        val buttonSearch = findViewById<Button>(R.id.search_button)
        val buttonSearchClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Переходим в поиск", Toast.LENGTH_SHORT).show()
            }
        }
        buttonSearch.setOnClickListener(buttonSearchClickListener)

        // lambda for the second button
        val buttonMedia = findViewById<Button>(R.id.media_library_button)
        buttonMedia.setOnClickListener {
            Toast.makeText(this@MainActivity, "Открываем медиабиблиотеку", Toast.LENGTH_SHORT).show()
        }

        // lambda for the third button
        val buttonSettings = findViewById<Button>(R.id.settings_button)
        buttonSettings.setOnClickListener {
            Toast.makeText(this@MainActivity, "Открываем настройки", Toast.LENGTH_SHORT).show()
        }
    }
}