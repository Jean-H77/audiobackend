package org.csun.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AudioDetailsDto(
        @JsonProperty("filename")
        String fileName,
        @JsonProperty("overall_tempo")
        double overallTempo,
        @JsonProperty("peak_1")
        double peakOne,
        @JsonProperty("peak_2")
        double peakTwo) {
}
