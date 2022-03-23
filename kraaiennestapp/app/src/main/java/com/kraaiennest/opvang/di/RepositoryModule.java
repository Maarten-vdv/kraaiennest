package com.kraaiennest.opvang.di;

import com.kraaiennest.opvang.api.APIService;
import com.kraaiennest.opvang.repository.ChildRepository;
import com.kraaiennest.opvang.repository.RegistrationRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class RepositoryModule {


    @Provides
    @Singleton
    public static RegistrationRepository registrationRepository(APIService apiService) {
        return new RegistrationRepository(apiService);
    }

    @Provides
    @Singleton
    public static ChildRepository childRepository(APIService apiService) {
        return new ChildRepository(apiService);
    }
}
