package ru.afpf;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;


public class Bot_auth {

    public static boolean checkUser(Integer userID) {
        int[] array = null;
        try (BufferedReader in = new BufferedReader(new FileReader("user.list")))
        {
            array = in.lines().mapToInt(Integer::parseInt).toArray();
        }
        catch (IOException | NumberFormatException e)
        {
            e.printStackTrace();
        }
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                if (array[i] == userID) {
                    System.out.println("User valid");
                    return true;
                }
            }
        }
        return false;
    }
 }
