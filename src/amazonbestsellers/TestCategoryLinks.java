/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package amazonbestsellers;

import static amazonbestsellers.AmazonBestScrapeMandeep.chromePath;
import static amazonbestsellers.AmazonBestScrapeMandeep.driver;
import static amazonbestsellers.AmazonBestScrapeMandeep.waitForJSandJQueryToLoad;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 *
 * @author Khushbu
 */
public class TestCategoryLinks {

    public static void main(String[] args) {
        String chromePath = "C:\\Users\\Khushbu\\Downloads\\chromedriver_win32(2)\\chromedriver.exe";
        ChromeOptions options = new ChromeOptions();
        System.setProperty("webdriver.chrome.driver", chromePath);

        //  options.addArguments("--headless");
        //    options.setBinary("C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe");
        //  System.out.println("Binary location=C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe");
        ChromeDriver driver = new ChromeDriver(options);
        //  driver.manage().window().maximize();
        driver.get("https://www.amazon.co.uk/books-used-books-textbooks/b/?ie=UTF8&node=266239&ref_=nav_cs_books");
        waitForJSandJQueryToLoad(driver);
        WebElement div = driver.findElementByClassName("octopus-pc-category-card-v2-content");
        String[] catLinks = new String[20];
        int i = 0;
        for (WebElement e : div.findElements(By.tagName("a"))) {
            if (!e.getText().equals("")) {
                catLinks[i] = new String();
                catLinks[i] = "https://www.amazon.co.uk" + e.getAttribute("href");
                System.out.println("Category:" + e.getText());
                i++;
            }
        }
        i = 0;
        ArrayList<String> bookLinks = new ArrayList();
        for (String cLink : catLinks) {

            String categorycode = StringUtils.substringBetween(cLink, "node=", "&");
           
            if (categorycode != null && !categorycode.equals("null")) {
                bookLinks.add("https://www.amazon.co.uk/gp/most-gifted/books/" + categorycode);
                bookLinks.add("https://www.amazon.co.uk/gp/new-releases/books/" + categorycode);
                bookLinks.add("https://www.amazon.co.uk/gp/bestsellers/books/" + categorycode);
                bookLinks.add("https://www.amazon.co.uk/gp/most-wished-for/books/" + categorycode);
            }
        }

        for (String s : bookLinks) {
            System.out.println("-->" + s);
        }
    }
}
