package org.esolution.poc.models;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Contributor {

    @SerializedName("login")
    private String name;
    private Integer contributions;
}