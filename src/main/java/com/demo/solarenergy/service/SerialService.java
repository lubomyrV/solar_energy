package com.demo.solarenergy.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.demo.solarenergy.database.Sqlite;

public class SerialService implements Runnable {
    private String databaseName;
    private byte[] serialData;
    public SerialService(String databaseName, byte[] serialData) {
        this.databaseName = databaseName;
        this.serialData = serialData;
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

        String battery_voltage_pattern = "V\t";
        String battery_current_pattern = "I\t";

        String panel_voltage_pattern = "VPV\t";
        String panel_power_pattern = "PPV\t";

        String yield_total_pattern = "H19\t";
        String yield_today_pattern = "H20\t";
        String maximum_power_today_pattern = "H21\t";

        String charge_state_pattern = "CS\t";
        String error_code_pattern = "ERR\t";

        System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " received data: " + this.serialData.length);
        char[] dataArray = new  char[this.serialData.length];
        for (int i = 0; i < this.serialData.length; ++i) {
            dataArray[i] = (char)this.serialData[i];
        }
        String strData = new String(dataArray);
        String[] rows = strData.split("\n");
        for (String row : rows) {
            String[] parts = row.split("\t");
            if (parts.length < 2) {
                continue;
            }

            if (row.startsWith(charge_state_pattern)) {
                try {
                    String value = parts[1].trim();
                    charge_state = Integer.parseInt(value);
                } catch (Exception e) {
                    System.err.println(e);
                }

            }
            if (row.startsWith(error_code_pattern)) {
                try {
                    String value = parts[1].trim();
                    error_code = Integer.parseInt(value);
                } catch (Exception e) {
                    System.err.println(e);
                }

            }

            if (row.startsWith(battery_voltage_pattern)) {
                try {
                    String value = parts[1].trim();
                    battery_voltage_mV = Integer.parseInt(value);
                } catch (Exception e) {
                    System.err.println(e);
                }

            }
            if (row.startsWith(battery_current_pattern)) {
                try {
                    String value = parts[1].trim();
                    battery_current_mA = Integer.parseInt(value);
                    if (isDevMode) {
                        battery_current_mA = (int)(Math.random() * 5000 + 5);

                    }
                } catch (NumberFormatException e) {
                    System.err.println(e);
                }
            }

            if (row.startsWith(panel_voltage_pattern)) {
                try {
                    String value = parts[1].trim();
                    panel_voltage_mV = Integer.parseInt(value);
                    if (isDevMode) {
                        panel_voltage_mV = (int)(Math.random() * 15000 + 5);

                    }
                } catch (NumberFormatException e) {
                    System.err.println(e);
                }
            }
            if (row.startsWith(panel_power_pattern)) {
                try {
                    String value = parts[1].trim();
                    panel_power_W = Integer.parseInt(value);
                    if (isDevMode) {
                        panel_power_W = (int)(Math.random() * 95 + 5);

                    }
                } catch (NumberFormatException e) {
                    System.err.println(e);
                }
            }

            if (row.startsWith(yield_total_pattern)) {
                try {
                    String value = parts[1].trim();
                    yield_total_Wh = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    System.err.println(e);
                }
            }
            if (row.startsWith(yield_today_pattern)) {
                try {
                    String value = parts[1].trim();
                    yield_today_Wh = Integer.parseInt(value);
                    if (isDevMode) {
                        yield_today_Wh = (int)(Math.random() * 100 + 1);
                    }
                } catch (NumberFormatException e) {
                    System.err.println(e);
                }
            }
            if (row.startsWith(maximum_power_today_pattern)) {
                try {
                    String value = parts[1].trim();
                    maximum_power_today_W = Integer.parseInt(value);
                    if (isDevMode) {
                        maximum_power_today_W = (int)(Math.random() * 100 + 1);
                    }
                } catch (NumberFormatException e) {
                    System.err.println(e);
                }
            }
        }
        Sqlite connection = new Sqlite(this.databaseName);
        connection.upsertPower(maximum_power_today_W, yield_today_Wh, yield_total_Wh);
        if ((charge_state > 0 && panel_power_W >= 0) || isDevMode) {
            connection.insertPanel(panel_voltage_mV, panel_power_W);
            connection.insertEnergy(battery_voltage_mV, battery_current_mA);
            connection.insertController(MpptStaticData.getChargeStateByCode(charge_state), MpptStaticData.getErrorByCode(error_code));
        }
    }
}
