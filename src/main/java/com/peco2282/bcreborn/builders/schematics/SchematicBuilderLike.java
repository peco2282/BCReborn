package com.peco2282.bcreborn.builders.schematics;


import com.peco2282.bcreborn.common.builder.schematics.SchematicRotateMeta;

public class SchematicBuilderLike extends SchematicRotateMeta {
	public SchematicBuilderLike() {
	}

	@Override
	public void onNBTLoaded() {
		if (tileNBT != null) {
			tileNBT.remove("box");
			tileNBT.remove("bpt");
			tileNBT.remove("bptBuilder");
			tileNBT.remove("builderState");
			tileNBT.remove("done");
			tileNBT.remove("iterator");
			tileNBT.remove("path");
		}
	}
}
