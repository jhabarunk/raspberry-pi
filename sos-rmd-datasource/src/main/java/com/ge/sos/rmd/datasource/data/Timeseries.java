
package com.ge.sos.rmd.datasource.data;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Timeseries {

    @SerializedName("tags")
    @Expose
    private List<Tag> tags = new ArrayList<Tag>();

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

}
