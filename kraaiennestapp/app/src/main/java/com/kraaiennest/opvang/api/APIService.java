package com.kraaiennest.opvang.api;

import com.kraaiennest.opvang.model.CheckIn;
import com.kraaiennest.opvang.model.Child;
import com.kraaiennest.opvang.model.Registration;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public interface APIService {


    @GET("?endpoint=children")
    CompletableFuture<List<Child>> doGetChildren();

    @POST("post?endpoint=checkIn")
    CompletableFuture<CheckIn> doPostCheckIn(@Body CheckIn checkIn);

    @POST("post?endpoint=registration")
    CompletableFuture<Registration> doPostRegistration(@Body Registration registration);

    @GET("?endpoint=registration")
    CompletableFuture<List<Registration>> doGetRegistrationsOnDay(@Query("month") int month,@Query("day") int day);

    @GET("?endpoint=checkIn")
    CompletableFuture<List<CheckIn>> doGetCheckInsOnDay(@Query("month") int month,@Query("day") int day);
}
