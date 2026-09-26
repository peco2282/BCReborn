# 1.7.10 → 1.20.1 移植レビュー

調査日: 2026-09-26。最終確認時の HEAD: `2315880`。

## 第1段階: 高優先度項目の修正状況（2026-09-26）

後続の修正依頼に基づき、以下の P1 項目を実装した。以下に続くレビュー本文は修正前の調査記録として残している。この段階では P2 項目は対象外（第2段階で対応済み）。

| 指摘 | 修正内容 |
|---|---|
| 1: 設計図 NBT | 標準の文字列 ID とアイテム NBT を保持。旧数値 ID は設計図内の保存済みパレットから復元し、未知の ID や未対応の旧メタデータは拒否する。必要資材の読込失敗時はサバイバル建築を許可しない。 |
| 2: C2S 機械操作 | `ServerPacketAccess` に送信者・ロード済みチャンク・8ブロック以内の距離・メニュー対象と実体の一致を集約。Builder / Filler / Architect / Blueprint Library / Zone Plan の変更要求に適用。範囲外の選択値やアップロード範囲も拒否する。通常の同期要求とは分離。 |
| 3: 流体入力方向 | 方向が不変の面別ハンドラを公開し、タンク本体を共有。無方向アクセスは独立したハンドラを使用。再初期化・破棄で公開済み Capability を無効化する。 |
| 4: エネルギー接続 | 両側の Behaviour と遮断プラグを確認する共通判定を導入。接続表示・需要・実転送・Capability 受入れに適用。キャッシュ済み Capability でも毎回確認する。 |
| 6: 鉄エンジン冷却待ち | カウントダウンを燃焼中限定の処理から毎 server tick の更新へ移動。停止中にも待ち時間を減らし、温度条件を満たせば再着火する。 |

補足: 既存の誤変換ですでに AIR 等へ置き換わった設計図から、元のアイテム ID を推測して復元する処理は行わない。アップロードは最大 16 MiB に制限する。

検証用に `MigrationRegressionGameTests` を追加（7件）。NBT 往復・旧形式/未知 ID・流体ハンドラの保持と無効化・非接続の送電拒否・正常な送電・メニュー操作検証・冷却待ちの保存復元を対象とする。

第1段階の検証: `./gradlew.bat build --offline` 成功（コンパイル、JAR生成・再難読化、Spotlessチェック）。`git diff --check` も成功。JUnit は `NO-SOURCE` のため実行件数なし。標準チェックで検出された2ファイルの混在改行を LF に統一した。

`runGameTestServer --offline` はテスト実行開始前に、既存の Mod 初期化によるクライアント専用クラスの読み込みで停止した。`BCReborn*` の初期化で `Screen`、`ContextProcessor.packetRegister()` で `ClientLevel` が DEDICATED_SERVER 上にロードされる。Gradle が `BUILD SUCCESSFUL` を返しても GameTest 合格ではない。第1段階では GameTest は未実行だった。第2段階で起動障害を修正し、再実行した。

## 第2段階: 残りの不具合への対応（2026-09-26）

| 対象 | 修正内容 |
|---|---|
| 5: エネルギー受入れ | 照会と実行が同じ次tickバッファ・抵抗・容量計算を使う。照会では状態を変更しない。整数FEで返す量だけを投入し、端数による無償投入も防止。木パイプの吸出し見積りも共通化。 |
| 7: 燃料保存 | 消費中の燃料IDを残り時間と保存。最後の1mB消費後でも復元。旧保存はタンクから補完可能な場合に復元し、燃料定義が失われた場合は安全に停止。 |
| 8: レッドストーン制御 | 信号OFFで着火・燃料消費・発電を停止し、残り時間を保持して自然冷却。ONで再開。 |
| 9: 落下ブロック | 砂・砂利の前提を下側の支持ブロックに修正。 |
| 10: 水没固体 | 流体自身のブロックとwaterlogged固体を区別。固体を採掘し、水源を残す。 |
| 無方向エネルギーAPI | 輸送に繋がらない独立バッファの公開をやめ、FEアクセスは方向指定を必須にする。 |
| Quarryフレーム | 障害物や未ロード位置をキューに保持し、フレーム未完成で採掘段階に進まない。障害物除去後に再開。設置成功時だけ25FEを消費し、既設フレームには重複課金しない。 |
| 専用サーバー起動 | 設定画面登録・パケットのMinecraft参照・Factory描画イベントをクライアント側に隔離。 |
| エンジン更新 | 石・鉄・クリエイティブエンジンの不足していたBlockEntityTickerを登録。実ワールドで発電・冷却処理が動くようにする。 |
| 組立レシピ | 複数材料を「いずれか」ではなく個別の必須材料として生成。コンパレータ用のIDをcomp_chipsetに分離し、diamond_chipsetとの重複を解消。 |

仕様の扱い:
- 燃料APIは現在の登録値に合わせ、時間をtick/mB、出力をFE/tと明記。呼出しはgetBurnTimePerMilliBucketへ変更した。石油の登録値は維持し、1.7.10の燃費・出力への再調整は行っていない。
- 流体パイプは単一タンクの設計を維持。旧7セクションの容量・遅延・分岐挙動を完全再現する変更は含めない。
- Quarryは障害物待機を採用。旧版の自動整地を再実装したわけではなく、フレーム位置の障害物は利用者が除去する。

検証対象: MigrationRegressionGameTestsは13件。新規の受入量一致・信号制御と燃料復元・支持方向・waterlogged採掘・フレーム待機/保存/再開に加え、第1段階の回帰を含む。既存の組立テストには、ダイヤだけでは製造できず両材料が必要なことと、別IDの存在を追加した。

最終検証: `./gradlew.bat runData --offline` 成功。`./gradlew.bat runGameTestServer build --offline` 成功。専用サーバーのログで **All 25 required tests passed** を確認（回帰13件＋既存12件）。JAR生成・再難読化・Spotlessチェックも成功。`git diff --check` 成功。JUnitはNO-SOURCE。クライアント画面の実機操作は未検証。

生成リソースは既存設定によりGit管理外のsrc/generated/resourcesへ出力される。別チェックアウトでも修正を反映するには `./gradlew.bat runData --offline` を実行する。

## 修正前の調査記録

`original/BuildCraft` と現行ソースを比較した静的レビュー。重点対象は Transport、鉄エンジン、Quarry、設計図、C2S パケット。全クラスの網羅監査ではない。調査中に作業ツリーが更新されたため、以下の指摘は更新後にも存在することを再確認した。実装コードの修正は行っていない。

検証: `./gradlew.bat compileJava --offline --rerun-tasks` は成功。GameTest・実機での再現確認は未実施。以下の発生条件とテスト案はコード経路から導いたものであり、実行済みテストではない。

優先度: P1 = データ破損、操作検証の欠落、基本機能の停止など。P2 = 条件付きの機能不整合。コンパイルに通ることと移植の動作互換性は別に評価する。

## 1. P1: 設計図の ItemStack NBT に旧数値 ID の変換が残っている

- 現行: [MappingRegistry.java:169](src/main/java/com/peco2282/bcreborn/api/blueprints/MappingRegistry.java#L169)、[SchematicBlock.java:273](src/main/java/com/peco2282/bcreborn/api/blueprints/SchematicBlock.java#L273)
- 旧実装: [MappingRegistry.java:160](original/BuildCraft/api/buildcraft/api/blueprints/MappingRegistry.java#L160)
- 問題: `stackToRegistry()` が `id` を `getShort()` で読み、`putShort()` で書き換えている。1.20.1 の `ItemStack.save()` が生成する `id` は名前空間付き文字列。文字列を数値として読むと正しいアイテムを特定できず、復元側も数値を書き戻すため `ItemStack.of()` と整合しない。必要資材の保存・復元が壊れる。`isStackLayout()` も旧形式のトップレベル `Damage` / `ShortTag` を前提にしている。
- 修正方針: 現行形式では ResourceLocation の文字列 ID と ItemStack 標準 NBT を保持する。独自パレット ID が必要なら `id` とは別キーに保存し、復元時に標準形式へ戻す。旧数値 ID の読み込みは形式バージョン付きの専用変換処理へ隔離し、不明 ID を AIR に黙って変換しない。
- 回帰テスト: 名前・耐久値・エンチャント・独自 NBT のあるアイテムと通常ブロックを設計図に保存し、再読込後の必要資材が一致すること。旧形式は受け付ける範囲と拒否する範囲を明示する。

## 2. P1: C2S の機械操作が対象座標と型だけで許可される

- 現行: [CustomPacket.java:30](src/main/java/com/peco2282/bcreborn/common/packet/CustomPacket.java#L30)、[EraseBuilderTankPacket.java:39](src/main/java/com/peco2282/bcreborn/common/packet/c2s/EraseBuilderTankPacket.java#L39)、[SetFillerPatternPacket.java:39](src/main/java/com/peco2282/bcreborn/common/packet/c2s/SetFillerPatternPacket.java#L39)
- 問題: クライアント指定座標に対し、送信者の Level から BlockEntity を取得して変更している。これらの経路には操作距離、対象のメニューを開いているか、対象チャンクがロード済みかの確認がない。GUI を介さないパケットでも、同じディメンションの離れた機械を操作できる。Filler では任意の `delta` を `currentPattern` に代入する分岐もある。
- 修正方針: 変更系パケットのサーバー処理で sender、ロード済み座標、対象 BE、距離、開いているメニューと対象の一致、値の範囲を検証する。メニューを持たない操作はその操作用の検証条件を設ける。読取・同期要求まで一律に同じ条件で縛らない。
- 回帰テスト: 通常 GUI 操作は成功し、遠距離・別対象メニュー・未ロード座標・不正インデックスは状態を変更しないこと。
- 根拠: [Forge 1.20.1 SimpleImpl](https://docs.minecraftforge.net/en/1.20.1/networking/simpleimpl/) はクライアントから届くデータを信用せず、座標アクセス前にチャンクのロード状態を確認するよう説明している。

## 3. P1: 流体 Capability が入力方向を共有変数で保持している

- 現行: [PipeBlockEntity.java:668](src/main/java/com/peco2282/bcreborn/transport/block/entity/PipeBlockEntity.java#L668)、[PipeFluidHandler.java:29](src/main/java/com/peco2282/bcreborn/transport/block/entity/PipeFluidHandler.java#L29)
- 旧実装: [PipeTransportFluids.java:573](original/BuildCraft/common/buildcraft/transport/PipeTransportFluids.java#L573) は `fill(from, ...)` の呼出しごとに方向を受け取る。
- 問題: 全方向に同一ハンドラを返し、`getCapability(side)` のたびに `fillDirection` を変更する。北側ハンドラを保持した後で南側 Capability を取得すると、北側のハンドラで実際に fill しても南側が入力扱いになる。単一スレッドでも発生し、TTL による逆流防止が誤った面に適用される。`side == null` の取得では以前の方向が残る。
- 修正方針: 面ごとに方向を不変フィールドとして持つハンドラと LazyOptional を用意する。タンク本体だけを共有し、null 面の扱いは明示する。無効化も公開した全インスタンスに対して行う。
- 回帰テスト: 北・南ハンドラを先に取得して保持し、北から fill したとき北だけが Input になること。取得順を逆にしても結果が変わらないこと。
- 根拠: [Forge 1.20.1 Capabilities](https://docs.minecraftforge.net/en/1.20.1/datastorage/capabilities/) の面別インスタンスとライフサイクルの説明に合わせる。

## 4. P1: エネルギー搬送がパイプの接続判定を迂回する

- 現行: [EnergyTransportModule.java:95](src/main/java/com/peco2282/bcreborn/transport/pipe/transport/EnergyTransportModule.java#L95)、[StandardEnergyPipeBehaviour.java:37](src/main/java/com/peco2282/bcreborn/transport/pipe/behaviour/impl/energy/StandardEnergyPipeBehaviour.java#L37)
- 旧実装: `original/BuildCraft/common/buildcraft/transport/PipeTransportPower.java` の接続先キャッシュ、`outputOpen()`、需要収集処理。
- 問題: Behaviour は石と丸石の接続を禁止しているが、輸送側は隣接する ENERGY パイプなら受信先として採用し、需要伝播も直接行う。接続 BlockState / Behaviour を確認しないため、見た目では接続していない石・丸石間でも需要と電力が通る経路がある。
- 修正方針: 接続可否・入力可否・出力可否の判定を共通化し、需要収集、需要伝播、実転送、外部 Capability からの受入れで一貫して使用する。双方の面の条件も確認する。
- 回帰テスト: 電源→石→丸石→消費機械では境界を越えて送電しないこと。同材質で組んだ経路では正常に送電すること。

## 5. P2: エネルギー受入れの SIMULATE と EXECUTE が異なるバッファを見る

- 現行: [PipeEnergyStorage.java:30](src/main/java/com/peco2282/bcreborn/transport/pipe/transport/PipeEnergyStorage.java#L30)、[EnergyTransportModule.java:248](src/main/java/com/peco2282/bcreborn/transport/pipe/transport/EnergyTransportModule.java#L248)
- 問題: SIMULATE は `internalPower` の空きから受入量を返すが、実受入れは `internalNextPower` の空きと抵抗から決める。同一 tick に既に受入済みの場合や、現 tick のバッファだけが満杯の場合に結果が食い違う。実受入れ自体には上限があるため、これだけで複製が起きるとは断定しないが、接続先の需要見積りや送電判断を誤らせる。
- 修正方針: モジュール側に副作用なしの受入可能量計算を設け、tick 境界・損失・整数丸めを含めて SIMULATE / EXECUTE が同じ判定を利用する。
- 回帰テスト: 同一 tick の複数受入れ、次 tick への切替、抵抗あり、満杯付近で照会量と直後の実受入量が一致すること。SIMULATE 自体で蓄電量が変化しないこと。

## 6. P1: 鉄エンジンが過熱後に冷却待ちから復帰できない

- 現行: [IronEngineBlockEntity.java:91](src/main/java/com/peco2282/bcreborn/energy/block/entity/IronEngineBlockEntity.java#L91)、[EngineBlockEntity.java:183](src/main/java/com/peco2282/bcreborn/common/block/entity/EngineBlockEntity.java#L183)
- 旧実装: [TileEngineIron.java:247](original/BuildCraft/common/buildcraft/energy/TileEngineIron.java#L247) は冷却条件を満たすと燃焼とは別に penalty を減らす。
- 問題: `overheat()` が `burnTime = 0` と `penaltyCoolingTime = 1000` を設定する。penalty を減らす処理は `burning()` 内だけだが、親クラスは `isBurning()` が true のときしか呼ばない。再着火も penalty が 0 以下になることを要求するため、復帰条件を満たせない。
- 修正方針: 燃焼処理と冷却・待機時間の更新を分離する。冷却待ちから復帰する温度条件を決め、非燃焼時にも必ず状態遷移を進める。
- 回帰テスト: 爆発しない条件で過熱状態を与え、停止→冷却→待機解除→再着火まで進むこと。途中の保存・再読込でも復帰すること。

## 7. P2: 鉄エンジンの燃焼途中の保存・復元で燃料情報を失う

- 現行: [IronEngineBlockEntity.java:75](src/main/java/com/peco2282/bcreborn/energy/block/entity/IronEngineBlockEntity.java#L75)、[IronEngineBlockEntity.java:149](src/main/java/com/peco2282/bcreborn/energy/block/entity/IronEngineBlockEntity.java#L149)
- 旧実装: [TileEngineIron.java:172](original/BuildCraft/common/buildcraft/energy/TileEngineIron.java#L172) は燃焼時にタンクから `currentFuel` を再取得する。
- 問題: `burnTime` は復元するが `currentFuel` は復元しない。`burnTime > 0` で再ロードした新しい BE は `currentFuel == null` のため発電・残り時間減算を行わず、`updateProgress()` も残り時間が 0 になるまで燃料を再設定しない。
- 発生条件: 残り燃焼時間が正の状態で保存された場合。現在の組込み登録は石油の燃焼時間が 1 のため、通常の再現機会は限定されるが、長時間燃料や今後の燃料値修正で表面化する。
- 修正方針: 消費中の燃料の ResourceLocation を残り燃焼時間とともに永続化し、ロード時にレジストリから復元する。最後の 1mB を消費してタンクが空の場合にも復元できるようにする。燃料定義が消失した場合の停止方針も決める。
- 回帰テスト: 複数 tick 燃える燃料を使い、タンク残量あり／最後の 1mB 消費後の両方で保存・再読込して燃焼が継続すること。

## 8. P2: 鉄エンジンの燃料消費がレッドストーン信号を無視する

- 現行: [IronEngineBlockEntity.java:74](src/main/java/com/peco2282/bcreborn/energy/block/entity/IronEngineBlockEntity.java#L74)、[EngineBlockEntity.java:183](src/main/java/com/peco2282/bcreborn/common/block/entity/EngineBlockEntity.java#L183)
- 旧実装: [TileEngineIron.java:182](original/BuildCraft/common/buildcraft/energy/TileEngineIron.java#L182) の燃焼処理は `isRedstonePowered` で制御される。
- 問題: 現行は着火・燃料消費・発電に信号条件がない。親クラスの信号判定はピストン制御などには使われるが、これらの処理を止めない。信号 OFF で出力動作が止まっても、燃料消費と発熱が続く。
- 修正方針: 旧実装準拠なら信号 OFF 時の燃焼停止を明示する。残り燃焼時間の保持、自然冷却、再始動待ちを一つの状態遷移として整理する。
- 回帰テスト: 信号 OFF の燃料投入で消費しないこと。ON→OFF→ON の途中で燃料総量と残り燃焼時間が仕様どおり変化すること。

## 9. P2: 砂・砂利の先行建築条件が上下逆

- 現行: [SchematicBlock.java:39](src/main/java/com/peco2282/bcreborn/api/blueprints/SchematicBlock.java#L39)、[SchematicBlock.java:155](src/main/java/com/peco2282/bcreborn/api/blueprints/SchematicBlock.java#L155)
- 旧実装: [SchematicBlock.java:111](original/BuildCraft/api/buildcraft/api/blueprints/SchematicBlock.java#L111) は `ForgeDirection.DOWN.ordinal()` を使う。
- 問題: 配列の 0 番が `(0,-1,0)`、1 番が `(0,1,0)` なのに、FallingBlock の前提条件として `RELATIVE_INDEXES[1]` を指定している。支持ブロックより上のブロックを待つことになり、配置順序が壊れる。
- 修正方針: `BlockPos.ZERO.below()` などで下方向を直接表し、コメントと配列インデックスの対応への依存を減らす。ほかの支持方向付き Schematic も同じ基準で確認する。
- 回帰テスト: 下地＋砂／砂利＋上部ブロックの設計図で下地が先に配置され、砂・砂利が意図しない位置へ落下しないこと。

## 10. P2: Quarry が水没した固体ブロックも液体として除外する

- 現行: [QuarryBlockEntity.java:355](src/main/java/com/peco2282/bcreborn/builders/block/entity/QuarryBlockEntity.java#L355)
- 旧実装: [TileQuarry.java:497](original/BuildCraft/common/buildcraft/builders/TileQuarry.java#L497) は液体ブロック型を除外する。
- 問題: `!state.getFluidState().isEmpty()` を理由に採掘対象から外すため、水没した階段・ハーフブロックなど waterlogged の固体まで採掘しない。1.20.1 では「流体を含む」と「ブロック自体が液体」は同義ではない。
- 修正方針: 液体ブロック自身の除外と、水を含む固体ブロックの採掘を分離する。採掘後に残る流体状態も仕様として扱う。MOD 独自流体ブロックについては別途対応範囲を決める。
- 回帰テスト: 水源は採掘対象外、水没した階段・ハーフブロックは採掘され、固体のドロップと水の残留状態が正しいこと。

## 不具合として断定せず、仕様を確定させたい差分

| 論点 | 現状・旧実装との差 | 決めること |
|---|---|---|
| 燃料の燃焼時間単位 | 旧鉄エンジンは `getTotalBurningTime() / BUCKET_VOLUME` を 1mB あたりに使う。現行は 1mB 消費で全時間を使い、組込み石油登録は `(1, 1)`。 | 時間が 1mB / 1バケツのどちらか、出力が FE/t かを API・登録値・実装で統一する。現状に対して単純に「燃費1000倍」とは断定しない。 |
| 流体パイプの構造 | 旧実装の中央＋6方向セクションから単一タンクへ簡略化している。 | 容量・遅延・分岐・逆流・tick 順序依存をどこまで再現するか。簡略化だけを不具合とは扱わない。 |
| Quarry のフレーム前処理 | 非空ブロックに当たるとフレーム候補を捨てて進む。 | 旧実装の整地・フレーム構築段階を再現するか。障害物と保護領域に対する停止条件を決める。 |
| エネルギー Capability の null 面 | null 面は輸送モジュールとは別の `energyStorage` を公開する。 | 無方向アクセスを許可するか、許可するなら輸送バッファとの整合をどう取るか。 |

## 修正の進め方

1. **永続化と操作検証**: 指摘 1・2 を先に修正。設計図形式をバージョン管理し、不正パケットが状態を変更しないことを保証する。
2. **搬送の共通基盤**: 指摘 3〜5 を修正。方向別 Capability、共通接続条件、受入れ計算を整え、流体量・電力量が保存されるかをテストする。意図した抵抗損失は区別する。
3. **エンジンの状態遷移**: 指摘 6〜8 をまとめて修正。燃焼・冷却・信号・保存復元の責務を整理し、燃料単位もこの段階で確定する。
4. **建築・採掘**: 指摘 9・10 を修正してから、チャンク境界、負の Y、破壊イベントの拒否、途中アンロードを含む GameTest を追加する。
5. **旧実装との互換表**: 材質別の搬送量、エンジン別の燃費・発熱、機械別の処理順を表にし、「再現する仕様」と「意図的に変える仕様」を明示する。TODO の数や README の実装済み表示だけで完成判定しない。

先にコンパイルを通すだけの置換を増やすより、上記のデータ形式・方向・状態遷移を固定し、それぞれの発生条件を GameTest または小さな回帰テストで再現できる状態にするのがよい。
