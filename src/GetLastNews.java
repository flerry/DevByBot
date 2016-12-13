import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

class GetLastNews {
    public static void getLastNews() {
        TgBot msgBridge = new TgBot();
        String urlMainPage = "https://dev.by";
        String url = "https://dev.by/lenta";
        Document lentaPage;
        Document mainPage;
        try {
            lentaPage = Jsoup.connect(url).timeout(30000).get();
            mainPage = Jsoup.connect(urlMainPage).timeout(30000).get();

            Element itemRateLinear = lentaPage.select("div[class=item-rate_linear]").first();

            Element itemTitleLinear = lentaPage.select("h3[class=item-title_linear]").first();


            Element articlePreviewTitle = mainPage.select("h2[class=article-preview__title]").first();

            Elements elementPublicLink = articlePreviewTitle.select("a[href]");

            String publicLink = elementPublicLink.attr("abs:href");


            String title = itemTitleLinear.text();

            msgBridge.sendMsgCustomUser(TgBot.chatId, title + "\n" + publicLink);


        } catch (IOException e) {
            msgBridge.sendMsgCustomUser(TgBot.chatId, "Извините, сайт не ответил на запрос...");
        }
    }
}


