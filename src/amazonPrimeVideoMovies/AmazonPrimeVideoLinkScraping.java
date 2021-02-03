/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package amazonPrimeVideoMovies;

import static amazonbestsellers.AmazonBestScrapeMandeep.waitForJSandJQueryToLoad;
import connectionManager.MyConnection;
import connectionManager.Utility;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 *
 * @author Khushbu
 */
public class AmazonPrimeVideoLinkScraping {

    static String chromePath = "C:\\Users\\Khushbu\\Downloads\\chromedriver_win32(2)\\chromedriver.exe";
    static String category = "Westerns";

    public static void main(String[] args) {
        startLinkScraping();
    }

    private static void startLinkScraping() {
        System.setProperty("webdriver.chrome.driver", chromePath);

        ChromeOptions options = new ChromeOptions();

        ChromeDriver driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        //     ArrayList<String> links = new ArrayList();
        boolean hasNextPage = false;
        String catURLs[] = {
            //   "https://www.amazon.com/s?k=movies&i=instant-video&bbn=2858778011&rh=p_n_theme_browse-bin%3A2650363011%7C2650382011&dc&page=209&qid=1611829117&rnid=2650362011&ref=sr_pg_208"
            //  "https://www.amazon.com/s?k=movies&i=instant-video&bbn=2858778011&rh=p_n_theme_browse-bin%3A2650371011&dc&qid=1611985641&rnid=2650362011&ref=sr_nr_p_n_theme_browse-bin_6"
            // "https://www.amazon.com/s?k=movies&i=instant-video&bbn=2858778011&rh=p_n_theme_browse-bin%3A2650365011&dc&qid=1612073511&rnid=2650362011&ref=sr_nr_p_n_theme_browse-bin_7"
            // "https://www.amazon.com/s?k=movies&i=instant-video&bbn=2858778011&rh=p_n_theme_browse-bin%3A2676836011&dc&qid=1612196779&rnid=2650362011&ref=sr_nr_p_n_theme_browse-bin_11"
            "https://www.amazon.com/s?k=movies&i=instant-video&bbn=2858778011&rh=p_n_theme_browse-bin%3A2650382011&dc&qid=1612229748&rnid=2650362011&ref=sr_nr_p_n_theme_browse-bin_12"
        };

        //   String url = "https://www.amazon.com/s?k=movies&i=instant-video&ref=nb_sb_noss_1";
        //    String mainURL = StringUtils.substringBefore(url, "&ref");
        //   int pageno = 2;
        for (String url : catURLs) {
            do {
                System.out.println("Getting..." + url);
                driver.get(url);
                waitForJSandJQueryToLoad(driver);
                Document doc = Jsoup.parse(driver.getPageSource());
                Element div = doc.getElementsByClass("s-main-slot").first();
                for (Element e : div.getElementsByClass("s-result-item")) {
                    //  System.out.println("->"+e.html());
                    if (!e.getElementsContainingOwnText("Prime Video").isEmpty()) {
                        String movieLink = "https://www.amazon.com" + e.getElementsByTag("a").first().attr("href");
                        String asin = StringUtils.substringBetween(movieLink, "/dp/", "/");
                        if (asin == null) {
                            asin = StringUtils.substringBetween(movieLink, "%2Fdp%2F", "%2F");
                        }
                        String insertQ = "INSERT INTO `amazon`.`prime_movies_links`\n"
                                + "(\n"
                                + "`url`,\n"
                                + "`category`,\n"
                                + "`asin`)\n"
                                + "VALUES\n"
                                + "("
                                + "'" + Utility.prepareString(movieLink) + "',"
                                + "'" + Utility.prepareString(category) + "',"
                                + "'" + asin + "'"
                                + ")";
                        MyConnection.getConnection("amazon");
                        MyConnection.insertData(insertQ);
                    }
                }
                if (!doc.getElementsByClass("template=PAGINATION").isEmpty()
                        && !doc.getElementsByClass("template=PAGINATION").first().getElementsByClass("a-last").isEmpty()) {
                    if (!doc.getElementsByClass("template=PAGINATION").first().getElementsByClass("a-last").first().hasClass("a-disabled")) {
                        url = "https://www.amazon.com"
                                + doc.getElementsByClass("template=PAGINATION").first().getElementsByClass("a-last").first().getElementsByTag("a").first().attr("href");
                        // url = mainURL + "&page=" + pageno + "&qid=1611726631&ref=sr_pg_" + pageno;
                        hasNextPage = true;
                        //  pageno++;
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(AmazonPrimeVideoLinkScraping.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    } else {
                        hasNextPage = false;
                    }
                } else {
                    hasNextPage = false;
                }
            } while (hasNextPage);
        }
    }

    private static void detailScrape(String movieLink, ChromeDriver driver) {
        driver.get(movieLink);
        waitForJSandJQueryToLoad(driver);
        String movieTitle = "";
        String genre = "";
        String durantion = "";
        String audio = "";
        String subtitle = "";
        Document doc = Jsoup.parse(driver.getPageSource());
        movieTitle = doc.getElementsByClass("_1GTSsh").first().text();
        genre = StringUtils.substringBetween(doc.html(), "Genres</span>", "</dl>");
        genre = Utility.html2text(genre);

        audio = StringUtils.substringBetween(doc.html(), "Audio languages</span>", "</dl>");
        audio = Utility.html2text(audio);
        audio = audio.replace(" more…", "");
        subtitle = StringUtils.substringBetween(doc.html(), "Subtitles</span>", "</dl>");
        subtitle = Utility.html2text(subtitle);
        subtitle = subtitle.replace(" more…", "");
        if (!doc.getElementsByAttributeValue("data-automation-id", "runtime-badge").isEmpty()) {
            durantion = doc.getElementsByAttributeValue("data-automation-id", "runtime-badge").first().text();
        }
        String content = "\"" + movieTitle + "\",\"" + genre + "\",\"" + durantion + "\",\"" + audio + "\",\"" + subtitle + "\"";
        System.out.println(content);
    }
}
