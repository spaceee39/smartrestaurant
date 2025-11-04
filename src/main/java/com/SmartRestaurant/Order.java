package com.SmartRestaurant;


import jakarta.persistence.*;
import lombok.Data;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "Orders")
public class Order {

    @Id
    @Column(name = "chatid")
    Long chatid;

    @Column(name = "customersname")
    String name;

    @Column(name = "tableid")
    int tableid;

    @Column(name = "status")
    OrderStatus status;

    enum OrderStatus{
        new_order;
    }

    @ManyToMany(
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY
    )
    @JoinTable(
            name = "order_food",
            joinColumns = @JoinColumn(name = "order_chatid"),
            inverseJoinColumns = @JoinColumn(name = "food_id")
    )
    List<Food> FoodInOrder = new ArrayList<>();

    @ManyToMany(
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY
    )
    @JoinTable(
            name = "order_drink",
            joinColumns = @JoinColumn(name = "order_chatid"),
            inverseJoinColumns = @JoinColumn(name = "drink_id")
    )
    List<Drink> DrinkInOrder = new ArrayList<>();

    public Order(Long chatid) {
        this.chatid = chatid;
    }

    public Order() {
    }

    public void addFood(Food food){
        FoodInOrder.add(food);
    }

    public void addDrink(Drink drink){
        DrinkInOrder.add(drink);
    }

}
