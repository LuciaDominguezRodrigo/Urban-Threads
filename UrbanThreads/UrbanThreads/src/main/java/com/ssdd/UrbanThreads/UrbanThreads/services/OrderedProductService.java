package com.ssdd.UrbanThreads.UrbanThreads.services;


import com.ssdd.UrbanThreads.UrbanThreads.entities.Order;
import com.ssdd.UrbanThreads.UrbanThreads.entities.OrderedProduct;
import com.ssdd.UrbanThreads.UrbanThreads.entities.Product;
import com.ssdd.UrbanThreads.UrbanThreads.entities.Size;
import com.ssdd.UrbanThreads.UrbanThreads.repository.OrderRepository;
import com.ssdd.UrbanThreads.UrbanThreads.repository.OrderedProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderedProductService {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderedProductRepository orderedProductRepository;

    public void saveOrderedProduct(OrderedProduct orderedProduct) {
        orderedProductRepository.save(orderedProduct);
    }

    public void addProductToCurrentOrder(Product product, Size size, String color, int quantity) {
        OrderedProduct newProduct = new OrderedProduct();

        newProduct.setOrder(orderService.getCurrentOrder());
        newProduct.setProduct(product);
        newProduct.setName(product.getName());
        newProduct.setSize(size);
        newProduct.setColor(color);
        newProduct.setQuantity(quantity);
        newProduct.setTotalPrice(product.getPrice() * quantity);

        orderedProductRepository.save(newProduct);
    }

    public void deleteOrderedProduct(int productId, String productSize, String productColor, int productQuantity) {
        List<OrderedProduct> productsInOrder = orderedProductRepository.findByOrder(orderService.getCurrentOrder());
        if (productsInOrder != null) {
            for (OrderedProduct p : productsInOrder) { //If product is ordered, must be found
                if(p.getId() == productId && p.getSize().equals(Size.valueOf(productSize)) && p.getColor().equals(productColor) && p.getQuantity() == productQuantity){
                    orderedProductRepository.delete(p);
                }
            }

        }
    }
}
