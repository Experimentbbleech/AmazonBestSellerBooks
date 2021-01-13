/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package amazonbestsellers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 *
 * @author Khushbu
 */
public class AmazonBestSellers {

    public static void main(String[] args) {
        // give category link whose best seller list you want
        // String mainLink = "https://www.amazon.in/";
        //String categoryLink = "https://www.amazon.in/gp/bestsellers/electronics";

        String mainLink = "https://www.amazon.co.uk";
        String categoryLink = "https://www.amazon.co.uk/gp/new-releases/books/69?ref_=Oct_s9_apbd_onr_hd_bw_b17_S&amp;pf_rd_r=5RF8XF3HZ5HZ5FET2E8J&amp;pf_rd_p=ad8d2b90-fe01-5f1f-8003-b984a85704bf&amp;pf_rd_s=merchandised-search-10&amp;pf_rd_t=BROWSE&amp;pf_rd_i=69";
        scrapeBestSellerProducts(categoryLink, mainLink);
    }

    private static void scrapeBestSellerProducts(String categoryLink, String mainLink) {
        boolean hasNextPage = false;
        do {
            try {
                Document doc = Jsoup.connect(categoryLink)
                        .ignoreContentType(true)
                        .ignoreHttpErrors(true)
                        .get();
                //System.out.println(""+doc.text());
                Element div = doc.getElementById("zg-ordered-list");
                for (Element e : div.getElementsByClass("zg-item-immersion")) {
                    String name = "";
                    String url = "";
                    String price = "";
                    String rating = "";
                    String totalReviews = "";

                    Element a = e.getElementsByTag("a").first();
                    name = a.text();
                    url = mainLink + a.attr("href");
                    if (!doc.getElementsByClass("a-color-price").isEmpty()) {
                        price = doc.getElementsByClass("a-color-price").first().text();
                    }
                    if (!doc.getElementsContainingOwnText("stars").isEmpty()) {
                        Element r = doc.getElementsContainingOwnText("stars").first();
                        rating = r.text();
                        totalReviews = r.parent().parent().nextElementSibling().text();
                    }
                    /*  if (!doc.getElementsContainingOwnText("a-size-small").isEmpty()) {
                    totalReviews = doc.getElementsContainingOwnText("a-size-small").first().text();
                }
                if (doc.getElementsByTag("a").size() >= 4) {
                    totalReviews = doc.getElementsByTag("a").get(2).text();
                }*/
                    System.out.println("" + name + ";" + price + ";" + rating + ";" + totalReviews + ";" + url);
                    if (!doc.getElementsByClass("a-pagination").isEmpty()) {
                        Element li = doc.getElementsByClass("a-pagination").first().getElementsByClass("a-last").first();
                        if (li != null) {
                            hasNextPage = true;
                            categoryLink = li.getElementsByTag("a").first().attr("href");
                        }
                    }
                }

            } catch (IOException ex) {
                Logger.getLogger(AmazonBestSellers.class.getName()).log(Level.SEVERE, null, ex);
            }
        } while (hasNextPage);
    }

}
