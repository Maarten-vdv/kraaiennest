package com.kraaiennest.opvang.api;

import com.kraaiennest.opvang.model.Child;
import com.kraaiennest.opvang.model.Presence;
import com.kraaiennest.opvang.model.Registration;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.kraaiennest.opvang.di.NetworkModule.SCRIPT_URL;


public interface APIService {

    @GET(SCRIPT_URL + "{scriptId}/exec?mode=presence")
    CompletableFuture<List<Presence>> doGetPresence(@Path(value = "scriptId") String scriptId);

    @GET(SCRIPT_URL + "{scriptId}/exec?mode=children")
    CompletableFuture<List<Child>> doGetChildren(@Path(value = "scriptId") String scriptId);

    @POST(SCRIPT_URL + "{scriptId}/exec?mode=checkIn")
    Call<ResponseBody> doPostCheckIn(@Path(value = "scriptId") String scriptId, @Body Child child);

    @POST(SCRIPT_URL + "{scriptId}/exec?mode=register")
    Call<ResponseBody> doPostRegister(@Path(value = "scriptId") String scriptId, @Body Registration registration);
}
