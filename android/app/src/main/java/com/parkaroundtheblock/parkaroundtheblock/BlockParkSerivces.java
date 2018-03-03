package com.parkaroundtheblock.parkaroundtheblock;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.PUT;

/**
 * Created by jacobilin on 3/3/18.
 */

public interface BlockParkSerivces {

    @PUT("/v1/ticker")
    Observable<Response<Void>> call();

}
