package com.example.smartgardenapp.api

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Url

interface apiService {
    @GET
    fun fetchData(@Url url: String): Single<dataModel>
}