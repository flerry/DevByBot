import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

class GetLastNews {
    private static final String url = "https://dev.by/?publication=true";

    public static void getLastNews() {
        TgBot msgBridge = new TgBot();
        try {
            Document lentaPage = Jsoup.connect(url).timeout(30000).get();

            Element articleMedium = lentaPage.select("div[class=articles__container]").select("div[class=article article--medium]").first();

            Element ahref = articleMedium.select("a[href]").first();

            Element articleHeader = articleMedium.select("div[class=article__header]").first();


            String link = ahref.attr("abs:href");

            String title = articleHeader.text();

            String text = articleMedium.select("div[class=article__content]").text();

            msgBridge.sendMsgCustomUser(TgBot.chatId, "*" + title + "*" + "\n" + "_" + text + "_" + "\n" + link);


        } catch (IOException e) {
            msgBridge.sendMsgCustomUser(TgBot.chatId, "Извините, сайт не ответил на запрос...");
        }
    }
}


