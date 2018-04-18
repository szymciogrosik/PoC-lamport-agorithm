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

    /**
     * Kontroler odpowiedzialny za wystartowanie symulacji.
     * @return
     */
    @GetMapping("/startSendMessage")
    public ResponseEntity sendYourValueToAnotherGenerals() {
        GeneralValueDto general = generalService.getNewGeneral();
        generalService.generalsValuesDto.getReceivedValuesFromGenerals()[generalService.getGeneralNumber(generalService.getThisGeneralPort())] = general.getNumber();

        log.info("Generał " + generalService.getGeneralNumber(generalService.getThisGeneralPort()) + " rozesłał wartość.");

        // Wysłanie wiadomości o liczności oddziału danego generała do innych generałów
        for (int port : generalService.getListWithGeneralsPort()) {
            if(generalService.getThisGeneralPort() != port) {
                String uri = "http://localhost:" + port + "/postValue";

                if(!generalService.getThisGeneralIsLoyal()) {
                    general = generalService.getNewGeneral();
                }

                GeneralValueDto body = new RestTemplate()
                        .postForEntity(uri, general, GeneralValueDto.class)
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

    // Kontroler odpowiedzialna za przyjmowanie wartości (liczebności) od poszczególnych generałów
    @PostMapping("/postValue")
    public ResponseEntity receiveValueFromAnotherGeneral(@RequestBody GeneralValueDto general) {
        generalService.generalsValuesDto.getReceivedValuesFromGenerals()[generalService.getGeneralNumber(general.getPort())] = general.getNumber();

//        log.info("Generał "
//                + generalService.getGeneralNumber(generalService.getThisGeneralPort())
//                + " , otrzymał wartość: "
//                + general.getNumber()
//                + ", od generała "
//                + generalService.getGeneralNumber(general.getPort())
//                + ".");

        // Jeżeli Generał otrzymał wszystkie wartości od pozostałych generałów wysyła otrzymany wektor wszystkim generałom
        // z wyłączeniem jego.
        if(generalService.isReceivedValuesFromGeneralsComplete()) {
            log.info("Generał "
                + generalService.getGeneralNumber(generalService.getThisGeneralPort())
                + " , otrzymał wartości od wszystkich generałów i wysyła go: "
                + Arrays.toString(generalService.generalsValuesDto.getReceivedValuesFromGenerals())
                + " dalej.");

            for (int port : generalService.getListWithGeneralsPort()) {
                if(generalService.getThisGeneralPort() != port) {
                    String uri = "http://localhost:" + port + "/postVector";

                    GeneralsValuesDto body = new RestTemplate()
                            .postForEntity(uri, generalService.generalsValuesDto, GeneralsValuesDto.class)
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

//        log.info("Generał: " + generalService.getGeneralNumber(generalService.getThisGeneralPort())
//                + " otzymał wektor: "
//                + Arrays.toString(generalsVector.getReceivedValuesFromGenerals())
//                + " od generała "
//                + generalService.getGeneralNumber(generalsVector.getPort())
//                + ".");

        if(generalService.isReceivedVectorsFromGeneralsComplete()) {
            log.info(generalService.getGeneralVectorsFromGeneralsInString());
        }

        generalService.findFinalSolution();

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
