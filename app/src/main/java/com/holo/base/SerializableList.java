package com.holo.base;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by 根深 on 2015/12/6.
 */
public class SerializableList implements Serializable {

    private List<Map<String, Object>> list;

    public List<Map<String, Object>> getList() {
        return list;
    }

    public void setList(List<Map<String, Object>> list) {
        this.list = list;
    }
}
