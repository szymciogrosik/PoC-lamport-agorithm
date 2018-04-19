package com.szymon;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.HashMap;
import java.util.LinkedList;

@SpringBootApplication
public class StartGeneral {

    public void start(String[] args, boolean isLoyal, int serverPort, LinkedList<Integer> generalList, int rangeOfArmySize, String address) {
        HashMap<String, Object> props = new HashMap<>();
        props.put("server.port", serverPort);
        props.put("isLoyal", isLoyal);
        props.put("generalPortList", generalList);
        props.put("rangeOfArmySize", rangeOfArmySize);
        props.put("address", address);

        new SpringApplicationBuilder()
                .sources(StartGeneral.class)
                .properties(props)
                .run(args);
    }
}
