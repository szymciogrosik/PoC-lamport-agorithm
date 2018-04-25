package com.szymon;

import com.szymon.service.SaveToFileService;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public class StartAllGenerals {
    public static void main(String[] args) {

        // Uwaga wszystkich procesów musi być n >= 3t+1 gdzie t-liczba zdajców
        final int LOYAL_GENERALS      = 9;
        final int TRAITOR_GENERALS    = 4;
        final int START_PORT          = 4000;
        final int RANGE_OF_ARMY_SIZE  = 10;
        final String ADDRESS          = "http://localhost:";

        LinkedList<Integer> generalsPortsList = new LinkedList<>();
        LinkedList<Integer> traitorGeneralsNumbers = new LinkedList<>();

        // Sporządzanie listy portów dla aplikacji
        for(int i = 0; i < LOYAL_GENERALS + TRAITOR_GENERALS; i++)
            generalsPortsList.add(START_PORT + i);

        // Losowanie, który port ma być zdrajcą
        Random generator = new Random();
        while(traitorGeneralsNumbers.size() != TRAITOR_GENERALS) {
            handleTraitor(generator.nextInt(LOYAL_GENERALS + TRAITOR_GENERALS), traitorGeneralsNumbers);
        }

        // Sortowanie listy zdrajców
        Collections.sort(traitorGeneralsNumbers);

        // Zapis określonych numerów zdrajców do pliku score.txt
        new SaveToFileService().writeToNewFile(
                "Aplikacja powołująca: wylosowani zdrajcy to generałowie o numerach: " +
                        traitorGeneralsNumbers.toString() +
                        "\n"
        );

        // Uruchamianie wszystkich aplikacji
        for (int i = 0; i < LOYAL_GENERALS+TRAITOR_GENERALS; i++) {
            if(isLoyal(i, traitorGeneralsNumbers))
                new StartGeneral().start(args, true, generalsPortsList.get(i), generalsPortsList, RANGE_OF_ARMY_SIZE, ADDRESS);
            else
                new StartGeneral().start(args, false, generalsPortsList.get(i), generalsPortsList, RANGE_OF_ARMY_SIZE, ADDRESS);
        }

        // Wysyłanie sygnału startowego do generałów po uruchomieniu aplikacji
        for (int element : generalsPortsList) {
          String body = new RestTemplate()
              .getForEntity(ADDRESS + element + "/startSendMessage", String.class)
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
