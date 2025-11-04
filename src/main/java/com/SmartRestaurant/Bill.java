package com.SmartRestaurant;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

public class Bill {
    Long chatid;

    String name;

    int tableid;

    List<Food> FoodInOrder = new ArrayList<>();

    List<Drink> DrinkInOrder = new ArrayList<>();

    int bill;

    public Bill(Order order) {
        tableid = order.getTableid();
        chatid = order.getChatid();
        name = order.getName();
        FoodInOrder = order.getFoodInOrder();
        DrinkInOrder = order.getDrinkInOrder();
        for (Food food : FoodInOrder){
            bill += food.getPrice();
        }
        for (Drink drink : DrinkInOrder){
            bill += drink.getPrice();
        }
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();

        sb.append("<b>✨ Ваш заказ:</b>\n\n")
                .append("<i>Заказчик: ").append(name).append("</i>\n")
                .append("<i>Номер столика: ").append(tableid).append("</i>\n\n")
                .append("<code>Ваш заказ:</code>\n\n");

        // Добавляем блюда
        if (!FoodInOrder.isEmpty()) {
            sb.append("<b>🍽️ Блюда:</b>\n");
            for (Food food : FoodInOrder) {
                sb.append("• ").append(food.getName())
                        .append(" - ").append(food.getPrice()).append(" $.\n");
            }
            sb.append("\n");
        }

        // Добавляем напитки
        if (!DrinkInOrder.isEmpty()) {
            sb.append("<b>🥤 Напитки:</b>\n");
            for (Drink drink : DrinkInOrder) {
                sb.append("• ").append(drink.getName())
                        .append(" - ").append(drink.getPrice()).append(" $.\n");
            }
            sb.append("\n");
        }

        // Если оба списка пусты
        if (FoodInOrder.isEmpty() && DrinkInOrder.isEmpty()) {
            sb.append("🔄 Заказ пуст\n\n");
        }

        // Финальный чек
        sb.append("———————————————\n")
                .append("<b>💰 Итого: ").append(bill).append(" $.</b>");

        return sb.toString();
    }
}
