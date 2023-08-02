package dms.service.stats;


import dms.config.multitenant.TenantIdentifierResolver;
import dms.dao.SchemaManager;
import dms.dto.stats.OverdueDevicesStats;
import dms.repository.StatsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.Tuple;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Slf4j
@Service
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;
    private final TenantIdentifierResolver tenantIdentifierResolver;
    private final SchemaManager sm;

    @Autowired
    public StatsServiceImpl(StatsRepository statsRepository,
                            TenantIdentifierResolver tenantIdentifierResolver,
                            SchemaManager sm) {
        this.statsRepository = statsRepository;
        this.tenantIdentifierResolver = tenantIdentifierResolver;
        this.sm = sm;
    }


    @Override
    public OverdueDevicesStats getOverdueDevicesStats() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("_yyyy_MM_dd");
        String currentSchemaName = tenantIdentifierResolver.resolveCurrentTenantIdentifier();
        String strDate = currentSchemaName.substring(sm.DRTU_SCHEMA_NAME.length());
        LocalDate chDate = LocalDate.parse(strDate, formatter);

        Date checkDate = Date.valueOf(chDate);

        OverdueDevicesStats root = new OverdueDevicesStats("root");
        List<Tuple> tupleList;

        tupleList = statsRepository.getNormalDevicesStatsAlt(checkDate);
        root.fillFromTuple(tupleList);

        tupleList = statsRepository.getOverdueDevicesStatsAlt(checkDate);
        root.fillFromTuple(tupleList);

        tupleList = statsRepository.getExtraOverdueDevicesStatsAlt(checkDate);
        root.fillFromTuple(tupleList);

        tupleList = statsRepository.getPassiveDevicesStatsAlt(checkDate);
        root.fillFromTuple(tupleList);

        return root;
    }

}
