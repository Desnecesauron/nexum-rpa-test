package br.com.desnecesauron.nexumrpatest.services;

import br.com.desnecesauron.nexumrpatest.entities.ProductSellingEntity;
import br.com.desnecesauron.nexumrpatest.repositories.ProductSellingRepository;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Configurable
public class ProductSellingService {

    @Autowired
    private ProductSellingRepository productSellingRepository;

    public boolean save(List<ProductSellingEntity> productSellingEntities) {
        productSellingEntities.forEach(productSellingRepository::save);
        return true;
    }

    public ChromeDriver newChromeDriver() {
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver");
        ChromeOptions options = new ChromeOptions();
//		options.addArguments("--headless");
        options.addArguments("--incognito");
        return new ChromeDriver(options);
    }

    // if get me some time remaining i'll do this
    public void getLastDataScrapInDb() {

    }

    public void initTask() {
        threeMostCheapXbox();
    }

    private void threeMostCheapXbox() {
        List<ProductSellingEntity> productSellingEntities = new ArrayList<>();
        ChromeDriver driver = newChromeDriver();

        // ProductsGrid__ProductsGridWrapper-sc-yqpqna-0 ipfubY
        //
        // //*[@id="__next"]/div[2]/div/div/div[4]/div[2]/div/section/ul
        // /html/body/div[1]/div[2]/div/div/div[4]/div[2]/div/section/ul

        // casas bahia
        driver.get("https://www.casasbahia.com.br/console-xbox-serie-s/b?sortby=relevance");

        // div > div> div > div > div> div > div > section > ul
//		WebElement webElement= driver.findElement(By.cssSelector("div > div> div > div > div> div > div > section > ul
//		> li"));
        String cssSelectorLiGeneralCasasBahia = "div > div > div > div > div > div > div > section > ul > li";
        List<WebElement> webElements = driver.findElements(By.cssSelector(cssSelectorLiGeneralCasasBahia));

        System.out.println("length");
        System.out.println(webElements.toArray().length);
        System.out.println("array");
        System.out.println(Arrays.toString(webElements.toArray()));

        int i = 0;
        for (WebElement element : webElements) {
            i++;
//            System.out.println("Volta " + i);

            WebElement webElementLinkInItem = driver.findElement(By.cssSelector("div > div > div > div > div > div >" +
                    " section > ul > li:nth-child(" + i + ") > div > div> a"));
            WebElement webElementTitleInItem = driver.findElement(By.cssSelector("div > div > div > div > div > div >" +
                    " section > ul > li:nth-child(" + i + ") > div > div> a  > h2"));
            WebElement webElementPriceInItem = driver.findElement(By.cssSelector("div > div > div > div > div > div >" +
                    " section > ul > li:nth-child(" + i + ") > div > div> a:nth-child(2) > div"));
//			System.out.println(element.getText());

            List<String> wordsUnnecessary = Arrays.asList("fonte", "bateria", "suporte", "skin", "case", "capa",
                    "charge",
                    "serie x", "series x");
            String titleCompare = webElementTitleInItem.getText().toLowerCase();
            boolean isConsole = true;
            if (!titleCompare.contains("xbox") &&
                    (!titleCompare.contains("series s") || !titleCompare.contains("serie s"))) isConsole = false;

            for (String doNotInclude : wordsUnnecessary) {
                if (webElementTitleInItem.getText().toLowerCase().contains(doNotInclude)) {
                    isConsole = false;
                }
            }
            if (webElementPriceInItem.getText().toLowerCase().contains("indisponível")) {
                continue;
            }
            if (!isConsole) continue;

            String strPrice = webElementPriceInItem.getText();
            String strPriceLowerCase = webElementPriceInItem.getText().toLowerCase();

            if (strPrice.contains("De R$")) {
                strPrice = strPrice.substring(strPriceLowerCase.indexOf("por r$") + 4, strPriceLowerCase.indexOf("em " +
                        "até ")).trim();
            } else {
                strPrice = strPrice.substring(strPriceLowerCase.indexOf("por r$") + 4,
                        strPriceLowerCase.indexOf(",") + 3).trim();
            }

            if (strPrice.toLowerCase().contains("no pix")) {
                strPrice = strPrice.substring(0, strPrice.toLowerCase().indexOf("no pix")).trim();
            }

            if (strPrice.toLowerCase().contains("à vista")) {
                strPrice = strPrice.substring(0, strPrice.toLowerCase().indexOf("à vista")).trim();
            }

            System.out.println("Link: " + webElementLinkInItem.getAttribute("href"));
            System.out.println("Title: " + webElementTitleInItem.getText());
//            System.out.println("Price: " + webElementPriceInItem.getText());
            System.out.println("Price: " + strPrice);

            double doublePrice = Double.parseDouble(strPrice.replace(".", "")
                    .replace(",", ".").substring(2).trim());

            productSellingEntities.add(new ProductSellingEntity(null,
                    webElementLinkInItem.getAttribute("href"), webElementTitleInItem.getText(), doublePrice,
                    LocalDateTime.now()));
        }


        // mercado livre ------------------------------------------------------------------------------------


        driver.get("https://lista.mercadolivre.com.br/xbox-serie-s");
        // div > div > section > ol > li
//        String cssSelectorLiGeneralMercadoLivre = "div > div > section > ol > li > div > div > a";
        String cssSelectorTitleMercadoLivre = "div > div > section > ol > li > div > div > a> div > div > h2";
        String cssSelectorPriceMercadoLivre = "div > div > section > ol > li > div > div > a> div > div > div " +
                ">div>div > span:nth-child(1)";
        String cssSelectorUrlMercadoLivre = "div > div > section > ol > li > div > div > div > a";
        // "div > div > section > ol > li > div > div > a> div > div:nth-child(1)";

        // "div > div > section > ol > li > div > div > a> div > div > h2" - title ad
        // "div > div > section > ol > li > div > div > a> div > div > div >div>div > span:nth-child(1)" - price
        // "div > div > section > ol > li > div > div > div > a" - url

        List<WebElement> titleItemMercadoLivre = driver.findElements(By.cssSelector(cssSelectorTitleMercadoLivre));
        List<WebElement> priceItemMercadoLivre = driver.findElements(By.cssSelector(cssSelectorPriceMercadoLivre));
        List<WebElement> urlItemMercadoLivre = driver.findElements(By.cssSelector(cssSelectorUrlMercadoLivre));

        System.out.println("length");
        System.out.println(titleItemMercadoLivre.toArray().length);

        List<ProductSellingEntity> productsMercadoLivre = new ArrayList<>();

        i = 0;
        for (WebElement element : titleItemMercadoLivre) {

            System.out.println("Volta " + i);
            System.out.println(element.getText());

            WebElement priceItemElement = priceItemMercadoLivre.get(i);
            WebElement urlItemElement = urlItemMercadoLivre.get(i);

            double doublePrice = Double.parseDouble(priceItemElement.getText().substring(0,
                            priceItemElement.getText().indexOf(" reais")).trim().replace(".", "")
                    .replace(",", ".").substring(2).trim());

            productsMercadoLivre.add(new ProductSellingEntity(null,
                    urlItemElement.getAttribute("href"), element.getText(), doublePrice,
                    LocalDateTime.now()));
            
            i++;
        }

        driver.close();

        productsMercadoLivre.forEach(System.out::println);

        productSellingEntities.forEach(System.out::println);
        if (save(productSellingEntities)) {
            System.out.println("Saved successfully");
        }
    }

}
