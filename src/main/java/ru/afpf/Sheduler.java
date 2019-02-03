package ru.afpf;

import java.util.TimerTask;

import static java.lang.Integer.parseInt;


class Sheduler extends TimerTask {
    AnsverInterface ansverInterface;
    Bot_t bot_t;
    Sheduler(AnsverInterface ansverInterface, Bot_t bot_t){
        this.ansverInterface = ansverInterface;
        this.bot_t = bot_t;
    }
    public void run() {
        int intPogrebT = parseInt(ansverInterface.getAnsver("pogrebT"));
        System.out.println("pogreb "+ intPogrebT);
        if (intPogrebT <= 4) {
            bot_t.sendMess(192211047, "Низкая температура в погребе! Всего "+intPogrebT+" градуса!");
        }

    }

}
