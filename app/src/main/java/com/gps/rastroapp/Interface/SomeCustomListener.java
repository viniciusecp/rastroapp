package com.gps.rastroapp.Interface;

import org.json.JSONObject;

public interface SomeCustomListener<T>
{
    public void getResult(JSONObject object);
}