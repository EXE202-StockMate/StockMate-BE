package com.stock_mate.BE.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RequistionResponse {
    String requistionID;
    String type;
    int quantity;
    String unit; //ký, cái, cuộn
    LocalDate createDate;
    LocalDate updateDate;
    String note;
    String userID;
    String materialID;
    String orderID;
    int shortageID;

}
