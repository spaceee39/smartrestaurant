package com.SmartRestaurant;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "visitors")
public class User {
    @Id
    @Column(name = "chatid", nullable = false)
    private Long chatid;

    @Column(name = "name")
    private String username;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    enum Role{
        visitor, Admin, worker
    }

    public User(Long chatid, String username){
        this.chatid=chatid;
        this.username=username;
        role=Role.visitor;
    }

    public User(){

    }

    public String toString(){
        return chatid+username+role;
    }
}
