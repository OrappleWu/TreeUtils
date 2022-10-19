# TreeUtils
Convert the data set of tree structure into the data of tree structure！

Download and import the jar package into your project！
So that you can use it this way！

For Example：
1.Define a class like this！
```
@Tree
public class Menu {

    private String id;

    private String fatherId;

    private String name;

    private String code;

    private List<Menu> children;

    public Menu(String id, String fatherId, String name, String code) {
        this.id = id;
        this.fatherId = fatherId;
        this.name = name;
        this.code = code;
    }
}
```
2.If you have such a dataset, you can use this tool！
```
List<Menu> menuList = new ArrayList<>();  // dataset
menuList.add(new Menu("1", null, "menu1", "menu1"));
menuList.add(new Menu("2", null, "menu2", "menu2"));
menuList.add(new Menu("3", "1", "menu3", "menu3"));
menuList.add(new Menu("4", "2", "menu4", "menu4"));
menuList.add(new Menu("5", "2", "menu5", "menu5"));
menuList.add(new Menu("6", "4", "menu6", "menu6"));

TreeUtils.transferToTreeData(menuList);  // use tool
```
You can get the following data structure:
```
   [
      {
        id:1,
        fatherId:null,
        name:menu1,
        code:menu1,
        children:[{
          id:3,
          fatherId:1,
          name:menu3,
          code:menu3,
          children:[]
        }]
      },
      {
        id:2,
        fatherId:null,
        name:menu2,
        code:menu2,
        children:[{
          id:4,
          fatherId:2,
          name:menu4,
          code:menu4,
          children:[{
            id:6,
            fatherId:4,
            name:menu6,
            code:menu6,
          }]
        },{
          id:5,
          fatherId:2,
          name:menu5,
          code:menu5,
          children:[]
        }]
      }
   ] 
```
```
TreeUtils.getTreeNodeDataList(menuList, 2)  // use tool
```
You can get the following data structure:
```
  [
    {
      id:3,
      fatherId:1,
      name:menu3,
      code:menu3,
      children:[]
    },
    {
      id:4,
      fatherId:2,
      name:menu4,
      code:menu4,
      children:[]
    },
    {
      id:5,
      fatherId:2,
      name:menu5,
      code:menu5,
      children:[]
    }
  ]
```
```
TreeUtils.getTreeNodeDataList(menuList, 2, "2")  // use tool
```
You can get the following data structure:
```
      [
        {
          id:4,
          fatherId:2,
          name:menu4,
          code:menu4,
          children:[]
        },
        {
          id:5,
          fatherId:2,
          name:menu5,
          code:menu5,
          children:[]
        }
      ]
```
```   
TreeUtils.getTreeNodeDataLevel(menuList, "6")  // use tool
```
You can get the following data structure: 3 【表示id为6的菜单所处的树的节点级别为3】


3.You can also define a class like this, which is supported by this tool！
```
@Tree(uniqueIdField = "mnId", fatherIdField = "pId", childListField = "childrenList")
public class MenuNew {
    private String mnId;

    private String pId;

    private String name;

    private String code;

    private List<MenuNew> childrenList;

    public MenuNew(String mnId, String pId, String name, String code) {
        this.mnId = mnId;
        this.pId = pId;
        this.name = name;
        this.code = code;
    }
}
```
4.Related notes on 'Tree' annotation!
```
// 标识实体对象的那个属性为关联树结构的父ID
String fatherIdField() default "fatherId";

// 标识实体对象的那个属性为关联树结构的唯一主键ID
String uniqueIdField() default "id";

// 标识实体对象的那个属性为树结构的排序依据
String orderField() default "";

// 标识实体对象的某个属性为存储树形结构子节点的数据
String childListField() default "children";

// 标识树节点的root值
String fatherRootValue() default "";
```
