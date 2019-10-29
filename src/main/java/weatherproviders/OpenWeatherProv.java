package weatherproviders;

import configs.ConfigReader;
import data_ent.ForeCastData;
import data_ent.enums.WindDirEn;
import localization.Locals;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
//get forecat from https://openweathermap.org/api

public class OpenWeatherProv implements WeatherProvider {
    private static Logger logger = LoggerFactory.getLogger(OpenWeatherProv.class);
    private CloseableHttpClient httpClient = HttpClients.createDefault();
    private String lat;
    private String lon;
    private Locals locals;

    public OpenWeatherProv(String lat, String lon, Locals locals) {
        this.lat = lat;
        this.lon = lon;
        this.locals = locals;
    }

    //get wheather for this day just parsing JSONObject
    public ForeCastData getWeatherToDay() {
        ForeCastData result = new ForeCastData();
        try {
            JSONObject get = getData(false);
            JSONObject jsO = get.getJSONObject("main");
            result.setProvider(ProvEnum.OPENWEATHER);
            result.setTemp(jsO.getInt("temp"));
            jsO = get.getJSONObject("wind");
            int speedWind = jsO.getInt("speed");
            result.setWind_speed(speedWind);
            if (speedWind > 1) {
                WindDirEn wd = WindDirEn.getEnumFromDegrees(jsO.getInt("deg"));
                result.setWind_dir(wd);
            }
            String descritp = ((JSONObject) (get.getJSONArray("weather").get(0))).getString("description");
            result.setWeatherDescription(descritp);
        } catch (Exception e) {
            logger.error("Error getting today weather OpenWeather", e);
        }
        return result;
    }

    //get forecast just parsing JSONObject
    public List<ForeCastData> getForecats() {
        List<ForeCastData> result = new ArrayList<ForeCastData>();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d hh:m:s");
            JSONObject get = getData(true);
            JSONArray array = get.getJSONArray("list");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jObj = (JSONObject) array.get(i);
                ForeCastData element = new ForeCastData();
                element.setProvider(ProvEnum.OPENWEATHER);
                Date date = sdf.parse(jObj.getString("dt_txt"));
                element.setDate(date);
                element.setTemp_min(jObj.getJSONObject("main").getInt("temp_min"));
                element.setTemp_max(jObj.getJSONObject("main").getInt("temp_max"));
                JSONObject weather = (JSONObject) (jObj.getJSONArray("weather").get(0));
                element.setWeatherDescription(weather.getString("description"));
                WindDirEn windDirEn = WindDirEn.getEnumFromDegrees(jObj.getJSONObject("wind").getInt("deg"));
                element.setWind_dir(windDirEn);
                element.setWind_speed(jObj.getJSONObject("wind").getInt("speed"));
                result.add(element);

            }
        } catch (Exception e) {
            logger.error("Error getting forecast OpenWeather", e);
        }
        return result;
    }

    //get JSONObject for today or forecast
    private JSONObject getData(boolean forecast) {
        JSONObject result = null;
        String token = ConfigReader.getInstance().getProp("OpenWeatherKey");
        try {
            HttpGet httpGet = null;
            if (forecast) {
                httpGet = new HttpGet("https://api.openweathermap.org/data/2.5/forecast?");
            } else {
                httpGet = new HttpGet("https://api.openweathermap.org/data/2.5/weather?");
            }

            String localAnswer = locals.toString().toLowerCase();

            URI uri = new URIBuilder(httpGet.getURI()).
                    addParameter("lat", lat).
                    addParameter("lon", lon).
                    addParameter("lang", localAnswer).
                    addParameter("units", "metric").
                    addParameter("APPID", token).
                    build();
            httpGet.setURI(uri);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            Header headers = entity.getContentType();
            if (entity != null && response.getStatusLine().getStatusCode() == 200) {
                String retSrc = EntityUtils.toString(entity);
                result = new JSONObject(retSrc);
            }
        } catch (Exception e) {
            logger.error("Error getting JSON data weather OpenWeather", e);
        }
        return result;
    }
}
