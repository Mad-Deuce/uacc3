package dms.service.structure;

import dms.RtubaseAuthService;
import dms.dto.ObjectTreeNode;
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

    private final RtubaseAuthService rtubaseAuthService;

    private final RailwayRepository railwayRepository;
    private final SubdivisionRepository subdivisionRepository;
    private final RtdFacilityRepository rtdFacilityRepository;
    private final LineFacilityRepository lineFacilityRepository;


    public StructureServiceImpl(RtubaseAuthService rtubaseAuthService,
                                RailwayRepository railwayRepository,
                                SubdivisionRepository subdivisionRepository,
                                RtdFacilityRepository rtdFacilityRepository,
                                LineFacilityRepository lineFacilityRepository) {
        this.rtubaseAuthService = rtubaseAuthService;
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
    public List<ObjectTreeNode> getChildrenAlt(String parentId, String parentClsId) {

        final String parentItem;

        if (parentClsId.equals(Cls.CLS2.getId())) {
            List<RailwayEntity> entityList = railwayRepository.findAll();
            return entityList.stream()
                    .map(item -> new ObjectTreeNode("" + item.getId(), "" + Cls.CLS131.getId(), true,
                            1, "" + item.getName(), "" + item.getName() + "-Stats",
                            item.getName() + ". Stats"))
                    .collect(Collectors.toList());
        }

        if (parentClsId.equals(Cls.CLS131.getId())) {
            List<SubdivisionEntity> entityList = subdivisionRepository.findAllByIdStartingWithOrderById(parentId);
            return entityList.stream()
                    .map(item -> new ObjectTreeNode("" + item.getId(), "" + Cls.CLS132.getId(), true,
                            2, "" + item.getName(), "" + Cls.CLS132.getId() + "-Stats",
                            Cls.CLS132.getName() + ". Stats"))
                    .collect(Collectors.toList());
        }

        if (parentClsId.equals(Cls.CLS132.getId())) {
            List<RtdFacilityEntity> entityList = rtdFacilityRepository.findAllByIdStartingWithOrderById(parentId);
            return entityList.stream()
                    .map(item -> new ObjectTreeNode("" + item.getId(), "" + Cls.CLS133.getId(), true,
                            3, "" + item.getName(), "" + Cls.CLS133.getId() + "-Stats",
                            Cls.CLS133.getName() + ". Stats"))
                    .collect(Collectors.toList());
        }

        if (parentClsId.equals(Cls.CLS133.getId())) {
            List<ObjectTreeNode> result = new ArrayList<>();
            parentItem = rtdFacilityRepository.findById(parentId).orElseThrow().getName();

            result.add(new ObjectTreeNode("" + parentId, "" + Cls.CLS2111.getId(), true,
                    4, "" + RegionType.EC.getName(), "" + RegionType.EC.getName(),
                    "" + RegionType.EC.getName()));
            result.add(new ObjectTreeNode("" + parentId, "" + Cls.CLS2112.getId(), true,
                    4, "" + RegionType.PG.getName(), "" + RegionType.PG.getName(),
                    "" + RegionType.PG.getName()));

            result.add(new ObjectTreeNode("" + parentId, "" + Cls.CLS21152.getId(), false,
                    4, "" + Cls.CLS21152.getName(), "" + Cls.CLS21152.getName() + " " + parentItem,
                    "" + Cls.CLS21152.getName() + " " + parentItem));

            result.add(new ObjectTreeNode("" + parentId, "" + Cls.CLS21151.getId(), false,
                    4, "" + Cls.CLS21151.getName(), "" + Cls.CLS21151.getName() + " " + parentItem,
                    "" + Cls.CLS21151.getName() + " " + parentItem));

            return result;
        }

        if (parentClsId.equals(Cls.CLS2111.getId())) {
            if (parentId.length() == 7) {
                List<ObjectTreeNode> result = new ArrayList<>();
                parentItem = lineFacilityRepository.findById(parentId).orElseThrow().getName();

                result.add(new ObjectTreeNode("" + parentId, "" + Cls.CLS21111.getId(), false,
                        6, "" + Cls.CLS21111.getName(), "" + Cls.CLS21111.getName() + "-" + parentItem,
                        "" + parentItem + ". " + Cls.CLS21111.getName()));
                result.add(new ObjectTreeNode("" + parentId, "" + Cls.CLS21114.getId(), false,
                        6, "" + Cls.CLS21114.getName(), "" + Cls.CLS21114.getName() + "-" + parentItem,
                        "" + parentItem + ". " + Cls.CLS21114.getName()));
                result.add(new ObjectTreeNode("" + parentId, "" + Cls.CLS21112.getId(), false,
                        6, "" + Cls.CLS21112.getName(), "" + Cls.CLS21112.getName() + "-" + parentItem,
                        "" + parentItem + ". " + Cls.CLS21112.getName()));

                return result;
            } else {
                List<LineFacilityEntity> entityList = lineFacilityRepository
                        .findAllByIdStartingWithOrderById(parentId + "0");
                return entityList.stream()
                        .map(item -> new ObjectTreeNode("" + item.getId(), "" + Cls.CLS2111.getId(),
                                true, 5, "" + item.getName(), "" + item.getName() + "-Stats",
                                "" + item.getName() + ". Stats"))
                        .collect(Collectors.toList());
            }
        }

        if (parentClsId.equals(Cls.CLS2112.getId())) {
            if (parentId.length() == 7) {
                List<ObjectTreeNode> result = new ArrayList<>();
                parentItem = lineFacilityRepository.findById(parentId).orElseThrow().getName();
                result.add(new ObjectTreeNode("" + parentId, "" + Cls.CLS21121.getId(), false,
                        6, "" + Cls.CLS21121.getName(), "" + Cls.CLS21121.getName() + "-" + parentItem,
                        "" + parentItem + ". " + Cls.CLS21121.getName()));
                result.add(new ObjectTreeNode("" + parentId, "" + Cls.CLS21122.getId(), false,
                        6, "" + Cls.CLS21122.getName(), "" + Cls.CLS21122.getName() + "-" + parentItem,
                        "" + parentItem + ". " + Cls.CLS21122.getName()));
                return result;
            } else {
                List<LineFacilityEntity> entityList = lineFacilityRepository
                        .findAllByIdStartingWithOrderById(parentId + "2");
                parentItem = Cls.CLS2112.getName();
                return entityList.stream()
                        .map(item -> new ObjectTreeNode("" + item.getId(), "" + Cls.CLS2112.getId(),
                                true, 5, "" + item.getName(), "" + item.getName() + "-Stats",
                                "" + item.getName() + ". Stats"))
                        .collect(Collectors.toList());
            }
        }

        return Collections.emptyList();
    }

    @Override
    public StructureDTO getRoot() {
        String principalPermitCode = rtubaseAuthService.getPrincipalPermitCode();
        final String parentItem;
        if (principalPermitCode.length() == 0) {
            return new StructureDTO(null, null, null, Cls.CLS2.getName(), true,
                    0, Cls.CLS2.getId(), Cls.CLS2.getName(), "");
        } else if (principalPermitCode.length() == 1) {
            RailwayEntity entity = railwayRepository.findById(principalPermitCode).orElseThrow();
            parentItem = Cls.CLS2.getName();
            return new StructureDTO(entity.getId(), null, null,
                    entity.getName(), true, 1, Cls.CLS131.getId(),
                    Cls.CLS131.getName(), parentItem);
        } else if (principalPermitCode.length() == 3) {
            SubdivisionEntity entity = subdivisionRepository.findById(principalPermitCode).orElseThrow();
            parentItem = Cls.CLS132.getName();
            return new StructureDTO(entity.getId(), null, null,
                    entity.getName(), true, 2, Cls.CLS132.getId(),
                    Cls.CLS132.getName(), parentItem);
        } else if (principalPermitCode.length() == 4) {
            RtdFacilityEntity entity = rtdFacilityRepository.findById(principalPermitCode).orElseThrow();
            parentItem = Cls.CLS133.getName();
            return new StructureDTO(entity.getId(), null, null,
                    entity.getName(), true, 2, Cls.CLS133.getId(),
                    Cls.CLS133.getName(), parentItem);
        } else {
            return new StructureDTO(null, null, null, "empty", false,
                    0, "", "", "");
        }
    }

    @Override
    public ObjectTreeNode getRootAlt() {
        String principalPermitCode = rtubaseAuthService.getPrincipalPermitCode();
        final String parentItem;
        if (principalPermitCode.length() == 0) {
            return new ObjectTreeNode(null, "" + Cls.CLS2.getId(), true,
                    0, "" + Cls.CLS2.getName(), Cls.CLS2.getName() + "-Stats",
                    Cls.CLS2.getName() + ". Stats");
        } else if (principalPermitCode.length() == 1) {
            RailwayEntity entity = railwayRepository.findById(principalPermitCode).orElseThrow();
            return new ObjectTreeNode("" + entity.getId(), "" + Cls.CLS131.getId(), true,
                    1, "" + Cls.CLS131.getName(), Cls.CLS131.getName() + "-Stats",
                    Cls.CLS131.getName() + ". Stats");
        } else if (principalPermitCode.length() == 3) {
            SubdivisionEntity entity = subdivisionRepository.findById(principalPermitCode).orElseThrow();
            return new ObjectTreeNode("" + entity.getId(), "" + Cls.CLS132.getId(), true,
                    2, "" + Cls.CLS132.getName(), Cls.CLS132.getName() + "-Stats",
                    Cls.CLS132.getName() + ". Stats");
        } else if (principalPermitCode.length() == 4) {
            RtdFacilityEntity entity = rtdFacilityRepository.findById(principalPermitCode).orElseThrow();
            return new ObjectTreeNode("" + entity.getId(), "" + Cls.CLS133.getId(), true,
                    3, "" + Cls.CLS133.getName(), Cls.CLS133.getName() + "-Stats",
                    Cls.CLS133.getName() + ". Stats");
        } else {
            return new ObjectTreeNode(null, "", false,
                    0, "empty", "empty",
                    "empty");
        }
    }
}
