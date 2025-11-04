package com.SmartRestaurant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
public class MessageConstructor {

    @Autowired
    FoodRepository foodRepository;
    @Autowired
    OrderRepository orderRepository;

    @Autowired
    DrinkRepository drinkRepository;

    public void Greetingmessage(long chatid, TelegramClient client){
        String text = "Бот запущен!";
        SendMessage sendMessage = new SendMessage(String.valueOf(chatid), text);
        try {
            client.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


    public void Menumessage(long chatid, TelegramClient client){
        String greeting = "✨ <b>Добро пожаловать в приложение бара семьи Вайн! 👋</b>\n\n" +
                "<i>Мы рады видеть вас! Здесь вы можете:</i>\n\n" +
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

    public void createNewOrder(long chatid, TelegramClient client){
        String text =   "Введите ваше имя на которое мы составим заказ: \n" +
                        "(Ваш игровой ник)";

        SendMessage sendMessage = new SendMessage(String.valueOf(chatid), text);
        try {
            client.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


    public void MenuEat(long chatid, TelegramClient client, int step){
        String text = "Для того чтобы выбрать какое то блюдо - нажмите на его номер на клавиатуре под фото.";
        InputFile file = new InputFile(new File("C:\\Users\\Leonid\\IdeaProjects\\smartrestaurant\\src\\main\\resources\\images\\Eat.png"));
        SendPhoto sendPhoto = new SendPhoto(String.valueOf(chatid), file);
        sendPhoto.setCaption(text);
        List<Food> food = foodRepository.findAll();
        List<InlineKeyboardRow> keyboardRows = new ArrayList<>();

        // Инициализируем currentrow ДО цикла
        InlineKeyboardRow currentrow = new InlineKeyboardRow();
        keyboardRows.add(currentrow);

        for (int i = 1; i <= food.size(); i++) {
            // Если текущая строка заполнена (3 кнопки), создаем новую
            if (i % 3 == 1 && i != 1) { // i != 1 чтобы не создавать новую строку для первой кнопки
                currentrow = new InlineKeyboardRow();
                keyboardRows.add(currentrow);
            }

            InlineKeyboardButton button = new InlineKeyboardButton(String.valueOf(i));
            button.setCallbackData(String.valueOf(i));
            currentrow.add(button);
        }

        // Добавляем кнопку "Пропустить"
        InlineKeyboardRow skipRow = new InlineKeyboardRow();
        InlineKeyboardButton skipButton = null;
        if(step==1){
            skipButton = new InlineKeyboardButton("Пропустить выбор еды");
        }
        if (step == 2) {
            skipButton = new InlineKeyboardButton("Продолжить и перейти к выбору напитка");
        }
        skipButton.setCallbackData("skipEat");
        skipRow.add(skipButton);
        keyboardRows.add(skipRow);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(keyboardRows);
        sendPhoto.setReplyMarkup(inlineKeyboardMarkup);
        try {
            client.execute(sendPhoto);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void MenuDrink(long chatid, TelegramClient client, int step){
        String text = "Для того чтобы выбрать какой либо напиток - нажмите на его номер на клавиатуре под фото.";
        InputFile file = new InputFile(new File("C:\\Users\\Leonid\\IdeaProjects\\smartrestaurant\\src\\main\\resources\\images\\Drinks.png"));
        SendPhoto sendPhoto = new SendPhoto(String.valueOf(chatid), file);
        sendPhoto.setCaption(text);
        List<Drink> drink = drinkRepository.findAll();
        List<InlineKeyboardRow> keyboardRows = new ArrayList<>();

        // Инициализируем currentrow ДО цикла
        InlineKeyboardRow currentrow = new InlineKeyboardRow();
        keyboardRows.add(currentrow);

        for (int i = 1; i <= drink.size(); i++) {
            // Если текущая строка заполнена (3 кнопки), создаем новую
            if (i % 3 == 1 && i != 1) { // i != 1 чтобы не создавать новую строку для первой кнопки
                currentrow = new InlineKeyboardRow();
                keyboardRows.add(currentrow);
            }

            InlineKeyboardButton button = new InlineKeyboardButton(String.valueOf(i));
            button.setCallbackData(String.valueOf(i));
            currentrow.add(button);
        }

        // Добавляем кнопку "Пропустить"
        InlineKeyboardRow skipRow = new InlineKeyboardRow();
        InlineKeyboardButton skipButton = null;
        if(step==1){
            skipButton = new InlineKeyboardButton("Пропустить выбор напитка");
        }
        if (step == 2) {
            skipButton = new InlineKeyboardButton("Продолжить ");
        }
        skipButton.setCallbackData("skipDrink");
        skipRow.add(skipButton);
        keyboardRows.add(skipRow);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(keyboardRows);
        sendPhoto.setReplyMarkup(inlineKeyboardMarkup);
        try {
            client.execute(sendPhoto);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void SuccessfulAdded(long chatid, TelegramClient client){
        String text = "Вы успешно добавили продукт в корзину";
        SendMessage sendMessage = new SendMessage(String.valueOf(chatid), text);
        try {
            client.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void message(String text, long chatid, TelegramClient client){
        SendMessage sendMessage = new SendMessage(String.valueOf(chatid), text);
        try {
            client.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void messagebill(String text, long chatid, TelegramClient client){
        SendMessage sendMessage = new SendMessage(String.valueOf(chatid), text);
        sendMessage.setParseMode("HTML");

        List<InlineKeyboardRow> rows = new ArrayList<>();
        InlineKeyboardRow row = new InlineKeyboardRow();
        InlineKeyboardButton b1 = new InlineKeyboardButton("Оформить заказ");
        b1.setCallbackData("New_0rder");
        row.add(b1);
        rows.add(row);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        try {
            client.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void selectTable(long chatid, TelegramClient client){
        InputFile file = new InputFile(new File("C:\\Users\\Leonid\\IdeaProjects\\smartrestaurant\\src\\main\\resources\\images\\tables.png"));
        SendPhoto sendPhoto = new SendPhoto(String.valueOf(chatid), file);
        sendPhoto.setCaption("Выберите номер вашего столика!");

        List<InlineKeyboardRow> rows = new ArrayList<>();
        InlineKeyboardRow row1 = new InlineKeyboardRow();
        InlineKeyboardRow row2 = new InlineKeyboardRow();
        InlineKeyboardButton b1 = null;
        for (int i = 1;i<=8;i++){
            if(i<=4){
                b1 = new InlineKeyboardButton(String.valueOf(i));
                b1.setCallbackData(String.valueOf(i));
                row1.add(b1);

            }else {
                b1 = new InlineKeyboardButton(String.valueOf(i));
                b1.setCallbackData(String.valueOf(i));
                row2.add(b1);
            }
        }
        rows.add(row1);
        rows.add(row2);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);
        sendPhoto.setReplyMarkup(inlineKeyboardMarkup);

        try {
            client.execute(sendPhoto);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

}
