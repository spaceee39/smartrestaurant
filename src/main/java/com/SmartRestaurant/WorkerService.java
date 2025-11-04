package com.SmartRestaurant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Arrays;
import java.util.List;

@Service
public class WorkerService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    WorkerMessage workerMessage;

    @Autowired
    MessageConstructor messageConstructor;


    @Transactional
    public void NewOrderNotification(TelegramClient client){
        List<User> users = userRepository.findAllByRole(User.Role.worker);
        for(User user : users){
            workerMessage.Nofitication("Поступил новый заказ!", user.getChatid(), client);

        }
    }

    @Transactional
    public void checkNewOrders(TelegramClient client, long chatid){
        List<Order> orders = orderRepository.findByStatus(Order.OrderStatus.new_order);
        workerMessage.sendOrders(orders, chatid, client);
    }


    public void sendOrderWork(String data, long chatid, TelegramClient client ){
    long orderid = Arrays.stream(data.split("_"))
                .filter(part -> part.matches("\\d+")) // оставляем только цифры
                .findFirst()
                .map(Long::valueOf)
                .orElse(null);

    workerMessage.getOrderForWorker(orderid , chatid, client );
    }

    @Transactional
    public void completeOrder(String data, long chatid, TelegramClient client){
        long orderid = Arrays.stream(data.split("_"))
                .filter(part -> part.matches("\\d+")) // оставляем только цифры
                .findFirst()
                .map(Long::valueOf)
                .orElse(null);
        Order order = orderRepository.findById(orderid).orElse(null);
        order.setStatus(null);
        orderRepository.save(order);
        messageConstructor.message("Вы закрыли заказ!",chatid, client);
    }

}
