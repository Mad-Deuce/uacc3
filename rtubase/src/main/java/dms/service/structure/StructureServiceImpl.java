package dms.service.structure;

import dms.RtubaseAuthService;
import dms.dto.ObjectTreeNodeDto;
import dms.standing.data.dock.val.Cls;
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
    public List<ObjectTreeNodeDto> getChildrenAlt(String parentId, String parentClsId) {

        final String parentItem;
        // Railways-----------------------------------------------------------------------------------------------------
        if (parentClsId.equals(Cls.CLS2.getId())) {
            List<RailwayEntity> entityList = railwayRepository.findAll();
            return entityList.stream()
                    .map(item -> new ObjectTreeNodeDto("" + item.getId(), "" + Cls.CLS131.getId(), true,
                            1, "" + item.getName(), "" + item.getName() + "-Stats",
                            item.getName() + ". Stats"))
                    .collect(Collectors.toList());
        } else
        // Subdivisions-------------------------------------------------------------------------------------------------
        if (parentClsId.equals(Cls.CLS131.getId())) {
            List<SubdivisionEntity> entityList = subdivisionRepository.findAllByIdStartingWithOrderById(parentId);
            return entityList.stream()
                    .map(item -> new ObjectTreeNodeDto("" + item.getId(), "" + Cls.CLS132.getId(), true,
                            2, "" + item.getName(), "" + Cls.CLS132.getName() + "-Stats",
                            Cls.CLS132.getName() + ". Stats"))
                    .collect(Collectors.toList());
        } else
        // RTDs---------------------------------------------------------------------------------------------------------
        if (parentClsId.equals(Cls.CLS132.getId())) {
            List<RtdFacilityEntity> entityList = rtdFacilityRepository.findAllByIdStartingWithOrderById(parentId);
            return entityList.stream()
                    .map(item -> new ObjectTreeNodeDto("" + item.getId(), "" + Cls.CLS133.getId(), true,
                            3, "" + item.getName(), "" + Cls.CLS133.getName() + "-Stats",
                            Cls.CLS133.getName() + ". Stats"))
                    .collect(Collectors.toList());
        } else
        // EC, PG, AVZ RTD, OBF RTD
        if (parentClsId.equals(Cls.CLS133.getId())) {
            List<ObjectTreeNodeDto> result = new ArrayList<>();
            parentItem = rtdFacilityRepository.findById(parentId).orElseThrow().getName();

            result.add(new ObjectTreeNodeDto("" + parentId, "" + Cls.CLS2111.getId(), true,
                    4, "" + Cls.CLS2111.getName(), "" + Cls.CLS2111.getName(),
                    "" +Cls.CLS2111.getName()));
            result.add(new ObjectTreeNodeDto("" + parentId, "" + Cls.CLS2112.getId(), true,
                    4, "" + Cls.CLS2112.getName(), "" + Cls.CLS2112.getName(),
                    "" + Cls.CLS2112.getName()));
            result.add(new ObjectTreeNodeDto("" + parentId, "" + Cls.CLS21152.getId(), false,
                    4, "" + Cls.CLS21152.getName(), "" + Cls.CLS21152.getName() + " " + parentItem,
                    "" + Cls.CLS21152.getName() + " " + parentItem));
            result.add(new ObjectTreeNodeDto("" + parentId, "" + Cls.CLS21151.getId(), false,
                    4, "" + Cls.CLS21151.getName(), "" + Cls.CLS21151.getName() + " " + parentItem,
                    "" + Cls.CLS21151.getName() + " " + parentItem));
            return result;
        } else
        // Objects------------------------------------------------------------------------------------------------------
        if (parentClsId.equals(Cls.CLS2111.getId())) {
            // Devices, AVZ, Overdue Devices
            if (parentId.length() == 7) {
                List<ObjectTreeNodeDto> result = new ArrayList<>();
                parentItem = lineFacilityRepository.findById(parentId).orElseThrow().getName();

                result.add(new ObjectTreeNodeDto("" + parentId, "" + Cls.CLS21111.getId(), false,
                        6, "" + Cls.CLS21111.getName(), "" + Cls.CLS21111.getName() + "-" + parentItem,
                        "" + parentItem + ". " + Cls.CLS21111.getName()));
                result.add(new ObjectTreeNodeDto("" + parentId, "" + Cls.CLS21114.getId(), false,
                        6, "" + Cls.CLS21114.getName(), "" + Cls.CLS21114.getName() + "-" + parentItem,
                        "" + parentItem + ". " + Cls.CLS21114.getName()));
                result.add(new ObjectTreeNodeDto("" + parentId, "" + Cls.CLS21112.getId(), false,
                        6, "" + Cls.CLS21112.getName(), "" + Cls.CLS21112.getName() + "-" + parentItem,
                        "" + parentItem + ". " + Cls.CLS21112.getName()));

                return result;
            } else {
                // Station Object Name
                List<LineFacilityEntity> entityList = lineFacilityRepository
                        .findAllByIdStartingWithOrderById(parentId + "0");
                return entityList.stream()
                        .map(item -> new ObjectTreeNodeDto("" + item.getId(), "" + Cls.CLS2111.getId(),
                                true, 5, "" + item.getName(), "" + item.getName() + "-Stats",
                                "" + item.getName() + ". Stats"))
                        .collect(Collectors.toList());
            }
        }

        if (parentClsId.equals(Cls.CLS2112.getId())) {
            // Devices, Overdue Devices
            if (parentId.length() == 7) {
                List<ObjectTreeNodeDto> result = new ArrayList<>();
                parentItem = lineFacilityRepository.findById(parentId).orElseThrow().getName();
                result.add(new ObjectTreeNodeDto("" + parentId, "" + Cls.CLS21121.getId(), false,
                        6, "" + Cls.CLS21121.getName(), "" + Cls.CLS21121.getName() + "-" + parentItem,
                        "" + parentItem + ". " + Cls.CLS21121.getName()));
                result.add(new ObjectTreeNodeDto("" + parentId, "" + Cls.CLS21122.getId(), false,
                        6, "" + Cls.CLS21122.getName(), "" + Cls.CLS21122.getName() + "-" + parentItem,
                        "" + parentItem + ". " + Cls.CLS21122.getName()));
                return result;
            } else {
                // Stage Object Name
                List<LineFacilityEntity> entityList = lineFacilityRepository
                        .findAllByIdStartingWithOrderById(parentId + "2");
                return entityList.stream()
                        .map(item -> new ObjectTreeNodeDto("" + item.getId(), "" + Cls.CLS2112.getId(),
                                true, 5, "" + item.getName(), "" + item.getName() + "-Stats",
                                "" + item.getName() + ". Stats"))
                        .collect(Collectors.toList());
            }
        }

        return Collections.emptyList();
    }

    @Override
    public ObjectTreeNodeDto getRootAlt() {
        String principalPermitCode = rtubaseAuthService.getPrincipalPermitCode();
        if (principalPermitCode.length() == 0) {
            return new ObjectTreeNodeDto(null, "" + Cls.CLS2.getId(), true,
                    0, "" + Cls.CLS2.getName(), Cls.CLS2.getName() + "-Stats",
                    Cls.CLS2.getName() + ". Stats");
        } else if (principalPermitCode.length() == 1) {
            RailwayEntity entity = railwayRepository.findById(principalPermitCode).orElseThrow();
            return new ObjectTreeNodeDto("" + entity.getId(), "" + Cls.CLS131.getId(), true,
                    1, "" + Cls.CLS131.getName(), Cls.CLS131.getName() + "-Stats",
                    Cls.CLS131.getName() + ". Stats");
        } else if (principalPermitCode.length() == 3) {
            SubdivisionEntity entity = subdivisionRepository.findById(principalPermitCode).orElseThrow();
            return new ObjectTreeNodeDto("" + entity.getId(), "" + Cls.CLS132.getId(), true,
                    2, "" + Cls.CLS132.getName(), Cls.CLS132.getName() + "-Stats",
                    Cls.CLS132.getName() + ". Stats");
        } else if (principalPermitCode.length() == 4) {
            RtdFacilityEntity entity = rtdFacilityRepository.findById(principalPermitCode).orElseThrow();
            return new ObjectTreeNodeDto("" + entity.getId(), "" + Cls.CLS133.getId(), true,
                    3, "" + Cls.CLS133.getName(), Cls.CLS133.getName() + "-Stats",
                    Cls.CLS133.getName() + ". Stats");
        } else {
            return new ObjectTreeNodeDto(null, "", false,
                    0, "empty", "empty",
                    "empty");
        }
    }
}
