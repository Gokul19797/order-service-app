package com.example.order.service.service;



import com.example.order.service.customExecptions.ExternalServiceException;
import com.example.order.service.customExecptions.ProductNotAvailableException;
import com.example.order.service.dto.ProductResponseDto;
import com.example.order.service.model.Order;
import com.example.order.service.repository.OrderRepository;
import com.example.order.service.utils.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestTemplate restTemplate;


    public Order createOrder(Order order) {
        String url = "http://product-service/api/products/fetch/" + order.getProductId();

        try {
            // Call external service and check product availability
            Boolean productIsPresent = restTemplate.getForObject(url, Boolean.class);

            if (Boolean.TRUE.equals(productIsPresent)) {
                // Build and save the order
                Order order1 = Order.builder()
                        .customerId(order.getCustomerId())
                        .productId(order.getProductId())
                        .status(OrderStatus.PENDING)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                return orderRepository.save(order1);
            } else {
                throw new ProductNotAvailableException("Product not available: " + order.getProductId());
            }
        } catch (HttpClientErrorException e) {
            throw new ExternalServiceException("Failed to fetch product details from Product Service", e);
        } catch (ResourceAccessException e) {
            throw new ExternalServiceException("Unable to connect to Product Service", e);
        }
    }


    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public Order updateOrderStatus(Long id, OrderStatus status) {
        Order order = getOrderById(id);
        order.setStatus(status);
        return orderRepository.save(order);
    }
}
