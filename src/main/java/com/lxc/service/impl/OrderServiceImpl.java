package com.lxc.service.impl;

import com.lxc.constants.OrderStatus;
import com.lxc.entity.Order;
import com.lxc.repository.OrderRepository;
import com.lxc.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final int PAGE_SIZE = 10;

    @Autowired
    OrderRepository orderRepository;

    @Override
    public Page<Order> findByUserId(Integer userId, int pageNum) {

        Pageable pageable = PageRequest.of(pageNum, PAGE_SIZE, new Sort(Sort.Direction.ASC, "id"));
        return orderRepository.findByUserId(userId, pageable);
    }

    @Override
    public void save(Order order) {

        orderRepository.saveAndFlush(order);
    }

    @Override
    public void setStatus(OrderStatus status, Integer id) {

        try {
            Order order = orderRepository.getOne(id);
            order.changeStatusTo(status);
            orderRepository.saveAndFlush(order);
        } catch (EntityNotFoundException e) {
            log.error("OrderId = {} does not exist!", id);
        }
    }
}
