package ws.dyt.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangxiaowei on 15/6/29.
 * TODO-NOTICE: 1. 暂不支持padding 2. 目前还不支持文本换行情况
 */
public class FlowView<E> extends ViewGroup {
    private static String TAG = "DEBUG";

    private int lineSpace = 0;
    private int itemSpace = 0;


    public FlowView(Context context) {
        this(context, null);
    }

    public FlowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TAG = getClass().getSimpleName();

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FlowView);
        lineSpace = ta.getDimensionPixelOffset(R.styleable.FlowView_layout_lineSpacing, 0);
        itemSpace = ta.getDimensionPixelOffset(R.styleable.FlowView_layout_itemSpacing, 0);

        ta.recycle();
    }

    //从第一条数据开始的偏移
    private int breakPointPostionOffset = 0;
    /**
     * 负责设置子控件的测量模式和大小 根据所有子控件设置自己的宽和高
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        rowViewList.clear();
        // 获得它的父容器为它设置的测量模式和大小
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        Log.e(TAG, sizeWidth + "," + sizeHeight);

        // 如果是warp_content情况下，记录宽和高
        //TODO  针对padding的情况，控件会被padding遮挡，需要修复该bug
        int width = getPaddingLeft() + getPaddingRight();
        int height = getPaddingTop() + getPaddingBottom();

        //记录每一行的宽度，width不断取最大宽度
        int maxLineWidth = 0;
        //每一行的高度，累加至height
        int maxLineHeight = 0;

        int cc = getChildCount();

        int lines = 1;
        int lastWidth = 0;
        boolean isNewLine = false;

        //用来存储每行的view
        List<ItemHolder> lineViews = new ArrayList();
        // 遍历每个子元素
        for (int i = 0; i < cc; i++){
            View child = getChildAt(i);
            // 测量每一个child的宽和高
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            // 得到child的lp
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            // 当前子空间实际占据的宽度
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin + (isNewLine ? 0 : itemSpace);
            // 当前子空间实际占据的高度
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            if (childWidth >= sizeWidth || lastWidth + childWidth > sizeWidth){
                //纪录该行所有的view以及需要的高度
                width = Math.max(lastWidth, width);
                lastWidth = childWidth;
                maxLineHeight = childHeight;
                height += maxLineHeight;
                if(0 != maxLine && lines == maxLine){
                    break;
                }
                rowViewList.add(lineViews);
                isNewLine = true;
                //需要换行
                lines++;

                lineViews = new ArrayList();
            }else {
                isNewLine = false;
                lastWidth += childWidth;
                maxLineHeight = Math.max(maxLineHeight, childHeight);
            }

            ItemHolder itemHolder = new ItemHolder(maxLineHeight, child);
            lineViews.add(itemHolder);
        }

        //需要加上最后一行     width = Math.max(lastWidth, width);
        height += maxLineHeight;

        // 记录最后一行
        rowViewList.add(lineViews);

        Log.i(TAG, "lines: " + lines + " : height: " + ((modeHeight == MeasureSpec.EXACTLY) ? sizeHeight : height + ((lines - 1) * lineSpace)));

        setMeasuredDimension(
                (modeWidth == MeasureSpec.EXACTLY)
                        ? sizeWidth : width,
                (modeHeight == MeasureSpec.EXACTLY)
                        ? sizeHeight : height + ((lines - 1) * lineSpace)
        );

    }

    private static class ItemHolder{
        //高度
        public int height;
        //
        public View view;

        public ItemHolder(int height, View view){
            this.height = height;
            this.view = view;
        }
    }

    private List<List<ItemHolder>> rowViewList = new ArrayList();

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int lineHeight = 0;
//        //用来存储每行的view
        List<ItemHolder> lineViews = new ArrayList();

        int left = getPaddingLeft();
        int top = getPaddingTop();
        // 得到总行数
        int lineNums = rowViewList.size();
        for (int i = 0; i < lineNums; i++){
            // 每一行的所有的views
            lineViews = rowViewList.get(i);
            int subSums = lineViews.size();
            // 获取当前行的最大高度
            if (subSums != 1){
                for (int m = 0; m < subSums - 1; m++){
                    ItemHolder e1 = lineViews.get(m);
                    ItemHolder e2 = lineViews.get(m + 1);

                    lineHeight = e1.height > e2.height ? e1.height : e2.height;
                }
            }else {
                lineHeight = lineViews.get(0).height;
            }

            Log.e(TAG, "第" + (i + 1) + "行 ：size: " + subSums + " , height: " + lineHeight + " , offset: " + breakPointPostionOffset);

            //需要加上行间距
            if(0 != i){
                top += lineSpace;
            }
            // 遍历当前行所有的View
            for (int j = 0; j < subSums; j++){
                ItemHolder child = lineViews.get(j);
                MarginLayoutParams lp = (MarginLayoutParams) child.view.getLayoutParams();

                //每行第一个item不加Margin
                left += (j == 0 ? 0 : itemSpace);
                //计算childView的left,top,right,bottom
                int lc = left + lp.leftMargin;
                int tc = top + lp.topMargin;
                int mw = child.view.getMeasuredWidth();
                int rc = lc + mw;
                int bc = tc + child.view.getMeasuredHeight() ;

                Log.e(TAG, i + " , l = " + lc + " , t = " + tc + " , r =" + rc + " , b = " + bc);

                child.view.layout(lc, tc, rc, bc);

                left += mw + lp.rightMargin + lp.leftMargin;
            }
            left = 0;
            top += lineHeight;
        }

        //移除多余添加的控件
        for (int i = 0; i < getChildCount(); i++) {
            if (0 == getChildAt(i).getWidth()){
                removeViewInLayout(getChildAt(i));
                invalidate();
            }
        }
        if (this.maxLine >= 0 && null != this.datas && !this.datas.isEmpty()){
            int ss = getChildCount();
            if (ss == this.datas.size()){
                if (null != this.onPageChangedListener){
                    onPageChangedListener.onOnlyOnePage(true);
                }
            }else {
                if (null != this.onPageChangedListener){
                    onPageChangedListener.onOnlyOnePage(false);
                }
            }
        }
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs){
        return new MarginLayoutParams(getContext(), attrs);
    }

    private List<E> datas = new ArrayList<>();

    /**
     * 添加要显示的数据(仅仅添加)
     * 需要调用{@link #showNextPage}
     * @param data
     */
    public FlowView addDataOnly(List<E> data){
        if (null == data || data.isEmpty()){
            return this;
        }
        this.datas.addAll(data);
        return this;
    }

    /**
     * 添加要显示的数据(清除旧数据后添加)
     * 需要调用{@link #showNextPage}
     * @param data
     */
    public FlowView addDataAfterClean(List<E> data){
        this.datas.clear();
        this.addDataOnly(data);
        return this;
    }

    /**
     * 添加数据，不需要调用show方法
     * @param e
     */
    public FlowView addData(E e){
        this.datas.add(e);
        addView(this.datas.size() - 1);
        return this;
    }

    /**
     * 删除数据，不需要调用show方法
     * @param position
     */
    public FlowView removeData(int position){
        if (position < 0 || position >= this.datas.size()) {
            return this;
        }
        this.datas.remove(position);
        //只刷新当前页
        this.flushCurrentPage();
        return this;
    }

    private int maxLine = 0;

    /**
     * 设置每页最大显示行数
     * @param maxLine
     * @return
     */
    public FlowView setMaxLine(int maxLine){
        this.maxLine = maxLine;
        return this;
    }

    public E getItem(int position){
        return datas.get(position);
    }

    /**
     * 调用该方法进行显示数据
     */
    public void showNextPage(){
        //针对设置了最大行的情况，一页显示不下，需要计算页数起始偏移量
        if (this.maxLine >= 0){
            int cc = getChildCount();
            for (int i = 0; i < cc; i++) {
                View tv = getChildAt(i);
                breakPointPostionOffset += tv.getWidth() == 0 ? 0 : 1;
            }
        }

        this.removeAllViewsInLayout();
        invalidate();
        if (null == adapter){
            throw new IllegalArgumentException("must set Adapter.");
        }
        if (null == datas || datas.isEmpty()){
            return;
        }
        int size = datas.size();
        if (breakPointPostionOffset >= size){
            breakPointPostionOffset = 0;
        }

        for (int i = breakPointPostionOffset; i < size; i++) {
            addView(i);
        }
    }

    /**
     * 刷新当前页
     */
    public void flushCurrentPage(){
        this.removeAllViewsInLayout();
        invalidate();
        if (null == adapter){
            throw new IllegalArgumentException("must set Adapter.");
        }
        if (null == datas || datas.isEmpty()){
            return;
        }
        int size = datas.size();

        for (int i = breakPointPostionOffset; i < size; i++) {
            addView(i);
        }
    }

    private FlowView addView(int position){
        ViewHolder view = adapter.create();
        adapter.bind(view, position);
        MarginLayoutParams mm = new MarginLayoutParams(MarginLayoutParams.WRAP_CONTENT, MarginLayoutParams.WRAP_CONTENT);
//        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getContext().getResources().getDisplayMetrics());
        int margin = itemSpace;
//        mm.leftMargin = margin;
//        mm.rightMargin = margin;
        this.addView(view.itemView, mm);
        requestLayout();
        invalidate();
        return this;
    }

    private Adapter adapter;
    public FlowView setAdapter(Adapter adapter){
        this.adapter = adapter;
        return this;
    }

    public static abstract class ViewHolder{
        public final View itemView;
        public ViewHolder(View itemView){
            if (itemView == null) {
                throw new IllegalArgumentException("itemView may not be null");
            }
            this.itemView = itemView;
        }
    }

    public static abstract class Adapter<VH extends ViewHolder>{
        abstract public VH create();

        abstract public void bind(VH vh, int position);
    }


    private OnPageChangedListener onPageChangedListener;
    public FlowView setOnPageChangedListener(OnPageChangedListener onPageChangedListener){
        this.onPageChangedListener = onPageChangedListener;
        return this;
    }
    public interface OnPageChangedListener{
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
}
