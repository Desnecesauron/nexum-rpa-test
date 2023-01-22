package br.com.desnecesauron.nexumrpatest.services;

import br.com.desnecesauron.nexumrpatest.entities.ProductSellingEntity;
import br.com.desnecesauron.nexumrpatest.repositories.ProductSellingRepository;
import jakarta.persistence.OptimisticLockException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Configurable
public class ProductSellingService {
    @Autowired
    private ProductSellingRepository productSellingRepository;

    private final List<String> wordsUnnecessary = Arrays.asList("fonte", "bateria", "suporte", "skin", "case", "capa",
            "charge", "serie x", "series x", "xbox one", "alça", "ventilador");

    public boolean saveAll(List<ProductSellingEntity> productSellingEntities) {
        try {
            productSellingEntities.forEach(productSellingRepository::save);
        } catch (IllegalArgumentException | OptimisticLockException ex) {
            System.out.println("Error: " + ex);
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public ChromeDriver newChromeDriver() {
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver");
        ChromeOptions options = new ChromeOptions();
//		options.addArguments("--headless");
        options.addArguments("--incognito");
        return new ChromeDriver(options);
    }

    // if I get some time remaining I'll do this
    public void getLastDataScrapInDb() {

    }

    public List<ProductSellingEntity> getThreeMostCheap(List<ProductSellingEntity> productSellingEntities) {
        Comparator<ProductSellingEntity> pSellingEComparator =
                Comparator.comparingDouble(ProductSellingEntity::getPrice);

        List<ProductSellingEntity> sellingEntities = new ArrayList<>();
        productSellingEntities.sort(pSellingEComparator);
        for (int j = 0; j < 3; j++) {
            System.out.println(productSellingEntities.get(j).toString());
            sellingEntities.add(productSellingEntities.get(j));
        }
        return sellingEntities;
    }

    public void initTask() {
        ChromeDriver driver = newChromeDriver();

        List<ProductSellingEntity> casasBahiaProducts = getDataScrapXboxCasasBahia(driver);
        System.out.println("Three most cheap data in Casas Bahia:");
        casasBahiaProducts = getThreeMostCheap(casasBahiaProducts);
        if (saveAll(casasBahiaProducts))
            System.out.println("Saved successfully!");

        List<ProductSellingEntity> mercadoLivreProducts = getDataScrapMercadoLivre(driver);
        System.out.println("Three most cheap data in Mercado Livre:");
        mercadoLivreProducts = getThreeMostCheap(mercadoLivreProducts);
        if (saveAll(mercadoLivreProducts))
            System.out.println("Saved successfully!");

        driver.close();
    }

    private List<ProductSellingEntity> getDataScrapXboxCasasBahia(ChromeDriver driver) {

        // casas bahia
        driver.get("https://www.casasbahia.com.br/console-xbox-serie-s/b?sortby=relevance");

        List<ProductSellingEntity> productSellingEntities = new ArrayList<>();
        String cssSelectorLiGeneralCasasBahia = "div > div > div > div > div > div > div > section > ul > li";
        List<WebElement> webElements = driver.findElements(By.cssSelector(cssSelectorLiGeneralCasasBahia));

        int i = 0;
        for (int index = 0; index < webElements.toArray().length; index++) {
            i++;

            WebElement webElementLinkInItem = driver.findElement(By.cssSelector("div > div > div > div > div > div >" +
                    " section > ul > li:nth-child(" + i + ") > div > div> a"));
            WebElement webElementTitleInItem = driver.findElement(By.cssSelector("div > div > div > div > div > div >" +
                    " section > ul > li:nth-child(" + i + ") > div > div> a  > h2"));
            WebElement webElementPriceInItem = driver.findElement(By.cssSelector("div > div > div > div > div > div >" +
                    " section > ul > li:nth-child(" + i + ") > div > div> a:nth-child(2) > div"));

            String titleCompare = webElementTitleInItem.getText().toLowerCase();
            if (dontIsThisTitleAConsoleNecessary(titleCompare)) continue;

            if (webElementPriceInItem.getText().toLowerCase().contains("indisponível")) {
                continue;
            }

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
            System.out.println("Price: " + strPrice);

            double doublePrice = Double.parseDouble(strPrice.replace(".", "")
                    .replace(",", ".").substring(2).trim());

            productSellingEntities.add(new ProductSellingEntity(null,
                    webElementLinkInItem.getAttribute("href"), webElementTitleInItem.getText(), doublePrice,
                    LocalDateTime.now()));
        }

        return productSellingEntities;
    }

    private List<ProductSellingEntity> getDataScrapMercadoLivre(ChromeDriver driver) {

        // mercado livre ------------------------------------------------------------------------------------
        driver.get("https://lista.mercadolivre.com.br/xbox-serie-s");
        String cssSelectorTitleMercadoLivre = "div > div > section > ol > li > div > div > a> div > div > h2";
        String cssSelectorPriceMercadoLivre = "div > div > section > ol > li > div > div > a> div > div > div " +
                ">div>div > span:nth-child(1)";
        String cssSelectorUrlMercadoLivre = "div > div > section > ol > li > div > div > div > a";

        List<WebElement> titleItemMercadoLivre = driver.findElements(By.cssSelector(cssSelectorTitleMercadoLivre));
        List<WebElement> priceItemMercadoLivre = driver.findElements(By.cssSelector(cssSelectorPriceMercadoLivre));
        List<WebElement> urlItemMercadoLivre = driver.findElements(By.cssSelector(cssSelectorUrlMercadoLivre));

        List<ProductSellingEntity> productsMercadoLivre = new ArrayList<>();

        int i = -1;
        for (WebElement element : titleItemMercadoLivre) {
            i++;

            WebElement priceItemElement = priceItemMercadoLivre.get(i);
            WebElement urlItemElement = urlItemMercadoLivre.get(i);

            String titleCompare = element.getText().toLowerCase();
            if (dontIsThisTitleAConsoleNecessary(titleCompare)) continue;

            String strPrice =
                    priceItemElement.getText().substring(0, priceItemElement.getText().indexOf(" reais"))
                            .trim().replace(".", "")
                            .replace(",", ".");

            double doublePrice = Double.parseDouble(strPrice.trim());

            if (strPrice.contains("$"))
                doublePrice = Double.parseDouble(strPrice.substring(2).trim());

            productsMercadoLivre.add(new ProductSellingEntity(null,
                    urlItemElement.getAttribute("href"), element.getText(), doublePrice,
                    LocalDateTime.now()));
        }

        return productsMercadoLivre;
    }

    private boolean dontIsThisTitleAConsoleNecessary(String titleCompare) {
        boolean dontIsConsole = !(titleCompare.contains("xbox") && (titleCompare.contains("series s") ||
                titleCompare.contains("serie s")));

        for (String doNotInclude : wordsUnnecessary) {
            if (titleCompare.contains(doNotInclude)) {
                dontIsConsole = true;
                break;
            }
        }
        return dontIsConsole;
    }
}
