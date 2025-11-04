package com.SmartRestaurant;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table
@Data
public class Drink {

    @Id
    @Column(name = "MenuNumber")
    Integer id;

    @Column(name = "Drink")
    String name;

    @Column(name = "Price")
    Integer price;
}

