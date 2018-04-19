package com.szymon.service;

import com.szymon.dto.GeneralValueDto;
import com.szymon.dto.GeneralsValuesDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

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
        String[] stringListWithGeneralsPort = environment.getProperty("generalPortList").split(",");
        LinkedList<Integer> integerListWithGeneralsPort = new LinkedList<>();

        for (String element : stringListWithGeneralsPort)
            integerListWithGeneralsPort.add(Integer.valueOf(element));

        return integerListWithGeneralsPort;
    }

    public int getThisGeneralPort() {
        return Integer.valueOf(environment.getProperty("server.port"));
    }

    public boolean getThisGeneralIsLoyal() {
        return Boolean.valueOf(environment.getProperty("isLoyal"));
    }

    private String getAddress() { return environment.getProperty("address");}

    private int getRangeOfArmySize() {
        return Integer.valueOf(environment.getProperty("rangeOfArmySize"));
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
                .number(new Random().nextInt(getRangeOfArmySize()))
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
        StringBuilder generalsVectorInString = new StringBuilder()
                .append("\n")
                .append("Generał ")
                .append(this.getGeneralNumber(this.getThisGeneralPort()))
                .append(" otrzymał wektory:")
                .append("\n");

        for (int[] element : this.generalsVectors)
            generalsVectorInString.append(Arrays.toString(element)).append("\n");

        generalsVectorInString
                .append("Ustalony konsensus: \n")
                .append(this.findFinalSolution())
                .append("\n");

        return generalsVectorInString.toString();
    }

    public String buildUri(int port, String query) {
        return getAddress() + port + query;
    }

    private int getRandomArmySize() {
        return new Random().nextInt(getRangeOfArmySize());
    }

    public void saveArmySize(GeneralValueDto general) {
        if(this.getThisGeneralIsLoyal())
            this.generalsValuesDto.getReceivedValuesFromGenerals()[this.getGeneralNumber(general.getPort())] = general.getNumber();
        else
            this.generalsValuesDto.getReceivedValuesFromGenerals()[this.getGeneralNumber(general.getPort())] = this.getRandomArmySize();
    }

    private String findFinalSolution() {
        // Dodaj elementy kolumny do kolekcji Set
        LinkedList<Integer> finalSollution = new LinkedList<>();

        // Kolumna
        for (int i = 0; i < generalsVectors.get(0).length; i++) {
            // Dodaj elementy kolumny do kolekcji Set
            Set<Integer> collectionWithUniQueValues = new HashSet<>();
            LinkedList<ElementToCompare> columnSolution = new LinkedList<>();

            // Wiersz
            for (int j = 0; j < generalsVectors.size(); j++) {
                collectionWithUniQueValues.add(generalsVectors.get(j)[i]);
            }
            for (int element : collectionWithUniQueValues) {
                int counter = 0;
                for (int[] generalsVector : generalsVectors) {
                    if (element == generalsVector[i])
                        counter++;
                }
                columnSolution.add(new ElementToCompare(element, counter));
            }

            columnSolution.sort((o1, o2) -> o2.counter - o1.counter);

            if(columnSolution.getFirst().counter >= (this.getListWithGeneralsPort().size()-1)/2 + 1)
                finalSollution.add(columnSolution.getFirst().number);
            else
                finalSollution.add(-1);
        }

        return finalSollution.toString();
    }

    @AllArgsConstructor
    private class ElementToCompare {
        @Getter
        private int number;
        @Getter
        private int counter;
    }
}
