package com.proyecto.hoteles.exception;

import org.springframework.http.HttpStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class BussinesRuleException extends Exception{

    private long id;
    private String code;
    private String title;
    private HttpStatus httpStatus;

    

    public BussinesRuleException(long id, String code, String message, HttpStatus httpStatus){
        super(message);
        this.id=id;
        this.code=code;
        this.httpStatus=httpStatus;
    }


    public BussinesRuleException( String code, String title, String message, HttpStatus httpStatus){
        super(message);
        this.title=title;
        this.code=code;
        this.httpStatus=httpStatus;
    }


    
    public BussinesRuleException(String code, String message, HttpStatus httpStatus){
        super(message);
        this.code=code;
        this.httpStatus=httpStatus;
    }


    
    public BussinesRuleException(String message, Throwable cause){
        super(message, cause);
    }
}
