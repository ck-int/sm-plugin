package com.inte.framework.utils.bean;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class Result<T> {

    @Builder.Default
    private int retCode = HttpStatus.OK.value();
    private T retData;
    @Builder.Default
    private String retMsg = "成功";
    private String sign;
    private Long timeStamp;

}
