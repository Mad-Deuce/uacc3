package dms.filter;


import lombok.Data;

import java.util.List;

@Data
public class Filter {
    private String fieldName;
    private List<String> values;
    private String matchMode;
}
