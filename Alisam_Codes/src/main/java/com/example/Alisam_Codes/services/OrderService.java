package com.example.Alisam_Codes.services;

import com.example.Alisam_Codes.models.Order;
import com.example.Alisam_Codes.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public Order createOrder(Order order) {
        order.setStatus("PENDING");
        return orderRepository.save(order);
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll(); // In a real app, use pagination or order by desc
    }

    public Order updateOrderStatus(Long id, String status) {
        Optional<Order> orderOptional = orderRepository.findById(id);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            order.setStatus(status);
            return orderRepository.save(order);
        }
        return null; // Handle exception properly in real app
    }

    public Order updateOrderDetails(Long id, String status, String trackingId, String shiprocketOrderId) {
        Optional<Order> orderOptional = orderRepository.findById(id);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            if (status != null && !status.isEmpty()) order.setStatus(status);
            if (trackingId != null) order.setTrackingId(trackingId);
            if (shiprocketOrderId != null) order.setShiprocketOrderId(shiprocketOrderId);
            return orderRepository.save(order);
        }
        return null;
    }
}
