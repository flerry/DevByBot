import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

class NewsSubscribe extends Thread {
    public static final Set<Long> subscribeIDUser = new LinkedHashSet<>();
    private String checkUpdateLink = "any_link";
    private final TgBot sendNews = new TgBot();

    @Override
    public void run() {
        String url = "https://dev.by/lenta";
        while (true) {
            Document mainPage = null;
            try {
                mainPage = Jsoup.connect(url).timeout(30000).get();

                Element itemRateLinear = mainPage.select("div[class=item-rate_linear]").first();

                Element itemTitleLinear = mainPage.select("h3[class=item-title_linear]").first();

                Elements itemRateLinks = itemRateLinear.select("a[href]");


                String articleLink = itemRateLinks.attr("abs:href");
                String title = itemTitleLinear.text();

                if (!checkUpdateLink.contains(articleLink)) {
                    for (Long i : subscribeIDUser)
                        if (i != 0) {
                            sendNews.sendMsgCustomUser(i, title + "\n" + articleLink);
                            checkUpdateLink = articleLink;
                        } else {
                            try {
                                Thread.sleep(120000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                } else {
                    try {
                        Thread.sleep(120000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
