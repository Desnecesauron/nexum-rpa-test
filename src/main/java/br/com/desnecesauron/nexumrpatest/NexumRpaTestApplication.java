package br.com.desnecesauron.nexumrpatest;

import br.com.desnecesauron.nexumrpatest.controllers.ProductSellingController;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NexumRpaTestApplication {

    @Autowired
    ProductSellingController productSellingController;

    public static void main(String[] args) {
        SpringApplication.run(NexumRpaTestApplication.class, args);
    }

    @PostConstruct
    public void init() {
        productSellingController.init();
    }
}