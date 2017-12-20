/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ef.models;

/**
 *
 * @author luis
 */
public class Blacklist {

    private final String ip;
    private final String reason;

    public Blacklist(String ip, String reason) {
        this.ip = ip;
        this.reason = reason;
    }

    public String getIp() {
        return ip;
    }

    public String getReason() {
        return reason;
    }
}
