package com.stock_mate.BE.service;

import com.stock_mate.BE.dto.request.OrderItemResquest;
import com.stock_mate.BE.dto.request.OrderRequest;
import com.stock_mate.BE.dto.response.OrderResponse;
import com.stock_mate.BE.entity.*;
import com.stock_mate.BE.entity.Order;
import com.stock_mate.BE.mapper.OrderMapper;
import com.stock_mate.BE.repository.OrderItemRepository;
import com.stock_mate.BE.repository.OrderRepository;
import com.stock_mate.BE.service.filter.BaseSpecificationService;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.ProviderNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class OrderService extends BaseSpecificationService<Order, OrderResponse> {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserService userService;
    private final CustomerService customerService;
    private final FinishProductService finishProductService;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        User user = userService.findById(request.userID());
        Customer customer = customerService.findById(request.customerID());

        //create order
        Order order = new Order();
        order.setUser(user);
        order.setCustomer(customer);
        orderRepository.save(order);
        List<OrderItem> items = new ArrayList<>();

        //order item
        for (OrderItemResquest item : request.items()) {
            FinishProduct product = finishProductService.findById(item.fgID());
            OrderItem i = new OrderItem();
            i.setOrder(order);
            i.setFinishProduct(product);
            i.setQuantity(item.quantity());
            orderItemRepository.save(i);
            items.add(i);
        }
        order.setOrderItems(items);
        return orderMapper.toOrderResponse(order);
    }

    public OrderResponse findById(String orderId) {
        return orderMapper.toOrderResponse(orderRepository.findById(orderId)
                .orElseThrow(() -> new ProviderNotFoundException("Order not found")));
    }

    public boolean deleteById(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ProviderNotFoundException("Order not found"));
        orderRepository.delete(order);
        return true;
    }


    @Override
    protected JpaSpecificationExecutor<Order> getRepository() {
        return orderRepository;
    }

    @Override
    protected Function<Order, OrderResponse> getMapper() {
        return orderMapper::toOrderResponse;
    }

    @Override
    protected Specification<Order> buildSpecification(String searchTerm) {
        return (root, query, cb) -> {
            //if searchTerm is null => no condition
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return cb.conjunction();
            }
            return getPredicate(searchTerm, root, cb);
        };
    }

    private Predicate getPredicate(String searchTerm, Root<Order> root, CriteriaBuilder cb) {
        String searchPattern = "%" + searchTerm.toLowerCase() + "%";
        // join sang bảng User và Customer
        Join<Order, User> userJoin = root.join("user", JoinType.LEFT);
        Join<Order, Customer> customerJoin = root.join("customer", JoinType.LEFT);
        return cb.or(
                cb.like(cb.lower(root.get("code")), searchPattern),
                cb.like(cb.lower(root.get("orderID")), searchPattern),
                cb.like(cb.lower(userJoin.get("fullName")), searchPattern),
                cb.like(cb.lower(customerJoin.get("customerName")), searchPattern)
        );
    }
}
