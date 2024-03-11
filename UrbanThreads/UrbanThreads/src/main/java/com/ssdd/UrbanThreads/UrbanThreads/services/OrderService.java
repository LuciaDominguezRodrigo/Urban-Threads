package com.ssdd.UrbanThreads.UrbanThreads.services;

import com.ssdd.UrbanThreads.UrbanThreads.entities.Order;;
import com.ssdd.UrbanThreads.UrbanThreads.entities.Product;
import com.ssdd.UrbanThreads.UrbanThreads.repository.OrderRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public int getSelectedOrder(){ return orderRepository.getSelectedOrder(); }

    public Order findOrder(int id) {
        return orderRepository.findOrder(id);
    }

    public Collection<Order> findAllOrders() {
        return orderRepository.findAllOrders();
    }

    public Order saveOrder(@NotNull Order order) {
        return orderRepository.saveOrder(order, this.getSelectedOrder());
    }

    public Order updateOrder(int id, Order order) {
        return orderRepository.updateOrder(id, order);
    }

    public void deleteOrder(int id) {
        orderRepository.deleteOrder(id);
    }

    public void addProductToCurrentOrder(Product product) {
        Order order = orderRepository.findOrder(orderRepository.getSelectedOrder());
        if (order != null) {
            order.getOrderProducts().add(product);
            orderRepository.updateOrder(orderRepository.getSelectedOrder(), order);
        } else {
            order = new Order();
            order.getOrderProducts().add(product);
            orderRepository.saveOrder(order, orderRepository.getSelectedOrder());
        }
    }

    public List<Product> getAllProductsInOrder() {
        List<Product> allProductsInOrder = new ArrayList<>();
        Collection<Order> allOrders = orderRepository.findAllOrders();

        for (Order order : allOrders) {
            List<Product> orderProducts = order.getOrderProducts();
            allProductsInOrder.addAll(orderProducts);
        }

        return allProductsInOrder;
    }

}

