package com.kraaiennest.opvang.di;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kraaiennest.opvang.api.APIService;
import com.kraaiennest.opvang.api.LocalDateTimeConverter;
import com.kraaiennest.opvang.retrofit.QueryConverterFactory;

import java.time.Duration;
import java.time.LocalDateTime;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {

    public static final String API_BASE_URL = "https://www.tkraaiennest-grembergen.be/opvang/";

    @Provides
    @Singleton
    Gson provideGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeConverter());
        return gsonBuilder.create();
    }

    @Provides
    @Singleton
    public static APIService provideAPI(Gson gson) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.level(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .followSslRedirects(true)
                .followRedirects(true)
                .connectTimeout(Duration.ofSeconds(30))
                .writeTimeout(Duration.ofSeconds(30))
                .readTimeout(Duration.ofSeconds(30))
                .build();
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(QueryConverterFactory.create())
                .baseUrl(API_BASE_URL)
                .client(client)
                .build().create(APIService.class);
    }
}
