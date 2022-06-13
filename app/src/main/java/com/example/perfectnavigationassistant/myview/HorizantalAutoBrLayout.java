package com.example.perfectnavigationassistant.myview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

//自定义换行容器控件：用于容纳textView的时候可以换行
public class HorizantalAutoBrLayout extends ViewGroup {
    /**
     * 可使用的最大宽度
     */
    private int maxWidth;
    /*子控件的高度*/
    int unitHeight;
    int containorHeight;
    public Boolean haveChild = false;

    public HorizantalAutoBrLayout(Context context) {
        super(context);
    }

    public HorizantalAutoBrLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizantalAutoBrLayout(Context context, AttributeSet attrs,
                                  int defStyle) {
        super(context, attrs, defStyle);
    }

//    @Override
//    public void onWindowFocusChanged(boolean hasWindowFocus) {
//        super.onWindowFocusChanged(hasWindowFocus);
//        int count = getChildCount();
//        View lastChild = getChildAt(count - 1);
//        if (count > 0) {
//            System.out.println(lastChild.getBottom());
//            System.out.println(lastChild.getTop());
//            System.out.println(((View) lastChild.getParent()).getHeight());
//        }
//        if (count > 0 && lastChild.getBottom() > ((View) lastChild.getParent()).getHeight()) {
//            containorHeight += unitHeight;
//        }
//
//    }

    //时机：每当添加一个子控件的时候，该方法就会被调用
    //作用：父控件询问子控件：“你有多大的尺寸，我要给你多大的地方才能容纳你？”
    //参数：两个参数就是父控件告诉子控件可获得的空间以及关于这个空间的约束条件
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        unitHeight = 0;
        /**
         * 容器的高度,也就是本布局的高度。初始化赋值为0.
         */
        //给父容器的大小初始化
        if (!haveChild) {
//            System.out.println("添加第一个子控件");
            containorHeight = heightMeasureSpec;
            haveChild = true;
        }
        /**
         * 获取该布局内子组件的个数
         */
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            /**
             * measure(int widthMeasureSpec,int
             * heightMeasureSpec)用于设置子组件显示模式.有三个值：
             * MeasureSpec.AT_MOST 该组件可以设置自己的大小,但是最大不能超过其父组件的限定
             * MeasureSpec.EXACTLY 无论该组件设置大小是多少,都只能按照父组件限制的大小来显示
             * MeasureSpec.UNSPECIFIED 该组件不受父组件的限制,可以设置任意大小
             */
            view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            // 把每个子组件的高度相加就是该组件要显示的高度。
//            containorHeight += view.getMeasuredHeight();

            //每个子组件的高度
            unitHeight = view.getMeasuredHeight();

        }
        //当最后一个子控件的底部与父控件的顶部距离大于父控件的高度的时候，父控件需要增加高度
        //但是需要注意：View的left、top、right、bottom等属性在Measure与Layout过程完成之后，才会被正确赋值
//        View lastChild = getChildAt(count - 1);
//        if (count > 0) {
//            System.out.println(lastChild.getBottom());
//            System.out.println(lastChild.getTop());
//            System.out.println(((View) lastChild.getParent()).getHeight());
//        }
//        if (count > 0 && lastChild.getBottom() > ((View) lastChild.getParent()).getHeight()) {
//            containorHeight += unitHeight;
//        }
        /**
         * onMeasure方法的关键代码,该句设置父容器的大小。
         */
        setMeasuredDimension(maxWidth, containorHeight);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 获取子组件数
        int childCount = getChildCount();
        // 子组件行数,初始化赋值为1
        int row = 1;
        // 子组件的左边“坐标”
        int left = 0;
        // 子组件的右边“坐标”
        int right = 0;
        // 子组件的顶部“坐标”
        int top = 0;
        // 子组件的底部“坐标”
        int bottom = 0;
        // 在父组件中设置的padding属性的值,该值显然也会影响到子组件在屏幕的显示位置
        int p = getPaddingLeft();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            // 测量子组件的宽
            int width = view.getMeasuredWidth();
            // 测量子组件的高
            int height = view.getMeasuredHeight();
            left = p + right;
            right = left + width;
            top = p * row + height * (row - 1);
            bottom = top + height;
            if (right > maxWidth) {
                row++;
                //每次换行后要将子组件左边“坐标”与右边“坐标”重新初始化
                left = 0;
                right = 0;
                left = p + right;
                right = left + width;
                top = p * row + height * (row - 1);
                bottom = top + height;
            }
            // 最后按照计算出来的“坐标”将子组件放在父容器内
            view.layout(left, top, right, bottom);
        }

        int count = getChildCount();
        View lastChild = getChildAt(count - 1);
        if (count > 0) {
//            System.out.println(lastChild.getBottom());
//            System.out.println(lastChild.getTop());
//            System.out.println(((View) lastChild.getParent()).getHeight());
        }
        if (count > 0 && lastChild.getBottom() > ((View) lastChild.getParent()).getHeight() - unitHeight) {
//            System.out.println("父控件高度增加");
            containorHeight += unitHeight;

        }
    }
}
