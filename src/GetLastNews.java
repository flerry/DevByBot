import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class GetLastNews {
    public static void getLastNews() {
        TgBot msgBridge = new TgBot();
        String url = "https://dev.by/lenta";
        Document mainPage;
        try {
            mainPage = Jsoup.connect(url).timeout(30000).get();


            Element itemRateLinear = mainPage.select("div[class=item-rate_linear]").first();

            Element itemTitleLinear = mainPage.select("h3[class=item-title_linear]").first();

            Elements itemRateLinks = itemRateLinear.select("a[href]");


            String articleLink = itemRateLinks.attr("abs:href");
            String title = itemTitleLinear.text();

            msgBridge.sendMsgCustomUser(TgBot.chatId, title + "\n" + articleLink);

        } catch (IOException e) {
            msgBridge.sendMsgCustomUser(TgBot.chatId, "Извините, сайт не ответил на запрос...");
        }
    }
}
