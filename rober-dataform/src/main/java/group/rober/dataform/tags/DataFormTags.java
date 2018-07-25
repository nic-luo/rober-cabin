package group.rober.dataform.tags;

import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleHash;

import java.util.Map;

/**
 * DataForm所有的标签集合
 */
public class DataFormTags extends SimpleHash {
    public DataFormTags() {
        super((ObjectWrapper)null);
        put("detailInfo", new DetailInfoTag());
    }

    public DataFormTags(Map map) {
        super(map,null);
    }


}
