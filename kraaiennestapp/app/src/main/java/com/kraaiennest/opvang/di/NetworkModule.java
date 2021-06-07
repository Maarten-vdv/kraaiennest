package com.kraaiennest.opvang.di;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kraaiennest.opvang.api.APIService;
import com.kraaiennest.opvang.api.LocalDateTimeConverter;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.inject.Singleton;
import java.time.LocalDateTime;

@Module
@InstallIn(ApplicationComponent.class)
public class NetworkModule {

    public static final String SCRIPT_BASE_URL = "https://script.google.com";
    public static final String SCRIPT_URL = "/macros/s/";

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
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(SCRIPT_BASE_URL)
                .build().create(APIService.class);
    }
}
