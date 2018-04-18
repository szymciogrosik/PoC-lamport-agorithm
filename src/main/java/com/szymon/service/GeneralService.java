package com.szymon.service;

import com.szymon.dto.GeneralValueDto;
import com.szymon.dto.GeneralsValuesDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

@Service
public class GeneralService {

    @Autowired
    Environment environment;

    public GeneralsValuesDto generalsValuesDto;
    public LinkedList<int[]> generalsVectors;

    @PostConstruct
    public void init() {
        this.generalsValuesDto = new GeneralsValuesDto(this.getListWithGeneralsPort().size(), this.getThisGeneralPort());
        this.generalsVectors = new LinkedList<>();
    }

    public LinkedList<Integer> getListWithGeneralsPort() {
        String[] stringListWithGeneralsPort = environment.getProperty("general.generalList").split(",");
        LinkedList<Integer> integerListWithGeneralsPort = new LinkedList<>();

        for (String element : stringListWithGeneralsPort)
            integerListWithGeneralsPort.add(Integer.valueOf(element));

        return integerListWithGeneralsPort;
    }

    public int getThisGeneralPort() {
        return Integer.valueOf(environment.getProperty("general.serverPort"));
    }

    public boolean getThisGeneralIsLoyal() {
        return Boolean.valueOf(environment.getProperty("general.isLoyal"));
    }

    public int getGeneralNumber(int port) {
        int number = -1;

        for (int i = 0; i < this.getListWithGeneralsPort().size() && number == -1; i++) {
            if(this.getListWithGeneralsPort().get(i) == port)
                number = i;
        }

        return number;
    }

    public GeneralValueDto getNewGeneral() {
        return GeneralValueDto.builder()
                .port(this.getThisGeneralPort())
                .number(new Random().nextInt(Integer.valueOf(environment.getProperty("rangeOfArmySize"))))
                .build();
    }

    public boolean isReceivedValuesFromGeneralsComplete() {
        boolean flag = true;

        for (int i = 0; i < this.generalsValuesDto.getReceivedValuesFromGenerals().length && flag; i++) {
            if(this.generalsValuesDto.getReceivedValuesFromGenerals()[i] == -1)
                flag = false;
        }

        return flag;
    }

    public boolean isReceivedVectorsFromGeneralsComplete() {
        boolean isComplete = false;

        if(this.generalsVectors.size() == this.getListWithGeneralsPort().size()-1)
            isComplete = true;

        return isComplete;
    }

    public String getGeneralVectorsFromGeneralsInString() {
        StringBuilder generalsVectorInString = new StringBuilder();

        for (int[] element : this.generalsVectors)
            generalsVectorInString.append(Arrays.toString(element)).append(" ");

        return generalsVectorInString.toString();
    }

    public void findFinalSolution() {
        LinkedList<Integer> finalSolution = new LinkedList<>();

        for (int i = 0; i < generalsVectors.size(); i++) {
            for (int j = 0; j < generalsVectors.get(i).length; j++) {
                // Todo: Algorytm porównywania wartości

            }
        }
    }
}
