package dms.service.stats;


import dms.repository.DeviceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


@Slf4j
@Service
public class StatsServiceImpl implements StatsService {
    private final DeviceRepository deviceRepository;

    @Autowired
    public StatsServiceImpl(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Override
    public Map<String, Long> getStats(String cls, String id) {
        Map<String, Long> resultMap = new LinkedHashMap<>();
        resultMap.putAll(getNormalDeviceQuantity(id));
        resultMap.putAll(getOverdueDeviceQuantity(id));
        resultMap.putAll(getExtraOverdueDeviceQuantity(id));
        resultMap.putAll(getPassiveDeviceQuantity(id));
        return resultMap;
    }

    private Map<String, Long> getNormalDeviceQuantity(String id) {
        Map<String, Long> resultMap = new LinkedHashMap<>();
        String mapKey = "NormalDeviceQuantity";
        Long mapValue = deviceRepository.getNormalDeviceQuantity(new Date(System.currentTimeMillis()), id + "%");
        resultMap.put(mapKey, mapValue);
        return resultMap;
    }

    private Map<String, Long> getPassiveDeviceQuantity(String id) {
        Map<String, Long> resultMap = new LinkedHashMap<>();
        String mapKey = "passiveDeviceQuantity";
        Long mapValue = deviceRepository.getPassiveDeviceQuantity(id + "%");
        resultMap.put(mapKey, mapValue);
        return resultMap;
    }

    private Map<String, Long> getOverdueDeviceQuantity(String id) {
        Map<String, Long> resultMap = new LinkedHashMap<>();
        String mapKey = "overdueDeviceQuantity";
        Long mapValue = deviceRepository.getOverdueDeviceQuantity(new Date(System.currentTimeMillis()), id + "%");
        resultMap.put(mapKey, mapValue);
        return resultMap;
    }

    private Map<String, Long> getExtraOverdueDeviceQuantity(String id) {
        Map<String, Long> resultMap = new LinkedHashMap<>();
        String mapKey = "extraOverdueDeviceQuantity";
        Long mapValue = deviceRepository.getExtraOverdueDeviceQuantity(new Date(System.currentTimeMillis()), id + "%");
        resultMap.put(mapKey, mapValue);
        return resultMap;
    }
}
