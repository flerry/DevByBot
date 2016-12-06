import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class ParseSalaries extends Thread {
    @Override
    public void run() {
        TgBot msgBridge = new TgBot();
        String url = "https://salaries.dev.by";
        StringBuilder returnSalaries = new StringBuilder();
        Document mainPage = null;
        try {
            mainPage = Jsoup.connect(url).timeout(3000).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert mainPage != null;
        Elements input_Shdw = mainPage.select("div[class=input shdw]");
        List<String> salaries = new ArrayList<>();
        for (Element a : input_Shdw) {
            salaries.add(a.select("span").text());
            salaries.add(a.select("strong").text());

        }
        for (String i : salaries) {
            returnSalaries.append(i).append("\n");
        }
        returnSalaries.append("Подробнее: https://salaries.dev.by");
        msgBridge.sendMsgCustomUser(TgBot.chatId, returnSalaries.toString());

    }
}