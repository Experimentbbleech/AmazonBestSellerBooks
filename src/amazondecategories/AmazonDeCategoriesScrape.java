/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package amazondecategories;

import static amazonbestsellers.AmazonBestScrapeMandeep.waitForJSandJQueryToLoad;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 *
 * @author Khushbu
 */
/**
 * GOAL:Go to the last possible category on amazon.de and scrape the first page
 */
public class AmazonDeCategoriesScrape {

    static String chromePath = "C:\\Users\\Khushbu\\Downloads\\chromedriver_win32(2)\\chromedriver.exe";

    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", chromePath);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");

        ChromeDriver driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        String url = "https://www.amazon.de/gp/browse.html?node=84230031&ref_=nav_em__bty_0_2_16_2";
        driver.get(url);
        goToLastCategory(driver);
        waitForJSandJQueryToLoad(driver);

    }

    private static void goToLastCategory(ChromeDriver driver) {
        
    }
}
