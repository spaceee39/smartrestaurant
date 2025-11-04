package com.SmartRestaurant;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table
@Data
public class Food {

    @Id
    @Column(name = "MenuNumber")
    Integer id;

    @Column(name = "Food")
    String name;

    @Column(name = "Price")
    Integer price;
}
