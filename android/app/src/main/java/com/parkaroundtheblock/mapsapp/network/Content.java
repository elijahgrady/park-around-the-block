package com.parkaroundtheblock.mapsapp.network;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jacobilin on 3/3/18.
 */

public class Content implements Serializable {

    @SerializedName("content")
    public List<LocationItem> content;

}
