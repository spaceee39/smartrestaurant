package com.SmartRestaurant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.HashMap;
import java.util.Map;

import static com.SmartRestaurant.MessageConstructor.Greetingmessage;
import static com.SmartRestaurant.MessageConstructor.Menumessage;

@Component("bot")
public class Controller_bot implements LongPollingSingleThreadUpdateConsumer {

    private TelegramClient client;

    @Autowired
    Service service;

    Map<Long, userstate> userstates = new HashMap<>();


    enum userstate{
        choooseTable, chooseMenu
    }


    //-------------------------

    @Override
    public void consume(Update update) {
        //getText
        if(update.hasMessage() && update.getMessage().hasText()){
            switch (update.getMessage().getText()){
                case "/start":
                    if(!service.checkreg(update.getMessage().getChatId())){
                        service.addNewUser(update.getMessage().getChatId(), update.getMessage().getChat().getUserName());
                    }
                    Greetingmessage(update.getMessage().getChatId(), client);
                case "/menu":
                    Menumessage(update.getMessage().getChatId(), client);
                    break;
            }
        }

        if(update.hasCallbackQuery() && !update.getCallbackQuery().getData().isEmpty()){
            switch (update.getCallbackQuery().getData()){
                case "CreateNewOrder":

            }
        }
    }


    public Controller_bot(@Value("${bot.token}") String token){
        client = new OkHttpTelegramClient(token);
    }
}
