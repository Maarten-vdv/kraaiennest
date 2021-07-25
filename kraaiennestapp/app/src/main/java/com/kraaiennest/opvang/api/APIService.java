package com.kraaiennest.opvang.api;

import com.kraaiennest.opvang.model.CheckIn;
import com.kraaiennest.opvang.model.Child;
import com.kraaiennest.opvang.model.Registration;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

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

    @GET("?endpoint=registrations&month={month}&day={day}")
    CompletableFuture<List<Registration>> doGetRegistrationsOnDay(int month, int day);

    @GET("?endpoint=checkIns&month={month}&day={day}")
    CompletableFuture<List<CheckIn>> doGetCheckInsOnDay(int month, int day);
}
