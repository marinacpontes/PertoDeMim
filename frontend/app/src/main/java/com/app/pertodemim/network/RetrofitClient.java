package com.app.pertodemim.network;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// Singleton para o cliente Retrofit
public class RetrofitClient {
    // 10.0.2.2 é o IP padrão para o emulador acessar o localhost do seu computador
    private static final String BASE_URL = "http://10.0.2.2:3000/";
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
