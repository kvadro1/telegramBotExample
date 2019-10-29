package weatherproviders;

import data_ent.ForeCastData;

import java.util.List;

//interface for weather providers
public interface WeatherProvider {
    ForeCastData getWeatherToDay();

    List<ForeCastData> getForecats();

}
