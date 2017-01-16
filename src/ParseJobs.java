import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

class ParseJobs {
    public static StringBuffer parseJobs(String direction) {
        String url = "https://jobs.dev.by/?utf8=✓&search-jobs%5Btoken%5D=" + direction + "&commit=&search-jobs%5Buser_type_of_activity%5D=&search-jobs%5Buser_level%5D=&search-jobs%5Bcity%5D=&search-jobs%5Bproject_exist%5D=0";
        StringBuffer returnJobs = new StringBuffer();
        Document mainPage = null;
        try {
            mainPage = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert mainPage != null;
        Elements jobs = mainPage.select("h3");
        for (Element i : jobs) {
            returnJobs.append(i.select("a[href]").attr("title")).append("\n");
            returnJobs.append(i.select("a[href]").attr("abs:href")).append("\n");
        }
        returnJobs.append("*Подробнее*: https://jobs.dev.by");
        return returnJobs;
    }
}
