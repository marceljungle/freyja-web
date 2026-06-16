package com.freyja.domain.port.out;

import java.util.Optional;

import com.freyja.domain.vo.CellLocation;
import com.freyja.domain.vo.CellTower;

/**
 * Outbound port that resolves an approximate geographic location from a serving
 * cell (e.g. via the OpenCelliD / Unwired Labs API). Implementations must fail
 * soft: an empty result means "could not resolve" and never an exception that
 * would block telemetry ingestion.
 */
public interface CellLocationResolver {

  Optional<CellLocation> resolve(CellTower tower);
}
