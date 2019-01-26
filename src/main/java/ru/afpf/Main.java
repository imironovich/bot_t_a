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
        Snmp_pdu snmp_pdu = null;
        try {
            snmp_pdu = new Snmp_pdu();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
