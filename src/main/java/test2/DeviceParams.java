package test2;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DeviceParams {
    private Set<String> macIds = new HashSet<>();

    public Set<String> getMacIds() {
        return macIds;
    }

    public void setMacIds(Set<String> macIds) {
        if (macIds != null) this.macIds = macIds;
    }

    public void addMacId(String macId) {
        if (macId != null && !macId.trim().isEmpty()) this.macIds.add(macId);
    }

    public void addMacIds(Set<String> macIds) {
        if (macIds != null) this.macIds.addAll(macIds);
    }

    public void addMacIds(String[] macIds) {
        if (macIds != null) this.macIds.addAll(Arrays.asList(macIds));
    }
}
