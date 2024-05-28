package com.example.springboot_demo.test.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReleaseTaskEntity {
    @NotBlank
    private String taskCode;
    @NotBlank
    private String cnStationNo;
    @NotBlank
    private String gpStationNo;

    private Long beginTime;

    private Long endTime;

    private Integer frequency;

}
