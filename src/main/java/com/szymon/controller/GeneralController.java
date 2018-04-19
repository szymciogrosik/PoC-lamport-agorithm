package com.szymon.controller;
import com.szymon.dto.GeneralValueDto;
import com.szymon.dto.GeneralsValuesDto;
import com.szymon.service.GeneralService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Slf4j
@RestController
public class GeneralController {

    @Autowired
    private GeneralService generalService;

    // Kontroler odpowiedzialny za wystartowanie symulacji.
    @GetMapping("/startSendMessage")
    public ResponseEntity sendYourValueToAnotherGenerals() {
        GeneralValueDto general = generalService.getNewGeneral();
        generalService.generalsValuesDto.getReceivedValuesFromGenerals()[generalService.getGeneralNumber(generalService.getThisGeneralPort())] = general.getNumber();

        log.info(buildLogToSendYourValueToAnotherGenerals());

        // Wysłanie wiadomości o liczności oddziału danego generała do innych generałów
        for (int port : generalService.getListWithGeneralsPort()) {
            if(generalService.getThisGeneralPort() != port) {

                if(!generalService.getThisGeneralIsLoyal())
                    general = generalService.getNewGeneral();

                GeneralValueDto body = new RestTemplate()
                        .postForEntity(generalService.buildUri(port, "/postValue"), general, GeneralValueDto.class)
                        .getBody();
            }
        }

        // Jeżeli generał który wysłał do pozostałych informacje o swojej liczebności był generałem ostatnim
        // (tzn. jego wektor wartości otrzymanych został usupełniony całkowicie) to wykonaj metodę receiveValueFromAnotherGeneral
        // dla tego generała.
        if(generalService.isReceivedValuesFromGeneralsComplete()) {
            this.receiveValueFromAnotherGeneral(general);
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // Kontroler odpowiedzialny za przyjmowanie wartości (liczebności) od poszczególnych generałów
    @PostMapping("/postValue")
    public ResponseEntity receiveValueFromAnotherGeneral(@RequestBody GeneralValueDto general) {
        generalService.saveArmySize(general);

//        log.info(buildLogToReceiveValueFromAnotherGeneralOneValue(general));

        // Jeżeli Generał otrzymał wszystkie wartości od pozostałych generałów wysyła otrzymany wektor wszystkim generałom
        // z wyłączeniem jego.
        if(generalService.isReceivedValuesFromGeneralsComplete()) {

            log.info(buildLogToReceiveValueFromAnotherGeneralLastValue());

            for (int port : generalService.getListWithGeneralsPort()) {
                if(generalService.getThisGeneralPort() != port) {
                    GeneralsValuesDto body = new RestTemplate()
                            .postForEntity(generalService.buildUri(port, "/postVector"), generalService.generalsValuesDto, GeneralsValuesDto.class)
                            .getBody();
                }
            }
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // Kontroler przyjmujący wektory od pozostałych generałów
    @PostMapping("/postVector")
    public ResponseEntity receiveVectorFromAnotherGeneral(@RequestBody GeneralsValuesDto generalsVector) {
        generalService.generalsVectors.add(generalsVector.getReceivedValuesFromGenerals());

//        log.info(buildLogToReceiveVectorFromAnotherGeneral(generalsVector));

        if(generalService.isReceivedVectorsFromGeneralsComplete()) {
            log.info(generalService.getGeneralVectorsFromGeneralsInString());
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }


    // Budowa logów
    private String buildLogToReceiveVectorFromAnotherGeneral(GeneralsValuesDto generalsVector) {
        return  "\n" +
                "Generał: " +
                generalService.getGeneralNumber(generalService.getThisGeneralPort()) +
                " otzymał wektor: " +
                Arrays.toString(generalsVector.getReceivedValuesFromGenerals()) +
                " od generała " +
                generalService.getGeneralNumber(generalsVector.getPort()) +
                ".";
    }

    private String buildLogToReceiveValueFromAnotherGeneralOneValue(GeneralValueDto general) {
        return  "\n" +
                "Generał "
                + generalService.getGeneralNumber(generalService.getThisGeneralPort())
                + " , otrzymał wartość: "
                + general.getNumber()
                + ", od generała "
                + generalService.getGeneralNumber(general.getPort())
                + ".";
    }

    private String buildLogToReceiveValueFromAnotherGeneralLastValue() {
        return  "\n" +
                "Generał "
                + generalService.getGeneralNumber(generalService.getThisGeneralPort())
                + " , otrzymał wartości od wszystkich generałów i wysyła go: "
                + Arrays.toString(generalService.generalsValuesDto.getReceivedValuesFromGenerals())
                + " dalej.";
    }

    private String buildLogToSendYourValueToAnotherGenerals() {
        return  "\n" +
                "Generał " +
                generalService.getGeneralNumber(generalService.getThisGeneralPort()) +
                " rozesłał wartość.";
    }
}
