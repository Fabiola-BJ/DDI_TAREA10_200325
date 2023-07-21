
package mx.edu.utxj.tidgs.ddi.tarea_10_200561.presentation

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.TextView
import androidx.activity.ComponentActivity
import mx.edu.utxj.tidgs.ddi.tarea_10_200561.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


/*
Para convertir grados Kelvin (K) a grados Celsius (°C), debes restar 273.15 a la temperatura en Kelvin.
Temperatura en °C = Temperatura en K - 273.15

URL NAVEGADOR:
XICOTEPEC
https://api.openweathermap.org/data/2.5/weather?lat=20.274167&lon=-97.95483032049188&appid=5e3120f5659a09f20f44e404a94d52ce
*/
class MainActivity : ComponentActivity() {

    private lateinit var TextViewHora: TextView
    private lateinit var TextViewSaludo: TextView
    private lateinit var TextviewTemperaturaGrados: TextView
    private lateinit var handler: Handler
    private lateinit var updateTimeRunnable: Runnable

    companion object {
        private const val BASE_URL = "http://api.openweathermap.org/data/2.5/"
        private const val API_KEY = "3d06df9b30489e192815578a54bedf56"
        private const val REQUEST_LOCATION_PERMISSION = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        TextViewHora = findViewById(R.id.TextViewHora)
        TextViewSaludo = findViewById(R.id.TextViewSaludo)
        TextviewTemperaturaGrados = findViewById(R.id.TextviewTemperaturaGrados)

        val Calendario = Calendar.getInstance()
        val HoraDia = Calendario.get(Calendar.HOUR_OF_DAY)
        val Saludar: String = when(HoraDia) {
            in 6..11 -> "GOOD MONRNING"
            in 12..17 -> "GOOD AFTERNOON"
            else -> "GOOD NIGHT"
        }
        TextViewSaludo.text = Saludar

        handler = Handler()
        updateTimeRunnable = object : Runnable {
            override fun run() {
                val currentTime = Calendar.getInstance().time
                val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val formattedTime = dateFormat.format(currentTime)
                TextViewHora.text = formattedTime

                obtenerTemperatura()

                handler.postDelayed(this, 1000)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        handler.post(updateTimeRunnable)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(updateTimeRunnable)
    }

    private fun obtenerTemperatura() {
        val latitud = 20.274167
        val longitud = -97.95483032049188


        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(WeatherService::class.java)

        val call = apiService.getWeather(latitud, longitud, API_KEY)
        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    val weatherResponse = response.body()
                    val TemperaturaApi = weatherResponse?.main?.temp
                    TextviewTemperaturaGrados.text = "$TemperaturaApi °C"
                } else {
                    Log.e("API_ERROR", "Error en la respuesta: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.e("API_ERROR", "Error en la solicitud: ${t.message}")
            }
        })
    }
}
