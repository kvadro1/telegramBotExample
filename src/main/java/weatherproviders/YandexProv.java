package weatherproviders;

import configs.ConfigReader;
import data_ent.ForeCastData;
import data_ent.enums.WindDirEn;
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
import java.util.List;

//get forecat from https://yandex.ru/dev/weather/doc/dg/concepts/forecast-test-docpage/#req-example
public class YandexProv implements WeatherProvider {
    private static Logger logger = LoggerFactory.getLogger(OpenWeatherProv.class);
    private CloseableHttpClient httpClient = HttpClients.createDefault();
    private String lat;
    private String lon;


    public YandexProv(String lat, String lon) {
        this.lat = lat;
        this.lon = lon;
    }

    //get weather today
    public ForeCastData getWeatherToDay() {
        ForeCastData result = new ForeCastData();
        try {
            JSONObject get = getData();
            JSONObject jsO = get.getJSONObject("fact");
            result.setProvider(ProvEnum.YANDEX);
            result.setTemp(jsO.getInt("temp"));
            result.setWind_speed(jsO.getInt("wind_speed"));
            WindDirEn wd = WindDirEn.valueOf(jsO.getString("wind_dir").toUpperCase().replaceAll("-", ""));
            result.setWind_dir(wd);
            result.setFeels_like(jsO.getInt("feels_like"));
        } catch (Exception e) {
            logger.error("Error getting today weather Yandex", e);
        }
        return result;
    }

    //get forecast just parsing JSONObject
    public List<ForeCastData> getForecats() {
        List<ForeCastData> result = new ArrayList<ForeCastData>();
        try {
            JSONObject get = getData();
            JSONArray array = get.getJSONArray("forecasts");
            if (array != null) {
                for (Object obj : array) {
                    result.add(getForecastForOneDay((JSONObject) obj));
                }
            }

        } catch (Exception e) {
            logger.error("Error getting forecast Yandex", e);
        }
        return result;
    }

    //get JSONObject today and forecast
    private JSONObject getData() {
        JSONObject result = null;
        try {
            String token = ConfigReader.getInstance().getProp("YandexKey");
            HttpGet httpGet = new HttpGet("https://api.weather.yandex.ru/v1/forecast?");
            httpGet.addHeader("X-Yandex-API-Key", token);

            URI uri = new URIBuilder(httpGet.getURI()).
                    addParameter("lat", lat).
                    addParameter("lon", lon).
                    addParameter("lang", "ru_RU").
                    addParameter("limit", "1").
                    addParameter("extra", "false").
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
            logger.error("Error getting JSON data weather Yandex", e);
        }
        return result;
    }

    //pars forecast for one day
    private ForeCastData getForecastForOneDay(JSONObject jsonObject) {
        ForeCastData result = new ForeCastData();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        try {
            //get date
            String dateString = jsonObject.getString("date");
            result.setProvider(ProvEnum.YANDEX);
            result.setDate(sdf.parse(dateString));
            JSONObject day = jsonObject.getJSONObject("parts").getJSONObject("day");
            JSONObject night = jsonObject.getJSONObject("parts").getJSONObject("night");
            //get min temp
            result.setTemp_min(night.getInt("temp_min"));
            //get max temp
            result.setTemp_max(day.getInt("temp_max"));
            //get wind speed
            result.setWind_speed(day.getInt("wind_speed"));
            //get wind direct
            result.setWind_dir(WindDirEn.
                    valueOf(day.getString("wind_dir").
                            toUpperCase().
                            replaceAll("-", "")));
            //get prec prob percent
            result.setPrec_prob(day.getInt("prec_prob"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


}
