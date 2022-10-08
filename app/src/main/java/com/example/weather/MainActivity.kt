package com.example.weather

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import android.location.Geocoder
import java.util.*
import java.util.jar.Manifest
import android.util.Log
import kotlin.collections.ArrayList
import android.location.Address
import android.view.View
import android.widget.*
import java.io.IOException
import android.content.pm.PackageManager.PERMISSION_GRANTED
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import android.net.sip.SipSession.Listener

import com.android.volley.Request
import com.android.volley.VolleyError
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONException


class MainActivity : AppCompatActivity() {

    //declaring the variables

    lateinit var homeRL:RelativeLayout
    lateinit var loadingPB:ProgressBar
    lateinit var citynameTv:TextView
    lateinit var TempTv:TextView
    lateinit var conditionTv:TextView
    lateinit var cityEdit:TextInputEditText
    lateinit var backiv:ImageView
    lateinit var searchiv:ImageView
    lateinit var iconiv:ImageView
    lateinit var weatherrv  :RecyclerView
    lateinit var weatherRVmodelArrayList:ArrayList<WeatherRVmodel>
    lateinit var weatherRVadapter: WeatherRVadapter

    lateinit var locationManager :LocationManager
    var PERMISSION_CODE =1
    lateinit var cityName:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        //Initialising the variables
        homeRL=findViewById(R.id.RLhome)
        loadingPB=findViewById(R.id.PBloading)
        citynameTv=findViewById(R.id.TVCityName)
        TempTv=findViewById(R.id.TVtemp)
        conditionTv=findViewById(R.id.TVcondition)
        cityEdit=findViewById(R.id.LLedit)
        backiv=findViewById(R.id.IVBack)
        searchiv=findViewById(R.id.IVSearch)
        iconiv=findViewById(R.id.IVicon)
        weatherrv=findViewById(R.id.RVWeather)

        //initialising the adapter
        val weatherRVModalArrayList = ArrayList<Any?>()
        weatherRVadapter = WeatherRVadapter(this, weatherRVmodelArrayList)
        weatherrv.setAdapter(weatherRVadapter)


        locationManager =getSystemService(LOCATION_SERVICE) as LocationManager

        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION),PERMISSION_CODE)

        }
        var location : Location? =locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        if (location != null) {
            cityName=getCityName(location.longitude,location.latitude)
        }
        getWeatherInfo(cityName)

        searchiv.setOnClickListener(View.OnClickListener {
            fun View.OnClick(v :View){
                var city:String=cityEdit.getText().toString();
                if(city.isEmpty()){
                    Toast.makeText(this@MainActivity,"Please Enter city Name",Toast.LENGTH_SHORT).show();
                }else{
                    citynameTv.setText(cityName);
                    getWeatherInfo(city);
                }
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==PERMISSION_CODE){
            if(grantResults.size==0 && grantResults[0]==PERMISSION_GRANTED){
                Toast.makeText(this,"PERMISSION GRANTED....",Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this,"Please provide the permission",Toast.LENGTH_SHORT).show()
                finish();
            }
        }
    }

    fun getCityName(longitude:Double, lattitude:Double):String{

        var cityName :String="NOT FOUND";
        val gcd=Geocoder(getBaseContext(), Locale.getDefault())

        try{
            val addresses: List<Address>
            addresses=gcd.getFromLocation(lattitude,longitude,10)
            var adr : Address
            for(adr  in addresses){
                if(adr!=null){
                    val city:String
                    city=adr.getLocality()
                    if(city!=null && !city.equals("")){
                        cityName=city
                    }
                    else{
                        Log.d("TAG","CITY NOT FOUND")
                        Toast.makeText(this,"USER's CITY NOT FOUND ...",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }catch (e:IOException ){
            e.printStackTrace()

        }
        return cityName


    }


    fun  getWeatherInfo( CityName:String):Unit{
        citynameTv.setText(CityName)
        val requestQuesue:RequestQueue
        requestQuesue= Volley.newRequestQueue(this)

        //creating a jsonObjectRequest




        val url = "http://api.weatherapi.com/v1/current.json?key=356ee3e3e0834a59a7115929220610&q="+CityName+"&aqi=no"

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,Response.Listener { response ->
                loadingPB.visibility = View.GONE
                homeRL.visibility = View.VISIBLE
            //clearing the recycler view ArrayList
            weatherRVmodelArrayList.clear()

            try{
                val temp =response.getJSONObject("current").getString("temp_c")
                TempTv.setText(temp+"degree C")

                val isDay =response.getJSONObject("Current").getInt("is_day")
                val condition=response.getJSONObject("Current").getJSONObject("condition").getString("text")
                val conditionicon=response.getJSONObject("Current").getJSONObject("condition").getString("icon")
                Picasso.get().load("http:"+conditionicon).into(iconiv)
                conditionTv.setText(condition)
                if(isDay==1){
                    //for Day
                    Picasso.get().load("https://wallpapercave.com/wp/wp8091988.jpg").into(backiv)
                }
                else{
                    //for night
                    Picasso.get().load("https://www.pixelstalk.net/wp-content/uploads/images3/Photos_For_Phone_Free.jpg").into(backiv)

                }
                val forecastobj:JSONObject=response.getJSONObject("forecast")
                val forcasto:JSONObject=forecastobj.getJSONArray("forecastDay").getJSONObject(0)
                val hourArray:JSONArray=forcasto.getJSONArray("hour")

                for (i in 0 until hourArray.length()) {
                    val hourobj:JSONObject=hourArray.getJSONObject(i)
                    val time:String=hourobj.getString("time")
                    val temper:String=hourobj.getString("temp_c")
                    val img:String=hourobj.getJSONObject("condition").getString("icon")
                    val wind:String=hourobj.getString("wind_kph")
                    weatherRVmodelArrayList.add(WeatherRVmodel(time,temper,img,wind))

                }
                weatherRVadapter.notifyDataSetChanged()

            }
            catch(e:JSONException){
                e.printStackTrace()
            }

        },Response.ErrorListener {
            fun onErrorResponse(error : VolleyError) {
                Toast.makeText(this,"Please Enter valis CityName ...",Toast.LENGTH_SHORT).show()
            }

        })

        requestQuesue.add(jsonObjectRequest)
    }

}








