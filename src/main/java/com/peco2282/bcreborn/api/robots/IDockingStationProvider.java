package com.peco2282.bcreborn.api.robots;

/**
 * By default, this can be either an IPipePluggable or a TileEntity.
 */
public interface IDockingStationProvider {
	DockingStation getStation();
}
