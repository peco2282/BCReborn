# BCReborn — 開発ロードマップ

> 最終更新: 2026-05-09  
> 対象バージョン: Minecraft 1.20.1 / Forge 47.x

---

## 凡例

| 記号 | 意味 |
|------|------|
| ✅ | 実装完了 |
| 🔧 | 部分実装・作業中 |
| ⬜ | 未着手 |

---

## 1. パイプシステム (transport モジュール)

### 1-1. 基盤

| 項目 | 状態 | 備考 |
|------|------|------|
| `PipeBlock` / `PipeBlockEntity` | ✅ | NBT・Capability・tick委譲 |
| `TravelingItem` | ✅ | entryDirection・progress・speed・bounceCount |
| `ItemTransportModule` | ✅ | TravelingItem ライフサイクル完成 |
| `MovementHelper` / `SpeedHelper` | ✅ | stateless utility |
| `RoutingHelper` | ✅ | 方向解決ロジック |
| `MovementResult` enum | ✅ | SUCCESS / NO_TARGET / BLOCKED |
| `PipeBehaviourManager` | ✅ | |
| `ExtractionModule` (Item) | ✅ | Standard / Filtered |

### 1-2. アイテムパイプ Behaviour (全種)

| 素材 | 状態 | 特殊挙動 |
|------|------|----------|
| Wood | ✅ | 隣接インベントリから抽出 |
| Stone | ✅ | ランダムルーティング |
| Cobblestone | ✅ | |
| Iron | ✅ | 強制出力方向 |
| Golden | ✅ | 高速 |
| Diamond | ✅ | フィルタールーティング |
| Emerald | ✅ | |
| Obsidian | ✅ | 地面アイテム吸引 |
| Sandstone | ✅ | |
| Clay | ✅ | |
| Quartz | ✅ | |
| Lapis | ✅ | |
| Void | ✅ | アイテム消去 |
| Stripes | ✅ | |
| Daizuli | ✅ | |

### 1-3. 流体パイプ Behaviour (全種)

| 素材 | Behaviour | TransportModule |
|------|-----------|-----------------|
| 全種 (12種) | ✅ クラス存在 | ⬜ `FluidTransportModule` 未実装 |

### 1-4. エネルギーパイプ Behaviour (全種)

| 素材 | Behaviour | TransportModule |
|------|-----------|-----------------|
| 全種 (8種) | ✅ クラス存在 | ⬜ `EnergyTransportModule` 未実装 |

### 1-5. 特殊パイプ機能 (未着手)

| 項目 | 状態 | 備考 |
|------|------|------|
| Iron Pipe 強制方向 UI | ⬜ | `ironPipeOutput` フィールドは存在 |
| Diamond Pipe フィルター UI | ⬜ | Behaviour は存在 |
| Obsidian Pipe 地面吸引ロジック | ⬜ | |
| Void Pipe アイテム消去ロジック | ⬜ | |
| 渋滞検知 (bounceCount 閾値) | ⬜ | フィールドは存在 |

### 1-6. レンダリング

| 項目 | 状態 |
|------|------|
| パイプ動的形状モデル | ⬜ |
| パイプ内アイテムレンダリング | ⬜ |
| パイプ内流体レンダリング | ⬜ |

---

## 2. エンジンシステム (energy モジュール)

### 2-1. 実装済みエンジン

| エンジン | Block | BlockEntity | Menu | Screen | 状態 |
|----------|-------|-------------|------|--------|------|
| Wood Engine | ✅ | ✅ | ⬜ | ⬜ | 基本動作のみ |
| Stone Engine | ✅ | ✅ | ✅ | ✅ | 燃料燃焼・PI制御・過熱・爆発 |
| Iron Engine | ✅ | ✅ | ✅ | ✅ | 液体燃料対応 |
| Creative Engine | ✅ | ✅ | ⬜ | ⬜ | 無限出力 |

### 2-2. 未実装エンジン

| エンジン | 状態 | 概要 |
|----------|------|------|
| Combustion Engine | ⬜ | 水冷却・液体燃料・高出力 |
| Stirling Engine | ⬜ | BC2系 (Stone Engineの前身) |
| Quartz Engine | ⬜ | BCReborn 独自拡張 (任意) |

### 2-3. エンジン共通課題

| 項目 | 状態 | 備考 |
|------|------|------|
| `IEngine` インターフェース充実 | 🔧 | 現在空インターフェース |
| Wood Engine Menu / Screen | ⬜ | |
| Creative Engine Menu / Screen | ⬜ | |
| エンジンレンダリング (ピストン動作) | 🔧 | `EngineBlockRenderer` / `EngineModel` 存在 |
| エンジン → パイプへの FE 出力 | ⬜ | Capability 接続 |
| 過熱・爆発バランス調整 | 🔧 | Stone/Iron は実装済み |

---

## 3. 流体システム (common/fluids)

| 項目 | 状態 | 備考 |
|------|------|------|
| `Tank` / `TankManager` | ✅ | 基盤クラス存在 |
| 流体パイプ輸送 (`FluidTransportModule`) | ⬜ | 最優先 |
| 流体抽出モジュール (`FluidExtractionModule`) | 🔧 | クラス存在、中身要確認 |
| 流体タンクブロック | ⬜ | |

---

## 4. Builders モジュール

| 項目 | 状態 | 備考 |
|------|------|------|
| Quarry | ⬜ | BuildCraft 最重要機能 |
| Filler | ⬜ | |
| Architect Table | ⬜ | |
| Builder | ⬜ | |
| Land Mark | ⬜ | |
| Mining Well | ⬜ | |

---

## 5. Core / 共通

| 項目 | 状態 | 備考 |
|------|------|------|
| Wrench Item | ✅ | |
| `IWrench` インターフェース | ✅ | |
| Wood Engine Block (core) | ✅ | |
| レシピ / データ生成 | 🔧 | DataGatherEvent 存在 |
| 言語ファイル (en_us / ja_jp) | 🔧 | BCLanguageProvider 存在 |
| タグ (Block / Item) | 🔧 | CommonBlockTags / CommonItemTags 存在 |

---

## 6. 優先実装順 (推奨)

```
Phase 1 — パイプ完成
  1. FluidTransportModule (流体パイプ輸送)
  2. EnergyTransportModule (FEパイプ輸送)
  3. パイプ動的形状モデル (レンダリング)
  4. パイプ内アイテム/流体レンダリング
  5. Iron Pipe / Diamond Pipe UI

Phase 2 — エンジン完成
  6. Wood Engine / Creative Engine の Menu・Screen
  7. Combustion Engine 実装
  8. エンジン → パイプ FE 出力接続
  9. IEngine インターフェース充実

Phase 3 — Builders
  10. Land Mark (範囲指定)
  11. Quarry (採掘)
  12. Filler / Builder

Phase 4 — 仕上げ
  13. レシピ・タグ・言語ファイル整備
  14. 渋滞検知 (bounceCount)
  15. バランス調整・GameTest 拡充
```

---

## 7. 既知の制限事項 (意図的・変更不要)

- **同一tick多段ホップ**: PipeA→PipeB が同tickで処理される。BuildCraft互換として許容。
- **バウンスループ (A↔B)**: bounceCount 閾値で将来対処予定。
- **Capabilityキャッシュなし**: tick毎ルックアップは意図的。
- **PipeNetworkグラフなし**: 現スケールでは不要。
