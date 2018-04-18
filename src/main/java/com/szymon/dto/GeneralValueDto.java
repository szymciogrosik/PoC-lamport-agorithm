package com.szymon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneralValueDto implements Serializable {
    @Getter
    private int port;
    @Getter
    private int number;
}
