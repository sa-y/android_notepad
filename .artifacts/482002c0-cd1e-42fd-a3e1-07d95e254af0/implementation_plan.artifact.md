# AppCompat ActionMode Migration for ViewNoteFragment

`ViewNoteFragment` で使用されているアクションモードを標準の `android.view.ActionMode` から AppCompat 版の `androidx.appcompat.view.ActionMode` に移行します。これにより、`app:actionLayout` などの AppCompat 属性が正しく機能し、デザインの一貫性が保たれるようになります。

## Proposed Changes

### [Fragment]

#### [MODIFY] [ViewNoteFragment.java](file:///C:/cygwin64/home/masah/work/android_notepad_x/app/src/main/java/org/routine_work/notepad/fragment/ViewNoteFragment.java)
- `import android.view.ActionMode;` を `import androidx.appcompat.view.ActionMode;` に変更。
- `findWordActionMode` フィールドの型を更新。
- `FindWordActionModeCallback` 内部クラスが `androidx.appcompat.view.ActionMode.Callback` を実装するように変更。
- `startFindWordActionMode()` メソッド内で `((AppCompatActivity) requireActivity()).startSupportActionMode(findWordActionModeCallback)` を使用するように修正。
- `onCreateActionMode` 内で `mode.getMenuInflater()` を使用するように変更（推奨される作法）。

### [Resource]

#### [MODIFY] [find_word_actionmode_menu.xml](file:///C:/cygwin64/home/masah/work/android_notepad_x/app/src/main/res/menu/find_word_actionmode_menu.xml)
- 前の手順で追加した暫定的な `android:actionLayout` および `android:showAsAction` を削除し、AppCompat 用の `app:` 属性に統一します。

## Verification Plan

### Automated Tests
- プロジェクトのビルドが通り、コンパイルエラーが発生しないことを確認します。
  - `gradlew :app:assembleDebug`

### Manual Verification
1. ノート表示画面を開く。
2. オプションメニューから "Find word" を選択する。
3. アクションバー部分に検索用の `EditText` が正しく表示されることを確認する。
4. 検索語を入力し、強調表示とスクロールが機能することを確認する（以前指摘したスクロール位置のバグ修正もついでに行うのが望ましいですが、まずは表示の修正を優先します）。
