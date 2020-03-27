package tech.tanaysinghania.interviewapp;

public class ScheduledData {
    public ScheduledData(String time, String ee, String er) {
        this.time = time;
        this.ee = ee;
        this.er = er;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getEe() {
        return ee;
    }

    public void setEe(String ee) {
        this.ee = ee;
    }

    public String getEr() {
        return er;
    }

    public void setEr(String er) {
        this.er = er;
    }

    private String time;
    private String ee;
    private String er;

}
