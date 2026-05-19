package com.peco2282.bcreborn.common.builder.schematics;

import com.peco2282.bcreborn.api.blueprints.BuildingPermission;
import com.peco2282.bcreborn.api.blueprints.SchematicTile;

public class SchematicTileCreative extends SchematicTile {
    @Override
    public BuildingPermission getBuildingPermission() {
        return BuildingPermission.CREATIVE_ONLY;
    }
}
