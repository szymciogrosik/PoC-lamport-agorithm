package com.szymon.dto;

import lombok.*;

import java.io.Serializable;
import java.util.Arrays;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneralsValuesDto implements Serializable {

    @Getter @Setter
    private int[] receivedValuesFromGenerals;

    @Getter
    private int port;

    public GeneralsValuesDto(int size, int port) {
        receivedValuesFromGenerals = new int[size];
        Arrays.fill(this.receivedValuesFromGenerals, -1);
        this.port = port;
    }

}
