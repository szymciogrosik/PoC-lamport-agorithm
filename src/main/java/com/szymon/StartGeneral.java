package com.szymon;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.HashMap;
import java.util.LinkedList;

@SpringBootApplication
public class StartGeneral {

    private final int rangeOfArmySize = 10;

    public void start(String[] args, boolean isLoyal, int serverPort, LinkedList<Integer> generalList) {
        HashMap<String, Object> props = new HashMap<>();
        props.put("server.port", serverPort);
        props.put("general.serverPort", serverPort);
        props.put("general.isLoyal", isLoyal);
        props.put("general.generalList", generalList);
        props.put("rangeOfArmySize", rangeOfArmySize);

        new SpringApplicationBuilder()
                .sources(StartGeneral.class)
                .properties(props)
                .run(args);
    }
}
