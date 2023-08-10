package dms.dto.stats;

import dms.standing.data.dock.val.Status;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Tuple;
import java.util.ArrayList;
import java.util.List;


@Data
@Slf4j
public class OverdueDevicesStats {

    private String id;
    private String name;
    private Long normalDevicesQuantity;
    private Long overdueDevicesQuantity;
    private Long extraOverdueDevicesQuantity;
    private Long passiveDevicesQuantity;

    private List<OverdueDevicesStats> children;


    public OverdueDevicesStats(String id) {
        this.id = id;
        this.normalDevicesQuantity = 0L;
        this.overdueDevicesQuantity = 0L;
        this.extraOverdueDevicesQuantity = 0L;
        this.passiveDevicesQuantity = 0L;
    }

    public void fillFromTuple(List<Tuple> tupleList) {
        tupleList.forEach(tuple -> {

            ArrayList<String> idList = getIdList(tuple);

            OverdueDevicesStats item = this;
            sumValue(item, tuple);
            setItemName(item, tuple);

            for (String id : idList) {

                if (item.isChildExistsById(id)) {
                    item = item.getChildById(id);
                } else {
                    item = item.addChild(id);
                }
                assert item != null;
                sumValue(item, tuple);
                setItemName(item, tuple);
            }
        });
    }

    public static void setItemName(OverdueDevicesStats item, Tuple tuple) {
        if (item.getName() != null && item.getName().length() > 0) return;
        if (item.getId().equals("root")) {
            item.setName("root");
        } else if (item.getId().length() == 1) {
            item.setName(tuple.get(1, String.class));
        } else if (item.getId().length() == 3) {
            item.setName(tuple.get(3, String.class));
        } else if (item.getId().length() == 4) {
            item.setName(tuple.get(6, String.class));
        } else if (item.getId().length() == 7) {
            item.setName(tuple.get(8, String.class));
        } else {
            item.setName(tuple.get(5, Status.class).getValueC());
        }
    }

    public static void sumValue(OverdueDevicesStats item, Tuple tuple) {
        int i = tuple.toArray().length - 1;
        if (tuple.get(0).toString().equals("normal_devices")) {
            item.setNormalDevicesQuantity(item.getNormalDevicesQuantity() + tuple.get(i, Long.class));
        }

        if (tuple.get(0).toString().equals("overdue_devices")) {
            item.setOverdueDevicesQuantity(item.getOverdueDevicesQuantity() + tuple.get(i, Long.class));
        }

        if (tuple.get(0).toString().equals("extra_overdue_devices")) {
            item.setExtraOverdueDevicesQuantity(item.getExtraOverdueDevicesQuantity() + tuple.get(i, Long.class));
        }

        if (tuple.get(0).toString().equals("passive_devices")) {
            item.setPassiveDevicesQuantity(item.getPassiveDevicesQuantity() + tuple.get(i, Long.class));
        }
    }


    private ArrayList<String> getIdList(Tuple tuple) {
        ArrayList<String> idList = new ArrayList<>();

        if (tuple.get(2) != null) {
            idList.add(0, tuple.get(2).toString());
        }

        if (tuple.get(4) != null) {
            idList.add(tuple.get(4).toString());
        }

        if (tuple.toArray().length > 7) {
            if (tuple.get(7) != null) {
                idList.add(tuple.get(7).toString());
            }

            if (tuple.get(9) != null) {
                idList.add(tuple.get(9).toString());
            }
        }
        return idList;
    }

    private boolean isChildExistsById(String id) {
        if (this.children == null) return false;
        if (this.children.isEmpty()) return false;
        return this.children.stream().anyMatch(c -> c.id.equals(id));
    }

    private OverdueDevicesStats getChildById(String id) {
        if (this.children == null) return null;
        if (this.children.isEmpty()) return null;
        return this.children.stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null);
    }

    private OverdueDevicesStats addChild(String id) {
        if (this.children == null) this.children = new ArrayList<>();
        OverdueDevicesStats child = new OverdueDevicesStats(id);
        this.children.add(child);
        return child;
    }


}
