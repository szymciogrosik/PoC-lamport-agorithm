package com.szymon;

import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;
import java.util.Random;

public class StartAllGenerals {

    public static void main(String[] args) {

        int LOYAL_GENERALS = 3;
        int TRAITOR_GENERALS = 1;
        int START_PORT = 4000;

        LinkedList<Integer> generalList = new LinkedList<>();
        LinkedList<Integer> traitorGeneralsNumbers = new LinkedList<>();

        // Sporządzanie listy portów dla aplikacji
        for(int i = 0; i < LOYAL_GENERALS + TRAITOR_GENERALS; i++)
            generalList.add(START_PORT + i);

        // Losowanie, który port ma być zdrajcą
        Random generator = new Random();
        for (int i = 0; i < TRAITOR_GENERALS; i++)
            traitorGeneralsNumbers.add(generator.nextInt(LOYAL_GENERALS + TRAITOR_GENERALS));

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
        boolean isLoyal = true;
        for (int element : traitorList) {
            if(actualNumber == element)
                isLoyal = false;
        }
        return isLoyal;
    }
}
