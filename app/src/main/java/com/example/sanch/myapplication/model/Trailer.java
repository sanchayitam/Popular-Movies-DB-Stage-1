package com.example.sanch.myapplication.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Trailer {
    private String id;
    private String key;
    private String name;
    private String site;

    public Trailer(){}

    public Trailer(JSONObject trailer) throws JSONException {
        this.id = trailer.getString("id");
        this.key = trailer.getString("key");
        this.name = trailer.getString("name");
        this.site = trailer.getString("site");
    }

    public String getId() {
        return id;
     }

    public String getKey() { return key; }

    public String getName() { return name; }

    public String getSite() { return site; }
}
