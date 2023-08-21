package dms.service.stats;


import dms.config.multitenant.TenantIdentifierResolver;
import dms.dao.SchemaManager;
import dms.dto.stats.OverdueDevicesStats;
import dms.entity.OverdueDevsStatsEntity;
import dms.repository.OverdueDevsStatsRepository;
import dms.repository.StatsRepository;
import dms.service.db.DatabaseSessionManager;
import dms.standing.data.entity.RailwayEntity;
import dms.standing.data.entity.SubdivisionEntity;
import dms.standing.data.repository.RailwayRepository;
import dms.standing.data.repository.SubdivisionRepository;
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
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Service
public class StatsServiceImpl implements StatsService {

    private final DatabaseSessionManager dsm;


    private final RailwayRepository railwayRepository;
    private final SubdivisionRepository subdivisionRepository;
    private final StatsRepository statsRepository;
    private final OverdueDevsStatsRepository overdueDevsStatsRepository;
    private final TenantIdentifierResolver tenantIdentifierResolver;
    private final SchemaManager sm;

    @Autowired
    public StatsServiceImpl(
            DatabaseSessionManager dsm,
            RailwayRepository railwayRepository,
            SubdivisionRepository subdivisionRepository,
            StatsRepository statsRepository,
            OverdueDevsStatsRepository overdueDevsStatsRepository,
            TenantIdentifierResolver tenantIdentifierResolver,
            SchemaManager sm) {
        this.dsm = dsm;
        this.railwayRepository = railwayRepository;
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

        saveCurrentSchemaOverdueDevsStats();

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

    @Override
    public List<OverdueDevsStatsEntity> getOverdueDevsStatsEntityList(String parentId) {
        List<OverdueDevsStatsEntity> result = overdueDevsStatsRepository.findByObjectIdStartsWith(parentId);
        return result.stream().filter(item ->
                        item.getObjectId().equals("1")
                                || item.getObjectId().equals("101")
                                || item.getObjectId().equals("102")
                                || item.getObjectId().equals("104")
                                || item.getObjectId().equals("106")
                                || item.getObjectId().equals("107")
                                || item.getObjectId().equals("111"))
                .collect(Collectors.toList());
    }

    @Override
    public void saveAllSchemaOverdueDevsStats() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("_yyyy_MM_dd");
//        HashMap<LocalDate, OverdueDevicesStats> result = new HashMap<>();
        List<String> schemaNameList = sm.getSchemaNameList();
        schemaNameList.sort(Comparator.naturalOrder());
//        String currentSchema = tenantIdentifierResolver.resolveCurrentTenantIdentifier();

        schemaNameList.forEach(item -> {
            dsm.unbindSession();
            tenantIdentifierResolver.setCurrentTenant(item);
            saveCurrentSchemaOverdueDevsStats();
            dsm.bindSession();
//            System.out.println(tenantIdentifierResolver.resolveCurrentTenantIdentifier());
//            String strDate = tenantIdentifierResolver.resolveCurrentTenantIdentifier().substring(sm.DRTU_SCHEMA_NAME.length());
//            LocalDate chDate = LocalDate.parse(strDate, formatter);
//            result.put(chDate, getShortOverdueDevicesStats(chDate, nId));
        });
//        dsm.unbindSession();
//        tenantIdentifierResolver.setCurrentTenant(currentSchema);
//        dsm.bindSession();
//        return result;
    }

    @Override
    public void saveCurrentSchemaOverdueDevsStats() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("_yyyy_MM_dd");
        String currentSchemaName = tenantIdentifierResolver.resolveCurrentTenantIdentifier();
        String strDate = currentSchemaName.substring(sm.DRTU_SCHEMA_NAME.length());
        LocalDate chDate = LocalDate.parse(strDate, formatter);
        List<OverdueDevsStatsEntity> overdueDevsStatsEntityList = collectOverdueDevsStatsEntityList(chDate);
        overdueDevsStatsRepository.saveAllAndFlush(overdueDevsStatsEntityList);
    }

    private List<OverdueDevsStatsEntity> collectOverdueDevsStatsEntityList(LocalDate checkDate) {
        HashMap<String, OverdueDevsStatsEntity> resultMap = new HashMap<>();

        Map<String, Long> nDevsQuantityMap = toDevicesQuantityMap(statsRepository.getNormalDevicesQuantity(Date.valueOf(checkDate), 0));
        nDevsQuantityMap.putAll(toDevicesQuantityMap(statsRepository.getNormalDevicesQuantity(Date.valueOf(checkDate), 1)));
        nDevsQuantityMap.putAll(toDevicesQuantityMap(statsRepository.getNormalDevicesQuantity(Date.valueOf(checkDate), 3)));

        Map<String, Long> expDevsQuantityMap = toDevicesQuantityMap(statsRepository.getExpiredDevicesQuantity(Date.valueOf(checkDate), 0));
        expDevsQuantityMap.putAll(toDevicesQuantityMap(statsRepository.getExpiredDevicesQuantity(Date.valueOf(checkDate), 1)));
        expDevsQuantityMap.putAll(toDevicesQuantityMap(statsRepository.getExpiredDevicesQuantity(Date.valueOf(checkDate), 3)));

        Map<String, Long> expWarrantyDevsQuantityMap = toDevicesQuantityMap(statsRepository.getExpiredWarrantyDevicesQuantity(Date.valueOf(checkDate), 0));
        expWarrantyDevsQuantityMap.putAll(toDevicesQuantityMap(statsRepository.getExpiredWarrantyDevicesQuantity(Date.valueOf(checkDate), 1)));
        expWarrantyDevsQuantityMap.putAll(toDevicesQuantityMap(statsRepository.getExpiredWarrantyDevicesQuantity(Date.valueOf(checkDate), 3)));

        Map<String, Long> hidedDevsQuantityMap = toDevicesQuantityMap(statsRepository.getHidedDevicesQuantity(0));
        hidedDevsQuantityMap.putAll(toDevicesQuantityMap(statsRepository.getHidedDevicesQuantity(1)));
        hidedDevsQuantityMap.putAll(toDevicesQuantityMap(statsRepository.getHidedDevicesQuantity(3)));

        addStatsValueToEntityMap(resultMap, nDevsQuantityMap, checkDate, 1);
        addStatsValueToEntityMap(resultMap, expDevsQuantityMap, checkDate, 2);
        addStatsValueToEntityMap(resultMap, expWarrantyDevsQuantityMap, checkDate, 3);
        addStatsValueToEntityMap(resultMap, hidedDevsQuantityMap, checkDate, 4);

        Map<String, String> objectNameMap = getObjectNameMap();
        addObjectNameToEntityMap(resultMap, objectNameMap, checkDate);

        return resultMap.values().stream().toList();
    }

    Map<String, String> getObjectNameMap() {
        HashMap<String, String> resultMap = new HashMap<>();
        List<SubdivisionEntity> subdivisionEntityList = subdivisionRepository.findAll();
        List<RailwayEntity> railwayEntityList = railwayRepository.findAll();
        resultMap.put("root", "UZ");
        resultMap.putAll(subdivisionEntityList.stream()
                .collect(Collectors.toMap(SubdivisionEntity::getId, SubdivisionEntity::getShortName)));
        resultMap.putAll(railwayEntityList.stream()
                .collect(Collectors.toMap(RailwayEntity::getId, RailwayEntity::getName)));
        return resultMap;
    }

    private void addStatsValueToEntityMap(HashMap<String, OverdueDevsStatsEntity> resultMap,
                                          Map<String, Long> devicesQuantityMap,
                                          LocalDate checkDate,
                                          int i) {
        devicesQuantityMap.forEach((k, v) -> {
            OverdueDevsStatsEntity parentEntity = resultMap.get(k.substring(0, 1));
            if (parentEntity == null) {
                parentEntity = new OverdueDevsStatsEntity();
                resultMap.put(k.substring(0, 1), parentEntity);
                parentEntity.setObjectId(k.substring(0, 1));
                parentEntity.setStatsDate(checkDate);
            }
            OverdueDevsStatsEntity entity = resultMap.get(k);
            if (entity == null) {
                entity = new OverdueDevsStatsEntity();
                resultMap.put(k, entity);
                entity.setObjectId(k);
                entity.setStatsDate(checkDate);
            }
            switch (i) {
                case 1 -> {
                    entity.setNormDevsQuantity(v);
                    parentEntity.setNormDevsQuantity(parentEntity.getNormDevsQuantity() == null ? v
                            : parentEntity.getNormDevsQuantity() + v);
                }
                case 2 -> {
                    entity.setExpDevsQuantity(v);
                    parentEntity.setExpDevsQuantity(parentEntity.getExpDevsQuantity() == null ? v
                            : parentEntity.getExpDevsQuantity() + v);
                }
                case 3 -> {
                    entity.setExpWarrantyDevsQuantity(v);
                    parentEntity.setExpWarrantyDevsQuantity(parentEntity.getExpWarrantyDevsQuantity() == null ? v
                            : parentEntity.getExpWarrantyDevsQuantity() + v);
                }
                case 4 -> {
                    entity.setPassDevsQuantity(v);
                    parentEntity.setPassDevsQuantity(parentEntity.getPassDevsQuantity() == null ? v
                            : parentEntity.getPassDevsQuantity() + v);
                }
                default -> throw new RuntimeException();
            }

        });
    }

    private void addObjectNameToEntityMap(HashMap<String, OverdueDevsStatsEntity> resultMap,
                                          Map<String, String> objectNameMap,
                                          LocalDate checkDate
    ) {
        objectNameMap.forEach((k, v) -> {
            OverdueDevsStatsEntity entity = resultMap.get(k);
            if (entity == null) {
                entity = new OverdueDevsStatsEntity();
                resultMap.put(k, entity);
                entity.setObjectId(k);
                entity.setStatsDate(checkDate);
            }
            entity.setObjectName(v);
        });
    }

    private Map<String, Long> toDevicesQuantityMap(List<Tuple> tupleList) {
        return tupleList.stream()
                .collect(Collectors.toMap(
                                tuple -> (tuple.get(0).equals("") ? "root" : (String) tuple.get(0)),
                                tuple -> (Long) tuple.get(1)
                        )
                );
    }


}
