package dev.cristianosilva.listatarefas.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import dev.cristianosilva.listatarefas.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL


class WeatherFragment : Fragment() {

    data class WeatherData(val temperature: Double, val description: String, val conditionCode: Int)
    data class DistrictDate(val city: String, val district: String, val country: String)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_weather, container, false)


        if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            getLocation(view);
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1);
        }


        return view;
    }

    private fun getWeatherIcon(conditionCode: Int): String {
        return when(conditionCode) {

            in 200..232 -> "trovoada"
            in 300..321 -> "chuvisco"
            in 500..531 -> "chuva"
            in 600..622 -> "neve"
            in 701..781 -> "nevoeiro"
            800 -> "ensolarado"
            801 -> "nublado"
            802 -> "nuvens"
            803, 804 -> "muitas_nuvens"
            1000 -> "ensolarado"
            1009 -> "nublado"
            1183 -> "chuvisco"
            else -> "nublado"
        }
    }

    private fun getWeatherColor(conditionCode: Int): String {
        return when(conditionCode) {
            in 200..232 -> "#637E90"
            in 300..321 -> "#29B3FF"
            in 500..531 -> "#14C2DD"
            in 600..622 -> "#E5F2F0"
            in 701..781 -> "#FFFEA8"
            800 -> "#FBC740"
            801 -> "#BCECE0"
            802 -> "#BCECE0"
            803, 804 -> "#36EEE0"
            1000 -> "#FBC740"
            1009 -> "#BCECE0"
            1183 -> "#888989"
            else -> "#FBC740"
        }
    }



    private fun updateUi(weatherData: WeatherData, cityDistrict: DistrictDate , view: View) {
        try{
            val temperatureView = view.findViewById<TextView>(R.id.temperatureView);
            val descriptionView = view.findViewById<TextView>(R.id.descriptionView);
            val cityStateCountryView = view.findViewById<TextView>(R.id.cityStateCountryView);

            temperatureView.text = "${weatherData.temperature} °C"
            descriptionView.text = weatherData.description
            cityStateCountryView.text = "${cityDistrict.city}, ${cityDistrict.district} - ${cityDistrict.country}"

            val iconName = getWeatherIcon(weatherData.conditionCode);
            val iconColor = getWeatherColor(weatherData.conditionCode);

            val imageView = view.findViewById<ImageView>(R.id.image_view)
            val drawableReference = resources.getIdentifier(iconName, "drawable", requireContext().packageName);
            imageView.setImageResource(drawableReference);

            val color = Color.parseColor(iconColor);
            imageView.setColorFilter(color);

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun getWeatherData(latitude: Double, longitude: Double): WeatherData {

        val apiKey = "0236a2c255a9456a8e120842252503"
        val url = "https://api.weatherapi.com/v1/current.json?lang=pt&key=$apiKey&q=$latitude,$longitude"
        val jsonText = withContext(Dispatchers.IO) {
            URL(url).readText()
        }

        val jsonObject = JSONObject(jsonText)
        val current = jsonObject.getJSONObject("current")

        val temperature = current.getDouble("temp_c")
        val description = current.getJSONObject("condition").getString("text")
        val conditionCode = current.getJSONObject("condition").getInt("code")

        return WeatherData(temperature, description, conditionCode);
    }

    private suspend fun getCityDistrict(latitude: Double, longitude: Double): DistrictDate {
        val apiKey = "bdc_7dc853c0ad9344f2a818d7043ddf673a" //"bdc_f018f000fa1f402b9a2458db356db6e9"
        val url = "https://api-bdc.net/data/reverse-geocode?latitude=$latitude&longitude=$longitude&localityLanguage=pt&key=$apiKey"
        val jsonText = withContext(Dispatchers.IO) {
            URL(url).readText()
        }
        val jsonObject = JSONObject(jsonText)
        val city = jsonObject.getString("city")
        val district = jsonObject.getString("locality")
        val country = jsonObject.getString("countryName")

        return DistrictDate(city, district, country);
    }
    private fun getLocation(view: View) {
        val locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if(ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f,
                object : LocationListener {
                    override fun onLocationChanged(p0: Location) {
                        lifecycleScope.launch {
                            val weatherData = getWeatherData(p0.latitude, p0.longitude)
                            val districtDate = getCityDistrict(p0.latitude, p0.longitude)
                            updateUi(weatherData, districtDate, view)
                        }

                    }

                })
        }
    }
}