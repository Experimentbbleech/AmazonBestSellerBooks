/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package amazonPrimeVideoMovies;

import static amazonbestsellers.AmazonBestScrapeMandeep.waitForJSandJQueryToLoad;
import connectionManager.MyConnection;
import connectionManager.Utility;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 *
 * @author Khushbu
 */
public class AmazonPrimeVideoDetailScraping {

    static String chromePath = "C:\\Users\\Khushbu\\Downloads\\chromedriver_win32(2)\\chromedriver.exe";

    public static void main(String[] args) {
        if (args.length != 0) {
            chromePath = args[0];
        }
        startCrawler();
    }

    private static void startCrawler() {

        String selectQ = "SELECT prime_movie_id,url FROM amazon.prime_movies_links where is_scraped=0 and category='Special Interest';";
        MyConnection.getConnection("amazon");
        ResultSet rs = MyConnection.getResultSet(selectQ);
        System.setProperty("webdriver.chrome.driver", chromePath);

        ChromeOptions options = new ChromeOptions();
         options.addArguments("--headless");

        ChromeDriver driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        try {
            while (rs.next()) {

                String url = rs.getString("url");
                int id = rs.getInt("prime_movie_id");
                url = StringUtils.substringBefore(url, "/ref=");
                System.out.println("->" + url);
                detailScrape(url, id, driver);
              
            }
        } catch (SQLException ex) {
            Logger.getLogger(AmazonPrimeVideoDetailScraping.class.getName()).log(Level.SEVERE, null, ex);
        }  finally {
            driver.close();
        }
    }

    private static void detailScrape(String url, int id, ChromeDriver driver) {

        try {
            driver.get(url);
            
            waitForJSandJQueryToLoad(driver);
            Thread.sleep(3000);
            String movieTitle = "";
            String genre = "";
            String durantion = "";
            String audio = "";
            String subtitle = "";
            String year = "";
            String IMDBrating = "";
            
            Document doc = Jsoup.parse(driver.getPageSource());
            if (doc.getElementsByAttributeValue("data-automation-id", "title").isEmpty()) {
                System.out.println("Not movie link...");
                String updateQ = "update amazon.prime_movies_links set is_scraped=-1 where prime_movie_id=" + id;
                MyConnection.insertData(updateQ);
                return;
            }
            movieTitle = doc.getElementsByAttributeValue("data-automation-id", "title").first().text();
            genre = StringUtils.substringBetween(doc.html(), "Genres</span>", "</dl>");
            genre = Utility.html2text(genre);
            
            audio = StringUtils.substringBetween(doc.html(), "Audio languages</span>", "</dl>");
            audio = Utility.html2text(audio);
            audio = audio.replace("more…", "");
            subtitle = StringUtils.substringBetween(doc.html(), "Subtitles</span>", "</dl>");
            subtitle = Utility.html2text(subtitle);
            subtitle = subtitle.replace("more…", "");
            
            if (!doc.getElementsByAttributeValue("data-automation-id", "runtime-badge").isEmpty()) {
                durantion = doc.getElementsByAttributeValue("data-automation-id", "runtime-badge").first().text();
            }
            if (!doc.getElementsByAttributeValue("data-automation-id", "imdb-rating-badge").isEmpty()) {
                IMDBrating = doc.getElementsByAttributeValue("data-automation-id", "imdb-rating-badge").first().text();
            }
            if (!doc.getElementsByAttributeValue("data-automation-id", "release-year-badge").isEmpty()) {
                year = doc.getElementsByAttributeValue("data-automation-id", "release-year-badge").first().text();
            }
            //    String content = "\"" + movieTitle + "\",\"" + genre + "\",\"" + durantion + "\",\"" + audio + "\",\"" + subtitle + "\"";
            //   System.out.println(content);
            String insertQ = "INSERT INTO `amazon`.`prime_movie_data`\n"
                    + "("
                    + "`title`,\n"
                    + "`genre`,\n"
                    + "`audio`,\n"
                    + "`duration`,\n"
                    + "`subtitle`,\n"
                    + "`year`,\n"
                    + "`IMDB_rating`,\n"
                    + "`link_id`)\n"
                    + "VALUES\n"
                    + "("
                    + "'" + Utility.prepareString(movieTitle) + "',"
                    + "'" + Utility.prepareString(genre) + "',"
                    + "'" + Utility.prepareString(audio) + "',"
                    + "'" + Utility.prepareString(durantion) + "',"
                    + "'" + Utility.prepareString(subtitle) + "',"
                    + "'" + Utility.prepareString(year) + "',"
                    + "'" + Utility.prepareString(IMDBrating) + "',"
                    + id
                    + ")";
            
            MyConnection.getConnection("amazon");
            if (MyConnection.insertData(insertQ)) {
                String updateQ = "update amazon.prime_movies_links set is_scraped=1 where prime_movie_id=" + id;
                MyConnection.insertData(updateQ);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(AmazonPrimeVideoDetailScraping.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
