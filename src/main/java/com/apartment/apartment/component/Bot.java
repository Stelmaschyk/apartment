package com.apartment.apartment.component;

import com.apartment.apartment.exception.TelegramException;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
@Getter
public class Bot extends TelegramLongPollingBot {
    @Value("${bot.name}")
    private String botUserName;
    @Value("${bot.token}")
    private String botToken;
    private final Set<Long> adminChatIds = new HashSet<>();

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String text = update.getMessage().getText();

            if (text.equals("/start")) {
                adminChatIds.add(update.getMessage().getChatId());
                sendMessage(chatId, "Welcome! You are now subscribed to notifications.");
            }
        }
    }

    public void sendMessage(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new TelegramException("Error while sending message");
        }
    }

    public void sendMessageToAdmins(String text) {
        for (Long chatId : adminChatIds) {
            sendMessage(chatId.toString(), text);
        }
    }
}
