package ru.afpf;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Timer;


public class Main {
    public static void main(String[] args){
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        Properties properties = new Properties();

        Authable authable = new Bot_auth();
        AnsverInterface ansverInterface = new Snmp_pdu(properties);
        Bot_t bot_t = new Bot_t(ansverInterface, authable, properties);

        try {
            properties.load(new FileInputStream(new File("Bot.properties")));
            telegramBotsApi.registerBot(bot_t);
            System.out.println("Ya_startovallo");

        } catch (TelegramApiException e) {
            e.printStackTrace();
        } catch (IOException e){
            System.out.println();
        }
        Sheduler hourJob = new Sheduler(ansverInterface, bot_t);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(hourJob, 0, 12*3600*1000);
    }

}
