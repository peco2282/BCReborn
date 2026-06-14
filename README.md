# BCReborn

BuildCraft 1.7.10 のパイプ・エンジン・Builders 機能を Minecraft 1.20.1 / Forge 47.x 向けに再実装した非公式 Mod です。

---

## 概要

BCReborn は、往年の BuildCraft が持つ以下の機能群を現代の Forge 環境で再現することを目標としています。

- **パイプシステム** — アイテム・液体・エネルギーの搬送
- **エンジンシステム** — 木材・石・鉄エンジンによる MJ/FE 発電
- **Builders モジュール** — Quarry・Filler・Architect Table などの自動建築・採掘
- **Silicon モジュール** — Assembly Table・Gate などのオートメーション拡張 *(実装予定)*

---

## 動作環境

| 項目 | バージョン |
|------|-----------|
| Minecraft | 1.20.1 |
| Forge | 47.4.8 |
| Java | 17 |
| ライセンス | GNU LGPL 3.0 |

---

## モジュール構成

| Mod ID | 名称 | 内容 |
|--------|------|------|
| `bcreborncore` | BCReborn-Core | 共通 API・レジストリ・ユーティリティ |
| `bcrebornenergy` | BCReborn-Energy | エンジン・エネルギー管理 |
| `bcrebornbuilders` | BCReborn-Builders | Quarry・Filler・Architect Table |
| `bcreborntransport` | BCReborn-Transport | パイプ搬送システム |

---

## 主な実装済み機能

### パイプシステム

- **アイテムパイプ** — Wood / Stone / Cobblestone / Iron / Golden / Diamond / Emerald / Obsidian / Void など全 16 種の Behaviour 実装
- **液体パイプ** — Wood / Stone / Iron / Diamond など主要種の Behaviour 実装
- **エネルギーパイプ** — Wood / Stone / Iron / Golden / Diamond など主要種の Behaviour 実装
- `ItemTransportModule` による TravelingItem ライフサイクル管理
- `RoutingHelper` / `MovementHelper` / `SpeedHelper` による搬送ロジック

### エンジンシステム

- **Wood Engine** — 基本動作
- **Stone Engine** — 燃料・PI 制御・過熱・冷却・爆発
- **Iron Engine** — 液体燃料対応
- **Creative Engine** — 無限エネルギー供給 (開発用)

### Builders モジュール

- **Quarry** — 自動採掘
- **Filler** — 各種パターンによる自動建築
- **Architect Table** — 建築スキャン
- **Builder** — スキーマティクスによる自動建築

---

## ビルド方法

### 前提条件

- JDK 17
- インターネット接続 (Gradle が依存関係を自動ダウンロード)

### ビルド手順

```powershell
# Windows PowerShell
./gradlew.bat build
```

成果物は `build/libs/` に生成されます。

### 開発環境セットアップ

```powershell
# IntelliJ IDEA 用プロジェクト生成
./gradlew.bat genIntellijRuns
```

### データ生成

```powershell
./gradlew.bat runData
```

生成されたリソースは `src/generated/resources/` に出力されます。

### GameTest 実行

```powershell
./gradlew.bat runGameTestServer
```

---

## パッケージ構成

```
com.peco2282.bcreborn
├── api/            — 公開 API (安定インターフェース)
├── common/         — 共通レジストリ・ユーティリティ
├── energy/         — エンジン・エネルギーモジュール
├── builder/        — Builders モジュール
└── transport/      — パイプ搬送モジュール
    ├── block/      — PipeBlock / PipeBlockEntity
    └── pipe/       — Behaviour・Transport・Extraction
```

---

## 開発状況

詳細な進捗状況と今後の計画については、[ROADMAP.md](ROADMAP.md) を参照してください。

### 🚀 現在の注力タスク
- **Transport UI**: Diamond Pipe / Iron Pipe の設定 GUI 実装
- **Heavy Machines**: Quarry / Filler の動作ロジック実装
- **Landmarks**: 範囲指定システムの基盤構築

---

## ライセンス

BC Reborn is a modernization and continuation of concepts and code
derived from BuildCraft.

Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
Copyright (c) 2025-2026 peco2282

Show details [LICENSE](LICENSE)
