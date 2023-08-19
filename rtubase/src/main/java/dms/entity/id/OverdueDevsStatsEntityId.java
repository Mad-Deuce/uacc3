package dms.entity.id;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class OverdueDevsStatsEntityId implements Serializable {

    private String objectId;

    private LocalDate statsDate;
}
