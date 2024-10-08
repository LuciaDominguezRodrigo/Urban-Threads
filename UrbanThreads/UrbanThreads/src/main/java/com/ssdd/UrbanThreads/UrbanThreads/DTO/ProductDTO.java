package com.ssdd.UrbanThreads.UrbanThreads.DTO;

import com.ssdd.UrbanThreads.UrbanThreads.entities.Product;
import com.ssdd.UrbanThreads.UrbanThreads.entities.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ProductDTO {
    private Long id;

    private String name;

    private String category;

    private String description;

    private double price;

    private String photo;

    private Map<Size, Integer> availableSizes;

    public ProductDTO(Long id,String name, String category, String description, double price, String photo, Map<Size, Integer> availableSizes) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
        this.price = price;
        this.photo = photo;
        this.availableSizes = availableSizes;

    }

    public  ProductDTO (Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.category = product.getCategory().getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.photo = "./api/products/img/" + product.getId();
        this.availableSizes  = product.getAvailableSizes();

    }
}



