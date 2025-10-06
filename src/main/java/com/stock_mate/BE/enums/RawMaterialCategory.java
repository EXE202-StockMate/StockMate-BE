package com.stock_mate.BE.enums;

public enum RawMaterialCategory {
    // Wood types
    GO_CAO_SU("Gỗ cao su"),
    GO_THONG("Gỗ thông"),
    GO_TRAM("Gỗ tràm"),
    GO_SOI("Gỗ sồi"),

    // MDF board - main category
    VAN_MDF_E2("Ván MDF E2"),
    VAN_MDF_CAP("Ván MDF cạp"),
    PHU_KIEN("Phụ kiện"),
    SON_PU("Sơn PU"),
    ;
    private final String displayName;

    RawMaterialCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

