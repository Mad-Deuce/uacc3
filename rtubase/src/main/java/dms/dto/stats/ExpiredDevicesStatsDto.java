package dms.dto.stats;

import dms.entity.OverdueDevsStatsEntity;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Data
public class ExpiredDevicesStatsDto {
    String objectId;
    String objectName;
    Map<LocalDate, Long> expiredDevicesQuantity;
    Map<LocalDate, Long> expiredWarrantyDevicesQuantity;
    Map<LocalDate, Long> normalDevicesQuantity;
    Map<LocalDate, Long> passiveDevicesQuantity;


    public static Map<String, ExpiredDevicesStatsDto> toDtoList(List<OverdueDevsStatsEntity> inpData) {
        Map<String, ExpiredDevicesStatsDto> result = new TreeMap<>();
        Map<LocalDate, Long> standardDateMap = new TreeMap<>();
        for (OverdueDevsStatsEntity entity : inpData) {
            ExpiredDevicesStatsDto dto = result.get(entity.getObjectId());
            if (dto == null) {
                dto = new ExpiredDevicesStatsDto();
                result.put(entity.getObjectId(), dto);
                dto.setObjectId(entity.getObjectId());
                dto.setObjectName(entity.getObjectName());
                dto.setExpiredDevicesQuantity(new TreeMap<>());
                dto.setExpiredWarrantyDevicesQuantity(new TreeMap<>());
                dto.setNormalDevicesQuantity(new TreeMap<>());
                dto.setPassiveDevicesQuantity(new TreeMap<>());
            }
            standardDateMap.put(entity.getStatsDate(), 0L);
            dto.getExpiredDevicesQuantity().put(entity.getStatsDate(), entity.getExpiredDevicesQuantity());
            dto.getExpiredWarrantyDevicesQuantity().put(entity.getStatsDate(), entity.getExpiredWarrantyDevicesQuantity());
            dto.getNormalDevicesQuantity().put(entity.getStatsDate(), entity.getNormalDevicesQuantity());
            dto.getPassiveDevicesQuantity().put(entity.getStatsDate(), entity.getPassiveDevicesQuantity());
        }
        checkComplete(result, standardDateMap);
        return result;
    }
    private static void checkComplete(Map<String, ExpiredDevicesStatsDto> result, Map<LocalDate, Long> standardDateMap) {
        for (ExpiredDevicesStatsDto item : result.values()) {
            for (LocalDate key : standardDateMap.keySet()) {
                item.getExpiredDevicesQuantity().putIfAbsent(key, null);
                item.getExpiredWarrantyDevicesQuantity().putIfAbsent(key, null);
            }
        }
    }
}

