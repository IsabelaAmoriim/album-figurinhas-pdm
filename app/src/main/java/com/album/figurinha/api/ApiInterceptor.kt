package com.album.figurinha.api
import okhttp3.Interceptor
import okhttp3.Response

class ApiInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()
            .newBuilder()
            .addHeader("x-apisports-key", "9946a0ee6f4786aa0f4efdee47ed5448")
            .build()

        return chain.proceed(request)
    }
}