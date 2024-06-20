package com.demo.solarenergy.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.demo.model.MpptData;
import com.demo.solarenergy.database.Sqlite;

public class MpptRestService implements Runnable {
    private String databaseName;
    private String url;
    public MpptRestService(String databaseName, String url) {
        this.databaseName = databaseName;
        this.url = url;
    }

    @Override
    public void run() {        
        boolean isDevMode = false;
        int battery_voltage_mV = 0;
        int battery_current_mA = 0;

        int panel_voltage_mV = 0;
        int panel_power_W = 0;

        int yield_today_Wh = 0;
        int yield_total_Wh = 0;
        int maximum_power_today_W = 0;
        
        Integer charge_state = -1;
        Integer error_code = -1;

        String data = "";
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(this.url).openConnection();
            con.setRequestMethod("GET");
            int statusCode = con.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                data = response.toString();
            } else {
                System.err.println("Failed to get data from " + this.url + ", status code " + statusCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        MpptData mppt = new MpptData(data);

        yield_today_Wh = mppt.getYieldToday();
        yield_total_Wh = mppt.getYieldTotal();

        battery_voltage_mV = mppt.getBatteryVoltage();
        battery_current_mA = mppt.getBatteryCurrent();

        panel_voltage_mV = mppt.getPanelVoltage();
        panel_power_W = mppt.getPanelPower();

        maximum_power_today_W = mppt.getMaximumPowerToday();
        
        charge_state = mppt.getChargeState();
        error_code = mppt.getErrorCode();

        System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " received data: " + data.length());

        Sqlite connection = new Sqlite(this.databaseName);
        connection.upsertPower(maximum_power_today_W, yield_today_Wh, yield_total_Wh);
        if ((charge_state > 0 && panel_power_W >= 0) || isDevMode) {
            connection.insertPanel(panel_voltage_mV, panel_power_W);
            connection.insertEnergy(battery_voltage_mV, battery_current_mA);
            connection.insertController(MpptStaticData.getChargeStateByCode(charge_state), MpptStaticData.getErrorByCode(error_code));
        }
    }
}
