
package com.ge.sos.rmd.datasource.data;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Attributes {

    @SerializedName("floor_id")
    @Expose
    private List<String> floorId = new ArrayList<String>();

    public List<String> getFloorId() {
        return floorId;
    }

    public void setFloorId(List<String> floorId) {
        this.floorId = floorId;
    }

}
