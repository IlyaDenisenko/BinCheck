package com.zil.binbank

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.zil.binbank.databinding.ActivityMainBinding
import com.zil.binbank.retrofit.RetrofitServices
import com.zil.binbank.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*


class MainActivity : AppCompatActivity() {

    val URL = "https://lookup.binlist.net/"
    private lateinit var card: Card
    private lateinit var countTimer: CountDownTimer

    private lateinit var binding: ActivityMainBinding
    private lateinit var editText: EditText
    private lateinit var buttonConfirm: Button
    private lateinit var buttonHistory: Button
    private lateinit var loadView: ProgressBar

    private lateinit var bankName: TextView
    private lateinit var bankURL: TextView
    private lateinit var bankPhone: TextView
    private lateinit var countryEmoji: TextView
    private lateinit var countryName: TextView
    private lateinit var countryLocation: TextView
    private lateinit var cardScheme: TextView
    private lateinit var cardBrand: TextView
    private lateinit var cardType: TextView
    private lateinit var cardPrepaid: TextView
    private lateinit var cardLength: TextView
    private lateinit var cardLuhn: TextView
    private lateinit var layoutData: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        editText = binding.editText
        buttonConfirm = binding.buttonConfirm
        buttonHistory = binding.buttonHistory
        loadView = binding.loadBar

        bankName = binding.nameBank
        bankURL = binding.urlBank
        bankPhone = binding.phoneBank
        countryEmoji = binding.emoji
        countryName = binding.nameCountry
        countryLocation = binding.locationCountry
        cardScheme = binding.cardScheme
        cardBrand = binding.cardBrand
        cardType = binding.cardType
        cardPrepaid = binding.cardPrepaid
        cardLength = binding.cardLength
        cardLuhn = binding.cardLuhn
        layoutData = binding.layoutCardData
        setContentView(binding.root)

        buttonConfirm.setOnClickListener {
            layoutData.visibility = View.GONE
            if (conditionsEditText()){
                connect(editText.text.toString())
            }
        }

        clickPhone()
        clickLocation()
        clickURLBank()
        clickButtonHistory()
    }

    private fun connect(bin: String){
        loadView.visibility = View.VISIBLE
        val retrofit = RetrofitClient.getClient(URL)
        val apiService = retrofit.create(RetrofitServices::class.java)
        apiService.loadRepo(bin).enqueue(object : Callback<Card>{

            override fun onResponse(call: Call<Card>, response: Response<Card>) {
                if (response.isSuccessful){
                    card = response.body()!!

                    val luhn = if(card.number.luhn) "Yes"
                    else "No"
                    val prepaid = if(card.prepaid) "Yes"
                    else "No"

                    bankName.text = card.bank.name + ", " + card.bank.city
                    bankURL.text = card.bank.url
                    bankPhone.text = card.bank.phone
                    countryEmoji.text = card.country.emoji
                    countryName.text = card.country.name
                    countryLocation.text = getString(R.string.location, card.country.latitude, card.country.longitude)
                    cardScheme.text = card.scheme
                    cardBrand.text = card.brand
                    cardType.text = card.type
                    cardPrepaid.text = prepaid
                    cardLength.text = card.number.length.toString()
                    cardLuhn.text = luhn
                    layoutData.visibility = View.VISIBLE

                    val historyFile = File(filesDir.toString(), "history_bin.txt")
                    val out = FileOutputStream(historyFile, true)
                    out.write(editText.text.toString().toByteArray() + ",".toByteArray())
                    out.close()
                    loadView.visibility = View.GONE
                }
                else{
                    Toast.makeText(this@MainActivity, "BIN not found", Toast.LENGTH_SHORT).show()
                    loadView.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<Card>, t: Throwable){
                countTimer = object : CountDownTimer(5000, 1000){
                    override fun onTick(p0: Long) {

                    }

                    override fun onFinish() {
                        loadView.visibility = View.GONE
                        Toast.makeText(this@MainActivity, "Connection error", Toast.LENGTH_SHORT).show()
                    }

                }.start()
            }
        })

    }

    private fun conditionsEditText(): Boolean{
         if (editText.text.length < 6){
            Toast.makeText(this, "The bin must contain at least 6 digits", Toast.LENGTH_SHORT).show()
            return false
        }
        else return true
    }

    private fun clickPhone(){
        bankPhone.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel: " + card.bank.phone)

            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, Array(1){android.Manifest.permission.CALL_PHONE}, 1)
            }
            else {
                startActivity(intent)
            }
        }
    }

    private fun clickLocation(){
        countryLocation.setOnClickListener {
            val geoUriString = "geo:" + card.country.latitude + "," + card.country.longitude + "?z=15"
            val geoUri = Uri.parse(geoUriString)
            val intent = Intent(Intent.ACTION_VIEW, geoUri)
            startActivity(intent)
        }
    }

    private fun clickURLBank(){
        bankURL.setOnClickListener {
            val address = Uri.parse("http://" + card.bank.url)
            val intent = Intent(Intent.ACTION_VIEW, address)
            startActivity(intent)
        }
    }

    private fun clickButtonHistory(){
        buttonHistory.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }
    }
}