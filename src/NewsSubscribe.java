import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

class NewsSubscribe extends Thread {
    public static final Set<Long> subscribeIDUser = new LinkedHashSet<>();
    private String checkUpdateLink = "any_link";
    private static final String url = "https://dev.by/?publication=true";
    private final TgBot sendNews = new TgBot();

    @Override
    public void run() {
        while (true) {
            try {
                Document lentaPage = Jsoup.connect(url).timeout(30000).get();

                Element articleMedium = lentaPage.select("div[class=articles__container]").select("div[class=article article--medium]").first();

                Element ahref = articleMedium.select("a[href]").first();

                Element articleHeader = articleMedium.select("div[class=article__header]").first();


                String link = ahref.attr("abs:href");

                String title = articleHeader.text();

                String text = articleMedium.select("div[class=article__content]").text();

                if (!checkUpdateLink.contains(link)) {
                    for (Long i : subscribeIDUser)
                        if (i != 0) {
                            sendNews.sendMsgCustomUser(i, "*" + title + "*" + "\n" + "_" + text + "_" + "\n" + link);
                            checkUpdateLink = link;
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
