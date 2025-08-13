package com.simba.project01.store;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class StoreCreateRequest
{
    @NotBlank(message = "가게명은 필수 입력 항목입니다.")
    @Size(min=2, max=50, message = "가게명은 2자 이상 50자 이내여야 합니다.")
    private String name;

    private BigDecimal latitude; //위도

    private BigDecimal longitude; //경도

    @NotBlank(message = "카테고리는 필수 선택 항목입니다.")
    private StoreCategory category;

    @Size(min=2, max=100, message = "가게 설명은 2자 이상 100자 이내여야 합니다.")
    private String description;
}
