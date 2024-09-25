package dora.widget

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.Display
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import dora.widget.flowlayout.R


class FlowLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    private var spanSize = 0

    init {
        initAttrs(context, attrs)
    }

    fun setSpanSize(size: Int) {
        this.spanSize = size
        requestLayout()
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout)
        if (a.hasValue(R.styleable.FlowLayout_dview_fl_spanSize)) {
            spanSize = a.getDimensionPixelSize(R.styleable.FlowLayout_dview_fl_spanSize, 0)
        }
        a.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        var width = 0
        var height = 0
        var lineWidth = 0
        var lineHeight = 0
        val childCount = this.childCount
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility == GONE) {
                if (i == childCount - 1) {
                    width = lineWidth.coerceAtLeast(width)
                    height += lineHeight
                }
                continue
            }
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            val lp = child
                .layoutParams as LayoutParams
            val childWidth = (child.measuredWidth + lp.leftMargin
                    + lp.rightMargin)
            val childHeight = (child.measuredHeight + lp.topMargin
                    + lp.bottomMargin)
            if (lineWidth + childWidth > widthSize - paddingLeft - paddingRight) {
                width = width.coerceAtLeast(lineWidth)
                lineWidth = childWidth
                height += lineHeight
                lineHeight = childHeight
            } else {
                lineWidth += childWidth
                lineHeight = lineHeight.coerceAtLeast(childHeight)
            }
            if (i == childCount - 1) {
                width = lineWidth.coerceAtLeast(width)
                height += lineHeight
            }
        }
        setMeasuredDimension(
            if (widthMode == View.MeasureSpec.EXACTLY) widthSize else width + paddingLeft + paddingRight,
            if (heightMode == View.MeasureSpec.EXACTLY) heightSize else height + paddingTop + paddingBottom
        )
    }

    private fun getScreenWidth(): Int {
        val w = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val d = w.defaultDisplay
        val metrics = DisplayMetrics()
        d.getMetrics(metrics)
        var widthPixels = metrics.widthPixels
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) try {
            widthPixels = Display::class.java.getMethod("getRawWidth").invoke(d) as Int
        } catch (ignored: Exception) {
        }
        if (Build.VERSION.SDK_INT >= 17) try {
            val realSize = Point()
            Display::class.java.getMethod("getRealSize", Point::class.java).invoke(d, realSize)
            widthPixels = realSize.x
        } catch (ignored: Exception) {
        }
        return widthPixels
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        if (params is LayoutParams) {
            if (params.lineChildCount != -1) {
                val width = getScreenWidth()
                val childWidth =
                    (width - (params.lineChildCount + 1) * spanSize) / params.lineChildCount
                params.setMargins(spanSize / 2, spanSize / 2, spanSize / 2, spanSize / 2)
                params.width = childWidth
            }
            super.addView(child, index, params)
        } else {
            super.addView(child, index, params)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val width = this.width
        var lineWidth = 0
        var lineHeight = 0
        var maxChildHeight = 0
        val childCount = this.childCount
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility == VISIBLE) {
                val lp = child.layoutParams as LayoutParams
                val childWidth = child.measuredWidth + lp.leftMargin + lp.rightMargin
                val childHeight = child.measuredHeight + lp.topMargin + lp.bottomMargin
                if (childWidth + lineWidth > width) {
                    lineWidth = 0
                    lineHeight += maxChildHeight
                    maxChildHeight = 0
                }
                val left = lineWidth + lp.leftMargin
                val top = lineHeight + lp.topMargin
                val right = left + childWidth - lp.leftMargin - lp.rightMargin
                val bottom = top + childHeight - lp.topMargin - lp.bottomMargin
                lineWidth = right + lp.rightMargin
                maxChildHeight = maxChildHeight.coerceAtLeast(childHeight)
                child.layout(left, top, right, bottom)
            }
        }
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams): ViewGroup.LayoutParams {
        return LayoutParams(p)
    }

    override fun generateLayoutParams(attrs: AttributeSet): LayoutParams {
        //解析子控件中定义的自己的属性
        return LayoutParams(context, attrs)
    }

    class LayoutParams : ViewGroup.MarginLayoutParams {
        var lineChildCount = -1

        constructor(c: Context, attrs: AttributeSet?) : super(c, attrs) {
            //获取配置在子控件上，ViewGroup自身的自定义属性
            val a = c.obtainStyledAttributes(attrs, R.styleable.FlowLayout)
            if (a.hasValue(R.styleable.FlowLayout_dview_fl_lineChildCount)) {
                lineChildCount = a.getInt(R.styleable.FlowLayout_dview_fl_lineChildCount, -1)
            }
            a.recycle()
        }

        constructor(width: Int, height: Int) : super(width, height)
        constructor(source: ViewGroup.LayoutParams?) : super(source)
    }
}