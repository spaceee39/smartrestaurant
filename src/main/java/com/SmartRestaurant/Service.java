package com.SmartRestaurant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Service {
    @Autowired
    UserRepository userRepository;

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


    public Order CreateNewOrder(long chatid, String name, int tableid){
        Order order = new Order(chatid);
        return order;
    }

}
