import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class ParseEvent extends Thread {
    @Override
    public void run() {
        TgBot msgBridge = new TgBot();
        String url = "https://events.dev.by";
        StringBuilder returnEvent = new StringBuilder();
        Document mainPage = null;
        try {
            mainPage = Jsoup.connect(url).timeout(6000).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert mainPage != null;
        Elements itemBodyLeft = mainPage.select("div[class=item-body left]");
        Elements itemBodyLinks = itemBodyLeft.select("a[href]");
        List<String> events = new ArrayList<>();
        for (Element i : itemBodyLinks) {
            events.add(i.attr("abs:href") + "\n");
            events.add(i.attr("title"));
            events.add("\n");
        }
        for (String i : events) {
            if (!i.contains("google.com") && !i.contains("calendar.ics")) {
                returnEvent.append(i);
            }
        }
        returnEvent.append("Подробнее: https://events.dev.by");
        msgBridge.sendMsgCustomUser(TgBot.chatId, returnEvent.toString());

    }


}
