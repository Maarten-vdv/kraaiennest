package com.kraaiennest.opvang.di;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kraaiennest.opvang.api.APIService;
import com.kraaiennest.opvang.api.LocalDateTimeConverter;
import com.kraaiennest.opvang.repository.ChildRepository;
import com.kraaiennest.opvang.repository.RegistrationRepository;
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
public class RepositoryModule {

    @Provides
    @Singleton
    public static RegistrationRepository registrationRepository() {
        return new RegistrationRepository();
    }

    @Provides
    @Singleton
    public static ChildRepository childRepository() {
        return new ChildRepository();
    }
}
