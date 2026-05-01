# Phase 10.1 — Convoy Awareness Contract

## 1. Summary of What Was Built

Implemented identity-aware multi-vehicle data pipeline.

Reworked Vehicle model to support consistent geographic precision (Double) and identity flag (isSelf).

Refactored DataSync to correctly store and update vehicles in a registry keyed by device ID.

Established RuntimeShell as the sole authority for injecting identity into the data layer.

Integrated vehicle list into UI rendering pipeline via MainActivity.

Enabled real-time rendering of vehicle positions using MapLibre projection.

Validated end-to-end pipeline from GPS → Runtime → DataSync → UI → Overlay.

---

## 2. Current System State

Status: partial

Core data pipeline is functional.

GPS ingestion and state updates are working.

Vehicle registry correctly stores and returns active vehicles.

Rendering pipeline is active and displays vehicle markers.

Camera and rendering alignment are not fully unified.

---

## 3. Active Subsystems (PGM1–PGM6)

PGM1 — GPS Engine: working

PGM2 — Map Rendering (MapLibre): working

PGM2 (Overlay) — VehicleOverlayView: working

PGM3 — UI Intent / ButtonBar: working

PGM4 — Data / Vehicle Registry: working

PGM5 — Transport: partial (send/receive structure present, multi-device not validated)

PGM6 — Control Layer (RuntimeShell): working

---

## 4. Known Issues

Camera behavior is not consistently aligned with vehicle-based position source.

Rendering pipeline is partially split between legacy state (latitude/longitude) and vehicle registry.

RenderTransformer is not currently integrated into active rendering path.

Single source of truth for position is not fully enforced across camera and rendering systems.

Multi-device rendering not yet validated.

---

## 5. Architectural Rules Enforced

RuntimeShell is the only component allowed to modify system state.

DataSync is the only component allowed to mutate vehicle registry.

Vehicle identity is injected by RuntimeShell and not inferred by DataSync.

Rendering layer consumes transformed data only and does not perform business logic.

MapCanvasView remains restricted to map rendering only.

Overlay layer is draw-only and does not access state or perform transformations.

Geographic coordinates are maintained as Double throughout the pipeline.

---

## 6. Next Phase Objective (Phase 10.2)

Unify camera control and rendering pipeline to use vehicle registry as the single source of truth for position.

---

## 7. One-Line Phase Summary

Phase 10.1 established an identity-aware vehicle registry and connected it to the rendering pipeline, enabling the foundation for multi-device convoy awareness.

