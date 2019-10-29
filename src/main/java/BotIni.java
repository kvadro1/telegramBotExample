import configs.ConfigReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;

//start bot
public class BotIni {

    private static final String PROXY_HOST;
    private static final int PROXY_PORT;
    private static Logger logger = LoggerFactory.getLogger(BotIni.class);

    static {
        //get config from config.properties
        PROXY_HOST = ConfigReader.getInstance().getProp("ProxyHostName");
        PROXY_PORT = ConfigReader.getInstance().getPropI("ProxyPort");
    }

    public static void main(String[] args) {
        StratBotRecur();
    }

    private static void StratBotRecur() {
        //i know i can catch stack overflow it's just example of bot starting
        try {
            //logger.info(BotIni.class.getPackage().getName());
            logger.info("Initializing API context...");
            ApiContextInitializer.init();

            TelegramBotsApi botsApi = new TelegramBotsApi();

            logger.info("Configuring bot options...");
            DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);
            if (PROXY_HOST != null) {
                botOptions.setProxyHost(PROXY_HOST);
                botOptions.setProxyPort(PROXY_PORT);
            }
            botOptions.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);

            logger.info("Registering Bot...");
            TelegramLongPollingCommandBot bot = new TelegramBot(botOptions);

            botsApi.registerBot(bot);

            logger.info("Bot is ready for work!");

        } catch (Exception e) {
            try {
                Thread.sleep(120000);
            } catch (InterruptedException z) {

            }
            logger.error("Error while initializing bot!" + e);
            logger.info("Try reconnect");
            StratBotRecur();
        }
    }
}
