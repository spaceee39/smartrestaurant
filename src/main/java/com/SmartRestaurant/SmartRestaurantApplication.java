package com.SmartRestaurant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@SpringBootApplication
public class SmartRestaurantApplication {
	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(SmartRestaurantApplication.class, args);
		String token = context.getEnvironment().getProperty("bot.token");
		Controller_bot bot = context.getBean("bot", Controller_bot.class);
		TelegramBotsLongPollingApplication telegramBotsLongPollingApplication = new TelegramBotsLongPollingApplication();
        try {
            telegramBotsLongPollingApplication.registerBot(token, bot );
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

}
