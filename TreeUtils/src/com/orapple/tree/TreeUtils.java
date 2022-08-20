package com.orapple.tree;

import com.orapple.tree.bean.TreeBean;
import com.orapple.tree.node.Tree;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public final class TreeUtils {

    private TreeUtils() {
    }

    private final static String FATHER_KEK = "$FATHER_KEK$";

    private final static String ID_KEK = "$ID_KEK$";

    private final static String CHILD_KEK = "$CHILD_KEK$";

    private final static String ORDER_KEK = "$ORDER_KEK$";

    private final static String ROOT_VALUE_KEK = "$ROOT_VALUE_KEK$";

    // 将不是树结构的数据结构转换成树形结构的数据
    public static <T> List<T> transferToTreeData(List<T> data) {
        final Map<String, Object> treeNoteMap = getAnnotationInfo(data);
        final Field childField = (Field) treeNoteMap.get(CHILD_KEK);
        return transferTreeBeanToTreeData(initListDataToTreeBean(data, treeNoteMap), childField);
    }

    // 将不是树结构的数据结构转换成树形结构的数据
    public static <T> List<TreeBean<T>> transferToTreeBeans(List<T> data) {
        return initListDataToTreeBean(data, getAnnotationInfo(data));
    }

    // Root节点的级别为1，其它叶子节点的级别依次递增
    public static <T> List<T> getTreeNodeDataList(List<T> data, int level) {
        return getTreeNodeDataList(data, level, null);
    }

    public static <T> List<T> getTreeNodeDataList(List<T> data, int level, String rootUniqueId) {
        if (level < 1) {
            throw new RuntimeException("The level of the query tree node cannot be less than 1!");
        }
        List<TreeBean<T>> treeBeans = initListDataToTreeBean(data, getAnnotationInfo(data));
        if (rootUniqueId != null) {
            List<TreeBean<T>> treeBeanList = treeBeans.stream()
                    .filter(v -> rootUniqueId.equals(v.getTreeId())).collect(Collectors.toList());
            return getTreeNodeDataByLevelInTreeBeans(treeBeanList, level);
        }
        return getTreeNodeDataByLevelInTreeBeans(treeBeans, level);
    }

    // 获取树节点数据的级别
    public static <T> int getTreeNodeDataLevel(List<T> data, String uniqueId) {
        List<TreeBean<T>> treeBeans = initListDataToTreeBean(data, getAnnotationInfo(data));
        TreeBean<T> treeBean = findTreeBeanByTreeId(treeBeans, uniqueId);
        if (treeBean != null) {
            return treeBean.getTreeLeave();
        }
        return -1;
    }

    private static <T> TreeBean<T> findTreeBeanByTreeId(List<TreeBean<T>> treeBeans, String uniqueId) {
        List<TreeBean<T>> tempTreeBeans = new ArrayList<>();
        for (TreeBean<T> treeBean : treeBeans) {
            if (uniqueId.equals(treeBean.getTreeId())) {
                return treeBean;
            } else {
                tempTreeBeans.add(findTreeBeanByTreeId(treeBean.getChildData(), uniqueId));
            }
        }
        while (tempTreeBeans.remove(null)) ;
        if (tempTreeBeans.isEmpty()) {
            return null;
        }
        return tempTreeBeans.get(0);
    }

    private static <T> List<T> getTreeNodeDataByLevelInTreeBeans(List<TreeBean<T>> treeBeans, int level) {
        List<T> list = new ArrayList<>();
        for (TreeBean<T> treeBean : treeBeans) {
            if (treeBean.getTreeLeave() == level) {
                list.add(treeBean.getTreeData());
            } else {
                list.addAll(getTreeNodeDataByLevelInTreeBeans(treeBean.getChildData(), level));
            }
        }
        return list;
    }

    // 将查询的具备可能组成树结构数据的对象初始化成通用的树结构Bean
    private static <T> List<TreeBean<T>> initListDataToTreeBean(List<T> data, Map<String, Object> treeNoteMap) {
        List<TreeBean<T>> result = new ArrayList<>();
        if (data != null && !data.isEmpty()) {
            final String fatherRootValue = String.valueOf(treeNoteMap.get(ROOT_VALUE_KEK));
            List<T> fatherData = getCurRootData(data, fatherRootValue, treeNoteMap);
            result.addAll(initTreeBeans(data, treeNoteMap, fatherData, 1));
        }
        return result;
    }

    private static <T> List<TreeBean<T>> initTreeBeans
            (List<T> data, Map<String, Object> treeNoteMap, List<T> nodeDataList, int level) {
        List<TreeBean<T>> result = new ArrayList<>();
        for (T tempData : nodeDataList) {
            TreeBean<T> treeBean = new TreeBean<>();
            treeBean.setTreeData(tempData);
            treeBean.setTreeLeave(level);
            treeBean.setTreeId(initTreeId(tempData, (Field) treeNoteMap.get(ID_KEK)));
            treeBean.setChildData(initTreeBeans(data, treeNoteMap, getCurRootData(data, treeNoteMap, tempData), level + 1));
            result.add(treeBean);
        }
        return result;
    }

    private static <T> String initTreeId(T tempData, Field idField) {
        try {
            return String.valueOf(idField.get(tempData));
        } catch (Exception e) {
            throw new RuntimeException(e.getClass().getName() + "@" + e.getMessage());
        }
    }

    private static <T> List<T> getCurRootData(List<T> data, Map<String, Object> treeNoteMap, T tempData) {
        return getCurRootData(data, initTreeId(tempData, (Field) treeNoteMap.get(ID_KEK)), treeNoteMap);
    }

    private static <T> List<T> getCurRootData(List<T> data, String fatherRootValue, Map<String, Object> treeNoteMap) {
        try {
            List<T> result = new ArrayList<>();
            final Field fatherField = (Field) treeNoteMap.get(FATHER_KEK);
            final Field orderField = (Field) treeNoteMap.get(ORDER_KEK);
            for (T tempData : data) {
                Object tempFatherValue = fatherField.get(tempData);
                if (tempFatherValue == null) {
                    if (fatherRootValue.length() == 0) {
                        result.add(tempData);
                    }
                } else {
                    if (tempFatherValue.equals(fatherRootValue)) {
                        result.add(tempData);
                    }
                }
            }
            if (orderField != null) {
                result.sort(Comparator.comparing(v -> {
                    try {
                        return String.valueOf(orderField.get(v));
                    } catch (Exception e) {
                        throw new RuntimeException(e.getClass().getName() + "@" + e.getMessage());
                    }
                }));
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e.getClass().getName() + "@" + e.getMessage());
        }
    }

    private static <T> Map<String, Object> getAnnotationInfo(List<T> data) {
        // 检查参数的数据是否合法，并将为null的元素除去！
        if (data == null) {
            throw new RuntimeException("The data set to be initialized cannot be empty!");
        } else {
            while (data.remove(null)) ;
        }
        Map<String, Object> treeNoteMap = new HashMap<>();
        if (!data.isEmpty()) {
            Class<?> clazz = data.get(0).getClass();
            Tree tree = clazz.getDeclaredAnnotation(Tree.class);
            if (tree == null) {
                throw new RuntimeException(clazz.getName() + "@The annotation 'Tree' must be used on this type!");
            }
            if (tree.fatherIdField().length() != 0) {
                treeNoteMap.put(FATHER_KEK, getFieldByFieldName(clazz, tree.fatherIdField()));
            } else {
                throw new RuntimeException("@ The annotation 'Tree#fatherIdField()' must used a non empty value!");
            }
            if (tree.uniqueIdField().length() != 0) {
                treeNoteMap.put(ID_KEK, getFieldByFieldName(clazz, tree.uniqueIdField()));
            } else {
                throw new RuntimeException("@ The annotation 'Tree#uniqueIdField()' must used a non empty value!");
            }
            if (tree.orderField().length() != 0) {
                treeNoteMap.put(ORDER_KEK, getFieldByFieldName(clazz, tree.orderField()));
            }
            if (tree.childListField().length() != 0) {
                treeNoteMap.put(CHILD_KEK, getFieldByFieldName(clazz, tree.childListField()));
            } else {
                throw new RuntimeException("@ The annotation 'Tree#childListField()' must used a non empty value!");
            }
            treeNoteMap.put(ROOT_VALUE_KEK, tree.fatherRootValue());
        }
        return treeNoteMap;
    }

    private static Field getFieldByFieldName(Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (Exception e) {
            throw new RuntimeException(e.getClass().getName() + "@" + clazz.getName() + "#" + e.getMessage());
        }
    }

    private static <T> T getTreeNodeData(TreeBean<T> treeBean, Field childField) {
        try {
            T treeData = treeBean.getTreeData();
            childField.set(treeData, transferTreeBeanToTreeData(treeBean.getChildData(), childField));
            return treeData;
        } catch (Exception e) {
            throw new RuntimeException(e.getClass().getName() + "@" + e.getMessage());
        }
    }

    private static <T> List<T> transferTreeBeanToTreeData(List<TreeBean<T>> treeBeans, Field childField) {
        List<T> result = new ArrayList<>();
        for (TreeBean<T> treeBean : treeBeans) {
            result.add(getTreeNodeData(treeBean, childField));
        }
        return result;
    }

}
