package dms.service.structure;

import dms.dto.StructureDTO;
import dms.standing.data.dock.val.Cls;
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
    public List<StructureDTO> getChildren(String parentId, String regionType, String parentCls) {

        if (parentCls.equals(Cls.CLS2.getId())) {
            List<RailwayEntity> entityList = railwayRepository.findAll();
            return entityList.stream()
                    .map(item -> new StructureDTO(item.getId(), null, null, item.getName(), true, 1, Cls.CLS131.getId()))
                    .collect(Collectors.toList());
        }

        if (parentCls.equals(Cls.CLS131.getId())) {
            List<SubdivisionEntity> entityList = subdivisionRepository.findAllByIdStartingWithOrderById(parentId);
            return entityList.stream()
                    .map(item -> new StructureDTO(item.getId(), null, null, item.getName(), true, 2, Cls.CLS132.getId()))
                    .collect(Collectors.toList());
        }

        if (parentCls.equals(Cls.CLS132.getId())) {
            List<RtdFacilityEntity> entityList = rtdFacilityRepository.findAllByIdStartingWithOrderById(parentId);
            return entityList.stream()
                    .map(item -> new StructureDTO(item.getId(), null, null, item.getName(), true, 3, Cls.CLS133.getId()))
                    .collect(Collectors.toList());
        }

        if (parentCls.equals(Cls.CLS133.getId())) {
            List<StructureDTO> result = new ArrayList<>();
            result.add(new StructureDTO(parentId, Status.PS11.getName(), RegionType.EC.getName(), Cls.CLS2111.getName(), true, 4, Cls.CLS2111.getId()));
            result.add(new StructureDTO(parentId, Status.PS11.getName(), RegionType.PG.getName(), Cls.CLS2112.getName(), true, 4, Cls.CLS2112.getId()));
            result.add(new StructureDTO(parentId, Status.PS32.getName(), null, Cls.CLS21152.getName(), false, 4, Cls.CLS21152.getId()));
            result.add(new StructureDTO(parentId, Status.PS31.getName(), null, Cls.CLS21151.getName(), false, 4, Cls.CLS21151.getId()));
            return result;
        }

        if (parentCls.equals(Cls.CLS2111.getId())) {
            if (parentId.length() == 7) {
                List<StructureDTO> result = new ArrayList<>();
                result.add(new StructureDTO(parentId, Status.PS11.getName(), RegionType.EC.getName(), Cls.CLS21111.getName(), false, 6, Cls.CLS21111.getId()));
                result.add(new StructureDTO(parentId, Status.PS21.getName(), RegionType.EC.getName(), Cls.CLS21114.getName(), false, 6, Cls.CLS21114.getId()));
                result.add(new StructureDTO(parentId, Status.PS11.getName(), RegionType.EC.getName(), Cls.CLS21112.getName(), false, 6, Cls.CLS21112.getId()));
                return result;
            } else {
                List<LineFacilityEntity> entityList = lineFacilityRepository.findAllByIdStartingWithOrderById(parentId + "0");
                return entityList.stream()
                        .map(item -> new StructureDTO(item.getId(), Status.PS11.getName(), RegionType.EC.getName(), item.getName(), true, 5, Cls.CLS2111.getId()))
                        .collect(Collectors.toList());
            }
        }

        if (parentCls.equals(Cls.CLS2112.getId())) {
            if (parentId.length() == 7) {
                List<StructureDTO> result = new ArrayList<>();
                result.add(new StructureDTO(parentId, Status.PS11.getName(), RegionType.PG.getName(), Cls.CLS21121.getName(), false, 6, Cls.CLS21121.getId()));
                result.add(new StructureDTO(parentId, Status.PS11.getName(), RegionType.PG.getName(), Cls.CLS21122.getName(), false, 6, Cls.CLS21122.getId()));
                return result;
            } else {
                List<LineFacilityEntity> entityList = lineFacilityRepository.findAllByIdStartingWithOrderById(parentId + "2");
                return entityList.stream()
                        .map(item -> new StructureDTO(item.getId(), Status.PS11.getName(), RegionType.PG.getName(), item.getName(), true, 5, Cls.CLS2112.getId()))
                        .collect(Collectors.toList());
            }
        }

        return Collections.emptyList();
    }

    @Override
    public StructureDTO getRoot() {
        return new StructureDTO(null, null, null, Cls.CLS2.getName(), true, 0, Cls.CLS2.getId());
    }
}
