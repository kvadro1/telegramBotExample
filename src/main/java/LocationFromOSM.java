import localization.Locals;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

//how it's work https://nominatim.org/release-docs/develop/api/Reverse/
public class LocationFromOSM {
    private static CloseableHttpClient httpClient = HttpClients.createDefault();

    public static String getLocationAddrFromCoord(String lat, String lon, Locals locals) {
        String result = null;
        JSONObject jsonObject = null;
        Logger logger = LoggerFactory.getLogger(LocationFromOSM.class);
        try {
            String local = locals.toString().toLowerCase();
            logger.info("Try get location from OSM");
            HttpGet httpGet = new HttpGet("https://nominatim.openstreetmap.org/reverse?");
            URI uri = new URIBuilder(httpGet.getURI()).
                    addParameter("format", "json").
                    addParameter("lat", lat).
                    addParameter("lon", lon).
                    addParameter("accept-language", local).
                    addParameter("addressdetails", "14").
                    build();
            httpGet.setURI(uri);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            Header headers = entity.getContentType();
            if (entity != null && response.getStatusLine().getStatusCode() == 200) {
                String retSrc = EntityUtils.toString(entity);
                jsonObject = new JSONObject(retSrc);
                result = jsonObject.getJSONObject("address").getString("city");
            }
        } catch (Exception e) {
            logger.error("Error OSM get location: ", e);
        }
        return result;
    }
}
