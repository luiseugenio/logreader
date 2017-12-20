/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ef.services;

import com.ef.models.Durations;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 *
 * @author luis
 */
public class Validates {

    public static boolean validateFile(String value) {
        Path path = Paths.get(value);
        return Files.exists(path, new LinkOption[]{LinkOption.NOFOLLOW_LINKS});
    }

    public static boolean validadeDate(String value) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd.HH:mm:ss");
            dateFormat.parse(value);
            return true;
        } catch (ParseException ex) {
            return false;
        }
    }

    public static boolean validadeDuration(String value) {
        return value.equalsIgnoreCase(Durations.hourly.toString()) || value.equalsIgnoreCase(Durations.daily.toString());
    }

    public static boolean validateNumber(String value) {
        for (char c : value.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }
}
