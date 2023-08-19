package dms.service.stats;


import dms.config.multitenant.TenantIdentifierResolver;
import dms.dao.SchemaManager;
import dms.dto.stats.OverdueDevicesStats;
import dms.entity.OverdueDevsStatsEntity;
import dms.repository.OverdueDevsStatsRepository;
import dms.repository.StatsRepository;
import dms.service.db.DatabaseSessionManager;
import dms.standing.data.entity.SubdivisionEntity;
import dms.standing.data.repository.SubdivisionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.Tuple;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
public class StatsServiceImpl implements StatsService {

    private final DatabaseSessionManager dsm;

    private final SubdivisionRepository subdivisionRepository;
    private final StatsRepository statsRepository;
    private final OverdueDevsStatsRepository overdueDevsStatsRepository;
    private final TenantIdentifierResolver tenantIdentifierResolver;
    private final SchemaManager sm;

    @Autowired
    public StatsServiceImpl(DatabaseSessionManager dsm,
                            SubdivisionRepository subdivisionRepository,
                            StatsRepository statsRepository,
                            OverdueDevsStatsRepository overdueDevsStatsRepository,
                            TenantIdentifierResolver tenantIdentifierResolver,
                            SchemaManager sm) {
        this.dsm = dsm;
        this.subdivisionRepository = subdivisionRepository;
        this.statsRepository = statsRepository;
        this.overdueDevsStatsRepository = overdueDevsStatsRepository;
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

        saveOverdueDevsStats();

        return root;
    }

    @Override
    public HashMap<LocalDate, OverdueDevicesStats> getOverdueDevicesStatsMap(String nodeId) {
        final String nId;
        if (nodeId.equals("root")) nId = "";
        else nId = nodeId;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("_yyyy_MM_dd");
        HashMap<LocalDate, OverdueDevicesStats> result = new HashMap<>();
        List<String> schemaNameList = sm.getSchemaNameList();
        schemaNameList.sort(Comparator.naturalOrder());
        String currentSchema = tenantIdentifierResolver.resolveCurrentTenantIdentifier();

        schemaNameList.forEach(item -> {
            dsm.unbindSession();
            tenantIdentifierResolver.setCurrentTenant(item);
            dsm.bindSession();
            System.out.println(tenantIdentifierResolver.resolveCurrentTenantIdentifier());
            String strDate = tenantIdentifierResolver.resolveCurrentTenantIdentifier().substring(sm.DRTU_SCHEMA_NAME.length());
            LocalDate chDate = LocalDate.parse(strDate, formatter);
            result.put(chDate, getShortOverdueDevicesStats(chDate, nId));
        });
        dsm.unbindSession();
        tenantIdentifierResolver.setCurrentTenant(currentSchema);
        dsm.bindSession();
        return result;
    }

    public OverdueDevicesStats getShortOverdueDevicesStats(LocalDate chDate, String nodeId) {
        Date checkDate = Date.valueOf(chDate);

        OverdueDevicesStats root = new OverdueDevicesStats(nodeId);
        List<Tuple> tupleList;

        tupleList = statsRepository.getNormalDevicesStatsShort(checkDate, nodeId + "%");
        root.fillFromTuple(tupleList);

        tupleList = statsRepository.getOverdueDevicesStatsShort(checkDate, nodeId + "%");
        root.fillFromTuple(tupleList);

        tupleList = statsRepository.getExtraOverdueDevicesStatsShort(checkDate, nodeId + "%");
        root.fillFromTuple(tupleList);

        tupleList = statsRepository.getPassiveDevicesStatsShort(nodeId + "%");
        root.fillFromTuple(tupleList);

        return root;
    }

    private List<OverdueDevsStatsEntity> getOverdueDevsStatsEntityList(OverdueDevicesStats overdueDevicesStats, LocalDate checkDate) {
        if (overdueDevicesStats == null
                || (overdueDevicesStats.getId().length() > 3 && !overdueDevicesStats.getId().equals("root")))
            return null;
        List<OverdueDevsStatsEntity> result = new ArrayList<>();
        OverdueDevsStatsEntity entity = new OverdueDevsStatsEntity();
        entity.setObjectId(overdueDevicesStats.getId());
        entity.setStatsDate(checkDate);
        entity.setNormDevsQuantity(overdueDevicesStats.getNormalDevicesQuantity());
        entity.setPassDevsQuantity(overdueDevicesStats.getPassiveDevicesQuantity());
        entity.setExpDevsQuantity(overdueDevicesStats.getOverdueDevicesQuantity());
        entity.setExpWarrantyDevsQuantity(overdueDevicesStats.getExtraOverdueDevicesQuantity());
        result.add(entity);
        if (!overdueDevicesStats.getChildren().isEmpty()) {
            overdueDevicesStats.getChildren().forEach(v -> {
                List<OverdueDevsStatsEntity> res = getOverdueDevsStatsEntityList(v, checkDate);
                if (res != null) result.addAll(res);

            });
        }
        return result;
    }

    @Override
    public void saveOverdueDevsStats() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("_yyyy_MM_dd");
        String currentSchemaName = tenantIdentifierResolver.resolveCurrentTenantIdentifier();
        String strDate = currentSchemaName.substring(sm.DRTU_SCHEMA_NAME.length());
        LocalDate chDate = LocalDate.parse(strDate, formatter);
        List<OverdueDevsStatsEntity> overdueDevsStatsEntityList = getOverdueDevsStatsEntityList(chDate);
        overdueDevsStatsRepository.saveAllAndFlush(overdueDevsStatsEntityList);
//        overdueDevsStatsEntityList.forEach(overdueDevsStatsEntity -> {
//
//        });
    }

    private List<OverdueDevsStatsEntity> getOverdueDevsStatsEntityList(LocalDate checkDate) {
        HashMap<String, OverdueDevsStatsEntity> resultMap = new HashMap<>();

        List<SubdivisionEntity> subdivisionEntityList =
                subdivisionRepository.findByIdIn(new ArrayList<>(Arrays.asList("101", "102", "104", "106", "107", "111")));
        addObjectNameToEntityMap(resultMap, subdivisionEntityList, checkDate);

        Map<String, Long> nDevsQuantityMap =
                toDevicesQuantityMap(statsRepository.getNormalDevicesQuantity(Date.valueOf(checkDate)));
        addStatsValueToEntityMap(resultMap, nDevsQuantityMap, checkDate, 1);

        Map<String, Long> expDevsQuantityMap =
                toDevicesQuantityMap(statsRepository.getExpiredDevicesQuantity(Date.valueOf(checkDate)));
        addStatsValueToEntityMap(resultMap, expDevsQuantityMap, checkDate, 2);

        Map<String, Long> expWarrantyDevsQuantityMap =
                toDevicesQuantityMap(statsRepository.getExpiredWarrantyDevicesQuantity(Date.valueOf(checkDate)));
        addStatsValueToEntityMap(resultMap, expWarrantyDevsQuantityMap, checkDate, 3);

        Map<String, Long> hidedDevsQuantityMap =
                toDevicesQuantityMap(statsRepository.getHidedDevicesQuantity(Date.valueOf(checkDate)));
        addStatsValueToEntityMap(resultMap, hidedDevsQuantityMap, checkDate, 4);

        return resultMap.values().stream().toList();
    }

    private Map<String, Long> toDevicesQuantityMap(List<Tuple> tupleList) {
        return tupleList.stream()
                .collect(Collectors.toMap(
                                tuple -> (String) tuple.get(0),
                                tuple -> (Long) tuple.get(1)
                        )
                );
    }

    private void addStatsValueToEntityMap(HashMap<String, OverdueDevsStatsEntity> resultMap,
                                          Map<String, Long> devicesQuantityMap,
                                          LocalDate checkDate,
                                          int i) {
        devicesQuantityMap.forEach((k, v) -> {
            OverdueDevsStatsEntity entity = resultMap.get(k);
            if (entity == null) {
                entity = new OverdueDevsStatsEntity();
                resultMap.put(k, entity);
                entity.setObjectId(k);
                entity.setStatsDate(checkDate);
            }
            switch (i) {
                case 1:
                    entity.setNormDevsQuantity(v);
                case 2:
                    entity.setExpDevsQuantity(v);
                case 3:
                    entity.setExpWarrantyDevsQuantity(v);
                case 4:
                    entity.setPassDevsQuantity(v);
            }

        });
    }

    private void addObjectNameToEntityMap(HashMap<String, OverdueDevsStatsEntity> resultMap,
                                          List<SubdivisionEntity> sdList,
                                          LocalDate checkDate
    ) {
        sdList.forEach((v) -> {
            OverdueDevsStatsEntity entity = resultMap.get(v.getId());
            if (entity == null) {
                entity = new OverdueDevsStatsEntity();
                resultMap.put(v.getId(), entity);
                entity.setObjectId(v.getId());
                entity.setStatsDate(checkDate);
            }
            entity.setObjectName(v.getShortName());

        });
    }
}
