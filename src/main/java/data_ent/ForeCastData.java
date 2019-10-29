package data_ent;

import data_ent.enums.WindDirEn;
import weatherproviders.ProvEnum;

import java.util.Date;

//this is part of dataset from there
//https://yandex.ru/dev/weather/doc/dg/concepts/forecast-test-docpage/#req-example

public class ForeCastData {
    private Date date;
    //    temp
    private int temp;
    //    feels_like
    private int feels_like;
    // temp_min
    private int temp_min;
    // temp_max
    private int temp_max;
    //    temp_water
    private int temp_water;
    //    icon
    private String icon;
    //    wind_speed
    private int wind_speed;
    //    wind_gust
    private int wind_gust;
    //    wind_dir
    private WindDirEn wind_dir;
    //    pressure_mm
    private int pressure_mm;
    //    pressure_pa
    private int pressure_pa;
    //    humidity
    private int humidity;
    //    polar
    private boolean polar;
    //    obs_time
    private String obs_time;
    //    prec_type
    private int prec_type;
    private int prec_prob;
    private String weatherDescription;

    private float prec_strength;

    private float cloudness;

    private ProvEnum provider;

    public ProvEnum getProvider() {
        return provider;
    }

    public void setProvider(ProvEnum provider) {
        this.provider = provider;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public int getFeels_like() {
        return feels_like;
    }

    public void setFeels_like(int feels_like) {
        this.feels_like = feels_like;
    }

    public int getTemp_water() {
        return temp_water;
    }

    public void setTemp_water(int temp_water) {
        this.temp_water = temp_water;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }


    public int getWind_speed() {
        return wind_speed;
    }

    public void setWind_speed(int wind_speed) {
        this.wind_speed = wind_speed;
    }

    public int getWind_gust() {
        return wind_gust;
    }

    public void setWind_gust(int wind_gust) {
        this.wind_gust = wind_gust;
    }

    public WindDirEn getWind_dir() {
        return wind_dir;
    }

    public void setWind_dir(WindDirEn wind_dir) {
        this.wind_dir = wind_dir;
    }

    public int getPressure_mm() {
        return pressure_mm;
    }

    public void setPressure_mm(int pressure_mm) {
        this.pressure_mm = pressure_mm;
    }

    public int getPressure_pa() {
        return pressure_pa;
    }

    public void setPressure_pa(int pressure_pa) {
        this.pressure_pa = pressure_pa;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }


    public boolean isPolar() {
        return polar;
    }

    public void setPolar(boolean polar) {
        this.polar = polar;
    }


    public String getObs_time() {
        return obs_time;
    }

    public void setObs_time(String obs_time) {
        this.obs_time = obs_time;
    }

    public int getPrec_type() {
        return prec_type;
    }

    public void setPrec_type(int prec_type) {
        this.prec_type = prec_type;
    }

    public float getPrec_strength() {
        return prec_strength;
    }

    public void setPrec_strength(float prec_strength) {
        this.prec_strength = prec_strength;
    }

    public float getCloudness() {
        return cloudness;
    }

    public void setCloudness(float cloudness) {
        this.cloudness = cloudness;
    }

    public int getTemp_min() {
        return temp_min;
    }

    public void setTemp_min(int temp_min) {
        this.temp_min = temp_min;
    }

    public int getTemp_max() {
        return temp_max;
    }

    public void setTemp_max(int temp_max) {
        this.temp_max = temp_max;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getPrec_prob() {
        return prec_prob;
    }

    public void setPrec_prob(int prec_prob) {
        this.prec_prob = prec_prob;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public void setWeatherDescription(String weatherDescription) {
        this.weatherDescription = weatherDescription;
    }

}
