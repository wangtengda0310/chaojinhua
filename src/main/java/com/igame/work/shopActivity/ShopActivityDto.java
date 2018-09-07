package com.igame.work.shopActivity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.mongodb.morphia.annotations.Id;

import java.util.HashMap;
import java.util.Map;

public class ShopActivityDto {
    @Id
    @JsonIgnore
    public int _id;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public Map<Long, ShopActivityPlayerDto> players = new HashMap<>();

}