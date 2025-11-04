package com.SmartRestaurant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.HashMap;
import java.util.Map;

@Component("bot")
@Transactional
public class Controller_bot implements LongPollingSingleThreadUpdateConsumer {

    private TelegramClient client;

    @Autowired
    Service service;

    @Autowired
    WorkerService workerService;

    @Autowired
    MessageConstructor messageConstructor;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    FoodRepository foodRepository;

    @Autowired
    DrinkRepository drinkRepository;

    Map<Long, userstate> userstates = new HashMap<>();


    public enum userstate{
        chooseTable, choooseName, chooseMenuEat, chooseMenuDrinks, finalstep
    }


    //-------------------------

    @Override
    public void consume(Update update) {
            if(service.checkStatus(update) == User.Role.worker){
                if(update.hasCallbackQuery() && update.getCallbackQuery().getData().contains("order_detail_")){
                    workerService.sendOrderWork(update.getCallbackQuery().getData(), update.getCallbackQuery().getFrom().getId(), client);
                }
                if(update.hasCallbackQuery() && update.getCallbackQuery().getData().contains("complete_order_")){
                    workerService.completeOrder(update.getCallbackQuery().getData(), update.getCallbackQuery().getFrom().getId(), client);
                }


                if(update.hasMessage() && update.getMessage().hasText()) {
                    switch (update.getMessage().getText()) {
                        case "/menu":
                            workerService.checkNewOrders(client, update.getMessage().getChatId());
                            break;
                    }
                }
            } else {
                if (update.hasMessage() && update.getMessage().hasText()) {   //getText
                    switch (update.getMessage().getText()) {
                        case "/start":
                            service.addNewUser(update.getMessage().getChatId(), update.getMessage().getFrom().getUserName());
                        case "/menu":
                            if (service.checkreg(update.getMessage().getChatId()) == false)
                                service.addNewUser(update.getMessage().getChatId(), update.getMessage().getFrom().getUserName());
                            userstates.remove(update.getMessage().getChatId()); // убрать статус у этого чатид
                            messageConstructor.Menumessage(update.getMessage().getChatId(), client); //sms
                            break;

                        case "/test":
                            service.debugFood(update.getMessage().getChatId());
                            break;
                    }
                }

                if (update.hasCallbackQuery() && !update.getCallbackQuery().getData().isEmpty()) { // CALLBACKDATA
                    switch (update.getCallbackQuery().getData()) {
                        case "CreateNewOrder":
                            if(!service.haveOrder(update.getCallbackQuery().getFrom().getId())) {
                                userstates.put(update.getCallbackQuery().getFrom().getId(), userstate.choooseName);
                                messageConstructor.createNewOrder(update.getCallbackQuery().getFrom().getId(), client); // sms
                            }else {
                                messageConstructor.message("У вас уже есть активный заказ!\n" +
                                        "Вы сможете оформить новый после того как ваш текущий заказ будет завершен!\n" +
                                        "Благодарим за понимание.", update.getCallbackQuery().getFrom().getId(), client);
                            }
                            break;

                        case "skipEat":
                            userstates.put(update.getCallbackQuery().getFrom().getId(), userstate.chooseMenuDrinks);
                            messageConstructor.MenuDrink(update.getCallbackQuery().getFrom().getId(), client, 1);      // отправка меню с напитками
                            break;

                        case "skipDrink":
                            userstates.put(update.getCallbackQuery().getFrom().getId(), userstate.finalstep);// тут присылается составленный заказ и счет
                            messageConstructor.messagebill(service.getBill(update.getCallbackQuery().getFrom().getId()), update.getCallbackQuery().getFrom().getId(), client);

                            break;
                        case "New_0rder":
                            messageConstructor.message("\uD83C\uDF7D\uFE0F *Ваш заказ успешно оформлен!* \uD83C\uDF7D\uFE0F\n" + "\n" + "Приятного аппетита! Ожидайте за столиком ✨", update.getCallbackQuery().getFrom().getId(), client);
                            service.confirmtheorder(update.getCallbackQuery().getFrom().getId());
                            workerService.NewOrderNotification(client);
                            break;
                    }
                }




                if (update.hasCallbackQuery() && userstates.get(update.getCallbackQuery().getFrom().getId()) == userstate.chooseMenuEat) {     // тут уже принимается реакция на сообщение с меню еды
                    service.addFood(Integer.parseInt(update.getCallbackQuery().getData()), update.getCallbackQuery().getFrom().getId());
                    messageConstructor.SuccessfulAdded(update.getCallbackQuery().getFrom().getId(), client);
                    messageConstructor.MenuEat(update.getCallbackQuery().getFrom().getId(), client, 2);
                }


                if (update.hasCallbackQuery() && userstates.get(update.getCallbackQuery().getFrom().getId()) == userstate.finalstep) {     // тут уже принимается реакция после нажатия кнопки на меню с напитками
                }


                if (update.hasMessage() && update.getMessage().hasText() && userstates.containsKey(update.getMessage().getChatId())) {    // Создание нового заказа
                    if (userstates.get(update.getMessage().getChatId()) == userstate.choooseName) {
                        service.CreateNewOrder(update.getMessage().getChatId(), update.getMessage().getText()); // создает новый заказ в базе, устанавливает ему имя
                        userstates.put(update.getMessage().getChatId(), userstate.chooseTable); // добавляем статус в userstate
                        messageConstructor.selectTable(update.getMessage().getChatId(), client);
                    }
                }

                if (update.hasCallbackQuery() && userstates.get(update.getCallbackQuery().getFrom().getId()) == userstate.chooseTable) {
                    service.setTable(update.getCallbackQuery().getFrom().getId(), Integer.parseInt(update.getCallbackQuery().getData()));
                    messageConstructor.MenuEat(update.getCallbackQuery().getFrom().getId(), client, 1);
                    userstates.put(update.getCallbackQuery().getFrom().getId(), userstate.chooseMenuEat);
                }

                if (update.hasCallbackQuery() && userstates.get(update.getCallbackQuery().getFrom().getId()) == userstate.chooseMenuDrinks ) {// тут уже принимается реакция на сообщение с меню напитков
                    if(!update.getCallbackQuery().getData().equals("skipEat")){
                        service.addDrink(Integer.parseInt(update.getCallbackQuery().getData()), update.getCallbackQuery().getFrom().getId());
                        messageConstructor.SuccessfulAdded(update.getCallbackQuery().getFrom().getId(), client);
                        messageConstructor.MenuDrink(update.getCallbackQuery().getFrom().getId(), client, 2);
                    }
                }
            }
    }

    public Controller_bot(@Value("${bot.token}") String token) {
            client = new OkHttpTelegramClient(token);
        }

}
