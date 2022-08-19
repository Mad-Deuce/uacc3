package com.dms_uz.rtubase.dto;

import lombok.Data;

@Data
public class DevRequestDTO {

    private Long devId;
    private Integer currentPageNumber = 0;
    private Integer pageSize = 20;
    private String sortColumnName = "lastName";
    private String sortDirection = "desc";
    private String filterText;

}
