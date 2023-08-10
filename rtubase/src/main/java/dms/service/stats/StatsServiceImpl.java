package dms.service.stats;


import dms.config.multitenant.TenantIdentifierResolver;
import dms.dao.SchemaManager;
import dms.dto.stats.OverdueDevicesStats;
import dms.repository.StatsRepository;
import dms.service.db.DatabaseSessionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.Tuple;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


@Slf4j
@Service
public class StatsServiceImpl implements StatsService {

    private final DatabaseSessionManager dsm;

    private final StatsRepository statsRepository;
    private final TenantIdentifierResolver tenantIdentifierResolver;
    private final SchemaManager sm;

    @Autowired
    public StatsServiceImpl(DatabaseSessionManager dsm,
                            StatsRepository statsRepository,
                            TenantIdentifierResolver tenantIdentifierResolver,
                            SchemaManager sm) {
        this.dsm = dsm;
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

        tupleList = statsRepository.getNormalDevicesStats(checkDate);
        root.fillFromTuple(tupleList);

        tupleList = statsRepository.getOverdueDevicesStats(checkDate);
        root.fillFromTuple(tupleList);

        tupleList = statsRepository.getExtraOverdueDevicesStats(checkDate);
        root.fillFromTuple(tupleList);

        tupleList = statsRepository.getPassiveDevicesStats(checkDate);
        root.fillFromTuple(tupleList);

        return root;
    }

    @Override
    public HashMap<LocalDate, OverdueDevicesStats> getOverdueDevicesStatsMap() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("_yyyy_MM_dd");
        HashMap<LocalDate, OverdueDevicesStats> result = new HashMap<>();
        List<String> schemaNameList = sm.getSchemaNameList();
        schemaNameList.sort(Comparator.naturalOrder());

        schemaNameList.forEach(item -> {
            dsm.unbindSession();
            tenantIdentifierResolver.setCurrentTenant(item);
            dsm.bindSession();
            System.out.println(tenantIdentifierResolver.resolveCurrentTenantIdentifier());
            String strDate = tenantIdentifierResolver.resolveCurrentTenantIdentifier().substring(sm.DRTU_SCHEMA_NAME.length());
            LocalDate chDate = LocalDate.parse(strDate, formatter);
            result.put(chDate, getShortOverdueDevicesStats(chDate));
        });
        return result;
    }


    public OverdueDevicesStats getShortOverdueDevicesStats(LocalDate chDate) {
        Date checkDate = Date.valueOf(chDate);

        OverdueDevicesStats root = new OverdueDevicesStats("root");
        List<Tuple> tupleList;

        tupleList = statsRepository.getNormalDevicesStatsShort(checkDate);
        root.fillFromTuple(tupleList);

        tupleList = statsRepository.getOverdueDevicesStatsShort(checkDate);
        root.fillFromTuple(tupleList);

        tupleList = statsRepository.getExtraOverdueDevicesStatsShort(checkDate);
        root.fillFromTuple(tupleList);

        tupleList = statsRepository.getPassiveDevicesStatsShort(checkDate);
        root.fillFromTuple(tupleList);

        return root;
    }

}
