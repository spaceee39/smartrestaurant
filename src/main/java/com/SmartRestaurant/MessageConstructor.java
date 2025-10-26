package com.SmartRestaurant;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.List;

@Component
public class MessageConstructor {

    public static void Greetingmessage(long chatid, TelegramClient client){
        String text = "Бот уже запущен!";
        SendMessage sendMessage = new SendMessage(String.valueOf(chatid), text);
        try {
            client.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


    public static void Menumessage(long chatid, TelegramClient client){
        String greeting = "✨ <b>Добро пожаловать в Smart Restaurant! 👋</b>\n\n" +
                "<i>Мы рады видеть вас! В нашем боте вы можете:</i>\n\n" +
                "🍽 <b>Заказать блюда</b> - выбирайте из нашего меню\n" +
                "📅 <b>Забронировать столик</b> <code>(скоро)</code>\n" +
                "⏰ <b>Заказать доставку</b> <code>(скоро)</code>\n\n" +
                "<u>Для продолжения выберите опцию ниже</u> 👇";

        SendMessage sendMessage = new SendMessage(String.valueOf(chatid), greeting);
        sendMessage.setParseMode("HTML");

        List<InlineKeyboardRow> keyboardRows = new ArrayList<>();
        InlineKeyboardRow row1 = new InlineKeyboardRow();
        InlineKeyboardButton doOrder = new InlineKeyboardButton("Cделать заказ");
        doOrder.setCallbackData("CreateNewOrder");
        row1.add(doOrder);
        keyboardRows.add(row1);
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(keyboardRows);
        sendMessage.setReplyMarkup(keyboardMarkup);

        try {
            client.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static void createNewOrder(long chatid, TelegramClient client){
        String text = "Выберите столик за который вы бы хотели оформить заказ";
        SendMessage sendMessage = new SendMessage(String.valueOf(chatid), text);
    }

}
