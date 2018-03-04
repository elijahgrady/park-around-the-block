package com.parkaroundtheblock.mapsapp.network;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by jacobilin on 3/3/18.
 */

public class Properties implements Serializable {

    @SerializedName("orgPixelCoordinates")
    public String orgPixelCoordinates;

    @SerializedName("pixelCoordinates")
    public String pixelCoordinates;

    @SerializedName("objectUid")
    public String objectUid;

    @SerializedName("geoCoordinates")
    public String geoCoordinates;

    @SerializedName("imageAssetUid")
    public String imageAssetUid;

    @SerializedName("assetUid")
    public String assetUid;

}
