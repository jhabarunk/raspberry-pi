
package com.ge.sos.rmd.datasource.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Stats {

    @SerializedName("rawCount")
    @Expose
    private Integer rawCount;

    public Integer getRawCount() {
        return rawCount;
    }

    public void setRawCount(Integer rawCount) {
        this.rawCount = rawCount;
    }

}
