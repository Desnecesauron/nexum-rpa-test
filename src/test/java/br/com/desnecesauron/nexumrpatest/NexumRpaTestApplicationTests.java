package br.com.desnecesauron.nexumrpatest;

import br.com.desnecesauron.nexumrpatest.entities.ProductSellingEntity;
import br.com.desnecesauron.nexumrpatest.services.ProductSellingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;


@SpringBootTest
class NexumRpaTestApplicationTests {

    @Autowired
    ProductSellingService productSellingService;

    @Test
    @DisplayName("Run all the application")
    void contextLoads() {
    }

    @Test
    @DisplayName("Testing save Db without entities inside")
    void testDb() {

        List<ProductSellingEntity> productSellingEntityList = new ArrayList<>();

        Assert.isTrue(productSellingService.saveAll(productSellingEntityList), "Assertion failed: must be false");
    }

    @Test
    @DisplayName("Testing if get any data from the Db")
    void testDbLastData() {
        Assert.noNullElements(productSellingService.getLastDataScrapInDb(), "Return is null");
    }

    @Test
    @DisplayName("List empty to getThreeMostCheap() method")
    void testDbClean() {
        List<ProductSellingEntity> productSellingEntityList = new ArrayList<>();
        Assert.isNull(productSellingService.getThreeMostCheap(productSellingEntityList),
                "Assertion failed: return is different of null");
    }

}
