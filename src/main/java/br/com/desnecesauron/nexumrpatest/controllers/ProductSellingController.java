package br.com.desnecesauron.nexumrpatest.controllers;

import br.com.desnecesauron.nexumrpatest.services.ProductSellingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class ProductSellingController {

    @Autowired
    private ProductSellingService productSellingService;

    public void init() {
        productSellingService.initTask();
    }

}
