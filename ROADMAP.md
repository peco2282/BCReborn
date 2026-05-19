# BCReborn — 実装ロードマップ

> 最終更新: 2026-05-19
> 対象バージョン: Minecraft 1.20.1 / Forge 47.x

---

## 凡例

| 記号 | 意味          |
|----|-------------|
| ✅  | 実装済み・動作確認済み |
| 🔧 | 部分実装・作業中    |
| ⬜  | 未着手         |

---

## 1. パイプシステム (transport モジュール)

### 1-1. 基盤

| 項目                               | 状態 | 備考                                        |
|----------------------------------|----|-------------------------------------------|
| `PipeBlock` / `PipeBlockEntity`  | ✅  | NBT・Capability・tick委譲                     |
| `TravelingItem`                  | ✅  | entryDirection・progress・speed・bounceCount |
| `ItemTransportModule`            | ✅  | TravelingItem ライフサイクル管理                   |
| `FluidTransportModule`           | ✅  | クラス存在・中身確認要                               |
| `EnergyTransportModule`          | ✅  | クラス存在・中身確認要                               |
| `MovementHelper` / `SpeedHelper` | ✅  | stateless utility                         |
| `RoutingHelper`                  | ✅  | 方向解決ロジック                                  |
| `MovementResult` enum            | ✅  | SUCCESS / NO_TARGET / BLOCKED             |
| `PipeBehaviourManager`           | ✅  |                                           |
| `ExtractionModule` (Item)        | ✅  | Standard / Filtered                       |
| `FluidExtractionModule`          | ✅  | クラス存在                                     |

### 1-2. アイテムパイプ Behaviour (全種)

| 種類          | 状態 | 特殊挙動         |
|-------------|----|--------------|
| Wood        | ✅  | 隣接インベントリから抽出 |
| Stone       | ✅  | ランダムルーティング   |
| Cobblestone | ✅  |              |
| Iron        | ✅  | 強制出力方向       |
| Golden      | ✅  | 高速           |
| Diamond     | ✅  | フィルタールーティング  |
| Emerald     | ✅  |              |
| Obsidian    | ✅  | 地面アイテム吸引     |
| Sandstone   | ✅  |              |
| Clay        | ✅  |              |
| Quartz      | ✅  |              |
| Lapis       | ✅  |              |
| Void        | ✅  | アイテム消去       |
| Stripes     | ✅  |              |
| Daizuli     | ✅  |              |
| Emzuli      | ✅  |              |

### 1-3. 液体パイプ Behaviour (全種)

| 種類                                                   | 状態 |
|------------------------------------------------------|----|
| Wood / Stone / Cobblestone / Iron / Golden           | ✅  |
| Diamond / Emerald / Quartz / Sandstone / Clay / Void | ✅  |

### 1-4. エネルギーパイプ Behaviour (全種)

| 種類                                         | 状態 |
|--------------------------------------------|----|
| Wood / Stone / Cobblestone / Iron / Golden | ✅  |
| Diamond / Quartz / Standard                | ✅  |

### 1-5. 特殊パイプ機能 (未実装)

| 項目                     | 状態 | 備考                        |
|------------------------|----|---------------------------|
| Iron Pipe 強制方向 UI      | ⬜  | `ironPipeOutput` フィールドは存在 |
| Diamond Pipe フィルター UI  | ⬜  | Behaviour は存在             |
| Obsidian Pipe 地面吸引ロジック | ⬜  |                           |
| Void Pipe アイテム消去ロジック   | ⬜  |                           |
| 渋滞検知 (bounceCount 閾値)  | ⬜  | フィールドは存在                  |

### 1-6. パイプ拡張機能 (original 参照)

| 項目                         | 状態 | 備考                     |
|----------------------------|----|------------------------|
| パイプワイヤー (PipeWire 4色)      | ⬜  | 信号伝播・WireMatrix        |
| Gate (ゲート)                 | ⬜  | Statements/Triggers 連動 |
| Facade (外装)                | ⬜  | FacadePluggable・見た目変更  |
| Plug / Lens / PowerAdapter | ⬜  | Pluggable 系            |
| Stripes Pipe ハンドラー群        | ⬜  | ブロック設置・農業・バケツ等         |
| FilteredBuffer ブロック        | ⬜  |                        |
| Gate Copier アイテム           | ⬜  |                        |

### 1-7. レンダリング

| 項目             | 状態 |
|----------------|----|
| パイプ動的形状モデル     | ⬜  |
| パイプ内アイテムレンダリング | ⬜  |
| パイプ内液体レンダリング   | ⬜  |

---

## 2. エンジンシステム (energy モジュール)

### 2-1. 実装済みエンジン

| エンジン            | Block | BlockEntity | Menu | Screen | 状態               |
|-----------------|-------|-------------|------|--------|------------------|
| Wood Engine     | ✅     | ✅           | ⬜    | ⬜      | 基本動作のみ           |
| Stone Engine    | ✅     | ✅           | ✅    | ✅      | 燃料・PI制御・過熱・冷却・爆発 |
| Iron Engine     | ✅     | ✅           | ✅    | ✅      | 液体燃料対応           |
| Creative Engine | ✅     | ✅           | ⬜    | ⬜      | 無限出力             |

### 2-2. 未実装エンジン

| エンジン              | 状態 | 備考                      |
|-------------------|----|-------------------------|
| Combustion Engine | ⬜  | 液体燃料・液体冷却・高出力           |
| Stirling Engine   | ⬜  | BC2系 (Stone Engine の前身) |
| Quartz Engine     | ⬜  | BCReborn 独自拡張 (仮)       |

### 2-3. エンジン共通基盤

| 項目                            | 状態 | 備考                                       |
|-------------------------------|----|------------------------------------------|
| `IEngine` インターフェース整備          | 🔧 | 現在空インターフェース                              |
| Wood Engine Menu / Screen     | ⬜  |                                          |
| Creative Engine Menu / Screen | ⬜  |                                          |
| エンジンレンダリング (ピストン動作)           | 🔧 | `EngineBlockRenderer` / `EngineModel` 存在 |
| エンジン → パイプへの FE 出力            | ⬜  | Capability 接続                            |
| 過熱・爆発バランス調整                   | 🔧 | Stone/Iron は実装済み                         |
| Oil / Fuel 液体                 | ✅  | `OilFluid` / `FuelFluid` 存在              |
| FuelManager / CoolantManager  | ⬜  | original 参照                              |

---

## 3. 液体システム (common/fluids)

| 項目                                  | 状態 | 備考                                 |
|-------------------------------------|----|------------------------------------|
| `Tank` / `TankManager`              | ✅  | 基底クラス存在                            |
| 液体パイプ一貫 (`FluidTransportModule`)    | ⬜  | 最優先                                |
| 液体抽出モジュール (`FluidExtractionModule`) | 🔧 | クラス存在・中身確認要                        |
| 液体タンクブロック                           | ⬜  | original: `TileTank` / `BlockTank` |

---

## 4. Builders モジュール

### 4-1. ブロック・BlockEntity (存在確認済み)

| ブロック                | Block | BlockEntity | Menu/Screen | ロジック | 状態 |
|---------------------|-------|-------------|-------------|------|----|
| Construction Marker | ✅     | ✅           | ⬜           | ⬜    | 🔧 |
| Architect Table     | ✅     | ✅           | ⬜           | ⬜    | 🔧 |
| Builder             | ✅     | ✅           | ⬜           | ⬜    | 🔧 |
| Filler              | ✅     | ⬜           | ⬜           | ⬜    | ⬜  |
| Quarry              | ✅     | ⬜           | ⬜           | ⬜    | ⬜  |
| Frame               | ✅     | —           | —           | —    | ✅  |
| Blueprint Library   | ✅     | ✅           | ⬜           | ⬜    | 🔧 |

### 4-2. Blueprint / Schematic 基盤

| 項目                                           | 状態 | 備考                                        |
|----------------------------------------------|----|-------------------------------------------|
| `Blueprint` / `Template`                     | ✅  |                                           |
| `BptBuilderBlueprint` / `BptBuilderTemplate` | ✅  |                                           |
| `SchematicRegistry`                          | ✅  |                                           |
| 各種 Schematic 実装                              | ✅  | BlockCreative / Floored / Free / Ignore 等 |
| Blueprint ネットワークパケット                         | ✅  | Upload/Download/Sync 系                    |

### 4-3. Filler パターン

| 項目                                                                                  | 状態 |
|-------------------------------------------------------------------------------------|----|
| PatternBox / Clear / Cylinder / Fill / Flatten / Frame / Horizon / Pyramid / Stairs | ✅  |
| FillerRegistry / FillerPattern                                                      | ✅  |
| Filler BlockEntity + Menu + Screen                                                  | ⬜  |
| Filler 実行ロジック                                                                       | ⬜  |

### 4-4. Quarry

| 項目                              | 状態 | 備考               |
|---------------------------------|----|------------------|
| Quarry BlockEntity              | ⬜  | BuildCraft 最重要機能 |
| 掘削ロジック (EntityMechanicalArm 相当) | ⬜  |                  |
| Land Mark (範囲指定)                | ⬜  |                  |
| Mining Well                     | ⬜  |                  |

---

## 5. Factory モジュール (original: `buildcraft.factory`) — 未着手

| ブロック           | 状態 | 備考            |
|----------------|----|---------------|
| Pump           | ⬜  | 液体汲み上げ        |
| Refinery       | ⬜  | Oil → Fuel 精製 |
| Auto Workbench | ⬜  | 自動クラフト        |
| Tank (Factory) | ⬜  | 大容量液体貯蔵       |
| Flood Gate     | ⬜  | 液体放流          |
| Hopper (BC版)   | ⬜  |               |

---

## 6. Silicon モジュール (original: `buildcraft.silicon`) — 未着手

| ブロック/アイテム               | 状態 | 備考            |
|-------------------------|----|---------------|
| Laser                   | ⬜  | テーブルへのエネルギー供給 |
| Assembly Table          | ⬜  | レーザー駆動クラフト    |
| Advanced Crafting Table | ⬜  |               |
| Integration Table       | ⬜  |               |
| Charging Table          | ⬜  |               |
| Programming Table       | ⬜  | Gate プログラム    |
| Stamping Table          | ⬜  |               |
| Packager                | ⬜  |               |
| Redstone Chipset (アイテム) | ⬜  | Gate 素材       |
| ItemPackage             | ⬜  |               |

---

## 7. Core / 共通

| 項目                       | 状態 | 備考                                  |
|--------------------------|----|-------------------------------------|
| Wrench Item              | ✅  |                                     |
| `IWrench` インターフェース       | ✅  |                                     |
| Wood Engine Block (core) | ✅  |                                     |
| レシピ・データ生成                | 🔧 | DataGatherEvent 存在                  |
| 言語ファイル (en_us / ja_jp)   | 🔧 | BCLanguageProvider 存在               |
| タグ (Block / Item)        | 🔧 | CommonBlockTags / CommonItemTags 存在 |
| Oil 世界生成                 | ⬜  | original: `OilPopulate` / Oil Biome |
| Statements / Triggers 基盤 | ⬜  | Gate 連動                             |
| GameTest 整備              | 🔧 | `TransportGameTests` 存在             |

---

## 8. 優先実装順序（詳細版）

> 凡例: ✅ 完了 / 🔧 部分実装 / ⬜ 未着手

---

### Phase 1 — パイプ: 特殊 Behaviour の完成

Behaviour クラスは全種存在するが、特殊ロジックが未完成のものを優先的に仕上げる。

#### 1-A. アイテムパイプ Behaviour 修正・追加実装

| # | 対象 Behaviour | 作業内容 | 状態 |
|---|--------------|---------|------|
| 1 | `ObsidianItemPipeBehaviour` | `tick()` で周囲の `ItemEntity` を吸引してパイプに注入するロジック実装 (`getEntitiesOfClass` → `injectItem`) | 🔧 クラス存在・吸引ロジック要確認 |
| 2 | `VoidItemPipeBehaviour` | `onReachedCenter()` でアイテムを消去 (ItemStack を drop せず破棄) するロジック確認・完成 | 🔧 クラス存在・消去ロジック要確認 |
| 3 | `IronItemPipeBehaviour` | `ironPipeOutput` フィールドを使った強制出力方向 UI (Menu + Screen) 実装。レンチ右クリックで方向切替は実装済み | 🔧 Behaviour 完成・UI 未実装 |
| 4 | `DiamondItemPipeBehaviour` | フィルタースロット UI (Menu + Screen) 実装。`chooseNextDirection()` / `usedFilters` bitmask ロジックは実装済み | 🔧 Behaviour 完成・UI 未実装 |
| 5 | `EmzuliItemPipeBehaviour` | 送信先アドレス指定ロジック確認・完成 (original: `PipeItemsEmzuli` の destination 管理) | 🔧 クラス存在・ロジック要確認 |
| 6 | `DaizuliItemPipeBehaviour` | Emzuli 派生の分配ロジック確認・完成 | 🔧 クラス存在・ロジック要確認 |
| 7 | `StripesItemPipeBehaviour` | ブロック設置・農業・バケツ操作ハンドラー群の実装 (original: `IStripesHandler` 各実装) | 🔧 基本クラス存在・ハンドラー群未実装 |
| 8 | `EmeraldItemPipeBehaviour` | 抽出フィルター付き Wood 相当ロジック確認・完成 | 🔧 クラス存在・要確認 |

#### 1-B. 液体パイプ Behaviour 修正・追加実装

| # | 対象 Behaviour | 作業内容 | 状態 |
|---|--------------|---------|------|
| 9 | `WoodenFluidPipeBehaviour` | `FluidExtractionModule.extract()` の動作確認・完成 | 🔧 tick 呼び出しは実装済み |
| 10 | `IronFluidPipeBehaviour` | 強制出力方向 UI (Menu + Screen) 実装。`canConnectTo()` / レンチ切替は実装済み | 🔧 Behaviour 完成・UI 未実装 |
| 11 | `DiamondFluidPipeBehaviour` | フィルタースロット UI (Menu + Screen) 実装。`transferFluid()` フィルターロジックは実装済み | 🔧 Behaviour 完成・UI 未実装 |
| 12 | `VoidFluidPipeBehaviour` | `tick()` でタンク内液体を全消去するロジック確認 (実装済みに見えるが動作確認) | 🔧 実装済み・動作確認要 |
| 13 | `EmeraldFluidPipeBehaviour` | 抽出フィルター付き Wood 相当ロジック確認・完成 | 🔧 クラス存在・要確認 |

#### 1-C. エネルギーパイプ Behaviour 修正・追加実装

| # | 対象 Behaviour | 作業内容 | 状態 |
|---|--------------|---------|------|
| 14 | `WoodenEnergyPipeBehaviour` | `extractEnergy()` で隣接エンジン/機械から FE を吸い出し `EnergyTransportModule` へ注入するロジック確認・完成 | 🔧 クラス存在・要確認 |
| 15 | `IronEnergyPipeBehaviour` | 転送レート上限 UI (Menu + Screen) 実装。`TRANSFER_STEPS` / レンチ切替は実装済み | 🔧 Behaviour 完成・UI 未実装 |
| 16 | `StandardEnergyPipeBehaviour` | 基底クラスの `transferEnergy()` ロジック確認・完成 | 🔧 クラス存在・要確認 |

#### 1-D. Transport Module 完成

| # | 対象 | 作業内容 | 状態 |
|---|------|---------|------|
| 17 | `FluidTransportModule` | tick ごとの液体移送ロジック (タンク間転送・方向決定) 確認・完成 | 🔧 クラス存在・中身確認要 |
| 18 | `EnergyTransportModule` | tick ごとの FE 分配ロジック確認・完成 | 🔧 クラス存在・中身確認要 |
| 19 | `FluidExtractionModule` | 隣接 `IFluidHandler` からの液体抽出ロジック確認・完成 | 🔧 クラス存在・中身確認要 |

#### 1-E. パイプレンダリング

| # | 対象 | 作業内容 | 状態 |
|---|------|---------|------|
| 20 | パイプ動的形状モデル | 接続方向に応じた BakedModel / OBJ 動的生成 | ⬜ |
| 21 | パイプ内アイテムレンダリング | `TravelingItem` の progress に応じた TESR 描画 | ⬜ |
| 22 | パイプ内液体レンダリング | 液体テクスチャの TESR 描画 | ⬜ |

---

### Phase 2 — エンジン: 未完成エンジンの完成

#### 2-A. ブロック・アイテム実装

| # | 対象 | 作業内容 | 状態 |
|---|------|---------|------|
| 23 | Wood Engine Menu + Screen | インベントリ・燃料スロット UI | ⬜ |
| 24 | Creative Engine Menu + Screen | 無限出力設定 UI | ⬜ |
| 25 | Combustion Engine Block + BlockEntity | 液体燃料・液体冷却・高出力エンジン全実装 | ⬜ |
| 26 | Combustion Engine Menu + Screen | 液体タンク表示 UI | ⬜ |

#### 2-B. 共通基盤

| # | 対象 | 作業内容 | 状態 |
|---|------|---------|------|
| 27 | `FuelManager` | 燃料液体 → 発熱量マッピング (original 参照) | ⬜ |
| 28 | `CoolantManager` | 冷却液体 → 冷却量マッピング | ⬜ |
| 29 | `IEngine` インターフェース整備 | 出力・過熱・爆発の共通 API 定義 | 🔧 空インターフェース |
| 30 | エンジン → パイプ FE 出力 | `IEnergyStorage` Capability 経由で隣接 Energy Pipe へ出力 | ⬜ |
| 31 | エンジンレンダリング (ピストン動作) | `EngineBlockRenderer` / `EngineModel` 完成 | 🔧 クラス存在 |

---

### Phase 3 — Builders: ロジック実装

#### 3-A. ブロック・アイテム実装順序

| # | 対象 | 作業内容 | 状態 |
|---|------|---------|------|
| 32 | Construction Marker BlockEntity 完成 | 範囲指定ロジック・Land Mark 連動 | 🔧 BlockEntity 存在 |
| 33 | Land Mark Block + BlockEntity | 3 軸マーカー・範囲確定ロジック | ⬜ |
| 34 | Quarry Block + BlockEntity | 掘削ロジック・アーム Entity・アイテム排出 | ⬜ |
| 35 | Quarry Menu + Screen | 進捗表示・フィルター UI | ⬜ |
| 36 | Filler BlockEntity | パターン実行ロジック・ブロック設置 | ⬜ |
| 37 | Filler Menu + Screen | パターン選択・インベントリ UI | ⬜ |
| 38 | Builder Menu + Screen | Blueprint 読み込み・建築ロジック UI | 🔧 BlockEntity 存在 |
| 39 | Architect Table Menu + Screen | Blueprint 作成 UI | 🔧 BlockEntity 存在 |
| 40 | Blueprint Library Menu + Screen | Blueprint 保存・読み込み UI | 🔧 BlockEntity 存在 |
| 41 | Mining Well Block + BlockEntity | 単穴掘削 (Quarry 簡易版) | ⬜ |

---

### Phase 4 — Factory モジュール: 新規実装

#### 4-A. ブロック・アイテム実装順序

| # | 対象 | 作業内容 | 状態 |
|---|------|---------|------|
| 42 | Pump Block + BlockEntity | 液体汲み上げ・`IFluidHandler` 出力 | ⬜ |
| 43 | Pump Menu + Screen | 汲み上げ状態表示 UI | ⬜ |
| 44 | Refinery Block + BlockEntity | Oil → Fuel 精製・液体 IO | ⬜ |
| 45 | Refinery Menu + Screen | 精製進捗 UI | ⬜ |
| 46 | Tank Block + BlockEntity | 大容量液体貯蔵 (original: `TileTank`) | ⬜ |
| 47 | Auto Workbench Block + BlockEntity | 自動クラフトロジック | ⬜ |
| 48 | Auto Workbench Menu + Screen | レシピ設定 UI | ⬜ |
| 49 | Flood Gate Block + BlockEntity | 液体放流ロジック | ⬜ |

---

### Phase 5 — Silicon モジュール: 新規実装

#### 5-A. ブロック・アイテム実装順序

| # | 対象 | 作業内容 | 状態 |
|---|------|---------|------|
| 50 | Laser Block + BlockEntity | テーブルへのエネルギービーム供給 | ⬜ |
| 51 | Assembly Table Block + BlockEntity | Laser 駆動クラフトロジック | ⬜ |
| 52 | Assembly Table Menu + Screen | レシピ・進捗 UI | ⬜ |
| 53 | Advanced Crafting Table Block + BlockEntity | 高度クラフト | ⬜ |
| 54 | Integration Table Block + BlockEntity | Chipset 統合 | ⬜ |
| 55 | Charging Table Block + BlockEntity | アイテム充電 | ⬜ |
| 56 | Programming Table Block + BlockEntity | Gate プログラム書き込み | ⬜ |
| 57 | Redstone Chipset アイテム群 | Iron / Gold / Diamond / Emerald Chipset | ⬜ |
| 58 | ItemPackage アイテム | パッケージ化アイテム | ⬜ |

---

### Phase 6 — パイプ拡張機能

#### 6-A. 実装順序

| # | 対象 | 作業内容 | 状態 |
|---|------|---------|------|
| 59 | パイプワイヤー (4色) | `PipeWire` 信号伝播・`WireMatrix` | ⬜ |
| 60 | Statements / Triggers 基盤 | Gate 連動の条件・アクション API | ⬜ |
| 61 | Gate Block + BlockEntity | Statements/Triggers 評価ロジック | ⬜ |
| 62 | Gate Menu + Screen | 条件・アクション設定 UI | ⬜ |
| 63 | Gate Copier アイテム | Gate 設定コピー | ⬜ |
| 64 | Facade (外装) | `FacadePluggable`・見た目変更 | ⬜ |
| 65 | Plug / Lens / PowerAdapter | Pluggable 系アイテム | ⬜ |
| 66 | Stripes Pipe ハンドラー群 | `IStripesHandler` 各実装 (ブロック設置・農業・バケツ等) | ⬜ |
| 67 | FilteredBuffer Block + BlockEntity | バッファー付きフィルター | ⬜ |

---

### Phase 7 — 仕上げ

| # | 対象 | 作業内容 | 状態 |
|---|------|---------|------|
| 68 | Oil 世界生成 | `OilPopulate` / Oil Biome | ⬜ |
| 69 | レシピ整備 | 全ブロック・アイテムのクラフトレシピ | 🔧 |
| 70 | タグ整備 | Block / Item タグ全種 | 🔧 |
| 71 | 言語ファイル整備 | en_us / ja_jp 全キー | 🔧 |
| 72 | 渋滞検知 | `bounceCount` 閾値・ジャム検出・アイテムドロップ | ⬜ |
| 73 | GameTest 拡充 | 全パイプ種・エンジン・Builders の自動テスト | 🔧 |
| 74 | バランス調整 | 速度・容量・燃費の数値調整 | ⬜ |

---

## 9. 既知の制限事項 (設計上の合意・変更不要)

- **同一 tick 多段ホップ**: PipeA→PipeB が同tick で処理される。BuildCraft 互換として許容。
- **バウンスループ (A↔B)**: bounceCount 閾値で将来対処予定。
- **Capability キャッシュなし**: tick毎ルックアップは意図的。
- **PipeNetwork グラフなし**: 現スケールでは不要。
