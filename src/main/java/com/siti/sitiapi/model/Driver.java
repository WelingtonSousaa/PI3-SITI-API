package com.siti.sitiapi.model;
import com.siti.sitiapi.enums.CNHCategoryEnum;
import java.time.LocalDate;
import lombok.Data;

@Data
public class Driver {
    private Long id;
    private String cnhNumber;
    private CNHCategoryEnum cnhCategory;
    private String name;
    private LocalDate birthDate;
    private LocalDate cnhValidityDate;
    private String phone;
    private Long idAddress;
    private User user;
}
