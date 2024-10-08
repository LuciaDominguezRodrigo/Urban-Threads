package com.ssdd.UrbanThreads.UrbanThreads.controllers;

import com.ssdd.UrbanThreads.UrbanThreads.DTO.OrderDTO;
import com.ssdd.UrbanThreads.UrbanThreads.DTO.OrderedProductDTO;
import com.ssdd.UrbanThreads.UrbanThreads.entities.*;
import com.ssdd.UrbanThreads.UrbanThreads.services.OrderService;
import com.ssdd.UrbanThreads.UrbanThreads.services.OrderedProductService;
import com.ssdd.UrbanThreads.UrbanThreads.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderRESTController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderedProductService orderedProductService;

    @Autowired
    private ProductService productService;

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable Long id) {
        Order selectedOrder = orderService.getOrderById(id);
        if (selectedOrder == null) {
            return ResponseEntity.status(404).build();
        }

        OrderDTO oDTO = new OrderDTO(selectedOrder);
        return ResponseEntity.status(200).body(oDTO);
    }

    @GetMapping("/all")
    public ResponseEntity<Collection<OrderDTO>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        List<OrderDTO> oDTO = new ArrayList<>();
        if (orders == null) {
            return ResponseEntity.notFound().build();
        }
        for (Order o: orders){
            OrderDTO orderDTO = new OrderDTO(o);
            oDTO.add(orderDTO);
        }
        return ResponseEntity.status(200).body(oDTO);
    }

    @PostMapping("/new")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO orderDTO) {
        Order newOrder = new Order();
        orderService.saveOrder(newOrder);
        for (OrderedProductDTO o : orderDTO.getOrderedProductsDTO()) {
            Optional<Product> productOptional = productService.findProduct(o.getProductId());
            if(!productOptional.isPresent()){
                return ResponseEntity.status(404).build();
            }
            Product orderedProduct = productOptional.get();

            //If ordered product quantity is bigger than available and elegible quantity (product total units of selected size - selected size product units that are already in an existing order)
            if(o.getQuantity() > (orderedProduct.getAvailableSizes().get(Size.valueOf(o.getSize())) - productService.getSelectedProducts(orderedProduct, Size.valueOf(o.getSize())))){
                return ResponseEntity.status(406).build();
            }

            Long id = orderedProductService.addProductToOrder(newOrder, orderedProduct, Size.valueOf(o.getSize()), o.getColor(), o.getQuantity());
            o.setId(id);
            o.setTotalPrice(orderedProduct.getPrice() * o.getQuantity());

        }
        orderService.changeCurrentOrder(newOrder.getId());
        orderDTO.setOrderId(newOrder.getId());
        return ResponseEntity.status(201).body(orderDTO);
    }

    @PostMapping("/complete/{id}")
    public ResponseEntity<OrderDTO> completeOrder(@PathVariable Long id) {
        Order existingOrder = orderService.getOrderById(id);
        if (existingOrder == null) {
            return ResponseEntity.notFound().build();
        }
        existingOrder.setOrderStatus(OrderStatus.COMPLETED);
        productService.reduceProductsQuantity(existingOrder.getOrderedProducts());
        orderService.saveOrder(existingOrder);

        return ResponseEntity.status(200).body(new OrderDTO(existingOrder));
    }


    @PutMapping("/edit/{id}")
    public ResponseEntity<OrderDTO> updateOrder(@PathVariable Long id, @RequestBody OrderDTO orderDTO) {
        Order existingOrder = orderService.getOrderById(id);

        if (existingOrder == null) {
            return ResponseEntity.notFound().build();
        }

        if (orderDTO.getOrderStatus() != null) {
            existingOrder.setOrderStatus(orderDTO.getOrderStatus());
        }

        if (orderDTO.getOrderedProductsDTO() != null) {
            for (OrderedProductDTO o : orderDTO.getOrderedProductsDTO()) {
                Optional<Product> productOptional = productService.findProduct(o.getProductId());

                //Check if a product exists
                if (productOptional.isEmpty()) {
                    return ResponseEntity.status(404).build();
                }

                Product product = productOptional.get();

                OrderedProduct foundProduct = orderedProductService.findByIdAndOrder(o.getId(), existingOrder);

                if (foundProduct == null) {
                    OrderedProduct newOrderedProduct = new OrderedProduct(null, existingOrder, product, product.getName(), Size.valueOf(o.getSize()), o.getColor(), o.getQuantity(), product.getPrice() * o.getQuantity());
                    existingOrder.addOrderedProduct(newOrderedProduct);
                } else{
                    foundProduct.updateDetails(product, o.getSize(), o.getColor(), o.getQuantity());
                }
            }
        }

        orderService.saveOrder(existingOrder);
        return ResponseEntity.status(202).body(new OrderDTO(existingOrder));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id) {
        Order existingOrder = orderService.getOrderById(id);
        if (existingOrder == null) {
            return ResponseEntity.status(404).build();
        }
        orderService.deleteOrderById(id);

        //When an order is removed, the current order changes to next created order or, if there´s no more orders created, a new one is created and marked as current order.
        List<Long> allOrdersId = orderService.getAllPendingOrdersId();
        if(!allOrdersId.isEmpty()){
            orderService.changeCurrentOrder(allOrdersId.get(0));
        } else{
            Long newOrderId = orderService.addNewOrder(new Order());
            orderService.changeCurrentOrder(newOrderId);
        }

        return ResponseEntity.status(200).build();
    }

    @PatchMapping("/edit/{id}")
    public ResponseEntity<OrderDTO> updateOrderByPatching(@PathVariable Long id, @RequestBody OrderDTO partialOrderDTO) {
        Order existingOrder = orderService.getOrderById(id);

        if (existingOrder == null) {
            return ResponseEntity.notFound().build();
        }

        if (partialOrderDTO.getOrderStatus() != null) {
            existingOrder.setOrderStatus(partialOrderDTO.getOrderStatus());
        }

        if (partialOrderDTO.getOrderedProductsDTO() != null) {
            for (OrderedProductDTO o : partialOrderDTO.getOrderedProductsDTO()) {
                Optional<Product> productOptional = productService.findProduct(o.getProductId());

                //Check if a product exists
                if (productOptional.isEmpty()) {
                    return ResponseEntity.status(404).build();
                }

                Product product = productOptional.get();

                OrderedProduct foundProduct = orderedProductService.findByIdAndOrder(o.getId(), existingOrder);

                if (foundProduct == null) {
                    OrderedProduct newOrderedProduct = new OrderedProduct(null, existingOrder, product, product.getName(), Size.valueOf(o.getSize()), o.getColor(), o.getQuantity(), product.getPrice() * o.getQuantity());
                    existingOrder.addOrderedProduct(newOrderedProduct);
                } else{
                    foundProduct.updateDetails(product, o.getSize(), o.getColor(), o.getQuantity());
                }
            }
        }

        orderService.saveOrder(existingOrder);
        return ResponseEntity.status(200).body(new OrderDTO(existingOrder));
    }

}
