package com.kraaiennest.opvang.di;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kraaiennest.opvang.api.APIService;
import com.kraaiennest.opvang.api.LocalDateTimeConverter;
import com.kraaiennest.opvang.retrofit.QueryConverterFactory;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.inject.Singleton;
import java.time.LocalDateTime;

@Module
@InstallIn(ApplicationComponent.class)
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
                .build();
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(QueryConverterFactory.create())
                .baseUrl(API_BASE_URL)
                .client(client)
                .build().create(APIService.class);
    }
}
