package com.parkaroundtheblock.mapsapp.network;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by jacobilin on 3/3/18.
 */

public class LocationItem implements Serializable {

    @SerializedName("locationUid")
    public String locationUid;

    @SerializedName("assetUid")
    public String assetUid;

    @SerializedName("eventType")
    public String eventType;

    @SerializedName("timestamp")
    public Integer timestamp;

    @SerializedName("properties")
    public Properties properties;

}
