Phase 09.4 — Map Foundation

1. Summary of What Was Built


Integrated real GPS data into the runtime loop and verified continuous updates.


Established a working Runtime → State → UI subscription pipeline.


Implemented dynamic device identity (selfId) replacing hardcoded "SELF".


Built inbound transport polling and integrated it into the runtime heartbeat.


Implemented packet filtering to prevent self-duplication in inbound data.


Created initial map rendering using MapLibre within MapCanvasView.


Identified and isolated rendering limitations when combining MapLibre with custom canvas drawing.


Designed and began implementation of a dedicated overlay rendering layer (VehicleOverlayView) for markers.



2. Current System State
Status: partial


Core data pipeline is functioning.


GPS, runtime, and state propagation are working.


UI rendering pipeline is partially functional.


Marker rendering not yet visible due to layer separation issue.



3. Active Subsystems (PGM1–PGM6)
SubsystemNameStatusPGM1GPS EngineworkingPGM2Map Rendering (MapLibre)workingPGM2 (Overlay)Vehicle Overlay RenderingpartialPGM3UI Intent / ButtonBarworkingPGM4Data / Vehicle RegistryworkingPGM5Transport (Send + Poll Inbound)workingPGM6Button Control Layerworking

4. Known Issues


Vehicle markers are not visible on the map.


Custom drawing inside MapCanvasView.onDraw() is not rendering on top of MapLibre.


Overlay rendering layer (VehicleOverlayView) is not yet fully integrated.


Camera positioning does not yet consistently reflect GPS location.


Coordinate scaling and mapping require verification after overlay integration.



5. Architectural Rules Enforced


RuntimeShell does not directly reference UI components.


UI updates are driven exclusively through RuntimeShell.subscribe.


Identity is runtime-owned and no longer hardcoded.


Transport pipeline separates self vs. remote data.


Map rendering (MapLibre) is isolated from custom drawing logic.


Overlay rendering is separated into its own view layer.


Single source of truth maintained in system state.



6. Next Phase Objective (Phase 09.5)
Establish a fully functional overlay rendering system by:


Completing integration of VehicleOverlayView above the map layer.


Verifying marker visibility and alignment with GPS data.


Validating multi-vehicle rendering capability.


Preparing system for real multi-device field testing.



7. One-Line Phase Summary
Phase 09.4 established a complete data and identity pipeline and identified the correct rendering architecture by separating map and overlay layers.