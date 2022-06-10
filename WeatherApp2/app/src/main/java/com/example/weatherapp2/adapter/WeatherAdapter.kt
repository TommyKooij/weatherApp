package com.example.weatherapp2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp2.R
import com.example.weatherapp2.model.WeatherData
import kotlin.contracts.Returns

class WeatherAdapter(val c:Context, var weatherList:ArrayList<WeatherData>):
RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>()

{
    inner class WeatherViewHolder (v:View):RecyclerView.ViewHolder(v)
    {
        val tempImg = v.findViewById<ImageView>(R.id.idTemp_Img)
        val temp = v.findViewById<TextView>(R.id.idTemp_Txt)
        val tempInfo = v.findViewById<TextView>(R.id.idTemp_Info)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.weather_item,parent,false)
        return WeatherViewHolder(v)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        var newWeatherList = weatherList[position]
        holder.temp.text = newWeatherList.temp
        holder.tempInfo.text = newWeatherList.tempInfo
        holder.tempImg.setImageResource(newWeatherList.img)
    }

    override fun getItemCount(): Int = weatherList.size
}