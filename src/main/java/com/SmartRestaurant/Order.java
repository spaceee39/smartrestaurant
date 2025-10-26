package com.SmartRestaurant;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

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

    public Order(Long chatid) {
        this.chatid = chatid;
    }

    public Order() {
    }
}
