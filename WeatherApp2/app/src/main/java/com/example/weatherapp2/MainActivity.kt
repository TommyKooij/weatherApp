package com.example.weatherapp2

import android.annotation.SuppressLint
import android.location.Geocoder
import android.location.Location
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp2.adapter.WeatherAdapter
import com.example.weatherapp2.model.WeatherData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    var City :String = ""
    var API :String = "f6b408b2be1e46f3bc9104022220606"
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var weatherList: ArrayList<WeatherData>
    private lateinit var weatherAdapter: WeatherAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        obtainLocation()
        weatherBtn.setOnClickListener {
            var city_name = city_name.text.toString()
            if(!city_name.equals("")){
                City = city_name;
                WeatherTask().execute()
            }
        }

    }

    @SuppressLint("MissingPermission")
    private fun obtainLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location:Location? ->
                var current_City_Name = getCityName(location?.longitude,location?.latitude)
                City = current_City_Name
                WeatherTask().execute()
            }
    }

    private fun getCityName(longitude: Double?, latitude: Double?): String {
        var cityName = "Not Found"

        val gcd :Geocoder = Geocoder(this@MainActivity, Locale.getDefault())
        try {
            val address = gcd.getFromLocation(latitude!!,longitude!!,10)
            for(adr in address){
                if(adr != null){
                    val city = adr.locality
                    if(city != null && !city.equals("")){
                        cityName = city
                    }
                }
            }
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }

        return cityName
    }
    inner class WeatherTask() : AsyncTask<String,Void,String>()
    {
        override fun onPreExecute() {
            super.onPreExecute()
            loader.visibility = View.VISIBLE
            idMainContainer.visibility = View.GONE
        }

        override fun doInBackground(vararg p0: String?): String {
            var response :String?
            try {
                response = URL("https://api.openweathermap.org/data/2.5/weather?q=${City.lowercase()}&units=metric&appid=$API").readText(Charsets.UTF_8)
            }
            catch(e:Exception){
                response = null
            }

            return response !!
        }

        @RequiresApi(Build.VERSION_CODES.N)
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                val updateAt :Long = jsonObj.getLong("dt")
                val updateText = "Updated at: " + SimpleDateFormat("dd/MM//yyyy hh:mm a",Locale.getDefault())
                    .format(Date(updateAt * 1000))
                val temp = main.getString("temp") + "°C"
                val tempMin = "Min Temp: " + main.getString("temp_min") + "°C"
                val tempMax = "Max Temp: " + main.getString("temp_max") + "°C"
                val pressure = main.getString("pressure")
                val humidity = main.getString("humidity")
                val sunrise = sys.getLong("sunrise")
                val sunset = sys.getLong("sunset")
                val windSpeed = wind.getString("speed")
                val weatherDescription = weather.getString("description")
                val addr = jsonObj.getString("name")+","+sys.getString("country")

                idAddress.text = addr
                update_at.text = updateText
                idStatus.text = weatherDescription.capitalize()
                idTemp.text = temp
                idMin_Temp.text = tempMin
                idMax_Temp.text = tempMax

                weatherList = ArrayList()
                weatherAdapter = WeatherAdapter(this@MainActivity,weatherList)
                idTemp_Info_Rec.layoutManager = LinearLayoutManager(
                    this@MainActivity,LinearLayoutManager.HORIZONTAL,
                    false)

                idTemp_Info_Rec.adapter = weatherAdapter
                weatherList.add(
                    WeatherData(R.drawable.sunrise,"Sunrise",
                    SimpleDateFormat("hh:mm a", Locale.getDefault())
                        .format(Date(sunrise*1000))))
                weatherList.add(
                    WeatherData(R.drawable.sunset,"Sunset",
                        SimpleDateFormat("hh:mm a", Locale.getDefault())
                            .format(Date(sunset*1000))))
                weatherList.add(
                    WeatherData(R.drawable.wind,"Wind",windSpeed
                        ))
                weatherList.add(
                    WeatherData(R.drawable.pressuregauge,"Pressure",pressure
                    ))
                weatherList.add(
                    WeatherData(R.drawable.humidity,"Humidity",humidity
                    ))

                loader.visibility = View.GONE
                idMainContainer.visibility = View.VISIBLE
            }
            catch (e:Exception){
                loader.visibility = View.GONE
                error("Error!")
            }
        }

    }
}
