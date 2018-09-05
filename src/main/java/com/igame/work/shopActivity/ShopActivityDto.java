package com.igame.work.shopActivity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Transient;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ShopActivityDto implements Map<Long, ShopActivityPlayDto> {
    @Id
    @JsonIgnore
    public int _id;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    /**为了数据库结构少包装一层 所以这个类继承自Map 不然表里还有个players字段 里面才是key value*/
    @Transient
    private Map<Long, ShopActivityPlayDto> players = new HashMap<>();

    @Override
    public int size() {
        return players.size();
    }

    @Override
    public boolean isEmpty() {
        return players.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return players.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public ShopActivityPlayDto get(Object key) {
        return players.get(key);
    }

    @Override
    public ShopActivityPlayDto put(Long key, ShopActivityPlayDto value) {
        return players.put(key, value);
    }

    @Override
    public ShopActivityPlayDto remove(Object key) {
        return players.remove(key);
    }

    @Override
    public void putAll(Map<? extends Long, ? extends ShopActivityPlayDto> m) {
        players.putAll(m);
    }

    @Override
    public void clear() {
        players.clear();
    }

    @Override
    public Set<Long> keySet() {
        return players.keySet();
    }

    @Override
    public Collection<ShopActivityPlayDto> values() {
        return players.values();
    }

    @Override
    public Set<Entry<Long, ShopActivityPlayDto>> entrySet() {
        return players.entrySet();
    }
}

class ShopActivityPlayDto {
    long openMillis;
    /**时间戳:金额*/
    Map<Long,Long> goldActivityInfo = new HashMap<>();
    /**itemId:count*/
    Map<Integer, Integer> itemActivityInfo = new HashMap<>();

    public String getDescription() {
        return "openMillis 活动开始的时间戳";
    }
}
