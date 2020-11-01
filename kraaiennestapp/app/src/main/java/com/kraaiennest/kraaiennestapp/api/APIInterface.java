package com.kraaiennest.kraaiennestapp.api;

import com.kraaiennest.kraaiennestapp.model.Presence;
import com.kraaiennest.kraaiennestapp.model.PresenceResponse;
import retrofit2.Call;
import retrofit2.http.GET;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.kraaiennest.kraaiennestapp.Constants.SCRIPT_URL;

public interface APIInterface {

    @GET(SCRIPT_URL + "/exec?mode=presence")
    CompletableFuture<List<Presence>> doGetPresence();

//    @POST("/api/users")
//    Call<User> createUser(@Body User user);
//
//    @GET("/api/users?")
//    Call<UserList> doGetUserList(@Query("page") String page);
//
//    @FormUrlEncoded
//    @POST("/api/users?")
//    Call<UserList> doCreateUserWithField(@Field("name") String name, @Field("job") String job);
}
