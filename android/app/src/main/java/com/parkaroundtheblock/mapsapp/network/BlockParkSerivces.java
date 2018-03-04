package com.parkaroundtheblock.mapsapp.network;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by jacobilin on 3/3/18.
 */

public interface BlockParkSerivces {

    @POST("/spots")
    Observable<Response<Content>> getParkingSpots(@Path("lat") String lat, @Path("lng") String lng);

}
