package com.kraaiennest.kraaiennestapp.api;

import com.kraaiennest.kraaiennestapp.model.Child;
import com.kraaiennest.kraaiennestapp.model.Presence;
import com.kraaiennest.kraaiennestapp.model.PresenceResponse;
import com.kraaiennest.kraaiennestapp.model.Registration;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.kraaiennest.kraaiennestapp.Constants.SCRIPT_URL;

public interface APIInterface {

    @GET(SCRIPT_URL + "/exec?mode=presence")
    CompletableFuture<List<Presence>> doGetPresence();

    @GET(SCRIPT_URL + "/exec?mode=children")
    CompletableFuture<List<Child>> doGetChildren();

    @POST(SCRIPT_URL + "/exec?mode=checkIn")
    Call<ResponseBody> doPostCheckIn(@Body Child child);

    @POST(SCRIPT_URL + "/exec?mode=register")
    Call<ResponseBody> doPostRegister(@Body Registration registration);
}
