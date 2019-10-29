import configs.ConfigReader;
import data_ent.ForeCastData;
import localization.Locals;
import localization.LocationStrings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import weatherproviders.Factory;
import weatherproviders.ProvEnum;

import java.text.SimpleDateFormat;
import java.util.*;

//class work with telegram bot API through that library API https://github.com/rubenlagus/TelegramBots
public class TelegramBot extends TelegramLongPollingCommandBot {
    private static final String BOT_NAME;
    private static final String BOT_TOKEN;
    private static Logger logger = LoggerFactory.getLogger(TelegramBot.class);
    private static HashMap<Long, String[]> usersArray = new HashMap<Long, String[]>();

    static {
        //get bot token and bot name from config.properties
        BOT_NAME = ConfigReader.getInstance().getProp("BotName");
        BOT_TOKEN = ConfigReader.getInstance().getProp("BotToken");
    }

    public TelegramBot(DefaultBotOptions options) {
        super(options, BOT_NAME);
    }

    public String getBotToken() {
        return BOT_TOKEN;
    }

    public void processNonCommandUpdate(Update update) {
    }

    //take for processing messages to bot
    public void onUpdatesReceived(List<Update> updates) {
        //get class for localizations string
        LocationStrings local = LocationStrings.getInstance();
        //default location EN
        Locals userLocation = Locals.EN;
        for (Update update : updates) {
            logger.info("Full user message:" + update.toString());
            //remember chatID user
            Long chatID = update.getMessage().getChatId();
            if (update.hasMessage()) {
                String inString = update.getMessage().getText();
                //get language from user message
                userLocation = getUserLanguage(update.toString());
                logger.info("Message text form user: " + inString);
                if (update.getMessage().hasLocation()) {//if message content location
                    String lat = update.getMessage().getLocation().getLatitude().toString();
                    String lon = update.getMessage().getLocation().getLongitude().toString();
//                    if (usersArray.containsKey(chatID))
//                        usersArray.replace(chatID, new String[]{lat, lon});
//                    else
                    usersArray.put(chatID, new String[]{lat, lon});//remember user
                    sendKeyboardMessage(chatID, false, userLocation);//send user key for send coordinate
                    return;
                } else {
                    if ((inString.contains("Yandex") || inString.contains("Weather")) && usersArray.containsKey(chatID)) {//user push on of providers key
                        String lat = usersArray.get(chatID)[0];
                        String lon = usersArray.get(chatID)[1];
                        sendMess(local.getS("whait_data", userLocation), chatID);
                        if (inString.contains("Yandex")) {//get data from checked provider
                            sendForecast(chatID, lat, lon, ProvEnum.YANDEX, userLocation);
                        }
                        if (inString.contains("Open Weather")) {
                            sendForecast(chatID, lat, lon, ProvEnum.OPENWEATHER, userLocation);
                        }
                        sendKeyboardMessage(chatID, true, userLocation);//send user key for send coordinate
                    } else {
                        sendKeyboardMessage(chatID, true, userLocation);//send user key for send coordinate
                    }
                }

            }
        }
    }

    //send simple text message for user
    private void sendMess(String message, Long chatId) {
        logger.info("Send short message: " + message);
        if (message == null || message.length() == 0)
            return;
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId).setText(message);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            logger.error("Error send short message: ", e);
        }
    }

    //send long message
    private void sendMess(List<String> messageList, Long chatId) {
        logger.info("Send long message");
        try {
            if (messageList == null || messageList.size() == 0) {
                return;
            }
            if (messageList.size() < 200) { //if result message has more 200 lines
                //sbMessage.append(messageList);
                StringBuilder sbMessage = new StringBuilder();
                for (String s : messageList) {
                    sbMessage.append(s);
                }
                SendMessage sendMessage = new SendMessage().
                        setChatId(chatId).setText(sbMessage.toString());
                execute(sendMessage);
            } else {//divide into messages of 200 lines and sends them
                int stringCont = 0;
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < messageList.size(); i++) {
                    sb.append(messageList.get(i));
                    stringCont++;
                    if (stringCont == 200) {
                        SendMessage sendMessage = new SendMessage().
                                setChatId(chatId).
                                setText(sb.toString());
                        stringCont = 0;
                        sb.setLength(0);
                        sb.trimToSize();
                        execute(sendMessage);
                    } else {
                        if (i == messageList.size() - 1) {
                            SendMessage sendMessage = new SendMessage().
                                    setChatId(chatId).
                                    setText(sb.toString());
                            execute(sendMessage);
                        }
                    }
                }
            }
        } catch (TelegramApiException e) {
            logger.error("Error send long message: ", e);
        }
    }

    //send keys (location or providers keys)
    private void sendKeyboardMessage(Long chatId, boolean sendLocation, Locals locals) {
        LocationStrings local = LocationStrings.getInstance();
        if (sendLocation)
            logger.info("Send location key to user");
        else
            logger.info("Send providers key to user");
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keysList = new ArrayList<KeyboardRow>();
        KeyboardRow keyboardButtons = new KeyboardRow();
        String messageText = null;
        if (!sendLocation) {
            messageText = local.getS("forecast_provider", locals);
            KeyboardButton yK = new KeyboardButton(String.
                    format(local.getS("weather_from", locals), "Yandex"));
            KeyboardButton oK = new KeyboardButton(String.format(local.getS("weather_from", locals), "Open Weather"));
            keyboardButtons.add(yK);
            keyboardButtons.add(oK);
        } else {
            KeyboardButton locationKey = new KeyboardButton(local.getS("send_loc_key", locals)).
                    setRequestLocation(true);
            keyboardButtons.add(locationKey);
            messageText = local.getS("send_loc", locals);
        }
        keysList.add(keyboardButtons);
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(!sendLocation);
        keyboardMarkup.setKeyboard(keysList);
        SendMessage message = new SendMessage()
                .setChatId(chatId).setReplyMarkup(keyboardMarkup).setText(messageText);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Error send keyboard ", e);
        }
    }

    //form answer with forecast for selected provider
    private void sendForecast(Long chatId, String lat, String lon, ProvEnum prov, Locals locals) {
        LocationStrings local = LocationStrings.getInstance();
        try {
            List<String> answerText = new ArrayList<String>();
            answerText.add(local.getS("your_location", locals) + "\n");
            answerText.add("lat:" + lat + " lon:" + lon + "\n");
            String location = LocationFromOSM.getLocationAddrFromCoord(lat, lon, locals);
            if (location != null)
                answerText.add(location + "\n");
            //get now data
            ForeCastData toDayWeather = Factory.getProv(prov, lat, lon, locals).getWeatherToDay();
            answerText.add("-----------------------\n");
            answerText.add(local.getS("weather_now", locals) + "\n");
            answerText.add(String.format(local.getS("temper", locals), toDayWeather.getTemp()));
            answerText.add(String.format(local.getS("wind_speed", locals), toDayWeather.getWind_speed()));
            if (prov == ProvEnum.YANDEX)
                answerText.add(local.getS("wind_dir", locals) + " "
                        + local.getS(toDayWeather.getWind_dir().toString(), locals) +
                        "\n");
            else if (toDayWeather.getWind_speed() > 1)
                answerText.add(local.getS("wind_dir", locals) + " " +
                        local.getS(toDayWeather.getWind_dir().toString(), locals) +
                        "\n");
            if (prov == ProvEnum.YANDEX)
                answerText.add(String.format(local.getS("feeling", locals),
                        toDayWeather.getFeels_like()));
            answerText.add("-----------------------\n");
            //get forecast
            List<ForeCastData> forecastsList = Factory.getProv(prov, lat, lon, locals).getForecats();
            for (ForeCastData forec : forecastsList) {
                answerText.add("\n");
                Date date = forec.getDate();
                SimpleDateFormat sdf = null;
                if (prov == ProvEnum.YANDEX)
                    if (locals == Locals.RU)
                        sdf = new SimpleDateFormat("EEEE dd MMMM");
                    else
                        sdf = new SimpleDateFormat("EEEE dd MMMM", Locale.ENGLISH);
                else if (locals == Locals.RU)
                    sdf = new SimpleDateFormat("EEEE dd MMMM H:mm");
                else
                    sdf = new SimpleDateFormat("EEEE dd MMMM H:mm", Locale.ENGLISH);
                answerText.add("-----------------------\n");
                answerText.add(String.format("%s\n", upperCaseFirstChar(sdf.format(date))));
                answerText.add(String.format(local.getS("min_temp", locals), forec.getTemp_min()));
                answerText.add(String.format(local.getS("max_temp", locals), forec.getTemp_max()));
                if (prov == ProvEnum.YANDEX)
                    answerText.add(String.format(local.getS("chance_of_fall", locals), forec.getPrec_prob()));
                else
                    answerText.add(String.format("%s\n", upperCaseFirstChar(forec.getWeatherDescription())));
                if (prov == ProvEnum.YANDEX)
                    answerText.add(String.format(local.getS("wind", locals),
                            local.getS(forec.getWind_dir().toString(), locals),
                            forec.getWind_speed()));
                else if (forec.getWind_speed() > 1) {
                    answerText.add(String.format(local.getS("wind", locals),
                            local.getS(forec.getWind_dir().toString(), locals),
                            forec.getWind_speed()));
                }
            }
            sendMess(answerText, chatId);

        } catch (Exception e) {
            sendMess(local.getS("error_get_data", locals), chatId);
            logger.error("Error send forecast ", e);
        }
    }

    //get language of user from their message
    //yes i know that is not best way
    private Locals getUserLanguage(String inS) {
        Locals result = Locals.EN;
        try {
            int index = inS.indexOf("languageCode='");
            String needString = inS.
                    substring(index + "languageCode='".length()).
                    substring(0, 2).
                    toUpperCase();

            result = Locals.valueOf(needString);
        } catch (Exception e) {
            logger.error("Error parse language user: ", e);
        }
        return result;
    }

    //just uppercase first
    private String upperCaseFirstChar(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }


}
