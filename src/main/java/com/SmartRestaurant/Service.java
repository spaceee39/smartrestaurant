package com.SmartRestaurant;

import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class Service {
    @Autowired
    UserRepository userRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    FoodRepository foodRepository;

    @Autowired
    DrinkRepository drinkRepository;

    //reg


    public boolean checkreg(long chatid){
        boolean b = true;
        if(userRepository.findById(chatid).isEmpty()){
            b = false;
        }
        return b;
    }

    public void addNewUser(long chatid, String username){
        User user = new User(chatid, username);
        userRepository.save(user);
        System.out.println("add new user");
    }


    //status
    public User.Role checkStatus(Update update){
        long chatid = 0;
        if(update.hasMessage()){
            chatid = update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            chatid = update.getCallbackQuery().getFrom().getId();
        }

        User user = userRepository.findById(chatid).orElse(null);
        return user != null ? user.getRole() : User.Role.visitor;
    }


    //Order

    public void CreateNewOrder(long chatid, String name){
        Order order = new Order(chatid);
        order.setName(name);
        orderRepository.save(order);
    }

    @Transactional
    public void addFood(int idfood, long orderchatid){
        Order order = orderRepository.findById(orderchatid).orElse(null);
        Food food = foodRepository.findById(idfood).orElse(null);
        order.addFood(food);
        orderRepository.save(order);
    }

    @Transactional
    public void debugFood(long chatid){
        Order oreder = orderRepository.findById(chatid).orElse(null);
        for(Food food : oreder.FoodInOrder){
            System.out.println(food.name);
        }
    }

    @Transactional
    public void addDrink(int idfood, long orderchatid){
        Order order = orderRepository.findById(orderchatid).orElse(null);
        Drink drink = drinkRepository.findById(idfood).orElse(null);
        order.addDrink(drink);

    }

    @Transactional
    public void setTable(Long orderchatid, int tableid){
        Order order = orderRepository.findById(orderchatid).orElse(null);
        order.setTableid(tableid);
        orderRepository.save(order);
    }

    @Transactional
    public String getBill(long chatid ){
        Order order = orderRepository.findById(chatid).orElse(null);
        Bill bill = new Bill(order);
        return bill.toString();
    }

    @Transactional
    public void confirmtheorder(long chatid) {
        Order order = orderRepository.findById(chatid).orElse(null);
        order.setStatus(Order.OrderStatus.new_order);
        orderRepository.save(order);
    }

    @Transactional
    public boolean haveOrder(long chatid){
        Order order = orderRepository.findById(chatid).orElse(null);

        // Если заказ не найден или статус null - возвращаем false
        if (order == null || order.getStatus() == null) {
            return false;
        }

        return true;
    }
}
