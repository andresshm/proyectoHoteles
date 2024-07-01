package com.proyecto.hoteles.common;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class StandarizedApiExceptionResponse {

    private String type;
    private String title;
    private String code;
    private String detail;
    private String instance;

    public StandarizedApiExceptionResponse(String type, String title, String code, String detail){
        super();
        this.type=type;
        this.title=title;
        this.code=code;
        this.detail=detail;
    }

}
