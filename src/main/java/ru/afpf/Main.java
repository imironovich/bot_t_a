package ru.afpf;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Main {
    public static void main(String[] args){
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        Properties properties = new Properties();

        try {
            telegramBotsApi.registerBot(new Bot_t());
            System.out.println("Ya_startovallo");
            properties.load(new FileInputStream(new File("Bot.properties")));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        } catch (IOException e){
            System.out.println();
        }
    }

}
