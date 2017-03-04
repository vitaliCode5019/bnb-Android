package com.bicyclebnb.groupridefinder.apis;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by admin on 3/3/17.
 */

public interface ShopService {
    @GET("feeds/list/1ceQmezsovaTx1wCdjeqhos2-tJviw3OppRsTtuU_RzQ/1/public/values?alt=json")
    Call<ResponseBody> listShops();
}
