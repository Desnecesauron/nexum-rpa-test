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

		System.setProperty("webdriver.chrome.driver", "/home/desnecesauron/Desktop/nexum-rpa-test/src/main/resources/chromedriver");
		ChromeOptions options = new ChromeOptions();
		//options.addArguments("--headless");
		options.addArguments("--incognito");
		WebDriver driver = new ChromeDriver(options);


		// ProductsGrid__ProductsGridWrapper-sc-yqpqna-0 ipfubY
		//
		// //*[@id="__next"]/div[2]/div/div/div[4]/div[2]/div/section/ul
		// /html/body/div[1]/div[2]/div/div/div[4]/div[2]/div/section/ul

		// casas bahia
		driver.get("https://www.casasbahia.com.br/xbox-serie-s/b?sortby=relevance");

		// div > div> div > div > div> div > div > section > ul
//		WebElement webElement= driver.findElement(By.cssSelector("div > div> div > div > div> div > div > section > ul > li"));
		List<WebElement> webElements= driver.findElements(By.cssSelector("div > div> div > div > div> div > div > section > ul > li"));

		System.out.println("length");
		System.out.println(webElements.toArray().length);
		System.out.println("array");
		System.out.println(Arrays.toString(webElements.toArray()));
		int i = 0;
		for (WebElement element : webElements) {
			i++;
			System.out.println("Volta " + i);
			System.out.println(element.getText());
		}

		// mercado livre
		driver.get("https://lista.mercadolivre.com.br/xbox-serie-s");
		// div > div > section > ol > li
		List<WebElement> itensMercadoLivre= driver.findElements(By.cssSelector("div > div > section > ol > li"));

		System.out.println("length");
		System.out.println(itensMercadoLivre.toArray().length);
		System.out.println("array");
		System.out.println(Arrays.toString(itensMercadoLivre.toArray()));
		i = 0;
		for (WebElement element : itensMercadoLivre) {
			i++;
			System.out.println("Volta " + i);
			System.out.println(element.getText());
		}

		driver.close();
	}

	//https://www.casasbahia.com.br/xbox-serie-s/b?sortby=relevance
}