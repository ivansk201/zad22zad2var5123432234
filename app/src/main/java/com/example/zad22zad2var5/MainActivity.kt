package com.example.zad22zad2var5

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import java.util.Random


class MainActivity : AppCompatActivity() {
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var imageView: ImageView
    private lateinit var randomNumbers: IntArray
    private val random = Random()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imageView = findViewById(R.id.photopokemonsrandom)
        randomNumbers = resources.getIntArray(R.array.random_numbers)
        val randomIndex = random.nextInt(randomNumbers.size)
        val randomNumber = randomNumbers[randomIndex]
        val imageName = "photo$randomNumber"
        val imageResource = resources.getIdentifier(imageName, "drawable", packageName)
        if (imageResource != 0) {
            imageView.setImageResource(imageResource)
        }
        dbHelper = DatabaseHelper(this)

        usernameEditText = findViewById(R.id.editText)
        passwordEditText = findViewById(R.id.editText2)
        loginButton = findViewById(R.id.button2)
        registerButton = findViewById(R.id.button)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (dbHelper.checkUser(username, password)) {
                // Пользователь существует и введенные данные правильные
                // Вы можете выполнить вход
                val intent = Intent(this, MainActivity2::class.java)
                startActivity(intent)
            } else {
                // Пользователь не существует или введены неверные данные
                Toast.makeText(this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show()
            }
        }

        registerButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (dbHelper.checkUser(username, password)) {
                // Пользователь уже существует
                Toast.makeText(this, "Пользователь уже существует", Toast.LENGTH_SHORT).show()
            } else {
                val inserted = dbHelper.addUser(username, password)
                if (inserted != -1L) {
                    // Пользователь успешно зарегистрирован
                    Toast.makeText(this, "Пользователь успешно зарегистрирован", Toast.LENGTH_SHORT).show()
                } else {
                    // Произошла ошибка при регистрации
                    Toast.makeText(this, "Ошибка при регистрации", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}
