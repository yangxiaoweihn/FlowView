## 控件简介
    一个流控件
## 添加依赖
```
    compile 'ws.dyt.view:flowview:1.0'
```
## 屏幕截图
![FlowView](https://raw.githubusercontent.com/yangxiaoweihn/FlowView/flow-view/flowviewtest/screenshots/img.png)
    
## 使用
-   在布局中文件使用
```xml
    <ws.dyt.view.FlowView
        android:id="@+id/flowView_zise"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="25dp"
        app:layout_itemSpacing="10dp"
        app:layout_lineSpacing="10dp" />
```
### 属性支持
-    共用属性
```xml
    <!-- 行间距 -->
    <attr name="layout_lineSpacing" format="dimension" />
    <!-- Item间距 -->
    <attr name="layout_itemSpacing" format="dimension" />
```
- 添加监听器
```java
    /**
     *监听器
     */
    public interface setOnPageChangedListener{
        /**
         * 换页
         * @param page 当前页
         * @param offset 数据偏移
         */
        void onPageChanged(int page, int offset);
        /**
         * 是否只有一页
         * @param is
         */
        void onOnlyOnePage(boolean is);
    }
```
## License
```xml
        http://www.apache.org/licenses/LICENSE-2.0
```
