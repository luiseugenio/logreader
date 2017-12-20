/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ef.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author luis
 */
public class Utilities {

    public static java.sql.Timestamp getDate(java.sql.Timestamp date, int type) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(type, 1);
        return new java.sql.Timestamp(c.getTime().getTime());
    }

    public static java.sql.Timestamp convertStringToTimestamp(String value) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date parsedDate = dateFormat.parse(value);
        return new java.sql.Timestamp(parsedDate.getTime());
    }

    public static String convertTimestampToString(java.sql.Timestamp value) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String parsedDate = dateFormat.format(value);
        return parsedDate;
    }
}
