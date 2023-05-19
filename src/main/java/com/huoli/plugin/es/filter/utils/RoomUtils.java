package com.huoli.plugin.es.filter.utils;

import com.huoli.plugin.es.filter.entity.Room;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j2
public class RoomUtils {
    public static List<Room> parseRooms(Map<String, Object> ctx, String dataStr) {
        log.info("ctx------------------------------------------------------->begin     " + dataStr);
        Map<String, Object> doc = (Map<String, Object>) ctx.get("_source");
        // days.2023-05-01.room
        Map<String, Object> days = (Map<String, Object>) doc.get("days");

        // 根据日期遍历房态
        List lists = (List) days.get(dataStr);
        if (CollectionUtils.isEmpty(lists)) {
            return new ArrayList<>();
        }
        List<Room> rooms = new ArrayList<>();
        for (Object o : lists) {
            Room room = new Room();
            // 每个日期下的房态 转换为Map 类型
            Map<String, Object> objectMap = (Map<String, Object>) o;

            room.setTimeLimit((String) objectMap.get("timeLimit"));
            room.setPayType(Long.parseLong(objectMap.get("payType").toString()));
            room.setPrice((String) objectMap.get("price"));
            room.setHourRoomTime((List<String>) objectMap.get("hourRoomTime"));
            room.setSource((String) objectMap.get("source"));
            room.setSoldOut(Long.parseLong(objectMap.get("soldOut").toString()));
            rooms.add(room);
        }

        log.info("ctx------------------------------------------------------->end     " + dataStr);
        return rooms;
    }
}
