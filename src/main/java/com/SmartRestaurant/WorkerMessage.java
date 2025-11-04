package com.SmartRestaurant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.List;

@Component
public class WorkerMessage {

    @Autowired
    OrderRepository orderRepository;

    public void Nofitication(String text, long chatid, TelegramClient client){
        SendMessage sendMessage = new SendMessage(String.valueOf(chatid), text);
        try {
            client.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


    public void sendOrders(List<Order> orders, long chatid, TelegramClient client){
        if (orders.isEmpty()) {
            String emptyMessage = "📭 На данный момент активных заказов нет\n\n" +
                    "🔄 Заказы появятся здесь, как только клиенты их оформят";
            SendMessage message = new SendMessage(String.valueOf(chatid), emptyMessage);
            try {
                client.execute(message);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        // Создаем красивое сообщение БЕЗ Markdown
        StringBuilder sb = new StringBuilder();
        sb.append("📋 Активные заказы\n\n");
        sb.append("🎯 Всего заказов: ").append(orders.size()).append("\n");
        sb.append("────────────────\n\n");

        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            sb.append("🆔 Заказ #").append(i + 1).append("\n");
            sb.append("👤 Имя: ").append(order.getName()).append("\n");
            sb.append("🪑 Стол: ").append(order.getTableid()).append("\n");
            sb.append("💳 ID: ").append(order.getChatid()).append("\n");

            // Добавляем информацию о блюдах и напитках
            if (!order.getFoodInOrder().isEmpty()) {
                sb.append("🍽️ Блюда: ").append(order.getFoodInOrder().size()).append("\n");
            }
            if (!order.getDrinkInOrder().isEmpty()) {
                sb.append("🥤 Напитки: ").append(order.getDrinkInOrder().size()).append("\n");
            }
            sb.append("────────────────\n\n");
        }

        // Создаем инлайн-кнопки с правильным типом InlineKeyboardRow
        List<InlineKeyboardRow> keyboard = new ArrayList<>();
        InlineKeyboardRow currentRow = new InlineKeyboardRow();

        // Кнопки для каждого заказа (по 3 в строку)
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            InlineKeyboardButton button = InlineKeyboardButton.builder()
                    .text((i + 1) + "️⃣") // Номер заказа с эмодзи-цифрой
                    .callbackData("order_detail_" + order.getChatid())
                    .build();

            currentRow.add(button);

            // Каждые 3 кнопки в строку или последняя кнопка
            if (currentRow.size() == 3 || i == orders.size() - 1) {
                keyboard.add(currentRow);
                currentRow = new InlineKeyboardRow(); // Новая строка
            }
        }

        // Добавляем кнопку обновления
        InlineKeyboardRow refreshRow = new InlineKeyboardRow();
        refreshRow.add(InlineKeyboardButton.builder()
                .text("🔄 Обновить список")
                .callbackData("refresh_orders")
                .build());
        keyboard.add(refreshRow);

        // Добавляем кнопки для массовых действий (если заказов много)
        if (orders.size() > 5) {
            InlineKeyboardRow massActionRow = new InlineKeyboardRow();
            massActionRow.add(InlineKeyboardButton.builder()
                    .text("✅ Отметить все выполненными")
                    .callbackData("complete_all_orders")
                    .build());
            keyboard.add(massActionRow);
        }

        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboard(keyboard)
                .build();

        SendMessage message = SendMessage.builder()
                .chatId(String.valueOf(chatid))
                .text(sb.toString())
                //.parseMode("Markdown") // УБИРАЕМ Markdown полностью
                .replyMarkup(markup)
                .build();

        try {
            client.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void getOrderForWorker(long orderid, Long chatid, TelegramClient client){
        Order order = orderRepository.findById(orderid).orElse(null);

        if (order == null) {
            SendMessage errorMessage = SendMessage.builder()
                    .chatId(String.valueOf(chatid))
                    .text("❌ Заказ не найден")
                    .build();
            try {
                client.execute(errorMessage);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        // Создаем красивое сообщение с деталями заказа (без Markdown)
        StringBuilder sb = new StringBuilder();
        sb.append("📋 Детали заказа\n\n");
        sb.append("👤 Имя клиента: ").append(order.getName()).append("\n");
        sb.append("🪑 Номер стола: ").append(order.getTableid()).append("\n");
        sb.append("💳 ID заказа: ").append(order.getChatid()).append("\n");
        sb.append("📊 Статус: ").append(order.getStatus()).append("\n");
        sb.append("────────────────\n\n");

        // Добавляем блюда
        if (!order.getFoodInOrder().isEmpty()) {
            sb.append("🍽️ Блюда:\n");
            int totalFoodPrice = 0;
            for (Food food : order.getFoodInOrder()) {
                sb.append("   • ").append(food.getName())
                        .append(" - ").append(food.getPrice()).append(" $.\n");
                totalFoodPrice += food.getPrice();
            }
            sb.append("   Итого за блюда: ").append(totalFoodPrice).append(" $.\n\n");
        } else {
            sb.append("🍽️ Блюда: нет\n\n");
        }

        // Добавляем напитки
        if (!order.getDrinkInOrder().isEmpty()) {
            sb.append("🥤 Напитки:\n");
            int totalDrinkPrice = 0;
            for (Drink drink : order.getDrinkInOrder()) {
                sb.append("   • ").append(drink.getName())
                        .append(" - ").append(drink.getPrice()).append(" $.\n");
                totalDrinkPrice += drink.getPrice();
            }
            sb.append("   Итого за напитки: ").append(totalDrinkPrice).append(" $.\n\n");
        } else {
            sb.append("🥤 Напитки: нет\n\n");
        }

        // Общая сумма
        int totalBill = order.getFoodInOrder().stream().mapToInt(Food::getPrice).sum() +
                order.getDrinkInOrder().stream().mapToInt(Drink::getPrice).sum();

        sb.append("💰 Общая сумма: ").append(totalBill).append(" $.\n");
        sb.append("────────────────\n");

        // Создаем кнопки
        List<InlineKeyboardRow> keyboard = new ArrayList<>();

        // Кнопка закрыть заказ
        InlineKeyboardRow completeRow = new InlineKeyboardRow();
        completeRow.add(InlineKeyboardButton.builder()
                .text("✅ Закрыть заказ")
                .callbackData("complete_order_" + order.getChatid())
                .build());
        keyboard.add(completeRow);

        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboard(keyboard)
                .build();

        // Убираем parseMode или используем HTML если нужно
        SendMessage message = SendMessage.builder()
                .chatId(String.valueOf(chatid))
                .text(sb.toString())
                //.parseMode("Markdown") // убираем Markdown
                .replyMarkup(markup)
                .build();

        try {
            client.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    }

