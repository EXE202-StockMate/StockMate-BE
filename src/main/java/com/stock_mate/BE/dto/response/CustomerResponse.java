package com.stock_mate.BE.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

public record CustomerResponse (
    String customerID,
    String customerName,
    String description,
    @JsonFormat(pattern = "dd-MM-yyyy")
    LocalDate createDate,
    @JsonFormat(pattern = "dd-MM-yyyy")
    LocalDate updateDate) {
}
