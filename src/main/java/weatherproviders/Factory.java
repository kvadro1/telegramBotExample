package weatherproviders;

import localization.Locals;

//for create data provider by weather provider
public class Factory {

    static public WeatherProvider getProv(ProvEnum provNeed, String lat, String lon, Locals locals) {
        switch (provNeed) {
            case YANDEX: {
                return new YandexProv(lat, lon);
            }
            case OPENWEATHER: {
                return new OpenWeatherProv(lat, lon, locals);
            }
        }
        return null;
    }
}
