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
import java.util.Objects;
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
    public List<StructureDTO> getChildren(String parentId, String regionType) {

        if (parentId == null || parentId.equals("null") || parentId.length() == 0) {
            List<RailwayEntity> entityList = railwayRepository.findAll();
            return entityList.stream()
                    .map(item -> new StructureDTO(item.getId(), null, null, item.getName(), true,1))
                    .collect(Collectors.toList());
        }

        if (parentId.length() == 1) {
            List<SubdivisionEntity> entityList = subdivisionRepository.findAllByIdStartingWithOrderById(parentId);
            return entityList.stream()
                    .map(item -> new StructureDTO(item.getId(), null, null, item.getName(), true,2))
                    .collect(Collectors.toList());
        }

        if (parentId.length() == 3) {
            List<RtdFacilityEntity> entityList = rtdFacilityRepository.findAllByIdStartingWithOrderById(parentId);
            return entityList.stream()
                    .map(item -> new StructureDTO(item.getId(), null, null, item.getName(), true,3))
                    .collect(Collectors.toList());
        }

        if (parentId.length() == 4) {
            if (regionType == null || regionType.equals("null")) {
                List<StructureDTO> result = new ArrayList<>();
                result.add(new StructureDTO(parentId, Status.PS11.getName(), RegionType.EC.getName(), "Станции", true,4));
                result.add(new StructureDTO(parentId, Status.PS11.getName(), RegionType.PG.getName(), "Перегони", true,4));
                result.add(new StructureDTO(parentId, Status.PS32.getName(), null, "АВЗ РТД", false,4));
                result.add(new StructureDTO(parentId, Status.PS31.getName(), null, "ОБФ", false,4));
                return result;
            }

            if (regionType.equals(RegionType.EC.getName())) {
                List<LineFacilityEntity> entityList = lineFacilityRepository.findAllByIdStartingWithOrderById(parentId + "0");
                return entityList.stream()
                        .map(item -> new StructureDTO(item.getId(), Status.PS11.getName(), RegionType.EC.getName(), item.getName(), true,5))
                        .collect(Collectors.toList());
            }

            if (regionType.equals(RegionType.PG.getName())) {
                List<LineFacilityEntity> entityList = lineFacilityRepository.findAllByIdStartingWithOrderById(parentId + "2");
                return entityList.stream()
                        .map(item -> new StructureDTO(item.getId(), Status.PS11.getName(), RegionType.PG.getName(), item.getName(), true,5))
                        .collect(Collectors.toList());
            }
        }

        if (parentId.length() == 7) {
            if (Objects.equals(regionType, RegionType.EC.getName())) {
                List<StructureDTO> result = new ArrayList<>();
                result.add(new StructureDTO(parentId, Status.PS11.getName(), RegionType.EC.getName(), Status.PS11.getComment(), false,6));
                result.add(new StructureDTO(parentId, Status.PS21.getName(), RegionType.EC.getName(), Status.PS21.getComment(), false,6));
                return result;
            }

            if (Objects.equals(regionType, RegionType.PG.getName())) {
                List<StructureDTO> result = new ArrayList<>();
                result.add(new StructureDTO(parentId, Status.PS11.getName(), RegionType.PG.getName(), Status.PS11.getComment(), false,6));
                return result;
            }
        }


        return Collections.emptyList();
    }

    @Override
    public StructureDTO getRoot() {
        return new StructureDTO(null, null, null, "Укрзалізниця", true, 0);
    }
}
