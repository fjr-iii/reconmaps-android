# 📡 Phase 07 — Data Sync (PGM4 Expansion)

---

# 🧭 Thread Purpose

This thread transitions the system from:

> **single-device awareness → multi-device synchronization**

Phase 06 proved that the system can:
- acquire real GPS data
- propagate it through RuntimeShell
- transmit it via PGM5 (Transport)

However, the system currently only knows about **itself**.

---

# 🧱 What We Did (Previous Thread Summary — Phase 06)

In Phase 06, we:

- Replaced simulated coordinates with real GPS data
- Integrated Android LocationManager into PGM1
- Implemented runtime permission handling
- Filtered invalid GPS readings (0,0 and low accuracy)
- Verified real-world coordinates (~42, -73)
- Confirmed transport pipeline is sending real data

Pipeline achieved:

```
PGM1 (Real GPS) → RuntimeShell → TransportPacket → PGM5 → Server
```

👉 System is now **connected to the real world**

---

# ⚠️ Problem Identified (Current Limitation)

The system is still **single-device isolated**.

- No incoming packet handling
- No shared state between devices
- No visibility of other vehicles

👉 Each device operates independently

---

# ⚠️ Architectural Fork (NEW DECISION)

We are formally splitting responsibilities:

## 🔹 PGM-4 — Structured Data Sync Manager
- owns lightweight synchronized state
- vehicle positions
- waypoint metadata
- channel + timestamp data
- packet normalization

## 🔹 PGM-6 — Media & Shared Content Engine
- owns heavy assets
- images, routes, map packages
- file persistence + integrity

👉 Key Rule:

> **PGM-4 = metadata only**  
> **PGM-6 = file/content only**

This separation prevents:
- bloated sync payloads
- runtime coupling
- future scaling issues

---

# 🎯 Purpose of This Thread

This thread will:

1. Introduce **incoming data flow (receive path)**
2. Expand PGM5 to support packet reception
3. Route inbound packets through RuntimeShell
4. Implement PGM4 as a **vehicle state registry**
5. Synchronize multi-device positions

---

# 🧠 Rules for This Thread

- Do NOT modify GPS logic (PGM1 is stable)
- Do NOT modify UI logic yet
- Do NOT implement file/media handling (PGM6 future use)
- Do NOT introduce rendering complexity

This phase is strictly:

> **Receive → Normalize → Store → Reflect state**

---

# 🧪 Definition of Done

Phase 07 is complete when:

- Devices receive packets from server
- PGM4 stores remote vehicle data
- RuntimeShell updates SystemState
- UI reflects multiple vehicles (basic rendering acceptable)

### Real Test:

- Run app on **2–3 phones**
- All devices visible on each screen
- Positions update in near real-time

---

# 🚀 Next Step (After This Thread)

Once Phase 07 is complete:

## 👉 Phase 08 — Multi-Vehicle Rendering

Focus will shift to:
- clean marker rendering
- role-based symbols (leader, sweep, vehicle)
- visual clarity and stability

---

# 🧭 One-Line Summary

> Enable multi-device awareness by receiving and storing synchronized vehicle data.

---

**Status:** Bootstrap Initialized  
**Thread Name:** Phase 07 — Data Sync (PGM4 Expansion)

