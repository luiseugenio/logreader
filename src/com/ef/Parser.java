/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ef;

import com.ef.dao.BlacklistDAO;
import com.ef.dao.LogDAO;
import com.ef.models.Arguments;
import com.ef.models.Blacklist;
import com.ef.models.Durations;
import com.ef.models.Log;
import com.ef.services.Utilities;
import com.ef.services.Validates;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 * @author luis
 */
public class Parser {

    private static List<Log> logs;
    private static LogDAO logDAO;
    private static BlacklistDAO blacklistDAO;

    public static void main(String[] args) {
        try {
            initialize();
            Arguments arguments = read(args);
            readFileWith(arguments);
            insert(logs);
            process(arguments);
        } catch (ParseException ex) {
            Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void initialize() {
        logs = new ArrayList<>();
        logDAO = new LogDAO();
        blacklistDAO = new BlacklistDAO();
    }

    private static Arguments read(String[] args) throws ParseException {
        Arguments arguments = new Arguments();
        for (String arg : args) {
            if (arg.startsWith("--")) {
                String[] splitted = arg.split("=");
                switch (splitted[0]) {
                    case "--accesslog":
                        if (Validates.validateFile(splitted[1])) {
                            arguments.setAccessLog(splitted[1]);
                        }
                        break;
                    case "--startDate":
                        if (Validates.validadeDate(splitted[1])) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd.HH:mm:ss");
                            Date parsedDate = dateFormat.parse(splitted[1]);
                            arguments.setStartDate(new java.sql.Timestamp(parsedDate.getTime()));
                        }
                        break;
                    case "--duration":
                        if (Validates.validadeDuration(splitted[1])) {
                            arguments.setDuration(splitted[1]);
                        }
                        break;
                    case "--threshold":
                        if (Validates.validateNumber(splitted[1])) {
                            arguments.setThreshold(Integer.parseInt(splitted[1]));
                        }
                        break;
                }
            }

        }
        return arguments;
    }

    private static void readFileWith(Arguments arguments) {
        if (arguments != null && arguments.getAccessLog() != null) {
            try (Stream<String> stream = Files.lines(Paths.get(arguments.getAccessLog()))) {
                stream.forEach(line -> parse(line));
            } catch (IOException ex) {
                Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static void parse(String line) {
        String[] splitted = line.split("\\|");
        Log log = new Log();
        Class logClass = log.getClass();
        Field[] fields = logClass.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            try {
                Field field = logClass.getDeclaredField(fields[i].getName());
                field.setAccessible(true);
                field.set(log, splitted[i]);
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
                Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        logs.add(log);
    }

    private static void insert(List<Log> logs) {
        logDAO.insert(logs);
    }

    private static void process(Arguments arguments) {
        logDAO.getLogsBy(arguments).forEach(log -> {
            String msg = getMessage(log, arguments);
            System.out.println(msg);
            blacklistDAO.insert(new Blacklist(log.getIp(), msg));
        });
    }

    private static String getMessage(Log log, Arguments arguments) {
        String msg = "IP " + log.getIp() + " made more than " + arguments.getThreshold() + " requests starting from " + arguments.getStartDate() + " to ";
        if (arguments.getDuration().equalsIgnoreCase(Durations.hourly.toString())) {
            msg += Utilities.getDate(arguments.getStartDate(), Calendar.HOUR) + " (one hour).";
        } else if (arguments.getDuration().equalsIgnoreCase(Durations.daily.toString())) {
            msg += Utilities.getDate(arguments.getStartDate(), Calendar.DAY_OF_WEEK) + " (24 hours).";
        }
        return msg;
    }

}
