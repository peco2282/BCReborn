# BCReborn — 実装ロードマップ (Original BuildCraft 準拠)

> 最終更新: 2026-11-17
> 対象バージョン: Minecraft 1.20.1 / Forge 47.x

---

## 🎯 開発方針: 「オリジナルの BuildCraft へ」
BuildCraft 1.7.10 および 7.99 (1.12.2) の体験を現代の Minecraft 1.20.1 で再現することを最優先します。
単なる機能追加ではなく、**「パイプを流れるアイテムのワクワク感」「エンジンのピストン運動」「クァーリーが大地を削る高揚感」**を重視します。

---

## 凡例
| 記号 | 意味 |
|----|-------------|
| ✅ | 実装済み・動作確認済み |
| 🔧 | 部分実装・作業中    |
| ⬜ | 未着手 |

---

## 🛠 最優先タスク (Immediate Priority)
これまでの最優先タスク（Transport UI・建築マシン・エンジン）はコード上の実装を終え、GameTest で検証済み。次の優先度は以下。

1.  **実機での確認**: 画面を伴う部分は GameTest では検証できない。下の「実機確認チェックリスト」を `runClient` で確認し、問題がなければ 🔧 を ✅ にする。
2.  **入手手段のないアイテム**: 2026-11-18 にすべて解消。流体・動力パイプ（逆変換も）、ゲート本体、ファサード、Pipe Sealant、List のレシピ（RecipeGameTests）と、動力パイプ Sandstone / Emerald（PowerPipeGameTests）。Debugger は原作にもレシピがない（6 章）。
3.  **スタブのままの機能**: 7-1 章の残り（Pump の液面処理）。ストライプパイプは 2026-11-18 に原作の方式へ。ゲート拡張 Timer / Fader / Light Sensor と Stamping Table は 2026-11-17 に実装。Zone Planner・Blueprint Library・Requester・List の GUI まわりも同日に実装（実機確認は 8 章）。

---

## 1. パイプシステム (Transport)
アイテム、液体、エネルギーの物流を司る BC の心臓部。

### 1-1. アイテムパイプ (Item Pipes)
| 項目 | 状態 | 備考 |
|----------------------|----|-------------------------------------------|
| 基本輸送ロジック | ✅ | TravelingItem, MovementHelper, SpeedHelper |
| Wood / Emerald | 🔧 | PipeItemsWood / PipeItemsEmerald 準拠の抽出: エンジンがパイプのバッテリー（2560 RF、80 RF/t）を充電し、8 tick 以降はスタック全部を払えるとき、16 tick 以降は 10 RF あれば引き出す。1 個 10 RF で払える分だけ 1 スタックから取り、引き出すたびにバッテリーは空になる。Emerald のホワイト / ブラックリストは同じ方式、ラウンドロビンは現在のフィルターを 1 個ずつ取って次へ。Emerald も設置時に引き出す面を選ぶ。Emzuli も同じ周期で動く。WoodExtractionGameTests で検証。向き・モードの切替操作は原作と異なる（7-2） |
| Stone / Cobblestone | ✅ | 基本輸送・摩擦の違い |
| Iron | ✅ | PipeLogicIron 準拠: レンチ（アーム/中央）・レッドストーンで出力切替、全方向接続・出力1方向。オリジナル同様 GUI なし。出力アームは標準、それ以外は solid テクスチャで描画（EXTRACTION_SIDE に見た目としてミラー）。ゲートの「出力方向」アクションにも対応 |
| Golden | ✅ | 加速ロジック |
| Diamond | 🔧 | ルーティング・フィルター GUI（54 ゴーストスロット、保存対応）実装済み。GameTest で検証済み、実機での GUI 表示確認待ち |
| Obsidian | ✅ | 地面のアイテムを吸引するロジック実装済み |
| Void | ✅ | アイテムを消去するロジック実装済み |
| Lapis | ✅ | PipeItemsLapis 準拠: 中央に来たアイテムをパイプの色で塗る（色はスタックの NBT ではなく移動中のアイテムに持たせ、次のパイプへ引き継ぐ）。レンチで次の色・スニーク＋レンチで前の色・染料でその色、ゲートの色アクション。色ごとのテクスチャ（PIPE_COLOR のモデルデータで切替、実機確認待ち） |
| Daizuli | 🔧 | PipeItemsDaizuli 準拠: Iron と同じ出力1方向（レンチで切替、レッドストーンでは切り替わらない）。パイプと同じ色のアイテムは出力側へ、それ以外は出力側以外へ。スニーク＋レンチ・染料で色を変更、ゲートの色・方向アクション。GameTest 検証済み。出力面以外は solid 表示、色ごとのテクスチャ（Lapis と同じ仕組み、実機確認待ち） |
| Emzuli | 🔧 | PipeItemsEmzuli 準拠: Wood 系の抽出パイプ。4つのフィルタースロットがゲートの抽出プリセット（赤・青・緑・黄）に対応し、有効なプリセットのスロットに一致するアイテムだけを抽出、スロットごとの色で塗る（Daizuli で振り分け）。GUI でフィルターと塗る色を設定（左/右/中クリック）、保存と同期。プリセットが無いと抽出しない（原作通り）。GameTest 検証済み、GUI の実機確認待ち |
| 渋滞・逆流システム | ✅ | PipeTransportItems 準拠: 受け取れる面（アイテムパイプか、少しでも入るインベントリ）だけを行き先にし、行き先がなければ中央でアイテムを落とす（折り返すのは原作通り Iron・Daizuli・Clay のみ）。満杯で戻されたアイテムはそのパイプで行き先を選び直す。32 スタック以上で同じアイテムをまとめ、64 スタックか 1024 個を超えるとパイプが破裂（爆発の演出のみ、パイプと中身をドロップ）。GameTest 検証済み |

### 1-2. 液体・エネルギーパイプ (Fluid & Energy Pipes)
| 項目 | 状態 | 備考 |
|----------------------|----|-------------------------------------------|
| 液体パイプ基盤 | ✅ | FluidTransportModule, 各種 Behaviour |
| エネルギーパイプ基盤 | ✅ | EnergyTransportModule (Forge Energy 互換) |
| パイプ内レンダリング | 🔧 | PipeTransportFluidsRenderer 準拠: 水平部分は下から水位が上がり、垂直部分は量に応じて太くなる柱。量はクライアントで補間して滑らかに増減。流れは液体の flowing テクスチャを各アームの進行方向（入力側は中心へ、出力側は外へ）に沿って貼る。タンクは同じ tick で満たされて空になることが多いので、その tick に通過した量も同期して多い方を表示。バイオームの水色・溶岩の発光に対応。同期するデータは GameTest（PlugLensGameTests）で検証済み、見た目は実機確認待ち |

### 1-3. 拡張機能 (Gates & Logistics)
| 項目 | 状態 | 備考 |
|----------------------|----|-------------------------------------------|
| パイプワイヤー (4色) | 🔧 | 原作準拠: 4色のワイヤーアイテム（Assembly Table で作成）、右クリックで設置・スニークで撤去、パイプ破壊でドロップ、ゲートからの信号は強度255から1本ごとに-1、ワイヤー系トリガー/アクション。GameTest 検証済み。ワイヤーの描画（色ごとの角、接続方向へ延伸、信号ありで発光）も実装、実機確認待ち |
| ゲート (Gate) | 🔧 | パラメータ（アイテム・方向・ワイヤー・地図など）の保存と読み込みを修正（以前は読み込みで失敗するか、同じ種類のパラメータが 1 つのインスタンスを共有していた）。条件評価・アクション解決、提供元（Default/Pipe Trigger・Action Provider）、パイプへのアクション通知（Iron の方向）、レッドストーン出力、機械制御（Off/On/Loop）、Valve（面ごとの入出力、保存）、Lapis の色、Iron 電力パイプの制限（M2〜M128）を実装。GameTest 検証済み。Emzuli（抽出プリセット）・Daizuli（色/方向）のアクションも実装。GUI の実機確認は未対応。拡張は Pulsar・Timer・Fader・Light Sensor が動く（7-1）。ゲート本体は Assembly Table でチップセット＋ワイヤー（素材の `maxWireColor` 本）から AND ゲートを作る（エネルギーはチップセットと同じ値で、原作の値とは未照合） |
| 外装 (Facade) | 🔧 | FacadeItem と基本的な描画ロジック実装済み。位相ファサード（FacadePluggable 準拠）: 全状態をパイプ側に持ち、ワイヤーの信号で表示する状態を切り替える（有効なワイヤーの状態、なければ既定の状態）。透明状態は何も描かない。穴あき／穴なしの切替で接続も開閉。GameTest 検証済み、切替の見た目は実機確認待ち。基本レシピ: Assembly Table で構造パイプ 3 個＋ブロック → 6 個（8000 RF、ファサードにできるブロックごとにコードで生成） |
| 構造パイプ | ✅ | PipeStructureCobblestone 準拠: 何も運ばず、構造パイプ同士だけ接続。ワイヤーは隣のどのパイプとも（接続していなくても）つながる。プラガブルを付けられる。レシピ: 丸石・砂利・丸石 → 8 個。GameTest 検証済み |
| プラグ (Plug/Lens) | 🔧 | PlugPluggable / LensPluggable / LensFilterHandler 準拠。プラグ: 付けた面の接続を閉じる（隣のパイプ・インベントリ・ワイヤーとも）。レンズ: 通過するアイテムをその色で塗る（入るとき・出るとき）。フィルター: その色のアイテムだけ通し、一致するアイテムはフィルター側を優先。隣のパイプの向かい合う面のフィルター、レンズ＋フィルターの組み合わせも原作通り。16 色＋透明（透明レンズは色を消す、透明フィルターは無色だけ通す）。Assembly Table のレシピ（10000 RF）。右クリックで設置、スニーク＋素手で回収。GameTest（PlugLensGameTests）検証済み、描画とアイテムの見た目は実機確認待ち |

---

## 2. エネルギーシステム (Energy / MJ)
※ 内部的には Forge Energy (FE) を使用しつつ、MJ のような挙動 (蓄積・爆発) を再現。

### 2-1. エンジン (Engines)
| 項目 | 状態 | 備考 |
|----------------------|----|-------------------------------------------|
| Wood Engine | ✅ | 低出力、ピストン連動 |
| Stone Engine | ✅ | 燃料燃焼、冷却ロジック |
| Iron Engine | ✅ | 液体燃料（石油・精製燃料）、冷却液（水、氷・氷塊）。冷却液は未登録だったため追加 |
| Creative Engine | ✅ | テスト用無限出力 |
| ピストンアニメーション | 🔧 | TileEngineBase 準拠: クライアントが同期された段階・稼働フラグから自前でアニメーション（サーバーが位置を上書きしない）、速度はクライアント 0.02〜0.16／サーバーは熱に比例。実機確認待ち |
| エンジン GUI | 🔧 | Stone: テクスチャの炎ゲージ、Iron: 燃料/冷却液タンク（液体テクスチャ＋目盛り、ツールチップ）、両方にエネルギー台帳（出力・蓄積・熱）。メニュー同期・Shift クリックを修正。実機確認待ち |

---

## 3. 採掘と建築 (Builders)
BC を象徴する大型マシン。

### 3-1. ランドマーク (Landmarks)
| 項目 | 状態 | 備考 |
|----------------------|----|-------------------------------------------|
| ランドマーク配置 | 🔧 | TileMarker 準拠: 右クリックで軸ごとに連結（同ブロックのみ・範囲64）、赤レーザー枠、レッドストーンで未連結軸に青い信号ビーム。GameTest 検証済み、実機での描画確認待ち |
| 範囲計算ロジック | ✅ | Utils.getNearbyAreaProvider（isValidFromLocation）で Quarry/Filler/Architect が枠に接して受け取り、ランドマークを回収 |

### 3-2. マシン (Machines)
| 項目 | 状態 | 備考 |
|----------------------|----|-------------------------------------------|
| Quarry (クァーリー) | 🔧 | TileQuarry 準拠: フレーム建設（障害物は採掘、岩盤・機械は待機）→ 列単位の掘削、Off/On/Loop、雨天減速、ヘッド周辺のアイテム回収、フレーム破損時の再建設、レンチで再起動、破壊時フレーム撤去。チャンクローディング（quarryDoChunkLoading: Quarry のチャンクと範囲の全チャンクを強制読み込み、破壊で解除、ワールド読み込み時に存在しない Quarry の分を破棄、25 チャンク相当を超える範囲は既定範囲に戻す）。GameTest 検証済み |
| Mining Well | 🔧 | TileMiningWell 準拠: 真下を掘り進めて Plain Pipe を伸ばす、掘ったものは隣のインベントリ → パイプ → ドロップの順、岩盤などで停止、Off/On、撤去するとパイプも消える。Plain Pipe は何もドロップしない。GameTest（MiningWellGameTests）検証済み、LED 表示は実機確認待ち |
| Filler (フィラー) | 🔧 | TileFiller 準拠: 全9パターン（パラメータ付き）、パターンを ID で保存、done の永続化、excavate、Off/On/Loop、500RF で稼働。テンプレート建設の土台（AbstractBuilder のエネルギー/建材アイテム、SchematicMask 設置、soft 判定、負の Y 座標）を修正し実際に充填できるようになった。GUI: パターン切替、パラメータ欄（クリックで切替、使わない欄は暗転、ツールチップ）、excavate ボタン、パターン名表示（GuiFiller 準拠）。建材アイテムの飛行: 打ち出し時に近くのクライアントへ送り、後から読み込んだクライアントは飛行中の分を要求（launchItem / uploadBuildersInAction 準拠）、クライアントで弧を描いて描画。GameTest 検証済み、GUI と飛行の描画は実機確認待ち |
| Builder (ビルダー) | 🔧 | TileBuilder 準拠: スロット 0 の設計図 / テンプレートを本体と子設計図に分けて順に建築（RecursiveBlueprintBuilder）、範囲の枠表示、完成したら設計図を空きスロットへ移す（なければドロップ）、Off/On、クリエイティブではエネルギー不要、必要素材の一覧を GUI へ送る、建設途中の状態を保存。設計図は必要な素材をそのまま、テンプレートは任意のブロックを使う。GameTest 検証済み、GUI の実機確認待ち。経路マーカー（IPathProvider）: Builder の隣の経路マーカーを回収し、経路に沿って設計図を重ならない間隔で繰り返し建築（PathIterator 準拠、途中経過も保存）。GameTest 検証済み。経路のレーザー表示は未実装 |
| Architect Table | 🔧 | 範囲内を設計図・テンプレートとして記録（原作 TileArchitect / RecursiveBlueprintReader 準拠）。白紙の設計図を入れると出力スロットに記録済みの設計図が出る。Construction Marker でのリンクによる子設計図にも対応。GameTest（ArchitectGameTests）で Builder での再建築まで確認済み。GUI（名前入力・読み取り設定）は実機未確認 |

---

## 4. シリコンとロボット (Silicon & Robotics)
高度な自動化、エンドゲームコンテンツ。

| 項目 | 状態 | 備考 |
|----------------------|----|-------------------------------------------|
| Assembly Table | 🔧 | TileAssemblyTable 準拠: GUI でレシピを計画（クリックで選択/解除）し、計画したものだけをレーザーエネルギーで順番に製造。計画は保存・同期。GameTest 検証済み、GUI の実機確認待ち |
| Integration Table | 🔧 | 原作の動的レシピ（IIntegrationRecipe）を移植: ゲートへの拡張追加（Pulsar / Timer / Fader / Light Sensor）と Redstone Chipset によるロジック切替（GateExpansionRecipe）、位相ファサード（AdvancedFacadeRecipe、プラグで透明状態を追加）、ロボットへのボード取り付け（RobotIntegrationRecipe）。拡張スロットは有効なレシピに合うものだけ受け付ける。データパックのレシピも併用可。GameTest（IntegrationTableGameTests）検証済み。GUI に出力プレビュー（触れないスロット、エネルギーが溜まる前から表示）と、使えない拡張スロットの×表示を追加（GameTest でプレビューを確認、GUI は実機確認待ち） |
| Charging Table | 🔧 | TileChargingTable 準拠: レーザーのエネルギーをスロットのアイテムへ充電、充電するものがなければ毎 tick 10 ずつ減る、エネルギーを持てるアイテムだけ受け付ける。ロボットのアイテムは Forge のエネルギー Capability を公開（原作 IEnergyContainerItem 相当、ボードなしは充電不可）。GameTest（ChargingTableGameTests）検証済み、GUI は実機確認待ち |
| ロボット (Robots) | 🔧 | 基本動作を原作（EntityRobot / RobotRegistry / AIRobotMain）と照合: ステーションへの配置とリンク、ボードの実行、保存と読み込み、エネルギー切れでの停止、撤去時のステーション解放。登録簿の解放処理（メインステーションを保持、死亡時のみ全解放、アンロード時は保持）を原作に合わせた。ステーション（DockingStationPipe）を移植: ゲートのアクションの参照、パイプへのアイテム投入、木パイプ経由の搬出元インベントリ、液体の入出力、電力パイプの判定、アイテム要求。荷積み・荷下ろし（AIRobotLoad / AIRobotUnload）は「アイテムを提供」「アイテムを受け取る」アクションに従う。Carrier がステーション間でアイテムを運ぶことを GameTest（RobotGameTests）で確認。充電: 電力パイプに付けたステーションが、ドッキング中のロボットへ給電する（RobotStationPluggable.receiveEnergy 準拠）。安全量を下回ったロボットは電力ステーションに留まって充電する。GameTest で確認。ボード: Fluid Carrier（タンクの中身を見て積む・下ろす、GameTest で確認）、Pump（タンクの判定とフィルタ）、Planter（種の判定と植えられる場所の判定）、Delivery（アイテム要求を探して届ける）を原作どおりに実装。ロボットのタンク（1 個、バケツ 4 杯分）を正しく公開。ブロックを探すボードは空実装だった探索（AIRobotSearchBlock）を原作通りに実装して動くようになった: Miner（ステーションからつるはしを取得して採掘、道具の消耗、ドロップ）、Lumberjack、Shovelman、Leave Cutter、Harvester（AIRobotHarvest）、Farmer（くわで耕す。以前はブロックを壊していた）、Planter、Pump、Picker（拾ったアイテムを自分のインベントリへ）、Delivery（Requester の要求を届ける）。作業範囲（ActionRobotWorkInArea＋地図アイテム）の内側だけで働く。以上を GameTest（RobotBoardGameTests）で実行して確認。Butcher（動物を倒す）、Knight（敵だけを倒す）、Stripes（持っているブロックを設置）、Builder（建設マーカーの設計図を、ステーションから素材を取って建築）も実行して確認。Bomber は TNT の投下部分だけ確認（飛行から爆発までは他のテストを壊すため未実行）。専用サーバーでロボットが攻撃すると例外になる不具合、Builder ボードがサバイバルで素材を取りに行かない不具合（クリエイティブ判定が逆）を修正。剣で攻撃したときの耐久消費が攻撃者に null（`lastHurtByPlayer`）を渡して NullPointerException になっていたのを修正（Butcher / Knight のテストは攻撃自体は通るので失敗していなかった）。`/kill` などで死んだロボットが荷物・道具・装備品を落とさず消していたのを修正（`dropEquipment`）。経路探索の途中で保存されたロボットが読み込み時に NullPointerException になるのを修正（`AIRobotGotoBlock`） |

---

## 5. レンダリング・演出 (Visuals)
| 項目 | 状態 | 備考 |
|----------------------|----|-------------------------------------------|
| パイプ動的モデル | 🔧 | multipart blockstate（中央 + 6 方向アーム）で接続に応じて変化。実装済み、実機確認待ち |
| アイテム輸送描画 | 🔧 | ItemPipeRenderer: progress 補間、0.7 倍、最大 10 個（オリジナル準拠）。実装済み、実機確認待ち。塗られたアイテムは色の箱（item_box）に入れて描画（実機確認待ち） |
| レーザー描画 | 🔧 | ランドマークの枠・信号ビーム、レーザーの光線は描画処理あり。実機確認待ち |

---

## 6. 未実装 (原作にあって存在しないもの)
2026-11-17 にソースを再調査した結果。原作のソースは手元になく、BuildCraft 7.1 系の記憶との比較（数値や細部の一致までは未照合）。

### 6-1. Transport
| 項目 | 状態 | 備考 |
|----------------------|----|-------------------------------------------|
| 動力パイプ Sandstone / Emerald | ✅ | 原作 PipePowerSandstone / PipePowerEmerald: 容量は丸石の 4 倍（320 RF/t）と 32 倍（2560 RF/t）。Sandstone はパイプとだけ接続（機械・エンジンの隣を通しても触れない）、Emerald は木の動力パイプと同じくエンジンから吸い出す（`WoodenEnergyPipeBehaviour` を共用、上限は素材の転送量）。PowerPipeGameTests で検証、見た目は実機確認待ち |
| 流体・動力パイプのレシピ | ✅ | アイテムパイプ＋Pipe Sealant（緑の染料から作る、ID `pipe_waterproof`）→ 液体パイプ、アイテムパイプ＋レッドストーン → 動力パイプ（不定形）。原作どおり、液体・動力パイプ単体でアイテムパイプに戻せる。RecipeGameTests で検証 |
| ゲート本体のレシピ | ✅ | Assembly Table: チップセット＋ワイヤー（赤→青→緑→黄の順に `maxWireColor` 本）→ AND ゲート、10 万〜120 万 RF。エネルギーの値は原作と未照合。RecipeGameTests で検証 |
| ファサードの基本レシピ | ✅ | 原作 ItemFacade.addFacade: Assembly Table で構造パイプ 3 個＋ブロック → 6 個、8000 RF。`FacadeAssemblyRecipes` がリロードのたびにファサードにできるブロックごとに作る（`AssemblyRecipeManager.addDynamicSource`、同じ ID はデータパック優先）。RecipeGameTests で検証 |
| Gate Copier | ⬜ | 設定項目（`SiliconConfig.isGateCopier`）だけ |
| Filtered Buffer | ⬜ | メニューと画面だけあり、ブロックがない |
| Power Adapter | ⬜ | 痕跡なし |

### 6-2. Core / Energy / その他
| 項目 | 状態 | 備考 |
|----------------------|----|-------------------------------------------|
| List / Debugger のレシピ | ✅ | 原作 CoreSiliconRecipes: Assembly Table で緑の染料＋レッドストーン＋紙 8 枚 → List、20000 RF。Debugger は原作にもレシピがなく、クリエイティブ専用のまま。RecipeGameTests で検証 |
| 水の湧き水の生成 | ✅ | 原作 SpringPopulate 準拠のワールド生成 Feature（`WaterSpringFeature`、約 40 チャンクに 1 つ）: 最下層 5 段の岩盤を探して湧き水ブロックを置き、その上の岩を最初の空気まで水にする。オーバーワールドのみ。設定 `generateWaterSprings` で無効化（湧き水ブロックの給水も止まる）。WorldgenGameTests で検証 |
| 油田の生成 | 🔧 | 原作 OilPopulate.generateOil を Feature 化（`OilWellFeature`、チャンクごとに 1 回）: 大型 0.04%・中型 0.1%（砂地・タイガは 3 倍）、地表の油溜まり 2%（砂地・タイガのみ）、地下 20〜29 の球状鉱床と噴出柱、大型の 1/4 は岩盤に油の湧き水。チャンクの読み込みイベントで書き換える方式とバニラ湖での油の池は廃止。Feature は周囲 1 チャンクまでしか変更できないため、大型油田の周りの油溜まりは半径を縮める（原作は 25〜44）。Oil Desert / Oil Ocean バイオームと「greatly increased」バイオームの設定はなし。WorldgenGameTests で確率と登録を検証、生成は 576 チャンクの試験生成で確認 |
| コンパレーター出力 | ⬜ | どのブロックにもない（タンクなど） |
| Tablet | ⬜ | API（`api/tablet`）だけ |
| 実績 (Advancement) | ⬜ | レシピ解放用のものだけ |
| Breaker ロボット | ⬜ | 原作にあったかは要確認。ボードは 17 種 |

## 7. スタブ・原作と異なる実装 (未対応)
10 章の「意図的な差異」とは別で、原作に合わせる余地があるもの。

### 7-1. クラスはあるが中身がスタブ
| 項目 | 状態 | 備考 |
|----------------------|----|-------------------------------------------|
| ゲート拡張 Timer / Fader / Light Sensor | ✅ | 原作 GateExpansionTimer / RedstoneFader / LightSensor 準拠: Timer は 5 / 10 / 15 秒ごとに 5 tick 有効になるトリガー 3 種、Fader は「入力がちょうど N」のトリガーと「N を出力」のアクション（N = 1〜15。以前は 0〜14 で作られていた）、Light Sensor はゲート前方のブロック光で明 / 暗のトリガー。GameTest（GateExpansionGameTests）で確認 |
| Stamping Table | ✅ | 原作 TileStampingTable 準拠: 入力 1 個を 1 個だけのクラフトレシピに通す（原木 → 板材など）、Package は中の 3×3 をそのままレシピに、レシピがなければそのまま出力へ。容器の残り（空き瓶など）はスロット 2〜4、あふれたら隣のインベントリか上に落とす。1 個 400 RF、Package は中のスタック数 × 400 RF。メニューにエネルギーの同期を追加（進捗バーが出なかった）。GameTest で確認 |
| Zone Planner | 🔧 | TileZonePlan / ContainerZonePlan / GuiZonePlan 準拠で実装: 地図データ（MapManager）をサーバー起動時に開始して `<world>/buildcraft/zonemap` に保存、地図画像の計算、ブロック前面のプレビュー、地図アイテムへの範囲書き込み（KIND_ZONE、名前付き）と取り込み、画面の操作（色の選択、左ドラッグで塗る・Shift で消す、右ドラッグで移動、ホイールで拡大縮小、名前欄）。以前は GUI が開かず（`createMenu` が null）、MapManager も起動していなかった。GameTest（MachineGuiGameTests）で書き込み・取り込み・地図計算を確認、画面は実機確認待ち |
| Blueprint Library | 🔧 | TileBlueprintLibrary 準拠で実装: スロット 0 の設計図・テンプレート・本をプレイヤーのライブラリ（ゲームフォルダの `blueprints`）へ保存、選んだ項目をスロット 2 のアイテムへ書き込み（分割アップロード）、一覧の選択・削除はクライアント側。進捗はサーバーで進める（以前はクライアントだけで進み、何も起きなかった）。本の型ハンドラーの名前と読み込みの不具合も修正。GameTest で保存・読み込み・本の往復を確認、画面とクライアントとのやり取りは実機確認待ち |
| Requester | 🔧 | GuiRequester 準拠: 左の 20 枠をクリックすると手に持ったアイテム（個数ごと）が要求になる、空の手で解除。以前は要求を設定する手段がなかった。GameTest で確認、画面は実機確認待ち |
| List | 🔧 | 1.20 ではアイテムが 1 種類のため、旧形式のデータを持つリストだけ旧 GUI、それ以外は新 GUI を開く（以前は常に旧 GUI）。名前が空のときに表示名が消える不具合を修正 |
| ストライプパイプ | ✅ | 原作 PipeItemsStripes 準拠: 開いた端から落ちるアイテムを登録済みのストライプハンドラ（原作の優先度順）に通し、ハンドラが使わなければそのまま落とす（`ItemPipeBehaviour.onDropItem`）。アイテムパイプが届くとパイプが 1 ブロック伸び（`PipeExtensionListener`: ストライプパイプを中身ごと前へ移し、元の位置に届いたパイプを置く）、Void パイプなら 1 ブロック縮んで外したパイプを送り返す。他の種類のパイプは前に置く。動力（16000 RF、640 RF/t）があれば前のブロックを 10 RF で壊し、ドロップをパイプへ戻す（岩盤・液体は壊さない、トロッコはハンドラが壊す）。ゲートの「パイプの向き」で作業面を選べる。StripesGameTests で検証 |
| Pump | 🔧 | 液面に着いたときの処理に `TODO: Impl Fill fluid!` が残る（汲み上げは実装済み） |

### 7-2. 原作と異なる挙動
- **木パイプの向き切替**: レンチなしの右クリックでも切り替わる。
- **Emerald パイプのモード切替**: GUI のボタンではなくレンチ。画面にボタンがない。
- **Bomber ロボット**: TNT の投下が簡略実装。

---

## 🚀 ROADMAP: 今後のリリース計画

### Phase 1: 輸送と UI の完成 (Transport & UI)
*   [x] Diamond Pipe フィルター GUI / Iron Pipe 出力切替の実装（設定の保存と同期）
*   [ ] Diamond Pipe GUI の実機確認
*   [x] Iron Pipe 出力方向の描画（出力以外の面を solid 表示）
*   [x] パイプ内アイテムレンダリングの実装 (ワクワク感の向上)
*   [x] パイプの動的接続モデル (見た目の改善)
*   [ ] Phase 1 描画系の実機確認（アイテム移動・接続形状・Iron 出力面）

### Phase 2: 採掘と建築の始動 (Heavy Machines & Logic)
*   [x] ランドマーク (Landmark) とレーザー表示の実装（実機確認待ち）
*   [x] クァーリー (Quarry) の動作開始 (フレーム設置、掘削、アイテム排出、チャンクローディング)
*   [x] フィラー (Filler) の基本パターン動作とパラメータ / excavate の GUI（実機確認待ち）

### Phase 3: ゲートと論理制御 (Logistics & Silicon)
*   [x] ゲート (Gate) の基本システム (Trigger/Action)：解決・パイプへの通知・レッドストーン出力・機械制御・ワイヤー・Valve・Lapis 色・電力制限
*   [ ] ゲート GUI の実機確認
*   [x] Emzuli / Daizuli の原作準拠化とゲートアクション（抽出プリセット・色・方向）
*   [x] 基本的なチップ製造 (Assembly Table)（GUI の実機確認待ち）

### Phase 4: 高度な自動化と仕上げ (Advanced Features & Polish)
*   [x] ロボット (Robotics) システム（全ボードを GameTest で確認。Bomber の飛行〜爆発のみ未実行）
*   [x] 外装 (Facade) の位相ファサード（ワイヤーで切替、透明状態）
*   [ ] エンジンの爆発・オーバーヒートのバランス調整
*   [x] プラグ / レンズ (Plug / Lens)（実機確認待ち）
*   [x] 液体パイプ内の液体描画（実機確認待ち）

### Phase 5: 原作との差分の解消 (6 章・7 章)
*   [x] 流体・動力パイプ、ゲート、ファサードのレシピ（Pipe Sealant を含む）
*   [x] 動力パイプ Sandstone / Emerald、List のレシピ、液体・動力パイプからアイテムパイプへの逆変換
*   [x] ゲート拡張 Timer / Fader / Light Sensor の接続
*   [x] Stamping Table
*   [x] Zone Planner の地図、Blueprint Library の一覧と同期、Requester の要求設定（実機確認待ち）
*   [x] 木パイプの搬出を原作の方式へ（2026-11-18、WoodExtractionGameTests）
*   [x] ストライプパイプを原作の方式へ（ハンドラ、パイプの延長と縮小、動力での破壊）
*   [x] 湧き水・油田の生成（Oil Desert / Oil Ocean バイオームは除く）
*   [ ] Gate Copier / Filtered Buffer / Power Adapter

---

## 8. 実機確認チェックリスト
GameTest（`runGameTestServer`）はサーバー側の挙動だけを検証する。以下は `runClient` での目視確認が必要。

| 対象 | 確認すること |
|---|---|
| Diamond Pipe GUI | 6行×9列のゴーストスロット、置いたフィルターが再ログイン後も残る、色覚モードのテクスチャ |
| Iron Pipe | 出力アームだけ通常テクスチャ、レンチ（アーム／中央）とレッドストーンで向きが変わる |
| パイプ描画 | 接続形状、パイプ内を移動するアイテム |
| パイプワイヤー | 色ごとに別の角を通る、接続方向へ伸びる、信号ありで発光 |
| ゲート GUI | 条件・アクションの一覧（パイプ内容、ワイヤー、Valve、機械制御、レッドストーン）と設定 |
| Emzuli Pipe GUI | 4つのフィルタースロット、塗る色のボタン（左クリックで次、右で前、中で解除、ツールチップ）、再ログイン後も残る |
| ランドマーク | 赤い枠、レッドストーンで未連結軸の青いビーム、後から入ったプレイヤーにも見える |
| Quarry | フレーム建設、ヘッドとアームの動き |
| Filler | 各パターンで実際に充填・整地される。GUI のパラメータ欄のアイコンとクリック、excavate ボタン、パターン名 |
| 建材アイテム | Filler / Builder から置き場所へ弧を描いて飛ぶ、途中で入ったプレイヤーにも見える |
| Builder | 範囲の枠、必要素材の一覧、設計図を入れると建築が始まり完成で設計図が戻る |
| Assembly Table | 右の 3×4 レシピ欄、クリックで計画・解除、計画中/製造中/材料不足の枠、エネルギーバー |
| プラグ / レンズ | プラグ: 開口部をふさぐ板、付けた面のアームが消える。レンズ / フィルター: アームの先の枠と色付きガラス、アイテムの色付きアイコン（透明は白）。設置は狙ったアーム（なければクリックした面） |
| 色付きパイプ | Lapis / Daizuli が色ごとのテクスチャになる、レンチ・染料で色を変えるとすぐ変わる、塗られたアイテムが色の箱に入って流れる |
| 位相ファサード | ワイヤーの信号で表示ブロックが切り替わる、透明状態で消える |
| Integration Table | 出力プレビュー（101,36 のスロット）、使えない拡張スロットの×表示 |
| Zone Planner | 地図が表示される、色を選んで左ドラッグで塗る・Shift＋ドラッグで消す、右ドラッグで移動・ホイールで拡大縮小、名前欄、地図アイテムの書き込みと進捗バー、ブロック前面のプレビューが向きに合う |
| Blueprint Library | 一覧に `blueprints` フォルダの中身が出る、設計図を入れると一覧に増える、選んでから白紙を入れると書き込まれる、削除ボタン |
| Requester | 左の要求枠に持っているアイテムと個数が表示される、ツールチップ |
| 液体パイプ | 水位が滑らかに上下する、垂直は柱になる、流れのテクスチャが進行方向へ動く（逆向きに見えないか）、面の欠け・重なりがない、溶岩が暗所で光る |
| エンジン | ピストンが飛ばずに動く、Stone の炎ゲージ、Iron の燃料/冷却液タンク、右側のエネルギー台帳 |

---

## 8-2. ロボット・電力の回帰テスト (既存の範囲と不足)
2026-11-17 時点。外部レビュー（GitHub 上の `RobotGameTests` だけを見たもの）では「Pump / Planter / Delivery の専用テストなし」とされたが、未プッシュの `RobotBoardGameTests` に `pumpTakesFluidSource` / `planterPlantsSeed` / `deliveryBringsRequestedItems` がある。CI（`ci.yml`）は `runGameTestServer` を実行するので、合否はプッシュ後の CI で記録する。2026-11-17 にローカルで `runGameTestServer` を実行し、追加した 3 本を含む 150 本すべて成功。その後の 3 本を加えて 153 本、`RobotEdgeCaseGameTests` の 13 本を加えて 167 本すべて成功。

### 既存テスト
| テスト | 確認していること | 確認していないこと |
|---|---|---|
| `RobotGameTests.robotLinksToStationAndSurvivesSaving` | 生成、ステーションとの相互リンク、登録、ボード、NBT から作ったコピーの ID・電力・ボード、`discard()` でのステーション解放 | コピーをワールドに戻して動作が再開すること、チャンクの再読み込み、死亡時の解放、配送中の予約 |
| `RobotGameTests.robotWithoutEnergyShutsDown` | 電力 0 で生存したまま Shutdown | 停止中の荷物の保持、再充電後の復帰 |
| `RobotGameTests.carrierMovesItemsBetweenStations` | 提供アクションなしでは取れない、鉄 5 個を全部運び搬出元が空になる | 満載・部分受け入れ、搬送先満杯、NORTH 以外の面 |
| `RobotGameTests.powerPipeStationChargesRobot` | 電力パイプのステーションで充電され、充電中はドッキングを保つ | 満充電で止まること、有限の電源、通常の機械との分配 |
| `RobotGameTests.fluidCarrierMovesFluidBetweenStations` | タンク 1 個・4000 mB・初期は空、木パイプ側から積み、ロボット 1 杯分（4000 mB）以上が届く。毎 tick「元タンク＋元パイプ＋ロボット＋先パイプ＋先タンク = 8000」とロボットの 4000 mB 上限を確認 | 搬送先満杯、別の液体、フィルタ |
| `RobotGameTests.powerPipeStationSharesNetworkWithMachine` | 同じ電力パイプにステーションと Refinery を置き、両方に電力が届く、どちらも容量を超えない | 有限の電源での分け方、PowerMode での制限 |
| `RobotBoardGameTests.deadDeliveryRobotReleasesItsReservations` | 要求を予約した Delivery ロボットを `kill()` すると、死亡演出（20 tick）のあとに除去された時点で、要求・要求元ステーション・メインステーションが解放され、2 台目が同じ要求を 3 個だけ届ける。死亡演出中は除去前なので予約は残り、AI も動き続ける | 積んだあと・一部を届けたあとの死亡、ステーション破壊 |
| `RobotBoardGameTests.deliveryResumesAfterReload` | 要求の 3 個を積んだ Delivery ロボットを保存してアンロードし、読み込み直すと、荷物と要求を保ったまま 1 回だけ届ける（チェスト 5 個、要求先 3 個） | 一部を届けたあとの保存、ワールドの再起動そのもの |
| `RobotGameTests.killedRobotDropsWhatItCarries` | `kill()` したロボットが持っていた鉄 5 個を落とし、ステーションを解放する | 道具・装備品のドロップ |
| `RobotGameTests.fullRobotLeavesPowerToMachine` | 満充電のロボットのステーションは電力を受け取らず、隣の Refinery が満タンになる | 有限の電源 |
| `RobotBoardGameTests.pumpTakesFluidSource` | 水源 1 個を汲んでタンクに 1000 mB、水源が消える | 液体がない・フィルタ不一致・タンクの残り容量不足 |
| `RobotBoardGameTests.planterPlantsSeed` | 耕地に小麦の種を植える | 種でないもの、植えられない地面、植える場所がふさがっている |
| `RobotBoardGameTests.deliveryBringsRequestedItems` | 要求 3 個を届け、要求が満たされ、チェストから 3 個だけ減る | 要求の保存・復元、2 台が同じ要求を取り合う、要求先の消失 |
| `RobotBoardGameTests`（その他のボード） | Miner / Lumberjack / Harvester / Farmer / Shovelman / Leave Cutter / Picker / Butcher / Knight / Stripes / Builder を実行、Bomber は TNT の点火まで | 失敗時の扱い（道具なし・対象なし） |
| `ChargingTableGameTests` | Charging Table でロボットのアイテムを充電、ボードなしは電力を持たない | — |
| `TransportCompatibilityGameTests.powerSplitExcludesInputAndUsesRemainingDemand` | 電力パイプが入力側を除き、残りの需要で分ける | ステーションと通常の機械が同じ電力網にある場合 |
| `RobotEdgeCaseGameTests.stationWorksOnEverySide` | ステーション 6 面（UP / DOWN / EAST / WEST / NORTH）とゲートの面の 8 通りで、登録・提供アクション・ロボットのリンクとドッキング | SOUTH（チェスト側）のステーション |
| `RobotEdgeCaseGameTests.loadingStopsAtRobotCapacity` | 満載なら積まない、要求数だけ積む、在庫より多く求めたら在るだけ積む。スタックは丸ごと入るスロットにしか入らない（62 個のスロットに 5 個は入らず、2 個なら入る。原作 `isItemValidForSlot` どおり） | — |
| `RobotEdgeCaseGameTests.fluidLoadingStopsAtTankCapacityAndFluid` | 3999 mB のロボットは 1 mB だけ積み、満タンなら積まない、溶岩を持っていたら水を積まない | — |
| `RobotEdgeCaseGameTests.fluidUnloadingKeepsWhatTheTargetRefuses` / `fluidUnloadingFillsOnlyTheRoomLeft` | 別の液体のタンクには混ざらず、空きが少ないタンクは空き分だけ満たす。毎 tick 総量が一定 | — |
| `RobotEdgeCaseGameTests.brokenRequestingStationIsDeliveredOnceRebuilt` | 要求の予約中に要求元ステーションを壊すと登録から消え、作り直すと 1 回だけ届く。毎 tick 鉄の総数（チェスト＋要求先＋ロボット＋地面）が 8 | メインステーションの破壊 |
| `RobotEdgeCaseGameTests.partlyFulfilledRequestResumesAfterReload` | 3 個中 1 個を持つ要求先には 2 個だけ積み、積んだまま読み込み直しても 1 回だけ届く | — |
| `RobotEdgeCaseGameTests.limitedPowerPipeCapsStationAndMachine` | PowerMode M2（20 RF/t）の鉄の電力パイプから、ステーションと Refinery が受け取る合計が 200 tick で上限（＋パイプの蓄え 1 tick 分）を超えない | — |
| `RobotEdgeCaseGameTests.pumpHoldingOtherFluidLeavesSource` / `pumpFilterLeavesOtherFluids` | 溶岩を持つ Pump、溶岩フィルタの Pump は水源を汲まない | — |
| `RobotEdgeCaseGameTests.planterKeepsSeedsWithoutSoil` / `planterPlacesNothingThatIsNotASeed` | 石の上には植えず種を減らさない、種でないものは置かない | — |
| `RobotEdgeCaseGameTests.shutDownRobotKeepsItsLoad` / `lowRobotRechargesThenWorks` | 電力切れで停止しても荷物と液体を保つ。安全量を下回ったロボットは電力ステーションで充電してから植える | 停止したロボットはその場では再開しない（原作どおり、回収して充電する） |

### 残り
上の表の「確認していないこと」のうち、Delivery ロボットの 2 台同時の取り合い、一部を届けたあとの死亡、メインステーションの破壊、長時間の連続運転は未対応。

テストの記録には対象のコミット、テスト名、成功 / 失敗、失敗した tick、期待値と実測値を残す。

---

## 9. 既知の制限事項 (設計上の合意・変更不要)

- **同一 tick 多段ホップ**: PipeA→PipeB が同tick で処理される。BuildCraft 互換として許容。
- **バウンスループ (A↔B)**: 原作に詰まり検出（bounceCount）はなく、折り返すのは Iron・Daizuli・Clay だけ。2 本の折り返すパイプの間を往復し続けるのは原作通り。`bounceCount` は保存されるが使っていない。
- **Capability キャッシュなし**: tick毎ルックアップは意図的。
- **PipeNetwork グラフなし**: 現スケールでは不要。
- **ワイヤー信号の計算方法**: 原作は隣接パイプを再帰的に更新していたが、本実装はつながったパイプを `visited` 付きで探索し、信号源からの距離で強度を決める。結果は同じ（強度 255 から 1 本ごとに -1）。

## 10. 原作との意図的な差異
- **Zone Planner の地図データ**: 原作は専用スレッドから定期保存していた。地図データはサーバースレッドでも書き換わるため、サーバースレッドで 2 分ごと・ワールドのアンロード時・サーバー停止時に保存する。サーバー起動前から読み込まれていたチャンクは、地図を計算するときにまだ記録がなければその場で記録する。
- **Electronic Library の削除**: 原作どおりクライアントのライブラリから消す。サーバーへ削除を送るパケットは撤去した（統合サーバーでは、他のプレイヤーが送るとホストのファイルを消せた）。
- **パイプワイヤー**: 原作は 1 アイテムをダメージ値で 4 色に分けていた。1.20 では色ごとに別アイテム。
- **Quarry のフレーム建設**: 置き換え可能なブロックは上書き、壊せるブロックは採掘して置く。岩盤・BlockEntity を持つブロック（チェストや機械）・Quarry 自身の位置は壊さず待つ。フレーム 1 個のエネルギーは 25 RF（原作の設計図ビルダーの消費量は再現していない）。
- **Assembly Table**: レシピはサーバーにしかないため、GUI のレシピ一覧はサーバーで作って同期する。製造は原作通りエネルギーが溜まった時点で行い、レシピの `crafting_time` は使わない。
- **アイテムの色**: 原作と同じく移動中のアイテム（TravelingItem）に持たせる。以前の実装はスタックの NBT（`BCPipeColor`）に書いていたため、パイプから出たアイテムが元のアイテムとスタックしなかった。
- **Emzuli のスロット色の保存形式**: 原作は `EnumColor` の番号＋1、本実装は `DyeColor` の番号＋1（0 = 塗らない）。
- **Quarry のチャンクローディング**: 原作は Quarry ごとに Forge のチケットを取っていた。1.20 の Forge は所有ブロック位置ごとに強制チャンクを保存・復元するため、チケットは使わず追加・解除だけを行う。範囲の上限は 1.7.10 の Forge の既定値（1 チケット 25 チャンク）で固定。
- **ブロックの設計図対応（スキーマ）**: 原作はブロック＋メタデータごとに登録していた。1.20 ではブロックの既定の状態で登録し、他の状態はそこへフォールバックする。専用のスキーマがないブロックは起動時にまとめて登録する（HeuristicBlockDetection 準拠。BlockEntity を持つブロックはクリエイティブ専用）。
- **Filler GUI の操作**: 原作はクライアントでパラメータを変えてからサーバーへ送っていた。本実装はクリックをバニラのメニューボタンパケットで送り、サーバーでパラメータの `onClick` を呼ぶ。パラメータのアイコンは説明キーから `filler_parameter` の画像を選んで直接描画する。
- **Wood 系パイプのバッテリー**: Emerald / Emzuli にもエンジンの電力を受けるバッテリーを持たせた（以前は Wood だけで、Emerald は抽出できなかった）。
- **ブロッキングするプラガブルと接続**: 原作通り、プラグ・ゲート・穴なしファサード・ロボットステーションを付けた面は接続を閉じる（以前はエネルギーパイプだけが閉じていた）。プラガブルの設置はクリックした面ではなく狙ったアームに対して行う（原作 addOrStripPipePluggable）。
- **レンズ / フィルターのアイテム**: 原作は 1 アイテムをダメージ値で分けていた（0〜15 レンズ、16〜31 フィルター、32 / 33 透明）。1.20 では色と種類ごとに別アイテム。
- **色付きパイプのテクスチャ**: 原作はパイプの色からアイコンを選んでいた。色は BlockEntity の状態なので BlockState には入れず、モデルデータ（`PipeBlockEntity.PIPE_COLOR`）で渡し、`ColoredPipeModel` が既定色テクスチャの面をその色のテクスチャに貼り替える。
- **Integration Table のプレビュー**: 原作はインベントリのスロット 10 に入れていた。本実装はインベントリ外の 1 スロットに持ち、取り出しもドロップもされない。
- **経路マーカー建築の表示**: 原作は回収した経路をレーザーで描いていた。本実装は経路を保存して建築するが、レーザーは描かない。
- **自分で出力先を決めるパイプとフィルター**: Iron / Diamond が選んだ出力先をフィルターが通さない場合、折り返すパイプ（Iron）は来た方向へ戻し、それ以外は通常のルーティングで選び直す。
- **液体パイプの表示量**: 原作はパイプ内 7 区画の量をそのまま描いた。本実装はタンク 1 つなので、「タンクの量」と「その tick に通過した量」の多い方を、材質の転送量で割って水位にする（転送量いっぱいに流れていれば満杯に見える）。
- **ロボットの探索と経路探索**: 原作は専用スレッド（IterableAlgorithmRunner）で実行していた。別スレッドからのワールド読み取りは安全でないため、サーバースレッド上で 1 tick あたりの回数を区切って進める（探索 2000 ブロック、経路 400 ステップ）。打ち切りまでのステップ数は原作と同じ。
- **PowerMode**: 原作の M2〜M128（20〜1280 RF/t）に戻した。以前の独自値（OFF〜MAX）で保存したゲートの電力制限アクションは読み込まれない。

## 11. 開発メモ
- **GameTest ワールドは再利用される**: `run/` のワールドは実行のたびに作り直されず、前回の（途中で止めた）実行のブロックが残る。範囲探索を伴うテスト（ランドマークなど）は残骸に影響されない書き方にする。
- **GameTest の名前空間**: 有効なのは `bcreborntransport` のみ。他モジュールのテストも `@GameTestHolder(BCRebornTransport.MODID)` で transport 配下に置く。
- **テンプレート**: `empty_3x3` と、11×11 のフレームが収まる `quarry_13x8x13`（y=0..1 が石）。

