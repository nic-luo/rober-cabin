package group.rober.sql.listener;

/**
 * 数据更新时监听
 * Created by tisir yangsong158@qq.com on 2017-06-07
 */
public interface UpdateListener<T> {

    void before(T data);

    void after(T data);

}
