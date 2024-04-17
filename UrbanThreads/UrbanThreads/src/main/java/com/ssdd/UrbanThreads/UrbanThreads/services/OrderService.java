package com.ssdd.UrbanThreads.UrbanThreads.services;

import com.ssdd.UrbanThreads.UrbanThreads.entities.Order;

import com.ssdd.UrbanThreads.UrbanThreads.entities.OrderStatus;
import com.ssdd.UrbanThreads.UrbanThreads.entities.Product;
import com.ssdd.UrbanThreads.UrbanThreads.repository.OrderRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Getter
    private int selectedOrder = 0;

    public Order getCurrentOrder() {
        return orderRepository.findByOrderId(selectedOrder);
    }

    public Order getOrderById(int id) { return orderRepository.findByOrderId(id); }

    public List<Order> getAllOrders(){
        return orderRepository.findAll();
    }
    public void changeCurrentOrder(int orderId){
        this.selectedOrder = orderId;
    }

    public List<Integer> getAllPendingOrdersId() {
        return orderRepository.findOrderIdsByOrderStatus(OrderStatus.PENDING);
    }

    public int addNewOrder (Order o){
        return orderRepository.save(o).getOrderId();
    }

    public void saveOrder(Order currentOrder){
        orderRepository.save(currentOrder);
    }

    public void deleteCurrentOrder(){
        orderRepository.deleteOrderByOrderId(selectedOrder);
    }


    public Order deleteOrderById(int id){
        return orderRepository.deleteOrderByOrderId(id);
    }

    public void addProductToCurrentOrder(int productId, Product product, String size, String color, int quantity) {
    }
}

