package com.ms.ergoseatingdelivery.api

import com.ms.ergoseatingdelivery.model.DeliveryItem
import com.ms.ergoseatingdelivery.model.SuccessResponse
import com.ms.ergoseatingdelivery.model.LoginResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface  ApiInterface {
    @GET("delivery/{productType}")
    fun getDeliveryList(
        @Header("Authorization") token: String,
        @Path("productType") productType: String,
        @Query("fromDate") fromDate: String,
        @Query("toDate") toDate: String,
    ) : Call<List<DeliveryItem>>

    @FormUrlEncoded
    @POST("delivery/generatePDF")
    fun generateDeliveryPDF(
        @Header("Authorization") token: String,
        @Field("productType") productType: String,
        @Field("deliveryId") name: String,
    ): Call<SuccessResponse>


    @FormUrlEncoded
    @POST("delivery/sign")
    fun uploadSignature(
        @Header("Authorization") token: String,
        @Field("productType") productType: String,
        @Field("deliveryId") name: String,
        @Field("signature") image: String
    ): Call<SuccessResponse>

    @FormUrlEncoded
    @POST("user/login")
    fun userLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    companion object {
//        var BASE_URL = "http://192.168.6.242:4000/api/"
        var BASE_URL = "http://blueoceanblue.com/api/"
        fun create() : ApiInterface {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(ApiInterface::class.java)
        }
    }
}