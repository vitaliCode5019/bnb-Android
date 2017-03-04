package com.bicyclebnb.groupridefinder.apis;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bicyclebnb.groupridefinder.MapsActivity;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by admin on 3/3/17.
 */

public class ServiceInterceptor {
    static String TAG = "WebService";

    private final Context context;
    private final boolean showProgress;
    private final boolean handleError;
    private final Call<ResponseBody> call;

    private ServiceInterceptor(Builder builder) {
        this.context = builder.context;
        this.showProgress = builder.showProgress;
        this.handleError = builder.handleError;
        this.call = builder.call;
    }

    public void execute(final ResponseHandler responseHandler) {
        if(showProgress) {
            if(context instanceof MapsActivity) {
                ((MapsActivity)context).showHud();
            } else {
                Log.w(TAG, "HUD cannot be displayed on <" + context.getClass().getSimpleName() + ">");
            }
        }

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(showProgress && context instanceof MapsActivity) {
                    ((MapsActivity)context).dismissHud();
                }
                try {
                    if(response.body() == null) {
                        Log.e(TAG, "No result");
                        return;
                    }

                    String result = response.body().string();
//                    Log.e(TAG, result);
                    if (responseHandler != null) {
                        responseHandler.onSuccess(result);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if(showProgress && context instanceof MapsActivity) {
                    ((MapsActivity)context).dismissHud();
                }
                if(handleError) {
                    responseHandler.onFailure(0);
                }
                t.printStackTrace();
            }
        });
    }

    public static class Builder {
        private final Context context;
        private final Call<ResponseBody> call;

        private boolean showProgress = true;
        private boolean handleError = true;

        public Builder(@NonNull Context context, @NonNull Call<ResponseBody> call) {
            this.context = context;
            this.call = call;
        }

        public Builder showProgress(boolean showProgress) {
            this.showProgress = showProgress;
            return this;
        }

        public Builder handleError(boolean handleError) {
            this.handleError = handleError;
            return this;
        }

        public ServiceInterceptor build() {
            return new ServiceInterceptor(this);
        }
    }

    public interface ResponseHandler {
        void onFailure(int errCode);
        void onSuccess(String result);
    }
}

