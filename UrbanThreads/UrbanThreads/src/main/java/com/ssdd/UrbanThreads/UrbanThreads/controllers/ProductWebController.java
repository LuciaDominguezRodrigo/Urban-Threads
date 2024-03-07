package com.ssdd.UrbanThreads.UrbanThreads.controllers;


import com.ssdd.UrbanThreads.UrbanThreads.entities.Product;
import com.ssdd.UrbanThreads.UrbanThreads.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;


@Controller
public class ProductWebController {

    @Autowired
    private ProductService productService;

    private int nextProductIndex = 3;
    private int productsRefreshSize = 4; //this number controls how many elements are charged
                                         //with 'cargar más'button


    @GetMapping("/")
    public String index(Model model) {
        nextProductIndex = productsRefreshSize;
        List<Product> products = productService.findByIdRange(0, nextProductIndex-1);

        if (products.isEmpty()){
            model.addAttribute("products", new ArrayList<Product>());
        }
        else {
            model.addAttribute("products", products);
        }
        nextProductIndex = products.size();
        return "index";
    }


    @GetMapping("/newProducts")
    public String newEvents(Model model) {
        List<Product> products = productService.findByIdRange(nextProductIndex, (nextProductIndex+productsRefreshSize)-1);
        nextProductIndex += products.size();
        model.addAttribute("additionalProducts", products);
        if(nextProductIndex > productService.findAllProducts().size()){ //To show / hide Load more products button
            model.addAttribute("loadMoreProducts", false);
        } else{
            model.addAttribute("loadMoreProducts", true);
        }

        return "moreProducts";
    }

    @GetMapping("/product/{id}")
    public String showProduct(Model model, @PathVariable int id) {
        Product product = productService.findProduct(id);
        model.addAttribute("product", product);
        return "productDetails";
    }

    @GetMapping("/createProduct")
    public String newProduct(Model model) {
        //implementar
        return "createForm";
    }

}
