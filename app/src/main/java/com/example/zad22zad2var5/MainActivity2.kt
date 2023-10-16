package com.example.zad22zad2var5

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity2 : AppCompatActivity() {
    private lateinit var editTextPokemon: EditText
    private lateinit var buttonSearch: Button
    private lateinit var textViewResult: TextView
    private lateinit var imageViewPokemon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        editTextPokemon = findViewById(R.id.editText3)
        buttonSearch = findViewById(R.id.button_screath)
        textViewResult = findViewById(R.id.vivod)
        imageViewPokemon = findViewById(R.id.iamgepoklen)

        buttonSearch.setOnClickListener {
            val pokemonNameOrId = editTextPokemon.text.toString()
            searchPokemon(pokemonNameOrId)
        }
    }

    private fun searchPokemon(nameOrId: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val apiUrl = "https://pokeapi.co/api/v2/pokemon/$nameOrId"

            try {
                val url = URL(apiUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val responseCode = connection.responseCode

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = StringBuilder()
                    var line: String?

                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }

                    reader.close()

                    val jsonObject = JSONObject(response.toString())
                    val name = jsonObject.getString("name")
                    val id = jsonObject.getInt("id")

                    val types = jsonObject.getJSONArray("types")
                    val typeList = mutableListOf<String>()
                    for (i in 0 until types.length()) {
                        val typeObj = types.getJSONObject(i)
                        val typeName = typeObj.getJSONObject("type").getString("name")
                        typeList.add(typeName)
                    }

                    val abilities = jsonObject.getJSONArray("abilities")
                    val abilityList = mutableListOf<String>()
                    for (i in 0 until abilities.length()) {
                        val abilityObj = abilities.getJSONObject(i)
                        val abilityName = abilityObj.getJSONObject("ability").getString("name")
                        abilityList.add(abilityName)
                    }
                    val speciesUrl = jsonObject.getJSONObject("species").getString("url")
                    val speciesInfo = fetchSpeciesInfo(speciesUrl)
                    val imageUrl = jsonObject.getJSONObject("sprites").getString("front_default")

                    runOnUiThread {
                        val resultText = "Имя: $name\nID: $id\nТипы: ${typeList.joinToString()}\nСпособности: ${abilityList.joinToString()}\nХарактеристики: $speciesInfo"
                        textViewResult.text = resultText
                        Picasso.get().load(imageUrl).into(imageViewPokemon)
                    }
                } else {
                    runOnUiThread {
                        textViewResult.text = "Покемон не найден"
                    }
                }

                connection.disconnect()
            } catch (e: Exception) {
                runOnUiThread {
                    textViewResult.text = "Ошибка при выполнении запроса"
                }
            }
        }
    }

    private fun fetchSpeciesInfo(speciesUrl: String): String {
        val connection = URL(speciesUrl).openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        val responseCode = connection.responseCode

        return if (responseCode == HttpURLConnection.HTTP_OK) {
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = StringBuilder()
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }

            reader.close()

            val jsonObject = JSONObject(response.toString())
            val flavorTextEntries = jsonObject.getJSONArray("flavor_text_entries")

            var englishFlavorText = "Информация о характеристиках не найдена"

            for (i in 0 until flavorTextEntries.length()) {
                val entry = flavorTextEntries.getJSONObject(i)
                val language = entry.getJSONObject("language").getString("name")
                val flavorText = entry.getString("flavor_text")

                if (language == "en") {
                    englishFlavorText = flavorText
                    break
                }
            }

            englishFlavorText
        } else {
            "Информация о характеристиках не найдена"
        }
    }
}