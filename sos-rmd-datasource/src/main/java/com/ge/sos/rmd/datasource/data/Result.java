
package com.ge.sos.rmd.datasource.data;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("groups")
    @Expose
    private List<Group> groups = new ArrayList<Group>();
    @SerializedName("attributes")
    @Expose
    private Attributes attributes;
    @SerializedName("values")
    @Expose
    private List<List<Long>> values = new ArrayList<List<Long>>();

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

	public List<List<Long>> getValues() {
		return values;
	}

	public void setValues(List<List<Long>> values) {
		this.values = values;
	}

   

}
