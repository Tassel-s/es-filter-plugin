package com.huoli.plugin.es.filter.utils;

import com.huoli.plugin.es.filter.entity.Room;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
public class ScriptConstants {

    public static boolean priceRangeLimit(Map<String, Object> ctx, Map<String, Object> params) {
        log.info("PriceRangeLimit--------------------------->start");
        // 不根据日期查找
        if (!params.containsKey("sdays")) {
            log.info("PriceRangeLimit days no exist");
            return false;
        }

        List<Room> allRooms = new ArrayList<>();
        // 合并多天数据
        List<String> days = (List<String>) params.get("sdays");
        log.info("PriceRangeLimit days------------->{}", days);
        int count = days.size();
        for (String day : days) {
            List<Room> rooms = RoomUtils.parseRooms(ctx, day);
            if (CollectionUtils.isEmpty(rooms)) {
                return false;
            }
            allRooms.addAll(rooms);
        }

        // onlines 这个参数是必传的
        List<String> onlines = (List<String>) params.get("online");
        log.info("online: " + onlines);
        // discountMap
        Map<String, Double> discount = (Map<String, Double>) params.get("discountmap");
        // 价格区间
        int minprice = (int) params.get("minprice");
        int maxprice = (int) params.get("maxprice");
        log.info("allromms----------->" + allRooms);
        // 合并天数并剔除掉状态不正常的数据
        List<Room> roomList = allRooms.stream()
                .filter(room -> StringUtils.isNotBlank(room.getPrice()) && !"null".equals(room.getPrice()))
                .filter(room -> room.getSoldOut() != 1)
                .filter(room -> onlines.contains(room.getSource()))
                .filter(room -> room.getPayType() != 4 && room.getPayType() != 5)
                .filter(room -> Double.parseDouble(room.getPrice()) > 0)
                .collect(Collectors.toList());
        log.info("PriceRangeLimit rooms--------------->{}", roomList);
        // 根据渠道和支付状态分组
        Map<String, List<Room>> sourcePayTypeGroup = new HashMap<>();
        for (Room room : roomList) {
            String key = room.getSource() + "_" + room.getPayType();
            List<Room> value = sourcePayTypeGroup.get(key);
            if (value == null) {
                value = new ArrayList<>();
            }
            value.add(room);
            sourcePayTypeGroup.put(key, value);
        }

        List<Double> prices = new ArrayList<>();
        for (Map.Entry<String, List<Room>> entry : sourcePayTypeGroup.entrySet()) {
            String key = entry.getKey();
            List<Room> datePriceList = sourcePayTypeGroup.get(key);
            long paytype = datePriceList.get(0).getPayType();
            String source = datePriceList.get(0).getSource();
            if (datePriceList.size() != count) {
                continue;
            }
            // 总价格
            double total = datePriceList.stream().mapToDouble(d -> Double.parseDouble(d.getPrice())).sum();
            double p = 0.0;
            double price = total / count;
            if ((paytype == 1 || (source.equals("ctrip") && paytype == 3) || (source.equals("elong") && paytype == 8))
                    && discount.get(source) != null) {
                p = price * (discount.get(source));
            } else {
                p = price;
            }
            prices.add(p);
        }

        double mincomprice = 0.0;
        if (prices.size() > 0) {
            Collections.sort(prices);
            mincomprice = prices.get(0);
        }
        log.info("PriceRangeLimit price--------------->{}", mincomprice);
        log.info("PriceRangeLimit minprice--------------->{}", minprice);
        log.info("PriceRangeLimit maxprice--------------->{}", maxprice);
        if (mincomprice == 0) {
            return false;
        }

        if (minprice > 0 && mincomprice < minprice) {
            return false;
        }
        if (maxprice > 0 && mincomprice > maxprice) {
            return false;
        }
        return true;
    }



}
