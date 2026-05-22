package com.app.pertodemim.network;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// Singleton para o cliente Retrofit
public class RetrofitClient {
    // URL de produção do backend hospedado no Railway
    private static final String BASE_URL = "https://backend-production-b962.up.railway.app/";
    private static Retrofit retrofit = null;

    public static ApiService getApiService() {
        return getApiService(null);
    }

    public static ApiService getApiService(String token) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        
        if (token != null && !token.isEmpty()) {
            httpClient.addInterceptor(chain -> {
                Request original = chain.request();
                Request request = original.newBuilder()
                        .header("Authorization", "Bearer " + token)
                        .method(original.method(), original.body())
                        .build();
                return chain.proceed(request);
            });
        }

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        return retrofit.create(ApiService.class);
    }
}
