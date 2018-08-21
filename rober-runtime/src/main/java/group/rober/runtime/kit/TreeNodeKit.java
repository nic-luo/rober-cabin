package group.rober.runtime.kit;

import group.rober.runtime.lang.TreeNode;
import group.rober.runtime.lang.TreeNodeWrapper;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TreeNodeKit {

    private static final String DEFAULT_SPLITTER = "";


//    public static <T> TreeNodeWrapper<T> buildTreeNodeByObjectList(List<T> itemList, Function<T, String> sortCodeFunc){
//        List<TreeNodeWrapper<T>> branchList = buildWrapperTree(itemList, sortCodeFunc);
//        return branchList.get(0);
//    }

    public static <T> List<TreeNodeWrapper<T>> buildWrapperTree(List<T> itemList, Function<T, String> sortCodeFunc) {
        return buildWrapperTree(itemList, sortCodeFunc, DEFAULT_SPLITTER);
    }


    public static <T> List<TreeNodeWrapper<T>> buildWrapperTree(List<T> itemList, Function<T, String> sortCodeFunc, String splitter) {
        TreeBuilder<TreeNodeWrapper<T>> builder = new TreeBuilder<>(itemList.stream()
                .map(TreeNodeWrapper::new).collect(Collectors.toList()), node -> sortCodeFunc.apply(node.getValue()), splitter);
        return builder.build();
    }

    public static <T extends TreeNode<T>> List<T> buildTree(List<T> nodeList, Function<T, String> sortCodeFunc) {
        return buildTree(nodeList, sortCodeFunc, DEFAULT_SPLITTER);
    }

    public static <T extends TreeNode<T>> List<T> buildTree(List<T> nodeList, Function<T, String> sortCodeFunc, String splitter) {
        TreeBuilder<T> builder = new TreeBuilder<>(nodeList, sortCodeFunc, splitter);
        return builder.build();
    }

    /**
     * 组装树图节点，没有根节点的情况下，返回多个树图节点列表分支
     *
     * @param nodeList 节点列表
     * @param sortCodeFunc 排序属性提供函数
     * @param <T> T
     * @return list
     */
    public static <T> List<TreeNodeWrapper<T>> buildTreeBranches(List<TreeNodeWrapper<T>> nodeList, Function<T, String> sortCodeFunc){
        return buildTreeBranches(nodeList, sortCodeFunc, DEFAULT_SPLITTER);
    }

    public static <T> List<TreeNodeWrapper<T>> buildTreeBranches(List<TreeNodeWrapper<T>> nodeList, Function<T, String> sortCodeFunc,
                                                                 String splitter) {
        List<TreeNodeWrapper<T>> branchList;

        TreeBuilder<TreeNodeWrapper<T>> builder = new TreeBuilder<>(nodeList, node -> sortCodeFunc.apply(node.getValue()), splitter);
        builder.sort();
        branchList = builder.getRootBranches();

        HashSet<TreeNodeWrapper<T>> usedNodes = new HashSet<TreeNodeWrapper<T>>();
        usedNodes.addAll(branchList);

        for(TreeNodeWrapper<T> node : branchList){
            builder.buildChildren(node, nodeList, usedNodes);
        }

        return branchList;
    }

    /**
     * 组装树图节点,返回根节点
     *
     * @param nodeList 节点列表
     * @param sortCodeFunc 排序属性提供函数
     * @param <T> T
     * @return TreeNodeWrapper
     */
    public static <T> TreeNodeWrapper<T> buildTreeNode(List<TreeNodeWrapper<T>> nodeList, Function<T, String> sortCodeFunc){
        List<TreeNodeWrapper<T>> branchList = buildTreeBranches(nodeList, sortCodeFunc);
        return branchList.get(0);
    }

    private static class TreeBuilder<T extends TreeNode<T>>{
        private List<T> nodeList = null;
        private Function<T, String> sortCodeFunc;
        private String splitter;

        public TreeBuilder(List<T> nodeList, Function<T, String> sortCodeFunc, String splitter) {
            this.nodeList = nodeList;
            this.sortCodeFunc = sortCodeFunc;
            this.splitter = splitter;
            ensureSplitterAscii();
        }

        private void ensureSplitterAscii() {
            if (splitter.compareTo(Objects.toString(0L)) > 0)
                throw new IllegalArgumentException("splitter must be order before 0");
        }


        /**
         * 对树图节点排序
         */
        public void sort(){
            nodeList.sort(new Comparator<T>() {
                public int compare(T o1, T o2) {
                    String sv1 = sortCodeFunc.apply(o1);
                    String sv2 = sortCodeFunc.apply(o2);
                    return sv1.compareTo(sv2);
                }
            });
        }

        private boolean isChild(String parentSortCode, String childSortCode) {
            return parentSortCode.length() < childSortCode.length()
                    && (parentSortCode.endsWith(splitter) ? childSortCode.startsWith(parentSortCode) :
                    childSortCode.startsWith(parentSortCode + splitter));
        }

        private boolean isChild(T parent, T child) {
            return null != parent &&
                    isChild(sortCodeFunc.apply(parent), sortCodeFunc.apply(child));
        }

        /**
         * 搜索第一层节点（根节点）可能是一个，也有可能是多个(输入的数据，必需保证事先排序过）
         *
         * @return list
         */
        public List<T> getRootBranches(){
            List<T> rootList = new ArrayList<>();
            Set<T> used = new HashSet<T>();
            for(int i=0;i<nodeList.size();i++){
                T node1 = nodeList.get(i);
                if(used.contains(node1))continue;

                for(int j=i+1;j<nodeList.size();j++){
                    T node2 = nodeList.get(j);
                    if(used.contains(node2))continue;

                    String sort1 = sortCodeFunc.apply(node1);
                    String sort2 = sortCodeFunc.apply(node2);
                    if (isChild(sort1, sort2)){
                        if(!rootList.contains(node1))rootList.add(node1);
                        used.add(node2);
                        used.add(node1);
                    }
                }
                //可能的子节点都找过了，但还是找不到任何一个子节点，那么，他也是根
                if(!used.contains(node1)){
                    rootList.add(node1);
                }
            }
            return rootList;
        }

        public List<T> build() {
            sort();
            List<T> rootList = new ArrayList<>();
            Iterator<T> nodeIterator = nodeList.iterator();
            T lastNode = null;
            while (nodeIterator.hasNext()) {
                T node = nodeIterator.next();
                while (null != lastNode) {
                    if (isChild(lastNode, node)) {
                        lastNode.addChild(node);
                        lastNode = node;
                        break;
                    }
                    lastNode = lastNode.getParent();
                }
                if (null == lastNode) {
                    if (!rootList.contains(node)) rootList.add(node);
                    lastNode = node;
                }
            }
            return rootList;
        }

        public boolean hasChildren(T node, List<T> nodeList){
            int startIdx = nodeList.indexOf(node);
            if(startIdx<0)return false;
            String sort1 = sortCodeFunc.apply(node);
            for(int i=startIdx+1;i<nodeList.size();i++){
                T node2 = nodeList.get(i);
                String sort2 = sortCodeFunc.apply(node2);
                if (isChild(sort1, sort2)) return true;
            }
            return false;
        }

        /**
         * 构建树节点（(输入的数据，必需保证事先排序过）
         *
         * @param node node
         * @param nodeList nodeLIst
         * @param used used
         */
        public void buildChildren(T node, List<T> nodeList, Set<T> used){
            int startIdx = nodeList.indexOf(node);
            if(startIdx<0)return;
            String sort1 = sortCodeFunc.apply(node);

            for(int i=startIdx+1;i<nodeList.size();i++){
                T child = nodeList.get(i);
                String sort2 = sortCodeFunc.apply(child);
                if(hasChildren(child,nodeList)){
                    List<T> nodeLists=new ArrayList<>();
                    String sort1s = sortCodeFunc.apply(node);
                    for(int is=startIdx+1;is<nodeList.size();is++){
                        T nodeItem = nodeList.get(is);
                        String sort2s = sortCodeFunc.apply(nodeItem);
                        if (isChild(sort1s, sort2s)){
                            nodeLists.add(nodeList.get(is));
                        }
                    }
                    buildChildren(child,nodeLists,used);
                }
                if (isChild(sort1, sort2)){
                    if(!used.contains(child)){
                        node.addChild(child);
                    }
                    used.add(child);
                }
            }
        }

    }
}
