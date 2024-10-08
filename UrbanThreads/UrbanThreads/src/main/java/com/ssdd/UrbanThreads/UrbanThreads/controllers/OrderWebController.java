package com.ssdd.UrbanThreads.UrbanThreads.controllers;

import com.ssdd.UrbanThreads.UrbanThreads.entities.*;
import com.ssdd.UrbanThreads.UrbanThreads.services.OrderService;
import com.ssdd.UrbanThreads.UrbanThreads.services.OrderedProductService;
import com.ssdd.UrbanThreads.UrbanThreads.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@Controller
public class OrderWebController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderedProductService orderedProductService;

    @Autowired
    private ProductService productService;


    @PostMapping("/newProductInOrder")
    public String addToOrder(@RequestParam("id") Long productId,
                             @RequestParam("selectedSize") String size,
                             @RequestParam("selectedColor") String color,
                             @RequestParam("quantity") int quantity) {
        Optional<Product> product = productService.findProduct(productId);
        if (product.isPresent()) {
            orderedProductService.addProductToOrder(orderService.getCurrentOrder(),product.get(), Size.valueOf(size), color, quantity);
        }

        return "redirect:/orderPage";
    }


    @GetMapping("/orderPage")
    public String showOrderPage(Model model) {
        Order currentOrder = orderService.getCurrentOrder();
        if(currentOrder == null){
            currentOrder = new Order();
            orderService.addNewOrder(currentOrder);
            orderService.changeCurrentOrder(currentOrder.getId());
        }
        List<OrderedProduct> orderProducts = currentOrder.getOrderedProducts();
        model.addAttribute("orderId", currentOrder.getId());
        model.addAttribute("allOrdersId", orderService.getAllPendingOrdersId());
        model.addAttribute("productList", orderProducts);
        for (OrderedProduct op : orderProducts){
            int maxEligibleProducts = op.getProduct().getAvailableSizes().get(Size.XS) - productService.getSelectedProducts(op.getProduct(),op.getSize()) + op.getQuantity();
            model.addAttribute("maxEligibleProducts", maxEligibleProducts);
        }

        return "orderPage";
    }

    @PostMapping("/cancelOrder")
    public String cancelOrder() {
        orderService.deleteCurrentOrder();
        //When an order is removed, the current order changes to next created order or, if there´s no more orders created, a new one is created and marked as current order.
        List<Long> allOrdersId = orderService.getAllPendingOrdersId();
        if(!allOrdersId.isEmpty()){
            orderService.changeCurrentOrder(allOrdersId.get(0));
        } else{
            Long newOrderId = orderService.addNewOrder(new Order());
            orderService.changeCurrentOrder(newOrderId);
        }

        return "redirect:/";
    }

    @PostMapping("/orderReady")
    public String makeOrder(Model model) {
        Order currentOrder = orderService.getCurrentOrder();
        if (currentOrder != null) {
            model.addAttribute("productList", currentOrder.getOrderedProducts());
            currentOrder.setOrderStatus(OrderStatus.COMPLETED);
            boolean allProductsOrdered = productService.reduceProductsQuantity(currentOrder.getOrderedProducts());
            model.addAttribute("allProductsOrdered", allProductsOrdered);
            orderService.saveOrder(currentOrder);

            //When an order is removed, the current order changes to next created order or, if there´s no more orders created, a new one is created and marked as current order.
            List<Long> allOrdersId = orderService.getAllPendingOrdersId();
            if(!allOrdersId.isEmpty()){
                orderService.changeCurrentOrder(allOrdersId.get(0));
            } else{
                Long newOrderId = orderService.addNewOrder(new Order());
                orderService.changeCurrentOrder(newOrderId);
            }

            return "orderMade";
        } else {
            return "redirect:/";
        }
    }

    @PostMapping("/editOrder")
    public String editOrderProductQuantity(@RequestParam String productId,
                             @RequestParam String productSize,
                             @RequestParam String productColor,
                             @RequestParam("quantity") int quantity) {

        Long longProductId = Long.parseLong(productId);
        Order currentOrder = orderService.getCurrentOrder();
        OrderedProduct changedProduct = new OrderedProduct();

        //Locates edited product and updates its quantity as desired
        for (OrderedProduct orderProduct : orderedProductService.orderP()) {
            if(orderProduct.getId().toString().equals(productId)&& orderProduct.getSize().equals(Size.valueOf(productSize))
                    && orderProduct.getColor().equals(productColor)){
                changedProduct = orderProduct;
            }
        }

        if (changedProduct.getProduct() != null) {
            changedProduct.setQuantity(quantity);
            changedProduct.setTotalPrice(quantity * changedProduct.getProduct().getPrice());
            orderedProductService.saveOrderedProduct(changedProduct);
        } else {

        }

        return "redirect:/orderPage";
    }

    @PostMapping("/changeOrder")
    public String changeOrder(@RequestParam Long selectedOrder) {
        orderService.changeCurrentOrder(selectedOrder);
        return "redirect:/orderPage";
    }

    @PostMapping("/newOrder")
    public String newOrder() {
        Order newOrder = new Order();
        orderService.addNewOrder(newOrder);
        orderService.changeCurrentOrder(newOrder.getId());
        return "redirect:/orderPage";
    }


    @PostMapping("/deleteProductOrder")
    public String eliminarProductoDePedido(@RequestParam("productId") Long productId,
                                           @RequestParam("productSize") String productSize,
                                           @RequestParam("productColor") String productColor,
                                           @RequestParam("productQuantity") int productQuantity) {
        orderedProductService.deleteOrderedProduct(productId, productSize, productColor, productQuantity);
        return "redirect:/orderPage";
    }
}

