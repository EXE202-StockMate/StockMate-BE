package com.stock_mate.BE.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.stock_mate.BE.entity.Order;
import com.stock_mate.BE.entity.Shortage;
import com.stock_mate.BE.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
