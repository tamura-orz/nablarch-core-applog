package nablarch.core.log.app;

import nablarch.core.log.LogItem;
import nablarch.core.log.LogUtil.MapValueEditor;
import nablarch.core.log.LogUtil.MaskingMapValueEditor;

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class CustomDataFailureLogFormatter extends FailureLogFormatter {
    
    @Override
    protected Map<String, LogItem<FailureLogContext>> getLogItems(Map<String, String> props) {
        
        Map<String, LogItem<FailureLogContext>> logItems = super.getLogItems(props);
        
        // CustomDataItemで$data$を上書き設定する。
        logItems.put("$data$", new CustomDataItem());
        
        return logItems;
    }
    
    private static final class CustomDataItem extends DataItem {
        
        /** マスク文字 */
        private static final char MASKING_CHAR = '*';
        
        /** マスク対象のパターン */
        private static final Pattern[] MASKING_PATTERNS
                = new Pattern[] { Pattern.compile(".*password.*") };
        
        /** マップの値を編集するエディタ */
        private MapValueEditor mapValueEditor = new MaskingMapValueEditor(MASKING_CHAR, MASKING_PATTERNS);
        
        @Override
        @SuppressWarnings("unchecked")
        public String get(FailureLogContext context) {
            
            // FailureLogContextのgetDataメソッドを呼び出し処理対象データを取得する。
            Object data = context.getData();
            
            // Mapでない場合はフレームワークのデフォルト実装を呼び出す。
            if (!(data instanceof Map)) {
                return super.get(context);
            }
            
            // Mapをマスクした文字列を返す。
            Map<String, String> editedMap = new TreeMap<String, String>();
            for (Map.Entry<Object, Object> entry : ((Map<Object, Object>) data).entrySet()) {
                String key = entry.getKey().toString();
                editedMap.put(key, mapValueEditor.edit(key, entry.getValue()));
            }
            return editedMap.toString();
        }
    }
}
