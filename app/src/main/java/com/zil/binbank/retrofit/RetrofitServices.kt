package com.zil.binbank.retrofit

import com.zil.binbank.Card
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface RetrofitServices {
    @GET("{bin}")
    fun loadRepo(@Path("bin") bin: String): Call<Card>
}