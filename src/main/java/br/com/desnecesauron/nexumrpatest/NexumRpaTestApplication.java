package br.com.desnecesauron.nexumrpatest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class NexumRpaTestApplication {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(NexumRpaTestApplication.class, args);

        System.setProperty("webdriver.chrome.driver", "/home/desnecesauron/Desktop/nexum-rpa-test/src/main/resources" +
                "/chromedriver");
        ChromeOptions options = new ChromeOptions();
//		options.addArguments("--headless");
        options.addArguments("--incognito");
        WebDriver driver = new ChromeDriver(options);


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
            System.out.println("Volta " + i);

            WebElement webElementLinkInItem = driver.findElement(By.cssSelector("div > div > div > div > div > div >" +
                    " section > ul > li:nth-child(" + i + ") > div > div> a"));
            WebElement webElementTitleInItem = driver.findElement(By.cssSelector("div > div > div > div > div > div >" +
                    " section > ul > li:nth-child(" + i + ") > div > div> a  > h2"));
            WebElement webElementPriceInItem = driver.findElement(By.cssSelector("div > div > div > div > div > div >" +
                    " section > ul > li:nth-child(" + i + ") > div > div> a:nth-child(2) > div"));
//			System.out.println(element.getText());

            List<String> wordsUnnecessary = Arrays.asList("bateria", "suporte", "skin", "case", "capa", "charge",
                    "serie x", "series x");
            String titleCompare = webElementTitleInItem.getText().toLowerCase();
            boolean isConsole = true;
            if (!titleCompare.contains("xbox") &&
                    (!titleCompare.contains("series s") || !titleCompare.contains("serie s"))) isConsole = false;

            for (String doNotInclude : wordsUnnecessary) {
                if (webElementTitleInItem.getText().toLowerCase().contains(doNotInclude))
                    isConsole = false;
            }
            if (webElementPriceInItem.getText().toLowerCase().contains("indisponível")) {
                continue;
            }
            if (!isConsole) continue;

            System.out.println("Link: " + webElementLinkInItem.getAttribute("href"));
            System.out.println("Title: " + webElementTitleInItem.getText());
            System.out.println("Price: " + webElementPriceInItem.getText());

        }

        System.exit(0);

        // mercado livre ------------------------------------------------------------------------------------
        driver.get("https://lista.mercadolivre.com.br/xbox-serie-s");
        // div > div > section > ol > li
//        String cssSelectorLiGeneralMercadoLivre = "div > div > section > ol > li > div > div > a";
        String cssSelectorLiGeneralMercadoLivre = "div > div > section > ol > li > div > div > a> div > div";
        List<WebElement> itensMercadoLivre = driver.findElements(By.cssSelector(cssSelectorLiGeneralMercadoLivre));

        System.out.println("length");
        System.out.println(itensMercadoLivre.toArray().length);
        System.out.println("array");
        System.out.println(Arrays.toString(itensMercadoLivre.toArray()));
        i = 0;
        for (WebElement element : itensMercadoLivre) {
            i++;
            System.out.println("Volta " + i);
            System.out.println(element.getText());
         /*
         List<WebElement> webElementsInItemMercadoLivre =
                    driver.findElements(By.cssSelector(cssSelectorLiGeneralMercadoLivre + "div > div > section > ol >" +
                            " li > div > div > a> div > div"));

            for (WebElement elementItem : webElementsInItemMercadoLivre) {
                System.out.println(elementItem.getText());
            }
            */

			/*

			int rowData = 0;
            for (WebElement elementItem : webElementsInItem) {
                if (rowData == 0 || rowData == 3 || rowData == 4)
                    System.out.println(elementItem.getText());
                rowData++;
            }

			*/
        }

        driver.close();
    }

    //https://www.casasbahia.com.br/xbox-serie-s/b?sortby=relevance
}