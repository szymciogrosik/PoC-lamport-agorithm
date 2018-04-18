package com.szymon;

import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;
import java.util.Random;

public class StartAllGenerals {

    public static void main(String[] args) {

        int LOYAL_GENERALS = 5;
        int TRAITOR_GENERALS = 2;
        int START_PORT = 4000;

        LinkedList<Integer> generalList = new LinkedList<>();
        LinkedList<Integer> traitorGeneralsNumbers = new LinkedList<>();

        // Sporządzanie listy portów dla aplikacji
        for(int i = 0; i < LOYAL_GENERALS + TRAITOR_GENERALS; i++)
            generalList.add(START_PORT + i);

        // Losowanie, który port ma być zdrajcą
        Random generator = new Random();
        while(traitorGeneralsNumbers.size() != TRAITOR_GENERALS) {
            handleTraitor(generator.nextInt(LOYAL_GENERALS + TRAITOR_GENERALS), traitorGeneralsNumbers);
        }

        // Uruchamianie wszystkich aplikacji
        for (int i = 0; i < LOYAL_GENERALS+TRAITOR_GENERALS; i++) {
            if(isLoyal(i, traitorGeneralsNumbers))
                new StartGeneral().start(args, true, generalList.get(i), generalList);
            else
                new StartGeneral().start(args, false, generalList.get(i), generalList);
        }

        // Wysyłanie sygnału startowego do generałów po uruchomieniu aplikacji
        for (int element : generalList) {
          String body = new RestTemplate()
              .getForEntity("http://localhost:" + element + "/startSendMessage", String.class)
              .getBody();
        }
    }

    private static boolean isLoyal(int actualNumber, LinkedList<Integer> traitorList) {
        for (int element : traitorList) {
            if(actualNumber == element)
                return false;
        }
        return true;
    }

    private static void handleTraitor(int generalNumber, LinkedList<Integer> traitorGeneralsNumbers) {
        boolean alreadyExists = false;
        for(Integer number: traitorGeneralsNumbers) {
            if(number == generalNumber) {
                alreadyExists = true;
                break;
            }
        }
        if(!alreadyExists) {
            traitorGeneralsNumbers.add(generalNumber);
        }
    }
}
