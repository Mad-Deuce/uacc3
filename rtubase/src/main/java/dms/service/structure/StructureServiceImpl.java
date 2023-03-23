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
    public List<StructureDTO> getChildren(String parentId, String regionType, String parentClsId) {

        final String parentItem;

        if (parentClsId.equals(Cls.CLS2.getId())) {
            List<RailwayEntity> entityList = railwayRepository.findAll();
            parentItem = Cls.CLS2.getName();
            return entityList.stream()
                    .map(item -> new StructureDTO(item.getId(), null, null,
                            item.getName(), true, 1, Cls.CLS131.getId(),
                            Cls.CLS131.getName(), parentItem))
                    .collect(Collectors.toList());
        }

        if (parentClsId.equals(Cls.CLS131.getId())) {
            List<SubdivisionEntity> entityList = subdivisionRepository.findAllByIdStartingWithOrderById(parentId);
            parentItem = railwayRepository.findById(parentId).orElseThrow().getName();
            return entityList.stream()
                    .map(item -> new StructureDTO(item.getId(), null, null,
                            item.getName(), true, 2, Cls.CLS132.getId(),
                            Cls.CLS132.getName(), parentItem))
                    .collect(Collectors.toList());
        }

        if (parentClsId.equals(Cls.CLS132.getId())) {
            List<RtdFacilityEntity> entityList = rtdFacilityRepository.findAllByIdStartingWithOrderById(parentId);
            parentItem = subdivisionRepository.findById(parentId).orElseThrow().getName();
            return entityList.stream()
                    .map(item -> new StructureDTO(item.getId(), null, null,
                            item.getName(), true, 3, Cls.CLS133.getId(),
                            Cls.CLS133.getName(), parentItem))
                    .collect(Collectors.toList());
        }

        if (parentClsId.equals(Cls.CLS133.getId())) {
            List<StructureDTO> result = new ArrayList<>();
            parentItem = rtdFacilityRepository.findById(parentId).orElseThrow().getName();
            result.add(new StructureDTO(parentId, Status.PS11.getName(), RegionType.EC.getName(),
                    Cls.CLS2111.getName(), true, 4, Cls.CLS2111.getId(),
                    Cls.CLS2111.getName(), parentItem));
            result.add(new StructureDTO(parentId, Status.PS11.getName(), RegionType.PG.getName(),
                    Cls.CLS2112.getName(), true, 4, Cls.CLS2112.getId(),
                    Cls.CLS2112.getName(), parentItem));
            result.add(new StructureDTO(parentId, Status.PS32.getName(), null,
                    Cls.CLS21152.getName(), false, 4, Cls.CLS21152.getId(),
                    Cls.CLS21152.getName(), parentItem));
            result.add(new StructureDTO(parentId, Status.PS31.getName(), null,
                    Cls.CLS21151.getName(), false, 4, Cls.CLS21151.getId(),
                    Cls.CLS21151.getName(), parentItem));
            return result;
        }

        if (parentClsId.equals(Cls.CLS2111.getId())) {
            if (parentId.length() == 7) {
                List<StructureDTO> result = new ArrayList<>();
                parentItem = lineFacilityRepository.findById(parentId).orElseThrow().getName();
                result.add(new StructureDTO(parentId, Status.PS11.getName(), RegionType.EC.getName(),
                        Cls.CLS21111.getName(), false, 6, Cls.CLS21111.getId(),
                        Cls.CLS21111.getName(), parentItem));
                result.add(new StructureDTO(parentId, Status.PS21.getName(), RegionType.EC.getName(),
                        Cls.CLS21114.getName(), false, 6, Cls.CLS21114.getId(),
                        Cls.CLS21114.getName(), parentItem));
                result.add(new StructureDTO(parentId, Status.PS11.getName(), RegionType.EC.getName(),
                        Cls.CLS21112.getName(), false, 6, Cls.CLS21112.getId(),
                        Cls.CLS21112.getName(), parentItem));
                return result;
            } else {
                List<LineFacilityEntity> entityList = lineFacilityRepository
                        .findAllByIdStartingWithOrderById(parentId + "0");
                parentItem = Cls.CLS2111.getName();
                return entityList.stream()
                        .map(item -> new StructureDTO(item.getId(), Status.PS11.getName(), RegionType.EC.getName(),
                                item.getName(), true, 5, Cls.CLS2111.getId(),
                                Cls.CLS2111.getName(), parentItem))
                        .collect(Collectors.toList());
            }
        }

        if (parentClsId.equals(Cls.CLS2112.getId())) {
            if (parentId.length() == 7) {
                List<StructureDTO> result = new ArrayList<>();
                parentItem = lineFacilityRepository.findById(parentId).orElseThrow().getName();
                result.add(new StructureDTO(parentId, Status.PS11.getName(), RegionType.PG.getName(),
                        Cls.CLS21121.getName(), false, 6, Cls.CLS21121.getId(),
                        Cls.CLS21121.getName(), parentItem));
                result.add(new StructureDTO(parentId, Status.PS11.getName(), RegionType.PG.getName(),
                        Cls.CLS21122.getName(), false, 6, Cls.CLS21122.getId(),
                        Cls.CLS21122.getName(), parentItem));
                return result;
            } else {
                List<LineFacilityEntity> entityList = lineFacilityRepository
                        .findAllByIdStartingWithOrderById(parentId + "2");
                parentItem = Cls.CLS2112.getName();
                return entityList.stream()
                        .map(item -> new StructureDTO(item.getId(), Status.PS11.getName(), RegionType.PG.getName(),
                                item.getName(), true, 5, Cls.CLS2112.getId(),
                                Cls.CLS2112.getName(), parentItem))
                        .collect(Collectors.toList());
            }
        }

        return Collections.emptyList();
    }

    @Override
    public StructureDTO getRoot() {
        return new StructureDTO(null, null, null, Cls.CLS2.getName(), true,
                0, Cls.CLS2.getId(), Cls.CLS2.getName(),"");
    }
}
