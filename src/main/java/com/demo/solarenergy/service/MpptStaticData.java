package com.demo.solarenergy.service;

import java.util.HashMap;
import java.util.Map;

public class MpptStaticData {
    public static String getErrorByCode(int code) {
        Map<Integer,String> errorByCode = new HashMap<>();
        errorByCode.put(0, "No error");
        errorByCode.put(2, "Battery voltage too high");
        errorByCode.put(17, "Charger temperature too high");
        errorByCode.put(18, "Charger over current");
        errorByCode.put(19, "Charger current reversed");
        errorByCode.put(20, "Bulk time limit exceeded");
        errorByCode.put(21, "Current sensor issue (sensor bias/sensor broken)");
        errorByCode.put(26, "Terminals overheated");
        errorByCode.put(28, "Converter issue (dual converter models only)");
        errorByCode.put(33, "Input voltage too high (solar panel)");
        errorByCode.put(34, "Input current too high (solar panel)");
        errorByCode.put(38, "Input shutdown (due to excessive battery voltage)");
        errorByCode.put(39, "Input shutdown (due to current flow during off mode)");
        errorByCode.put(65, "Lost communication with one of devices");
        errorByCode.put(66, "Synchronised charging device configuration issue");
        errorByCode.put(67, "BMS connection lost");
        errorByCode.put(68, "Network misconfigured");
        errorByCode.put(116, "Factory calibration data lost");
        errorByCode.put(117, "Invalid/incompatible firmware");
        errorByCode.put(119, "User settings invalid");
        return errorByCode.containsKey(code) ? errorByCode.get(code) : "Not found";
    }

    public static String getChargeStateByCode(int code) {
        Map<Integer,String> chargeStateByCode = new HashMap<>();
        chargeStateByCode.put(0, "Off");
        chargeStateByCode.put(2, "Fault");
        chargeStateByCode.put(3, "Bulk");
        chargeStateByCode.put(4, "Absorption");
        chargeStateByCode.put(5, "Float");
        chargeStateByCode.put(7, "Equalize (manual)");
        chargeStateByCode.put(245, "Starting-up");
        chargeStateByCode.put(247, "Auto equalize / Recondition");
        chargeStateByCode.put(252, "External Control");
        return chargeStateByCode.containsKey(code) ? chargeStateByCode.get(code) : "Not found";
    }

}
