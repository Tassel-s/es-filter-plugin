package com.huoli.plugin.es.filter.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Room implements Serializable {
    private String timeLimit;
    private Long payType;
    private String price;
    private List<String> hourRoomTime;
    private String source;
    private Long soldOut;

}