package com.demo.model;

import org.json.JSONObject;

public class MpptData {
    private String PID;
    private int FW;
    private String SER;
    private int V;
    private int I;
    private int VPV;
    private int PPV;
    private int CS;
    private int ERR;
    private String LOAD;
    private int IL;
    private int H19;
    private int H20;
    private int H21;
    private int H22;
    private int H23;
    private int HSDS;
    private int Checksum;

    public MpptData(String data) {
        JSONObject jsonObject = null; 
        try {
            jsonObject = new JSONObject(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (jsonObject != null) {
            this.PID = jsonObject.getString("PID");
            this.FW = jsonObject.getInt("FW");
            this.SER = jsonObject.getString("SER");
            this.V = jsonObject.getInt("V");
            this.I = jsonObject.getInt("I");
            this.VPV = jsonObject.getInt("VPV");
            this.PPV = jsonObject.getInt("PPV");
            this.CS = jsonObject.getInt("CS");
            this.ERR = jsonObject.getInt("ERR");
            this.LOAD = jsonObject.getString("LOAD");
            this.IL = jsonObject.getInt("IL");
            this.H19 = jsonObject.getInt("H19");
            this.H20 = jsonObject.getInt("H20");
            this.H21 = jsonObject.getInt("H21");
            this.H22 = jsonObject.getInt("H22");
            this.H23 = jsonObject.getInt("H23");
            this.HSDS = jsonObject.getInt("HSDS");
            this.Checksum = jsonObject.getInt("Checksum");
        }
    }

    public int getBatteryVoltage() {
        return this.V;
    }

    public int getBatteryCurrent() {
        return this.I;
    }

    public int getPanelVoltage() {
        return this.VPV;
    }

    public int getPanelPower() {
        return this.PPV;
    }

    public int getYieldTotal() {
        return this.H19;
    }

    public int getYieldToday() {
        return this.H20;
    }

    public int getMaximumPowerToday() {
        return this.H21;
    }

    public int getChargeState() {
        return this.CS;
    }

    public int getErrorCode() {
        return this.ERR;
    }

    @Override
    public String toString() {
        return "MpptData [PID=" + PID + ", FW=" + FW + ", SER=" + SER + ", V=" + V + ", I=" + I + ", VPV=" + VPV
                + ", PPV=" + PPV + ", CS=" + CS + ", ERR=" + ERR + ", LOAD=" + LOAD + ", IL=" + IL + ", H19=" + H19
                + ", H20=" + H20 + ", H21=" + H21 + ", H22=" + H22 + ", H23=" + H23 + ", HSDS=" + HSDS + ", Checksum="
                + Checksum + "]";
    }
    
}
