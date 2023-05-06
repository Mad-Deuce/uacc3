package dms.service.stats;


import dms.dto.stats.OverdueDevicesStats;
import dms.repository.DeviceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.Tuple;
import java.sql.Date;
import java.util.List;


@Slf4j
@Service
public class StatsServiceImpl implements StatsService {
    private final DeviceRepository deviceRepository;

    @Autowired
    public StatsServiceImpl(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }


    @Override
    public OverdueDevicesStats getOverdueDevicesStats() {
        Date nowDate = new Date(System.currentTimeMillis());
        OverdueDevicesStats root = new OverdueDevicesStats("root");
        List<Tuple> tupleList;

        tupleList = deviceRepository.getNormalDevicesStatsAlt(nowDate);
        root.fillFromTuple(tupleList);

        tupleList = deviceRepository.getOverdueDevicesStatsAlt(nowDate);
        root.fillFromTuple(tupleList);

        tupleList = deviceRepository.getExtraOverdueDevicesStatsAlt(nowDate);
        root.fillFromTuple(tupleList);

        tupleList = deviceRepository.getPassiveDevicesStatsAlt(nowDate);
        root.fillFromTuple(tupleList);

        return root;
    }

}
