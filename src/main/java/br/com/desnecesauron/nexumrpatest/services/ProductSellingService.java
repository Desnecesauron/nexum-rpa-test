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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
@Configurable
public class ProductSellingService {
    @Autowired
    private ProductSellingRepository productSellingRepository;

    private final List<String> WORDS_UNNECESSARY = Arrays.asList("fonte", "bateria", "suporte", "skin", "case", "capa",
            "charge", "serie x", "series x", "xbox one", "alça", "ventilador");

    private ChromeDriver newChromeDriver() {
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver");
        ChromeOptions options = new ChromeOptions();
//		options.addArguments("--headless"); //using this, don't work correctly
        options.addArguments("--incognito");
        return new ChromeDriver(options);
    }

    public void initTask() {
        System.out.println("\n\nBefore all: showing the data of last dataScrap\n");
        getLastDataScrapInDb();
        System.out.println("\n\nNow: cleaning the database to only stay the most recently dataScrap\n");
        cleanDb();

        System.out.println("Starting chromedriver");
        ChromeDriver driver = newChromeDriver();

        System.out.println("\n\nSearching the items on Casas Bahia and saving data in a List");
        List<ProductSellingEntity> casasBahiaProducts = getDataScrapXboxCasasBahia(driver);
        System.out.println("Filtering data to get the three most cheap items in Casas Bahia\n\n");
        System.out.println("Three most cheap data in Casas Bahia:");
        casasBahiaProducts = getThreeMostCheap(casasBahiaProducts);
        if (saveAll(casasBahiaProducts))
            System.out.println("Saved successfully!");


        System.out.println("Searching the items on Mercado Livre and saving data in a List");
        List<ProductSellingEntity> mercadoLivreProducts = getDataScrapMercadoLivre(driver);
        System.out.println("Filtering data to get the three most cheap items in Mercado Livre\n\n");
        System.out.println("Three most cheap data in Mercado Livre:");
        mercadoLivreProducts = getThreeMostCheap(mercadoLivreProducts);
        if (saveAll(mercadoLivreProducts))
            System.out.println("Saved successfully!");

        System.out.println("Closing chromedriver...");
        driver.close();
        System.out.println("Exiting application...");
    }

    public boolean saveAll(List<ProductSellingEntity> productSellingEntitiesToSave) {
        try {
            productSellingEntitiesToSave.forEach(productSellingRepository::save);
        } catch (IllegalArgumentException | OptimisticLockException ex) {
            System.out.println("Error: " + ex);
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public void getLastDataScrapInDb() {
        List<ProductSellingEntity> sixEntLastSave = productSellingRepository.findTop6ByOrderByTimestampGeneratedDesc();
        if (sixEntLastSave.size() == 0) {
            System.out.println("Db is clear, continuing.");
            return;
        }
        sixEntLastSave.forEach(System.out::println);
    }

    private void cleanDb() {
        productSellingRepository.deleteAll();
    }

    public List<ProductSellingEntity> getThreeMostCheap(List<ProductSellingEntity> productSellingEntities) {
        Comparator<ProductSellingEntity> pSEntityComparator =
                Comparator.comparingDouble(ProductSellingEntity::getPrice);

        // sorting the most cheap to the most expensive
        List<ProductSellingEntity> sellingEntities = new ArrayList<>();
        productSellingEntities.sort(pSEntityComparator);
        // getting the 3 most cheap
        for (int j = 0; j < 3; j++) {
            System.out.println(productSellingEntities.get(j).toString());
            sellingEntities.add(productSellingEntities.get(j));
        }
        return sellingEntities;
    }

    private List<ProductSellingEntity> getDataScrapXboxCasasBahia(ChromeDriver driver) {

        // going to casas bahia link
        driver.get("https://www.casasbahia.com.br/console-xbox-serie-s/b?sortby=relevance");

        // css Selector for Casas Bahia list items
        String cssSelectorLiGeneralCasasBahia = "div > div > div > div > div > div > div > section > ul > li";

        List<WebElement> webItemElements = driver.findElements(By.cssSelector(cssSelectorLiGeneralCasasBahia));
        List<ProductSellingEntity> productsCasasBahia = new ArrayList<>();

        for (int index = 0; index < webItemElements.toArray().length; index++) {

            // webElements for title, price and URL values, changing by index
            WebElement webElementLinkInItem = driver.findElement(By.cssSelector("div > div > div > div > div > div >" +
                    " section > ul > li:nth-child(" + index + ") > div > div> a"));
            WebElement webElementTitleInItem = driver.findElement(By.cssSelector("div > div > div > div > div > div >" +
                    " section > ul > li:nth-child(" + index + ") > div > div> a  > h2"));
            WebElement webElementPriceInItem = driver.findElement(By.cssSelector("div > div > div > div > div > div >" +
                    " section > ul > li:nth-child(" + index + ") > div > div> a:nth-child(2) > div"));

            // comparing the title if this is the required console
            String titleCompare = webElementTitleInItem.getText().toLowerCase();
            if (doNotIsThisTitleAConsoleNecessary(titleCompare)) continue;

            // consulting if is "Indisponível" the item
            if (webElementPriceInItem.getText().toLowerCase().contains("indisponível")) continue;

            String strPrice = webElementPriceInItem.getText();
            String strPriceLowerCase = webElementPriceInItem.getText().toLowerCase();

            // removing "dump text" with the price
            if (strPrice.contains("De R$")) {
                strPrice = strPrice.substring(strPriceLowerCase.indexOf("por r$") + 4, strPriceLowerCase.indexOf("em " +
                        "até ")).trim();
            } else {
                strPrice = strPrice.substring(strPriceLowerCase.indexOf("por r$") + 4,
                        strPriceLowerCase.indexOf(",") + 3).trim();
            }

            // removing "dump text" with the price
            if (strPrice.toLowerCase().contains("no pix"))
                strPrice = strPrice.substring(0, strPrice.toLowerCase().indexOf("no pix")).trim();

            // removing "dump text" with the price
            if (strPrice.toLowerCase().contains("à vista"))
                strPrice = strPrice.substring(0, strPrice.toLowerCase().indexOf("à vista")).trim();

            double doublePrice = Double.parseDouble(strPrice.replace(".", "")
                    .replace(",", ".").substring(2).trim());

            // adding to the list the console found
            productsCasasBahia.add(new ProductSellingEntity(null,
                    webElementLinkInItem.getAttribute("href"), webElementTitleInItem.getText(), doublePrice,
                    LocalDateTime.now()));
        }
        return productsCasasBahia;
    }

    private List<ProductSellingEntity> getDataScrapMercadoLivre(ChromeDriver driver) {

        // going to mercado livre link
        driver.get("https://lista.mercadolivre.com.br/xbox-serie-s");

        // css Selectors for Mercado Livre items
        final String cssSelectorTitleMercadoLivre = "div > div > section > ol > li > div > div > a> div > div > h2";
        final String cssSelectorPriceMercadoLivre = "div > div > section > ol > li > div > div > a> div > div > div " +
                ">div>div > span:nth-child(1)";
        final String cssSelectorUrlMercadoLivre = "div > div > section > ol > li > div > div > div > a";

        // css Selectors for title, price and URL values
        List<WebElement> titleItemsMercadoLivre = driver.findElements(By.cssSelector(cssSelectorTitleMercadoLivre));
        List<WebElement> priceItemsMercadoLivre = driver.findElements(By.cssSelector(cssSelectorPriceMercadoLivre));
        List<WebElement> urlItemsMercadoLivre = driver.findElements(By.cssSelector(cssSelectorUrlMercadoLivre));

        List<ProductSellingEntity> productsMercadoLivre = new ArrayList<>();

        int i = -1;
        for (WebElement element : titleItemsMercadoLivre) {
            i++;

            // webElements for price and URL values, changing by index
            WebElement priceItemElement = priceItemsMercadoLivre.get(i);
            WebElement urlItemElement = urlItemsMercadoLivre.get(i);

            // comparing the title if this is the required console
            String titleCompare = element.getText().toLowerCase();
            if (doNotIsThisTitleAConsoleNecessary(titleCompare)) continue;

            // removing "dump text" with the price
            String strPrice =
                    priceItemElement.getText().substring(0, priceItemElement.getText().indexOf(" reais"))
                            .trim().replace(".", "")
                            .replace(",", ".");

            double doublePrice;
            if (strPrice.contains("$"))
                doublePrice = Double.parseDouble(strPrice.substring(2).trim());
            else
                doublePrice = Double.parseDouble(strPrice.trim());
            // adding to the list the console found
            productsMercadoLivre.add(new ProductSellingEntity(null,
                    urlItemElement.getAttribute("href"), element.getText(), doublePrice,
                    LocalDateTime.now()));
        }
        return productsMercadoLivre;
    }

    private boolean doNotIsThisTitleAConsoleNecessary(String titleToCompare) {
        // testing words that MUST be INCLUDED in the title
        boolean dontIsConsole = !(titleToCompare.contains("xbox") && (titleToCompare.contains("series s") ||
                titleToCompare.contains("serie s")));

        // testing words that do not want to be included in the title
        for (String doNotInclude : WORDS_UNNECESSARY) {
            if (titleToCompare.contains(doNotInclude)) {
                dontIsConsole = true;
                break;
            }
        }
        return dontIsConsole;
    }
}
