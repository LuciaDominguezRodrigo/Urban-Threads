package com.ssdd.UrbanThreads.UrbanThreads.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ordered_products")
@Getter
@Setter
public class OrderedProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private Size size;

    private String color;

    private int quantity;

    @Column (nullable = false)
    private double totalPrice;

    public OrderedProduct() {
    }

    public OrderedProduct(Long id, Order order, Product product, String name, Size size, String color, int quantity, double totalPrice) {
        this.id = id;
        this.order = order;
        this.product = product;
        this.name = name;
        this.size = size;
        this.color = color;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public void updateDetails(Product product, String size, String color, int quantity) {
        if (product != null) {
            this.setProduct(product);
            this.setName(product.getName());
            this.setTotalPrice(product.getPrice() * quantity);
        }
        if (size != null) {
            this.setSize(Size.valueOf(size));
        }
        if (color != null) {
            this.setColor(color);
        }
        if (quantity > 0) {
            this.setQuantity(quantity);
            this.setTotalPrice(this.getProduct().getPrice() * quantity);
        }
    }
}
