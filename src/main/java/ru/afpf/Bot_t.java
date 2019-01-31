package ru.afpf;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class Bot_t extends TelegramLongPollingBot {

    private Properties properties;
    private Authable authable;
    private AnsverInterface ansverInterface;

    Bot_t(Properties properties, Authable authable, AnsverInterface ansverInterface) {
        this.properties =  properties;
        this.authable = authable;
        this.ansverInterface = ansverInterface;

        try {
            properties.load(new FileInputStream(new File("Bot.properties")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void onUpdateReceived(Update update) {
        try{
            Message message = update.getMessage();
            boolean validUser;
            Integer userID = update.getMessage().getFrom().getId();
            System.out.println("UserIDD "+userID);

            validUser = authable.checkUser(userID);
            if (!validUser){
                System.out.println("Invalid UserID");
                return;}


            if (message != null && message.hasText()) {
                switch (message.getText()) {
                    case "/start":
                        sendMsg(message, "поехали");
                        System.out.println(message.getText());
                        break;
                    case "Погреб":
                        sendMsg(message, ansverInterface.getAnsver("pogreb"));
                        System.out.println(message.getText());
                        break;
                    case "Прихожая":
                        sendMsg(message, ansverInterface.getAnsver("prihojaya"));
                        System.out.println(message.getText());
                        break;
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMsg (Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);

        // Создаем клавиуатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        // Создаем список строк клавиатуры
        List<KeyboardRow> keyboard = new ArrayList<>();

        // Первая строчка клавиатуры
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        // Добавляем кнопки в первую строчку клавиатуры
        keyboardFirstRow.add("Погреб");
        keyboardFirstRow.add("Прихожая");

        // Добавляем все строчки клавиатуры в список
        keyboard.add(keyboardFirstRow);

        // и устанваливаем этот список нашей клавиатуре
        replyKeyboardMarkup.setKeyboard(keyboard);

        sendMessage.setChatId(message.getChatId().toString());
        //sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }



    public String getBotUsername() {
        return properties.getProperty("BOT_NAME");
    }

    public String getBotToken() {
        return properties.getProperty("BOT_TOKEN");
    }

}

