package dms.dto;

import dms.filter.Filter;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@Data
public class RequestDTO {
    private List<Filter> filters;
//    private PageRequest pageable;
//    private Integer page;
//    private Integer size;
//    private Sort sort;
}
