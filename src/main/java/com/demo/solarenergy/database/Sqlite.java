package com.demo.solarenergy.database;

import org.json.JSONArray;
import java.io.File;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Sqlite {

    private DateTimeFormatter ISO8601 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String fileName;

    public Sqlite(String fileName) {
        this.fileName = fileName;
    }

    public int insertController(String charge_state, String error) {
        int result = 0;
        if (charge_state == null || error == null) {
            return result;
        }
        String query = "INSERT INTO controllers (date,datetime,charge_state,error) VALUES(?,?,?,?)";
        String datetime = LocalDateTime.now().format(this.ISO8601);
        String date = LocalDate.now().toString();
        Connection conn = null;
        try {
            conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, date);
            pstmt.setString(2, datetime);
            pstmt.setString(3, charge_state);
            pstmt.setString(4, error);
            result = pstmt.executeUpdate();
            if (result > 0) {
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("datetime", datetime);
                dataMap.put("charge_state", charge_state);
                dataMap.put("error", error);
                System.out.println("Controller record was saved " + dataMap.toString());
            }
        } catch (SQLException e) {
            System.err.println("SQLException on insertController " + e.getMessage());
        } finally {
            this.close(conn);
        }
        return result;
    }

    public int upsertPower(Integer maximum_power_today_W, Integer yield_today_Wh, Integer yield_total_Wh) {
        int result = 0;
        if (yield_today_Wh <= 0) {
            return result;
        }
        String date = LocalDate.now().toString();
        String query = "INSERT INTO powers (date,maximum_power_today_W,yield_today_Wh,yield_total_Wh)"
            + " VALUES(?,?,?,?)"
            + " ON CONFLICT(date)"
            + " DO UPDATE SET "
            + "     maximum_power_today_W=excluded.maximum_power_today_W," 
            + "     yield_today_Wh=excluded.yield_today_Wh,"
            + "     yield_total_Wh=excluded.yield_total_Wh"
            + " WHERE date = excluded.date";
        
        Connection conn = null;
        try {
            conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, date);
            pstmt.setInt(2, maximum_power_today_W);
            pstmt.setInt(3, yield_today_Wh);
            pstmt.setInt(4, yield_total_Wh);
            result = pstmt.executeUpdate();
            if (result > 0) {
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("date", date);
                dataMap.put("yield_total_Wh", yield_total_Wh);
                dataMap.put("yield_today_Wh", yield_today_Wh);
                dataMap.put("maximum_power_today_W", maximum_power_today_W);
                System.out.println("Power record was upserted " + dataMap.toString());
            }
        } catch (SQLException e) {
            System.err.println("SQLException on upsertPower " + e);
        } finally {
            this.close(conn);
        }
        return result;
    }

    public int insertEnergy(Integer battery_voltage_mV, Integer battery_current_mA) {
        int result = 0;
        if (battery_voltage_mV <= 0 || battery_current_mA <= 0) {
            return result;
        }
        String query = "INSERT INTO energy (date,datetime,voltage_mV,current_mA) VALUES(?,?,?,?)";
        String datetime = LocalDateTime.now().format(this.ISO8601);
        String date = LocalDate.now().toString();
        Connection conn = null;
        try {
            conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, date);
            pstmt.setString(2, datetime);
            pstmt.setInt(3, battery_voltage_mV);
            pstmt.setInt(4, battery_current_mA);
            result = pstmt.executeUpdate();
            if (result > 0) {
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("datetime", datetime);
                dataMap.put("battery_voltage_mV", battery_voltage_mV);
                dataMap.put("battery_current_mA", battery_current_mA);
                System.out.println("Energy record was saved " + dataMap.toString());
            }
        } catch (SQLException e) {
            System.err.println("SQLException on insertEnergy " + e.getMessage());
        } finally {
            this.close(conn);
        }
        return result;
    }

    public int insertPanel(Integer panel_voltage_mV, Integer panel_power_W) {
        int result = 0;
        if (panel_voltage_mV <= 0 || panel_power_W <= 0) {
            return result;
        }
        String query = "INSERT INTO panels (date,datetime,voltage_mV,power_W) VALUES(?,?,?,?)";
        int minutes = ThreadLocalRandom.current().nextInt(1, 240 + 1);
        minutes = 0;
        String datetime = LocalDateTime.now().plusMinutes(minutes).format(this.ISO8601);
        String date = LocalDate.now().toString();
        Connection conn = null;
        try {
            conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, date);
            pstmt.setString(2, datetime);
            pstmt.setInt(3, panel_voltage_mV);
            pstmt.setInt(4, panel_power_W);
            result = pstmt.executeUpdate();
            if (result > 0) {
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("datetime", datetime);
                dataMap.put("panel_voltage_mV", panel_voltage_mV);
                dataMap.put("panel_power_W", panel_power_W);
                System.out.println("Panel record was saved " + dataMap.toString());
            }
        } catch (SQLException e) {
            System.err.println("SQLException on insertPanel " + e.getMessage());
        } finally {
            this.close(conn);
        }
        return result;
    }

    public List<Map<String, Object>> getAllRecords() {
        String query = "SELECT * FROM panels";
        List<Map<String, Object>> result = new ArrayList<>();
        Connection conn = null;
        try {
            conn = this.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                try {
                    Map<String, Object> dataMap = new HashMap<>();
                    dataMap.put("date", LocalDate.parse(rs.getString("date")));
                    dataMap.put("datetime", LocalDateTime.parse(rs.getString("datetime")));
                    dataMap.put("voltage_mV", Float.parseFloat(rs.getString("voltage_mV")));
                    dataMap.put("power_W", Float.parseFloat(rs.getString("power_W")));
                    result.add(dataMap);
                } catch (Exception e) {
                    System.err.println("Exception on getAllRecords parse values " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.err.println("SQLException on getAllRecords " + e.getMessage());
        } finally {
            this.close(conn);
        }
        return result;
    }

    public List<Map<String, Object>> getRecordsByDate(String date) {
        String query = "SELECT * FROM panels WHERE date = ?";
        List<Map<String, Object>> result = new ArrayList<>();
        Connection conn = null;
        try {
            conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, date);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                try {
                    Map<String, Object> dataMap = new HashMap<>();
                    dataMap.put("datetime", LocalDateTime.parse(rs.getString("datetime"), this.ISO8601));
                    dataMap.put("date", LocalDate.parse(rs.getString("date")));
                    dataMap.put("voltage_mV", Float.parseFloat(rs.getString("voltage_mV")));
                    dataMap.put("power_W", Float.parseFloat(rs.getString("power_W")));
                    result.add(dataMap);
                } catch (Exception e) {
                    System.err.println("Exception on getRecordsByDate parse values " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.err.println("SQLException on getRecordsByDate " + e.getMessage());
        } finally {
            this.close(conn);
        }
        return result;
    }

    public List<Map<String, Object>> getSumWattByDates(Set<String> dates) {
        JSONArray datesJson = new JSONArray(dates);
        String datesStr = datesJson.toString();
        datesStr = datesStr.substring(1, datesStr.length() - 1);
        String query = "SELECT date, ROUND(SUM(yield_today_Wh),2) AS watt_hours FROM powers WHERE date IN ("+datesStr+") GROUP BY date";
        List<Map<String, Object>> result = new ArrayList<>();
        Connection conn = null;
        try {
            conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                try {
                    Map<String, Object> dataMap = new HashMap<>();
                    dataMap.put("date", LocalDate.parse(rs.getString("date")));
                    dataMap.put("watt_hours", Float.parseFloat(rs.getString("watt_hours")));
                    result.add(dataMap);
                } catch (Exception e) {
                    System.err.println("Exception on getSumWattByDates parse values " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.err.println("SQLException on getSumWattByDates " + e.getMessage());
        } finally {
            this.close(conn);
        }
        return result;
    }

    public float getTotalSumWattsHours() {
        String query = "SELECT yield_total_Wh AS total_watt_hours FROM powers ORDER BY date DESC LIMIT 1";
        float result = 0;
        Connection conn = null;
        try {
            conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                try {
                    result = Float.parseFloat(rs.getString("total_watt_hours"));
                } catch (Exception e) {
                    System.err.println("Exception on getTotalSumWattsHours parse values " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.err.println("SQLException on getTotalSumWattsHours " + e.getMessage());
        } finally {
            this.close(conn);
        }
        return result;
    }

    public void createNewDatabase() {
        String path = System.getProperty("user.dir") + File.separator + this.fileName;
        boolean isDbExists = new File(path).exists();
        if (!isDbExists) {
            Connection conn = null;
            try {
                conn = this.connect();
                if (conn != null) {
                    DatabaseMetaData meta = conn.getMetaData();
                    System.out.println("The driver name is " + meta.getDriverName());
                    System.out.println("A new " + fileName + " database has been created.");
                    this.createTables();
                }
            } catch (SQLException e) {
                System.err.println("SQLException on createNewDatabase " + e.getMessage());
            } finally {
                this.close(conn);
            }
        }

    }

    private void createTables() {
        String energy = "CREATE TABLE IF NOT EXISTS energy (\n"
            + "	energy_id   INTEGER PRIMARY KEY,\n"
            + "	date        TEXT,\n"
            + "	datetime    TEXT,\n"
            + "	voltage_mV  INTEGER,\n"
            + "	current_mA  INTEGER\n"
            + ");";
        String controllers = "CREATE TABLE IF NOT EXISTS controllers (\n"
            + "	controller_id       INTEGER PRIMARY KEY,\n"
            + "	date                TEXT,\n"
            + "	datetime            TEXT,\n"
            + "	charge_state        TEXT,\n"
            + "	error               TEXT\n"
            + ");";
        String panels = "CREATE TABLE IF NOT EXISTS panels (\n"
            + "	panel_id      INTEGER PRIMARY KEY,\n"
            + "	date          TEXT,\n"
            + "	datetime      TEXT,\n"
            + "	voltage_mV    INTEGER,\n"
            + "	power_W       INTEGER\n"
            + ");";
        String powers = "CREATE TABLE IF NOT EXISTS powers (\n"
            + "	power_id            INTEGER PRIMARY KEY,\n"
            + "	date                TEXT UNIQUE,\n"
            + "	maximum_power_today_W INTEGER,\n"
            + "	yield_today_Wh      INTEGER,\n"
            + "	yield_total_Wh      INTEGER\n"
            + ");";
        Connection conn = null;
        try {
            conn = this.connect();
            Statement stmt = conn.createStatement();
            // create a new table
            stmt.execute(energy);
            stmt.execute(controllers);
            stmt.execute(panels);
            stmt.execute(powers);
            System.out.println("Database schema has been created");
        } catch (SQLException e) {
            System.err.println("SQLException on createTables " + e.getMessage());
        } finally {
            this.close(conn);
        }
    }

    private void close(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private Connection connect() {
        String url = "jdbc:sqlite:" + System.getProperty("user.dir") + File.separator + this.fileName;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.err.println("SQLException on connect " + e.getMessage());
        }
        return conn;
    }

    // public int updateRecord(String datetime, String sf_id) {
    //     String query = "UPDATE energy " +
    //             "SET sf_id = ? " +
    //             "WHERE datetime = ?";
    //     int result = 0;
    //     try (Connection conn = this.connect();
    //          PreparedStatement pstmt = conn.prepareStatement(query)) {
    //         pstmt.setString(1, sf_id);
    //         pstmt.setString(2, datetime);
    //         result = pstmt.executeUpdate();
    //         if (result > 0) {
    //             System.out.println(datetime + " record has been updated.");
    //         } else {
    //             System.out.println("Result for update " + datetime + " record - was not updated");
    //         }
    //     } catch (SQLException e) {
    //         System.err.println("SQLException on insertRecord "+e.getMessage());
    //     }
    //     return result;
    // }

    // public List<Map<String, Object>> getRecordsToSyncSF() {
    //     String query = "SELECT * FROM energy WHERE sf_id IS NULL";
    //     List<Map<String, Object>> result = new ArrayList<>();
    //     try (Connection conn = this.connect();
    //          PreparedStatement pstmt = conn.prepareStatement(query)) {
    //         ResultSet rs = pstmt.executeQuery();
    //         // loop through the result set
    //         while (rs.next()) {
    //             try {
    //                 Map<String, Object> dataMap = new HashMap<>();
    //                 dataMap.put("datetime", LocalDateTime.parse(rs.getString("datetime")));
    //                 dataMap.put("date", LocalDate.parse(rs.getString("date")));
    //                 dataMap.put("voltage_mV", Float.parseFloat(rs.getString("voltage_mV")));
    //                 dataMap.put("current_mA", Float.parseFloat(rs.getString("current_mA")));
    //                 result.add(dataMap);
    //             } catch (Exception e) {
    //                 System.err.println("Exception on getRecordsToSyncSF parse values "+e.getMessage());
    //             }
    //         }
    //     } catch (SQLException e) {
    //         System.err.println("SQLException on getRecordsToSyncSF "+e.getMessage());
    //     }
    //     return result;
    // }
}
