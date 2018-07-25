package group.rober.runtime.kit;


import java.util.*;

/**
 * List常见操作工具
 * Created by tisir yangsong158@qq.com on 2017-06-03
 */
public abstract class ListKit {
    public static <E> ArrayList<E> newArrayList(int initialCapacity){
        return new ArrayList<E>(initialCapacity);
    };
    public static <E> ArrayList<E> newArrayList(){
//        return Lists.newArrayList();
        return new ArrayList<E>();
    };
    public static <E> ArrayList<E> listOf(E... elements){
        List<E> list = Arrays.asList(elements);
        return new ArrayList<E>(list);
    }

    /**
     * 合并去重
     *
     * @param lists lists
     * @param <E> e
     * @return list
     */
    public static <E> List<E> mergeDistinct(Collection<E>... lists){
        Set<E> set = new LinkedHashSet<>();
        for(Collection<E> l : lists){
            if(l==null)continue;
            set.addAll(l);
        }
        ArrayList<E> list = new ArrayList<E>();
        list.addAll(set);
        return list;
    }
    /**
     * 合并
     * @param lists lists
     * @param <E> e
     * @return list
     */
    public static <E> List<E> merge(Collection<E>... lists){
        ArrayList<E> list = new ArrayList<E>();
        for(Collection<E> l : lists){
            if(l==null)continue;
            list.addAll(l);
        }
        return list;
    }
}
