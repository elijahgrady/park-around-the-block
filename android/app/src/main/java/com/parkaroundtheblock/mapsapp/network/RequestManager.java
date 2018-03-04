package com.parkaroundtheblock.mapsapp.network;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * Created by jacobilin on 3/3/18.
 */

public class RequestManager {

    private static final String BASE_API_URL = "https://blockpark.com";

    private static BlockParkSerivces defaultRequestManager;

    private static Retrofit retrofit;

    private static Retrofit generateRetrofit(final Context context, final String contentType, boolean addGsonConverter) {
        Gson gson = new GsonBuilder()
                //.setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        final OkHttpClient client = getOkHttpClient(context, contentType);
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(BASE_API_URL);
        if (addGsonConverter) {
            // See https://github.com/square/retrofit/issues/1210
            builder = builder.addConverterFactory(new GsonStringConverterFactory());
        }
        builder = builder.addConverterFactory(GsonConverterFactory.create(gson));
        builder = builder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        return builder.client(client).build();
    }

    public static BlockParkSerivces getDefault(Context context) {
        if (retrofit == null) {
            retrofit = generateRetrofit(context, "application/json", false);
        }

        if (defaultRequestManager == null) {
            defaultRequestManager = retrofit.create(BlockParkSerivces.class);
        }
        return defaultRequestManager;
    }

    @NonNull
    private static OkHttpClient getOkHttpClient(final Context context, final String contentType) {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
                .readTimeout(20, TimeUnit.SECONDS)
                .connectTimeout(20, TimeUnit.SECONDS);

        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();

                Request.Builder requestBuilder = original.newBuilder()
                        .header("Content-Type", contentType)
                        .header("Accept", "application/json")
                        .header("X-Api-Key", "8a773626-2df0-4c4f-8a36-0ac977019c26")
                        .method(original.method(), original.body());
                return chain.proceed(requestBuilder.build());
            }
        });

        return builder.build();
    }

}
