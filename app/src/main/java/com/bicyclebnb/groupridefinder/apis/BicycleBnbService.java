package com.bicyclebnb.groupridefinder.apis;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by admin on 3/3/17.
 */

public interface BicycleBnbService {
    @GET("listings-page/")
    Call<ResponseBody> listRides();

    @GET("properties/")
    Call<ResponseBody> listAccommodations();

    @GET("race-listing/")
    Call<ResponseBody> listRaces();
}
