/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package amazonbestsellers;

import connectionManager.MyConnection;
import connectionManager.Utility;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.jsoup.Jsoup;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author Khushbu
 */
public class AmazonBestScrapeMandeep {

    /* static String chromePath = "C:\\Users\\Khushbu\\Downloads\\chromedriver_win32(2)\\chromedriver.exe";
    static String linkFilePath = "E:\\output\\output\\AmzonBestSellerBooks.csv";
    static ChromeDriver driver = null;
    static String imagePath = "E:\\output\\imgs\\";
    static final String LINE_SEPARATOR = System.getProperty("line.separator");
    static String dirPath = "E:";*/
    static String chromePath = "";
    static String linkFilePath = "";
    static ChromeDriver driver = null;
    static String imagePath = "";
    static final String LINE_SEPARATOR = System.getProperty("line.separator");
    static String dirPath = "";

    public static void main(String[] args) {
        try {
            SimpleDateFormat smt = new SimpleDateFormat("dd-MM-yy_hhmmss");
            Date dt = new Date(System.currentTimeMillis());

            if (args.length != 2) {
                System.out.println("Please enter two commandd line argument...");
                return;
            }

            dirPath = args[0];
            chromePath = args[1];
            linkFilePath = dirPath + "AmzonBestSellerBooks.csv";
            imagePath = dirPath + "\\imgs\\";

            String outputFilePath = dirPath + "\\output\\SampleData_AmazonBestSellerBooks_" + smt.format(dt) + ".csv";
            File file = new File(outputFilePath);

            try {
                file.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(AmazonBestScrapeMandeep.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.setProperty("webdriver.chrome.driver", chromePath);

            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");
            //    options.setBinary("C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe");
            System.out.println("Binary location=C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe");
            driver = new ChromeDriver(options);
            driver.manage().window().maximize();

            resetDatabase();
            String str[] = {
                "https://www.amazon.co.uk/gp/new-releases/books?ref_=Oct_s9_apbd_onr_hd_bw_b17GB_S&pf_rd_r=HKZ7QK4E7YSXVRB2RJ3K&pf_rd_p=5e2ba314-5607-5294-ae8e-8c939fabcdc6&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=266239",
                "https://www.amazon.co.uk/gp/most-gifted/books/57?ref_=Oct_s9_apbd_omg_hd_bw_bv_S&pf_rd_r=DH7NYBBRZKRV7GJDVHF7&pf_rd_p=a49edc35-128a-5a74-8f32-a0d71de6a35c&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=57",
                "https://www.amazon.co.uk/gp/bestsellers/books/57?ref_=Oct_s9_apbd_obs_hd_bw_bv_S&pf_rd_r=DH7NYBBRZKRV7GJDVHF7&pf_rd_p=b293d97a-4112-5884-abc8-3b1e8958ede9&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=57",
                "https://www.amazon.co.uk/gp/most-wished-for/books/57?ref_=Oct_s9_apbd_omwf_hd_bw_bv_S&pf_rd_r=DH7NYBBRZKRV7GJDVHF7&pf_rd_p=c39bb7f2-e310-56d4-b941-d4b68ab1f3a7&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=57",
                "https://www.amazon.co.uk/gp/new-releases/books/69?ref_=Oct_s9_apbd_onr_hd_bw_b17_S&pf_rd_r=MSJ81GQXGX6NVXWY0R3F&pf_rd_p=19d264a7-a011-5920-be3a-7f93ebf8e43e&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=69",
                "https://www.amazon.co.uk/gp/most-gifted/books/69?ref_=Oct_s9_apbd_omg_hd_bw_b17_S&pf_rd_r=MSJ81GQXGX6NVXWY0R3F&pf_rd_p=91afca6e-afb1-5d8e-9faf-65110c6074cf&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=69",
                "https://www.amazon.co.uk/gp/bestsellers/books/69?ref_=Oct_s9_apbd_obs_hd_bw_b17_S&pf_rd_r=MSJ81GQXGX6NVXWY0R3F&pf_rd_p=53772362-3be0-5082-a3aa-95757a486d60&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=69",
                "https://www.amazon.co.uk/gp/most-wished-for/books/69?ref_=Oct_s9_apbd_omwf_hd_bw_b17_S&pf_rd_r=MSJ81GQXGX6NVXWY0R3F&pf_rd_p=85af3d08-4fd3-5f2d-9048-8a2c7e40a611&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=69",
                "https://www.amazon.co.uk/gp/most-wished-for/books/564334?ref_=Oct_s9_apbd_omwf_hd_bw_b2MoA_S&pf_rd_r=DN5P5KRCM0GP0Z53VFYM&pf_rd_p=5313b206-736f-57e7-bf95-16ab21d58445&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=564334",
                "https://www.amazon.co.uk/gp/new-releases/books/564334?ref_=Oct_s9_apbd_onr_hd_bw_b2MoA_S&pf_rd_r=DN5P5KRCM0GP0Z53VFYM&pf_rd_p=e7a72bd5-51ef-5751-a9c9-25bd2ed43159&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=564334",
                "https://www.amazon.co.uk/gp/most-gifted/books/564334?ref_=Oct_s9_apbd_omg_hd_bw_b2MoA_S&pf_rd_r=DN5P5KRCM0GP0Z53VFYM&pf_rd_p=1dd8f0ef-a427-5365-85a7-69e3fb81959b&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=564334",
                "https://www.amazon.co.uk/gp/bestsellers/books/564334?ref_=Oct_s9_apbd_obs_hd_bw_b2MoA_S&pf_rd_r=DN5P5KRCM0GP0Z53VFYM&pf_rd_p=34bc3a75-9826-51fe-9ef4-b6a4923dece3&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=564334",
                "https://www.amazon.co.uk/gp/new-releases/books/59?ref_=Oct_s9_apbd_onr_hd_bw_bx_S&pf_rd_r=XGVZK3V65QP7NK209FAK&pf_rd_p=662d49ea-dfac-5330-aa1b-531454b53ade&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=59",
                "https://www.amazon.co.uk/gp/most-gifted/books/59?ref_=Oct_s9_apbd_omg_hd_bw_bx_S&pf_rd_r=XGVZK3V65QP7NK209FAK&pf_rd_p=2bff49dd-6b6d-5f99-81f9-82a7ab479613&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=59",
                "https://www.amazon.co.uk/gp/bestsellers/books/59?ref_=Oct_s9_apbd_obs_hd_bw_bx_S&pf_rd_r=XGVZK3V65QP7NK209FAK&pf_rd_p=76288e2b-5d1e-572c-a188-6cc9f8ce8e3f&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=59",
                "https://www.amazon.co.uk/gp/most-wished-for/books/59?ref_=Oct_s9_apbd_omwf_hd_bw_bx_S&pf_rd_r=XGVZK3V65QP7NK209FAK&pf_rd_p=69f01426-432c-5eed-8dc0-40035d71a05b&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=59",
                "https://www.amazon.co.uk/gp/most-gifted/books/91?ref_=Oct_s9_apbd_omg_hd_bw_b1T_S&pf_rd_r=S123FH8ND42CPFQZQRTN&pf_rd_p=59e5015b-c5fb-5f97-a329-184ffc103115&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=91",
                "https://www.amazon.co.uk/gp/bestsellers/books/91?ref_=Oct_s9_apbd_obs_hd_bw_b1T_S&pf_rd_r=S123FH8ND42CPFQZQRTN&pf_rd_p=3c83cb92-1827-530e-a61e-a6f8e86e558c&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=91",
                "https://www.amazon.co.uk/gp/most-wished-for/books/91?ref_=Oct_s9_apbd_omwf_hd_bw_b1T_S&pf_rd_r=S123FH8ND42CPFQZQRTN&pf_rd_p=b643a5ab-0b83-5cc5-a602-7c50e0f10ec2&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=91",
                "https://www.amazon.co.uk/gp/new-releases/books/67?ref_=Oct_s9_apbd_onr_hd_bw_b15_S&pf_rd_r=E3MZ9G7XVYWVFEGFS3X6&pf_rd_p=fd8c5d52-6c2d-5ef8-b1d9-73111bc0aace&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=67",
                "https://www.amazon.co.uk/gp/most-gifted/books/67?ref_=Oct_s9_apbd_omg_hd_bw_b15_S&pf_rd_r=E3MZ9G7XVYWVFEGFS3X6&pf_rd_p=fbf551f2-81ca-5bf9-9111-6afc6e98b43d&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=67",
                "https://www.amazon.co.uk/gp/bestsellers/books/67?ref_=Oct_s9_apbd_obs_hd_bw_b15_S&pf_rd_r=E3MZ9G7XVYWVFEGFS3X6&pf_rd_p=f54ce57e-b5a8-5acf-b44e-f9a63d5d7422&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=67",
                "https://www.amazon.co.uk/gp/most-wished-for/books/67?ref_=Oct_s9_apbd_omwf_hd_bw_b15_S&pf_rd_r=E3MZ9G7XVYWVFEGFS3X6&pf_rd_p=83913286-3996-5622-8878-31a0fe090884&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=67",
                "https://www.amazon.co.uk/gp/new-releases/books/14909553031?ref_=Oct_s9_apbd_onr_hd_bw_bGH0xpn_S&pf_rd_r=S5A0XRHJVZQHNKVVA359&pf_rd_p=3eaef644-7ab8-51f9-804a-6841c5a791d6&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=14909553031",
                "https://www.amazon.co.uk/gp/most-gifted/books/14909553031?ref_=Oct_s9_apbd_omg_hd_bw_bGH0xpn_S&pf_rd_r=S5A0XRHJVZQHNKVVA359&pf_rd_p=d0e5e9cd-b878-5362-b87f-236dd6685aeb&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=14909553031",
                "https://www.amazon.co.uk/gp/bestsellers/books/14909553031?ref_=Oct_s9_apbd_obs_hd_bw_bGH0xpn_S&pf_rd_r=S5A0XRHJVZQHNKVVA359&pf_rd_p=ebc6f154-5fcd-5db7-a743-616c5d29939d&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=14909553031",
                "https://www.amazon.co.uk/gp/most-wished-for/books/14909553031?ref_=Oct_s9_apbd_omwf_hd_bw_bGH0xpn_S&pf_rd_r=S5A0XRHJVZQHNKVVA359&pf_rd_p=d42be25c-e1b1-5575-b5a7-a575cc5c0d3d&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=14909553031",
                "https://www.amazon.co.uk/gp/most-gifted/books/65?ref_=Oct_s9_apbd_omg_hd_bw_b13_S&pf_rd_r=WY9EYHCA04N03FP2X6X6&pf_rd_p=2710eb45-e45f-5608-a2e5-fca828eceb94&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=65",
                "https://www.amazon.co.uk/gp/new-releases/books/65?ref_=Oct_s9_apbd_onr_hd_bw_b13_S&pf_rd_r=WY9EYHCA04N03FP2X6X6&pf_rd_p=0e3e566b-489b-5d1b-b35e-2669c19ac88e&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=65",
                "https://www.amazon.co.uk/gp/bestsellers/books/65?ref_=Oct_s9_apbd_obs_hd_bw_b13_S&pf_rd_r=WY9EYHCA04N03FP2X6X6&pf_rd_p=b17ad9d0-cd56-56fd-ae15-198263f912d5&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=65",
                "https://www.amazon.co.uk/gp/most-wished-for/books/65?ref_=Oct_s9_apbd_omwf_hd_bw_b13_S&pf_rd_r=WY9EYHCA04N03FP2X6X6&pf_rd_p=c960c9b7-6b6d-518d-94c1-29726ec8bfc5&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=65",
                "https://www.amazon.co.uk/gp/new-releases/books/68?ref_=Oct_s9_apbd_onr_hd_bw_b16_S&pf_rd_r=SFWVCN86H60BFCVEWEG1&pf_rd_p=b46aa2e8-7c5d-57d6-8891-98ea0d51e209&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=68",
                "https://www.amazon.co.uk/gp/most-gifted/books/68?ref_=Oct_s9_apbd_omg_hd_bw_b16_S&pf_rd_r=SFWVCN86H60BFCVEWEG1&pf_rd_p=09965b7b-cbc1-5fda-8dbc-69a167217ca6&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=68",
                "https://www.amazon.co.uk/gp/bestsellers/books/68?ref_=Oct_s9_apbd_obs_hd_bw_b16_S&pf_rd_r=SFWVCN86H60BFCVEWEG1&pf_rd_p=54f130df-c606-5fc7-85f9-109f3726f3db&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=68",
                "https://www.amazon.co.uk/gp/most-wished-for/books/68?ref_=Oct_s9_apbd_omwf_hd_bw_b16_S&pf_rd_r=SFWVCN86H60BFCVEWEG1&pf_rd_p=5bd1f848-c0d3-5c88-9d44-28be2843de98&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=68",
                "https://www.amazon.co.uk/gp/new-releases/books/61?ref_=Oct_s9_apbd_onr_hd_bw_bz_S&pf_rd_r=WCZ92NMEHFDBPRRYG7NE&pf_rd_p=f9b5c5c2-399d-543f-9065-d5ad515611be&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=61",
                "https://www.amazon.co.uk/gp/most-gifted/books/61?ref_=Oct_s9_apbd_omg_hd_bw_bz_S&pf_rd_r=WCZ92NMEHFDBPRRYG7NE&pf_rd_p=78262bc8-17ba-5e22-a151-09f6493e5b1a&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=61",
                "https://www.amazon.co.uk/gp/bestsellers/books/61?ref_=Oct_s9_apbd_obs_hd_bw_bz_S&pf_rd_r=WCZ92NMEHFDBPRRYG7NE&pf_rd_p=52b584c2-d4c4-557f-b02a-f0f989ed5aad&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=61",
                "https://www.amazon.co.uk/gp/most-wished-for/books/61?ref_=Oct_s9_apbd_omwf_hd_bw_bz_S&pf_rd_r=WCZ92NMEHFDBPRRYG7NE&pf_rd_p=428bb8b3-4e4d-5398-aa4f-494bba67f06e&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=61",
                "https://www.amazon.co.uk/gp/new-releases/books/58?ref_=Oct_s9_apbd_onr_hd_bw_bw_S&pf_rd_r=NGEHQWC2GT01PXNBKZT8&pf_rd_p=310e32d5-6b10-51da-9d10-0632c97585e4&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=58",
                "https://www.amazon.co.uk/gp/most-gifted/books/58?ref_=Oct_s9_apbd_omg_hd_bw_bw_S&pf_rd_r=NGEHQWC2GT01PXNBKZT8&pf_rd_p=97953569-b3bf-59ff-bac4-e78622b2e0b8&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=58",
                "https://www.amazon.co.uk/gp/bestsellers/books/58?ref_=Oct_s9_apbd_obs_hd_bw_bw_S&pf_rd_r=NGEHQWC2GT01PXNBKZT8&pf_rd_p=ad52ebdf-975c-50d6-9318-f0fbed9de73e&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=58",
                "https://www.amazon.co.uk/gp/most-wished-for/books/58?ref_=Oct_s9_apbd_omwf_hd_bw_bw_S&pf_rd_r=NGEHQWC2GT01PXNBKZT8&pf_rd_p=51717f19-4a86-5019-9813-01f3f5f96b36&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=58",
                "https://www.amazon.co.uk/gp/most-gifted/books/60?ref_=Oct_s9_apbd_omg_hd_bw_by_S&pf_rd_r=BW5NQ1TC59VF2DGK9H96&pf_rd_p=9ff20e2c-b4f8-5683-ba5e-ab5d2fd8c3eb&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=60",
                "https://www.amazon.co.uk/gp/bestsellers/books/60?ref_=Oct_s9_apbd_obs_hd_bw_by_S&pf_rd_r=BW5NQ1TC59VF2DGK9H96&pf_rd_p=b1f88c76-0b15-5ac6-b990-0cad0f5cfe91&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=60",
                "https://www.amazon.co.uk/gp/most-wished-for/books/60?ref_=Oct_s9_apbd_omwf_hd_bw_by_S&pf_rd_r=BW5NQ1TC59VF2DGK9H96&pf_rd_p=02a98c96-40b4-5516-af7f-62ec0a38af5c&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=60",};

            for (String s : str) {
                scrapeBestSellerProducts(s);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(AmazonBestScrapeMandeep.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            System.out.println("Link scraping finished...");
            //    driver.get("https://www.google.com");
            crawlLinks();

            driver.close();

            generateCSVFile(outputFilePath);
        } finally {
            driver.close();
        }
    }

    private static void scrapeBestSellerProducts(String categoryLink) {
        boolean hasNextPage = false;
        String category = "";
        String isBestSeller = "no";
        String isRecommended = "no";
        String isIncludedInPopularTitle = "no";
        String isIncludedInMustHave = "no";
        if (categoryLink.contains("/bestsellers/")) {
            isBestSeller = "yes";
        } else if (categoryLink.contains("/most-wished-for/")) {
            isIncludedInMustHave = "yes";
        } else if (categoryLink.contains("/most-gifted/")) {
            isIncludedInPopularTitle = "yes";
        } else if (categoryLink.contains("/new-releases/")) {
            isRecommended = "yes";
        }
        // if()

        List<String> urlCheckList = new ArrayList();
        do {

            System.out.println("->" + categoryLink);
            driver.get(categoryLink);
            waitForJSandJQueryToLoad(driver);
            Document doc = Jsoup.parse(driver.getPageSource());
            //System.out.println(""+doc.text());
            if (!doc.getElementsByClass("category").isEmpty()) {
                category = doc.getElementsByClass("category").first().text();
            } else {
                category = "Books";

            }
            Element div = doc.getElementById("zg-ordered-list");
            for (Element e : div.getElementsByClass("zg-item-immersion")) {
                // String name = "";
                String url = "";
                Element a = e.getElementsByTag("a").first();
                // name = a.text();
                url = "https://www.amazon.co.uk" + a.attr("href");
                String format = e.getElementsByClass("a-color-secondary").first().text();
                if (format.contains("Hardcover") || format.contains("Paperback")
                        || format.contains("Board book")) {

                    //urlList.add(url);
                    /* if (url.contains("?")) {
                    url = StringUtils.substringBefore(url, "?");
                }*/
                    insertLinkIntoDB(url, category, isBestSeller, isRecommended, isIncludedInPopularTitle, isIncludedInMustHave);

                } else {
                    //other format so check if paper or hardcover exisists
                    urlCheckList.add(url);
                }
            }
            if (!doc.getElementsByClass("a-pagination").isEmpty()) {
                Element li = doc.getElementsByClass("a-pagination").first().getElementsByClass("a-last").first();
                if (li != null && !li.getElementsByTag("a").isEmpty()) {
                    hasNextPage = true;
                    categoryLink = li.getElementsByTag("a").first().attr("href");
                } else {
                    hasNextPage = false;
                }

            } else {
                hasNextPage = false;
            }
            //break;
        } while (hasNextPage);
        System.out.println("Check all extra books for paperback format...");
        Iterator<String> iterator = urlCheckList.iterator();
        while (iterator.hasNext()) {

            String url = iterator.next();

            try {
                driver.get(url);
                waitForJSandJQueryToLoad(driver);
            } catch (Exception e) {
                System.out.println("Extra boxes Ex:" + e);
            }
            Document doc = Jsoup.parse(driver.getPageSource());
            Element div = doc.getElementById("tmmSwatches");
            if (div != null) {
                for (Element a : div.getElementsByTag("a")) {
                    if (!a.attr("href").equalsIgnoreCase("javascript:void(0)")) {
                        //    System.out.println("->" + a.text());
                        if (a.text().contains("Hardcover")
                                || a.text().contains("Paperback")
                                || a.text().contains("Board book")) {
                            String ahref = "https://www.amazon.co.uk/" + a.attr("href");

                            insertLinkIntoDB(ahref, category, isBestSeller, isRecommended, isIncludedInPopularTitle, isIncludedInMustHave);
                            //     System.out.println("Format found..inserted..");
                            break;
                        }
                    }
                }
            } else {
                System.out.println("Format box not found..");
            }
            try {
                //timeout
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                Logger.getLogger(AmazonBestScrapeMandeep.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private static void detailScrape(String url, int linkId) {

        System.out.println("Getting..." + url);
        driver.get(url);
        waitForJSandJQueryToLoad(driver);
        Document doc = Jsoup.parse(driver.getPageSource());
        /*  Document doc = Jsoup.connect(url)
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:80.0) Gecko/20100101 Firefox/80.0")
                    .timeout(0).get();*/
        int trycount = 5;
        if (doc.getElementById("productTitle") == null && trycount <= 10) {
            System.out.println("Found Amazon blank page...sleeping for sometime");
            try {
                Thread.sleep(trycount * 10 * 1000);
                trycount++;
            } catch (InterruptedException ex) {
                Logger.getLogger(AmazonBestScrapeMandeep.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Trying again...");
            driver.get(url);
            waitForJSandJQueryToLoad(driver);
            doc = Jsoup.parse(driver.getPageSource());
        }
        if (doc.getElementById("productTitle") == null && trycount > 10) {
            System.out.println("Unable to open Amazon on browser...returning...");
            return;
        }
        String name = doc.getElementById("productTitle").text();
        if (doc.getElementById("productSubtitle") != null) {
            String productSubTitle = doc.getElementById("productSubtitle").text();
            String format = "";
            String publisher = "";
            if (productSubTitle.contains("Hardcover") || productSubTitle.contains("Paperback")
                    || productSubTitle.contains("Board book")) {
                if (productSubTitle.contains("Hardcover")) {
                    format = "Hardcover";
                } else if (productSubTitle.contains("Paperback")) {
                    format = "Paperback";
                } else if (productSubTitle.contains("Board book")) {
                    format = "Board book";
                }
                String author = "";
                if (doc.getElementById("bylineInfo") != null) {
                    author = doc.getElementById("bylineInfo").text();
                    if (author.contains("›")) {
                        author = StringUtils.substringBefore(author, "›").trim();
                    }
                    author = author.replace("(Author)", "").trim();
                }
                String desc = "";
                if (doc.getElementById("bookDesc_override_CSS") != null && doc.getElementById("bookDesc_override_CSS").nextElementSibling() != null) {
                    desc = doc.getElementById("bookDesc_override_CSS").nextElementSibling().text();
                    desc = Utility.html2text(desc);
                    desc = desc.replace(",", ";");
                    desc = desc.replaceAll("\\p{C}", "");
                }
                String paperPrice = "";
                String paperRRP = "";

                String boardbookPrice = "";
                String boardbookRRP = "";

                String hardcoverPrice = "";
                String hardcoverRRP = "";

                String imageURL = "";
                String ISBN10 = "";
                /* if (format.equalsIgnoreCase("Paperback")) {*/
                ISBN10 = StringUtils.substringBetween(doc.html(), "ISBN-10", "</li>");
                ISBN10 = Utility.html2text(ISBN10);
                ISBN10 = ISBN10.replace(":", "").trim();
                // }
                publisher = StringUtils.substringBetween(doc.html(), "<span class=\"a-text-bold\">Publisher", "</li>");
                publisher = Utility.html2text(publisher);
                publisher = publisher.replace(":", "");
                if (publisher.contains("(")) {
                    publisher = StringUtils.substringBeforeLast(publisher, "(").trim();
                }
                String imageName = "";
                //String publicationDate = StringUtils.substringAfter(productSubTitle, "–");
                String publicationDate = StringUtils.substringBetween(doc.html(), "<span class=\"a-text-bold\">Publisher", "</li>");
                if (publicationDate != null) {
                    publicationDate = StringUtils.substringBetween(publicationDate, "(", ")");

                }

                if (doc.getElementById("imgBlkFront") != null) {
                    imageURL = doc.getElementById("imgBlkFront").attr("src");
                    if (imageURL.contains("data:image")) {
                        //  imageURL = "";
                        imageURL = doc.getElementById("imgBlkFront").attr("data-a-dynamic-image");
                        //  System.out.println("" + imageURL);
                        if (imageURL.contains("\":[")) {
                            imageURL = StringUtils.substringBefore(imageURL, "\":[");
                        }
                        imageURL = imageURL.replace("{\"", "");
                    }
                    try {
                        URL urlR = new URL(imageURL);
                        BufferedImage img = ImageIO.read(urlR);
                        /*   imageName = StringUtils.substringAfterLast(imageURL, "/");
                            if (imageName.contains(".")) {
                                imageName = StringUtils.substringBefore(imageName, ".");
                            }
                            imageName = imageName.replaceAll("[^\\w\\s]", "");*/
                        imageName = "img_" + linkId;

                        File outputfile = new File(imagePath + imageName + ".jpg");
                        imageName = imageName + ".jpg";
                        ImageIO.write(img, "jpg", outputfile);
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(AmazonBestScrapeMandeep.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(AmazonBestScrapeMandeep.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
                if (doc.getElementById("buyBoxInner") != null) {
                    Element b = doc.getElementById("buyBoxInner");
                    if (format.equalsIgnoreCase("Paperback")) {
                        paperRRP = StringUtils.substringBetween(b.html(), "RRP:", "</li>");
                        paperRRP = Utility.html2text(paperRRP);
                        paperRRP = paperRRP.replace("Â", "");
                    } else if (format.equalsIgnoreCase("Hardcover")) {
                        hardcoverRRP = StringUtils.substringBetween(b.html(), "RRP:", "</li>");
                        hardcoverRRP = Utility.html2text(hardcoverRRP);
                        hardcoverRRP = hardcoverRRP.replace("Â", "");
                    } else if (format.equalsIgnoreCase("Board book")) {
                        boardbookRRP = StringUtils.substringBetween(b.html(), "RRP:", "</li>");
                        boardbookRRP = Utility.html2text(boardbookRRP);
                        boardbookRRP = boardbookRRP.replace("Â", "");
                    }
                }
                if (doc.getElementById("buyNewSection") != null) {
                    if (format.equalsIgnoreCase("Paperback")) {
                        paperPrice = doc.getElementById("buyNewSection").text();
                        paperPrice = paperPrice.replace("Â", "");
                    }
                    if (format.equalsIgnoreCase("Hardcover")) {
                        hardcoverPrice = doc.getElementById("buyNewSection").text();
                        hardcoverPrice = hardcoverPrice.replace("Â", "");
                    }
                    if (format.equalsIgnoreCase("Board book")) {
                        boardbookPrice = doc.getElementById("buyNewSection").text();
                        boardbookPrice = boardbookPrice.replace("Â", "");
                    }
                }
                //get all prices
                Element div = doc.getElementById("tmmSwatches");
                if (div != null) {
                    for (Element a : div.getElementsByTag("a")) {
                        if (!a.attr("href").equalsIgnoreCase("javascript:void(0)")) {

                            if (a.text().contains("Hardcover")
                                    || a.text().contains("Paperback")
                                    || a.text().contains("Board book")) {

                                String ahref = "https://www.amazon.co.uk/" + a.attr("href");

                                driver.get(ahref);
                                waitForJSandJQueryToLoad(driver);
                                doc = Jsoup.parse(driver.getPageSource());
                                /*   doc = Jsoup.connect(ahref)
                                                .ignoreContentType(true)
                                                .ignoreHttpErrors(true)
                                                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:80.0) Gecko/20100101 Firefox/80.0")
                                                .timeout(0).get();*/

                                if (a.text().equalsIgnoreCase("Hardcover")) {
                                    if (doc.getElementById("buyBoxInner") != null) {
                                        Element b = doc.getElementById("buyBoxInner");
                                        hardcoverRRP = StringUtils.substringBetween(b.html(), "RRP:", "</li>");
                                        hardcoverRRP = Utility.html2text(hardcoverRRP);
                                        hardcoverRRP = hardcoverRRP.replace("Â", "");
                                    }
                                    if (doc.getElementById("buyNewSection") != null) {
                                        hardcoverPrice = doc.getElementById("buyNewSection").text();
                                        hardcoverPrice = hardcoverPrice.replace("Â", "");
                                    }
                                } else if (a.text().equalsIgnoreCase("Paperback")) {
                                    if (doc.getElementById("buyBoxInner") != null) {
                                        Element b = doc.getElementById("buyBoxInner");
                                        paperRRP = StringUtils.substringBetween(b.html(), "RRP:", "</li>");
                                        paperRRP = Utility.html2text(paperRRP);
                                        paperRRP = paperRRP.replace("Â", "");
                                    }
                                    if (doc.getElementById("buyNewSection") != null) {
                                        paperPrice = doc.getElementById("buyNewSection").text();
                                        paperPrice = paperPrice.replace("Â", "");
                                    }

                                    //GET ISBN10 also
                                    ISBN10 = StringUtils.substringBetween(doc.html(), "ISBN-10", "</li>");
                                    ISBN10 = Utility.html2text(ISBN10);
                                    ISBN10 = ISBN10.replace(":", "").trim();
                                } else if (a.text().equalsIgnoreCase("Board book")) {
                                    if (doc.getElementById("buyBoxInner") != null) {
                                        Element b = doc.getElementById("buyBoxInner");
                                        boardbookRRP = StringUtils.substringBetween(b.html(), "RRP:", "</li>");
                                        boardbookRRP = Utility.html2text(boardbookRRP);
                                        boardbookRRP = boardbookRRP.replace("Â", "");
                                    }
                                    if (doc.getElementById("buyNewSection") != null) {
                                        boardbookPrice = doc.getElementById("buyNewSection").text();
                                        boardbookPrice = boardbookPrice.replace("Â", "");
                                    }
                                }

                            }
                        }
                    }
                }

                String insertQ = "INSERT INTO `amazonbooks_bestseller`.`book_master`\n"
                        + "(\n"
                        + "`name`,\n"
                        + "`author`,\n"
                        + "`publisher`,\n"
                        + "`publication_date`,\n"
                        + "`format`,\n"
                        + "`ISBN10`,\n"
                        + "`paper_rrp`,\n"
                        + "`paper_price`,\n"
                        + "`boardbook_rrp`,\n"
                        + "`boardbook_price`,\n"
                        + "`hardcover_rrp`,\n"
                        + "`hardcover_price`,\n"
                        + "`desc`,\n"
                        + "`imageName`,\n"
                        + "`link_id`)\n"
                        + "VALUES\n"
                        + "("
                        + "'" + Utility.prepareString(name) + "',"
                        + "'" + Utility.prepareString(author) + "',"
                        + "'" + Utility.prepareString(publisher) + "',"
                        + "'" + Utility.prepareString(publicationDate) + "',"
                        + "'" + Utility.prepareString(format) + "',"
                        + "'" + Utility.prepareString(ISBN10) + "',"
                        + "'" + Utility.prepareString(paperRRP.replaceAll("[^0-9.]", "")) + "',"
                        + "'" + Utility.prepareString(paperPrice.replaceAll("[^0-9.]", "")) + "',"
                        + "'" + Utility.prepareString(boardbookRRP.replaceAll("[^0-9.]", "")) + "',"
                        + "'" + Utility.prepareString(boardbookPrice.replaceAll("[^0-9.]", "")) + "',"
                        + "'" + Utility.prepareString(hardcoverRRP.replaceAll("[^0-9.]", "")) + "',"
                        + "'" + Utility.prepareString(hardcoverPrice.replaceAll("[^0-9.]", "")) + "',"
                        + "'" + Utility.prepareString(desc) + "',"
                        + "'" + Utility.prepareString(imageName) + "',"
                        + linkId
                        + ");";
                MyConnection.getConnection("amazonbooks_bestseller");
                if (MyConnection.insertData(insertQ)) {
                    String updateQ = "update `amazonbooks_bestseller`.`link_master` set is_scraped=1 where link_id=" + linkId;
                    MyConnection.insertData(updateQ);
                }
            } else {
                System.out.println("Skipping e-book...");
            }
        } else {
            System.out.println("Subtitle not found...");
        }

    }

    public static boolean waitForJSandJQueryToLoad(ChromeDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, 30);
        try {

            // wait for jQuery to load
            ExpectedCondition<Boolean> jQueryLoad = new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver driver) {
                    try {
                        // return ((Long) ((JavascriptExecutor) getDriver()).executeScript("return jQuery.active") == 0);
                        return ((JavascriptExecutor) driver).executeScript("return jQuery.active == 0").equals(true);
                    } catch (Exception e) {
                        // no jQuery present
                        return true;
                    }
                }
            };

            // wait for Javascript to load
            ExpectedCondition<Boolean> jsLoad = new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver driver) {
                    // return ((JavascriptExecutor) getDriver()).executeScript("return document.readyState")
                    //        .toString().equals("complete");
                    return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
                }
            };
            return wait.until(jQueryLoad) && wait.until(jsLoad);
        } catch (Exception e) {
            System.out.println("Ex:" + e.toString());
        }
        return true;
    }

    private static ResultSet dbRecord(String url) {
        try {
            MyConnection.getConnection("amazonbooks_bestseller");
            String checkUrl = "select * from  `amazonbooks_bestseller`.`link_master` where link='" + url + "'";
            ResultSet rs = MyConnection.getResultSet(checkUrl);
            if (rs.next()) {
                return rs;
            }

        } catch (SQLException ex) {
            Logger.getLogger(AmazonBestScrapeMandeep.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static void resetDatabase() {
        System.out.println("Database reset initiated...");
        MyConnection.getConnection("amazonbooks_bestseller");
        String deleteQ = "delete from link_master";
        MyConnection.insertData(deleteQ);
        System.out.println("Database reset successfully completed...");
    }

    private static void crawlLinks() {
        String selectQ = "select * from link_master where is_scraped=0";
        MyConnection.getConnection("amazonbooks_bestseller");
        ResultSet rs = MyConnection.getResultSet(selectQ);
        try {
            while (rs.next()) {
                detailScrape(rs.getString("link"), rs.getInt("link_id"));

                //timeout
                Thread.sleep(3000);
            }
        } catch (SQLException ex) {
            Logger.getLogger(AmazonBestScrapeMandeep.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(AmazonBestScrapeMandeep.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void insertLinkIntoDB(String url, String category, String isBestSeller, String isRecommended,
            String isIncludedInPopularTitle, String isIncludedInMustHave) {
        ResultSet rs = dbRecord(url);
        if (rs == null) {
            String insertQ = "INSERT INTO `amazonbooks_bestseller`.`link_master`\n"
                    + "(\n"
                    + "`link`,\n"
                    + "`category`,\n"
                    + "`is_bestSeller`,\n"
                    + "`is_recommended`,\n"
                    + "`is_isIncludedInPopularTitle`,\n"
                    + "`is_IncludedInMustHave`)\n"
                    + "VALUES\n"
                    + "("
                    + "'" + Utility.prepareString(url) + "',"
                    + "'" + Utility.prepareString(category) + "',"
                    + "'" + Utility.prepareString(isBestSeller) + "',"
                    + "'" + Utility.prepareString(isRecommended) + "',"
                    + "'" + Utility.prepareString(isIncludedInPopularTitle) + "',"
                    + "'" + Utility.prepareString(isIncludedInMustHave) + "'"
                    + ")";
            MyConnection.getConnection("amazonbooks_bestseller");
            MyConnection.insertData(insertQ);
            // System.out.println("inserted:" + url);
        } else {
            System.out.println("Alredy exists..updating..");
            try {
                //update columns
                if (!rs.getString("is_bestSeller").equalsIgnoreCase(isBestSeller)) {
                    String updateQ = "update `amazonbooks_bestseller`.`link_master` "
                            + "set is_bestSeller='" + isBestSeller + "' where link_id=" + rs.getInt("link_id");
                    MyConnection.getConnection("amazonbooks_bestseller");
                    MyConnection.insertData(updateQ);
                    //    System.out.println("updated best seller for linkid:" + rs.getInt("link_id"));
                } else if (!rs.getString("is_recommended").equalsIgnoreCase(isRecommended)) {
                    String updateQ = "update `amazonbooks_bestseller`.`link_master` "
                            + "set is_recommended='" + isRecommended + "' where link_id=" + rs.getInt("link_id");
                    MyConnection.getConnection("amazonbooks_bestseller");
                    MyConnection.insertData(updateQ);
                    //    System.out.println("updated is recommended for linkid:" + rs.getInt("link_id"));

                } else if (!rs.getString("is_isIncludedInPopularTitle").equalsIgnoreCase(isIncludedInPopularTitle)) {
                    String updateQ = "update `amazonbooks_bestseller`.`link_master` "
                            + "set is_isIncludedInPopularTitle='" + isIncludedInPopularTitle + "' where link_id=" + rs.getInt("link_id");
                    MyConnection.getConnection("amazonbooks_bestseller");
                    MyConnection.insertData(updateQ);
                    //    System.out.println("updated in popular title for linkid:" + rs.getInt("link_id"));

                } else if (!rs.getString("is_IncludedInMustHave").equalsIgnoreCase(isIncludedInMustHave)) {
                    String updateQ = "update `amazonbooks_bestseller`.`link_master` "
                            + "set is_IncludedInMustHave='" + isIncludedInMustHave + "' where link_id=" + rs.getInt("link_id");
                    MyConnection.getConnection("amazonbooks_bestseller");
                    MyConnection.insertData(updateQ);
                    // System.out.println("updated in must have for linkid:" + rs.getInt("link_id"));

                }
            } catch (SQLException ex) {
                Logger.getLogger(AmazonBestScrapeMandeep.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    private static void generateCSVFile(String outputFilePath) {
        String selectQ = "select `name`,author,publisher,publication_date,format,ISBN10,paper_rrp,paper_price,"
                + "boardbook_rrp,boardbook_price,hardcover_rrp,hardcover_price,`desc`,imageName,link,category,"
                + "is_bestSeller,is_recommended,is_isIncludedInPopularTitle,is_IncludedInMustHave \n"
                + "from link_master l,book_master b where b.link_id=l.link_id;";
        MyConnection.getConnection("amazonbooks_bestseller");
        ResultSet rs = MyConnection.getResultSet(selectQ);
        String header = "Main Category,Sub Category,Product Title,Author/ Director/ Artist,Publisher/ Studio/ Brand,"
                + "Published On,Format,Genre,ISBN/ Barcode,Paperback RRP,Paperback Offer price,Paperback Quantity,"
                + "Board book RRP,Board book Offer price,Board book Quantity,Hardcover RRP,Hardcover Offer price,"
                + "Hardcover Quantity,Ebook RRP,Ebook Offer price,Ebook Quantity,Short Description,Long Description,"
                + "Image Name,Is Best Seller,Is Recommended,Include in Popular Title,Include in Must Have" + LINE_SEPARATOR;
        try {
            Files.write(Paths.get(outputFilePath), header.getBytes(), StandardOpenOption.APPEND);
            //   System.out.println("Inserted!!");
        } catch (IOException ex) {
            Logger.getLogger(AmazonBestScrapeMandeep.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            while (rs.next()) {
                String content = "\"Books\",\"" + rs.getString("category") + "\",\"" + rs.getString("name") + "\",\"" + rs.getString("author")
                        + "\",\"" + rs.getString("publisher") + "\",\""
                        + rs.getString("publication_date") + "\",\"" + rs.getString("format") + "\",\"" + "\",\"" + rs.getString("ISBN10") + "\",\""
                        + rs.getString("paper_rrp") + "\",\"" + rs.getString("paper_price") + "\",\"" + "\",\""
                        + rs.getString("boardbook_rrp") + "\",\"" + rs.getString("boardbook_price") + "\",\"" + "\",\""
                        + rs.getString("hardcover_rrp") + "\",\"" + rs.getString("hardcover_price") + "\",\"" + "\",\"\",\"\",\"\",\""
                        + rs.getString("desc") + "\",\"\",\"" + rs.getString("imageName") + "\",\"" + rs.getString("is_bestSeller")
                        + "\",\"" + rs.getString("is_recommended")
                        + "\",\"" + rs.getString("is_isIncludedInPopularTitle") + "\",\"" + rs.getString("is_IncludedInMustHave")
                        + "\"" + LINE_SEPARATOR;
                try {
                    Files.write(Paths.get(outputFilePath), content.getBytes(), StandardOpenOption.APPEND);
                    //   System.out.println("Inserted!!");
                } catch (IOException ex) {
                    Logger.getLogger(AmazonBestScrapeMandeep.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            System.out.println("Get your file at->" + outputFilePath);
        } catch (SQLException ex) {
            Logger.getLogger(AmazonBestScrapeMandeep.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
