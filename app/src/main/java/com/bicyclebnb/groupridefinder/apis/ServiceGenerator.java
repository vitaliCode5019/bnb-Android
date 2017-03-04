package com.bicyclebnb.groupridefinder.apis;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Created by admin on 3/3/17.
 */

public class ServiceGenerator {
    static final String BNB_BASE_URL = "http://bicyclebnb.com";
    static final String GOOGLE_SHEET_BASE_URL = "https://spreadsheets.google.com";
    static final String GOOGLE_MAP_BASE_URL = "https://maps.googleapis.com";

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder bnbBuilder =
            new Retrofit.Builder()
                    .baseUrl(BNB_BASE_URL);

    private static Retrofit.Builder googleMapBuilder =
            new Retrofit.Builder()
                    .baseUrl(GOOGLE_MAP_BASE_URL);

    private static Retrofit.Builder googleSheetBuilder =
            new Retrofit.Builder()
                    .baseUrl(GOOGLE_SHEET_BASE_URL);

    public static <S> S createGoogleMapService(Class<S> serviceClass) {
        OkHttpClient client = httpClient.build();
        Retrofit retrofit = googleMapBuilder.client(client).build();
        return retrofit.create(serviceClass);
    }

    public static <S> S createGoogleSheetService(Class<S> serviceClass) {
        OkHttpClient client = httpClient.build();
        Retrofit retrofit = googleSheetBuilder.client(client).build();
        return retrofit.create(serviceClass);
    }

    public static <S> S createBnbService(Class<S> serviceClass) {
        OkHttpClient client = httpClient
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = bnbBuilder.client(client).build();
        return retrofit.create(serviceClass);
    }
}