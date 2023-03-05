package dms.service.structure;

import dms.dto.StructureDTO;
import dms.standing.data.dock.val.RegionType;
import dms.standing.data.dock.val.Status;
import dms.standing.data.entity.LineFacilityEntity;
import dms.standing.data.entity.RailwayEntity;
import dms.standing.data.entity.RtdFacilityEntity;
import dms.standing.data.entity.SubdivisionEntity;
import dms.standing.data.repository.LineFacilityRepository;
import dms.standing.data.repository.RailwayRepository;
import dms.standing.data.repository.RtdFacilityRepository;
import dms.standing.data.repository.SubdivisionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StructureServiceImpl implements StructureService {

    private final RailwayRepository railwayRepository;
    private final SubdivisionRepository subdivisionRepository;
    private final RtdFacilityRepository rtdFacilityRepository;
    private final LineFacilityRepository lineFacilityRepository;


    public StructureServiceImpl(RailwayRepository railwayRepository,
                                SubdivisionRepository subdivisionRepository,
                                RtdFacilityRepository rtdFacilityRepository,
                                LineFacilityRepository lineFacilityRepository) {
        this.railwayRepository = railwayRepository;
        this.subdivisionRepository = subdivisionRepository;
        this.lineFacilityRepository = lineFacilityRepository;
        this.rtdFacilityRepository = rtdFacilityRepository;
    }

    @Override
    public List<StructureDTO> getChildren(String parentId, RegionType regionType) {

        if (parentId == null || parentId.length() == 0) {
            List<RailwayEntity> entityList = railwayRepository.findAll();
            return entityList.stream()
                    .map(item -> new StructureDTO(item.getId(), null, null, item.getName(), true))
                    .collect(Collectors.toList());
        }

        if (parentId.length() == 1) {
            List<SubdivisionEntity> entityList = subdivisionRepository.findAllByIdStartingWithOrderById(parentId);
            return entityList.stream()
                    .map(item -> new StructureDTO(item.getId(), null, null, item.getName(), true))
                    .collect(Collectors.toList());
        }

        if (parentId.length() == 3) {
            List<RtdFacilityEntity> entityList = rtdFacilityRepository.findAllByIdStartingWithOrderById(parentId);
            return entityList.stream()
                    .map(item -> new StructureDTO(item.getId(), null, null, item.getName(), true))
                    .collect(Collectors.toList());
        }

        if (parentId.length() == 4) {
            if (regionType == null) {
                List<StructureDTO> result = new ArrayList<>();
                result.add(new StructureDTO(parentId, Status.PS11, RegionType.EC, "Станции", true));
                result.add(new StructureDTO(parentId, Status.PS11, RegionType.PG, "Перегони", true));
                result.add(new StructureDTO(parentId, Status.PS32, null, "АВЗ РТД", false));
                result.add(new StructureDTO(parentId, Status.PS31, null, "ОБФ", false));
                return result;
            }

            if (regionType == RegionType.EC) {
                List<LineFacilityEntity> entityList = lineFacilityRepository.findAllByIdStartingWithOrderById(parentId + "0");
                return entityList.stream()
                        .map(item -> new StructureDTO(item.getId(), Status.PS11, RegionType.EC, item.getName(), true))
                        .collect(Collectors.toList());
            }

            if (regionType == RegionType.PG) {
                List<LineFacilityEntity> entityList = lineFacilityRepository.findAllByIdStartingWithOrderById(parentId + "2");
                return entityList.stream()
                        .map(item -> new StructureDTO(item.getId(), Status.PS11, RegionType.PG, item.getName(), true))
                        .collect(Collectors.toList());
            }
        }

        if (parentId.length() == 7) {
            if (regionType == RegionType.EC) {
                List<StructureDTO> result = new ArrayList<>();
                result.add(new StructureDTO(parentId, Status.PS11, RegionType.EC, Status.PS11.getComment(), false));
                result.add(new StructureDTO(parentId, Status.PS21, RegionType.EC, Status.PS21.getComment(), false));
                return result;
            }

            if (regionType == RegionType.PG) {
                List<StructureDTO> result = new ArrayList<>();
                result.add(new StructureDTO(parentId, Status.PS11, RegionType.PG, Status.PS11.getComment(), false));
                return result;
            }
        }


        return Collections.emptyList();
    }

    @Override
    public StructureDTO getRoot() {
        return new StructureDTO(null, null, null, "Укрзалізниця", true);
    }
}
