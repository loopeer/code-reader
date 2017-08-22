package com.loopeer.codereaderkt.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.v4.view.AccessibilityDelegateCompat
import android.support.v4.view.InputDeviceCompat
import android.support.v4.view.MotionEventCompat
import android.support.v4.view.NestedScrollingChild
import android.support.v4.view.NestedScrollingChildHelper
import android.support.v4.view.NestedScrollingParent
import android.support.v4.view.NestedScrollingParentHelper
import android.support.v4.view.VelocityTrackerCompat
import android.support.v4.view.ViewCompat
import android.support.v4.view.accessibility.AccessibilityEventCompat
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat
import android.support.v4.view.accessibility.AccessibilityRecordCompat
import android.support.v4.widget.EdgeEffectCompat
import android.support.v4.widget.ScrollerCompat
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.FocusFinder
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.ViewParent
import android.view.accessibility.AccessibilityEvent
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ScrollView

class NestHorizontalScrollView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr), NestedScrollingParent, NestedScrollingChild {

    private var mLastScroll: Long = 0

    private val mTempRect = Rect()
    private var mScroller: ScrollerCompat? = null
    private var mEdgeGlowLeft: EdgeEffectCompat? = null
    private var mEdgeGlowRight: EdgeEffectCompat? = null

    /**
     * Position of the last motion event.
     */
    private var mLastMotionX: Int = 0
    private var mLastMotionY: Int = 0

    /**
     * True when the layout has changed but the traversal has not come through yet.
     * Ideally the view hierarchy would keep track of this for us.
     */
    private var mIsLayoutDirty = true
    private var mIsLaidOut = false

    /**
     * The child to give focus to in the event that a child has requested focus while the
     * layout is dirty. This prevents the scroll from being wrong if the child has not been
     * laid out before requesting focus.
     */
    private var mChildToScrollTo: View? = null

    /**
     * True if the user is currently dragging this ScrollView around. This is
     * not the same as 'is being flinged', which can be checked by
     * mScroller.isFinished() (flinging begins when the user lifts his finger).
     */
    private var mIsBeingDragged = false

    /**
     * Determines speed during touch scrolling
     */
    private var mVelocityTracker: VelocityTracker? = null

    /**
     * When set to true, the scroll view measure its child to make it fill the currently
     * visible area.
     */
    /**
     * Indicates whether this ScrollView's content is stretched to fill the viewport.

     * @return True if the content fills the viewport, false otherwise.
     * *
     * *
     * @attr ref android.R.styleable#ScrollView_fillViewport
     */
    /**
     * Indicates this ScrollView whether it should stretch its content height to fill
     * the viewport or not.

     * @param fillViewport True to stretch the content's height to the viewport's
     * *        boundaries, false otherwise.
     * *
     * *
     * @attr ref android.R.styleable#ScrollView_fillViewport
     */
    var isFillViewport: Boolean = false
        set(fillViewport) {
            if (fillViewport != isFillViewport) {
                field = fillViewport
                requestLayout()
            }
        }

    /**
     * Whether arrow scrolling is animated.
     */
    /**
     * @return Whether arrow scrolling will animate its transition.
     */
    /**
     * Set whether arrow scrolling will animate its transition.
     * @param smoothScrollingEnabled whether arrow scrolling will animate its transition
     */
    var isSmoothScrollingEnabled = true

    private var mTouchSlop: Int = 0
    private var mMinimumVelocity: Int = 0
    private var mMaximumVelocity: Int = 0

    /**
     * ID of the active pointer. This is used to retain consistency during
     * drags/flings if multiple pointers are used.
     */
    private var mActivePointerId = INVALID_POINTER

    /**
     * Used during scrolling to retrieve the new offset within the window.
     */
    private val mScrollOffset = IntArray(2)
    private val mScrollConsumed = IntArray(2)
    private var mNestedXOffset: Int = 0

    private var mSavedState: SavedState? = null

    private val mParentHelper: NestedScrollingParentHelper
    private val mChildHelper: NestedScrollingChildHelper

    private var mHorizontalScrollFactor: Float = 0.toFloat()

    init {
        initScrollView()

        val a = context.obtainStyledAttributes(
            attrs, SCROLLVIEW_STYLEABLE, defStyleAttr, 0)

        isFillViewport = a.getBoolean(0, false)

        a.recycle()

        mParentHelper = NestedScrollingParentHelper(this)
        mChildHelper = NestedScrollingChildHelper(this)

        // ...because why else would you be using this widget?
        isNestedScrollingEnabled = true

        ViewCompat.setAccessibilityDelegate(this, ACCESSIBILITY_DELEGATE)
    }

    // NestedScrollingChild

    override fun setNestedScrollingEnabled(enabled: Boolean) {
        mChildHelper.isNestedScrollingEnabled = enabled
    }

    override fun isNestedScrollingEnabled(): Boolean {
        return mChildHelper.isNestedScrollingEnabled
    }

    override fun startNestedScroll(axes: Int): Boolean {
        return mChildHelper.startNestedScroll(axes)
    }

    override fun stopNestedScroll() {
        mChildHelper.stopNestedScroll()
    }

    override fun hasNestedScrollingParent(): Boolean {
        return mChildHelper.hasNestedScrollingParent()
    }

    override fun dispatchNestedScroll(dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int,
                                      dyUnconsumed: Int, offsetInWindow: IntArray?): Boolean {
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
            offsetInWindow)
    }

    override fun dispatchNestedPreScroll(dx: Int, dy: Int, consumed: IntArray?, offsetInWindow: IntArray?): Boolean {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow)
    }

    override fun dispatchNestedFling(velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed)
    }

    override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float): Boolean {
        return mChildHelper.dispatchNestedPreFling(velocityX, velocityY)
    }

    // NestedScrollingParent

    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        return nestedScrollAxes and ViewCompat.SCROLL_AXIS_HORIZONTAL != 0
    }

    override fun onNestedScrollAccepted(child: View, target: View, nestedScrollAxes: Int) {
        mParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes)
        startNestedScroll(ViewCompat.SCROLL_AXIS_HORIZONTAL)
    }

    override fun onStopNestedScroll(target: View) {
        stopNestedScroll()
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int,
                                dyUnconsumed: Int) {
        val oldScrollX = scrollX
        scrollBy(dxUnconsumed, 0)
        val mxConsumed = scrollX - oldScrollX
        val mxUnconsumed = dxUnconsumed - mxConsumed
        dispatchNestedScroll(mxConsumed, 0, mxUnconsumed, 0, null)
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        // Do nothing
    }

    override fun onNestedFling(target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        if (!consumed) {
            flingWithNestedDispatch(velocityX.toInt())
            return true
        }
        return false
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        // Do nothing
        return false
    }

    override fun getNestedScrollAxes(): Int {
        return mParentHelper.nestedScrollAxes
    }

    // ScrollView import

    override fun shouldDelayChildPressedState(): Boolean {
        return true
    }

    override fun getLeftFadingEdgeStrength(): Float {
        if (childCount == 0) {
            return 0.0f
        }

        val length = horizontalFadingEdgeLength
        val scrollX = scrollX
        if (scrollX < length) {
            return scrollX / length.toFloat()
        }

        return 1.0f
    }

    override fun getRightFadingEdgeStrength(): Float {
        if (childCount == 0) {
            return 0.0f
        }

        val length = horizontalFadingEdgeLength
        val rightEdge = width - paddingRight
        val span = getChildAt(0).right - scrollX - rightEdge
        if (span < length) {
            return span / length.toFloat()
        }

        return 1.0f
    }

    /**
     * @return The maximum amount this scroll view will scroll in response to
     * *   an arrow event.
     */
    val maxScrollAmount: Int
        get() = (MAX_SCROLL_FACTOR * width).toInt()

    private fun initScrollView() {
        mScroller = ScrollerCompat.create(context, null)
        isFocusable = true
        descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS
        setWillNotDraw(false)
        val configuration = ViewConfiguration.get(context)
        mTouchSlop = configuration.scaledTouchSlop
        mMinimumVelocity = configuration.scaledMinimumFlingVelocity
        mMaximumVelocity = configuration.scaledMaximumFlingVelocity
    }

    override fun addView(child: View) {
        if (childCount > 0) {
            throw IllegalStateException("ScrollView can host only one direct child")
        }

        super.addView(child)
    }

    override fun addView(child: View, index: Int) {
        if (childCount > 0) {
            throw IllegalStateException("ScrollView can host only one direct child")
        }

        super.addView(child, index)
    }

    override fun addView(child: View, params: ViewGroup.LayoutParams) {
        if (childCount > 0) {
            throw IllegalStateException("ScrollView can host only one direct child")
        }

        super.addView(child, params)
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        if (childCount > 0) {
            throw IllegalStateException("ScrollView can host only one direct child")
        }

        super.addView(child, index, params)
    }

    /**
     * @return Returns true this ScrollView can be scrolled
     */
    private fun canScroll(): Boolean {
        val child = getChildAt(0)
        if (child != null) {
            val childWidth = child.width
            return width < childWidth + paddingRight + paddingLeft
        }
        return false
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (!isFillViewport) {
            return
        }

        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        if (heightMode == View.MeasureSpec.UNSPECIFIED) {
            return
        }

        if (childCount > 0) {
            val child = getChildAt(0)
            var width = measuredWidth
            if (child.measuredWidth < width) {
                val lp = child.layoutParams as FrameLayout.LayoutParams

                val childHeightMeasureSpec = ViewGroup.getChildMeasureSpec(heightMeasureSpec,
                    paddingTop + paddingBottom, lp.height)
                width -= paddingRight
                width -= paddingLeft
                val childWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)

                child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
            }
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        // Let the focused view and/or our descendants get the key first
        return super.dispatchKeyEvent(event) || executeKeyEvent(event)
    }

    /**
     * You can call this function yourself to have the scroll view perform
     * scrolling from a key event, just as if the event had been dispatched to
     * it by the view hierarchy.

     * @param event The key event to execute.
     * *
     * @return Return true if the event was handled, else false.
     */
    fun executeKeyEvent(event: KeyEvent): Boolean {
        mTempRect.setEmpty()

        if (!canScroll()) {
            if (isFocused && event.keyCode != KeyEvent.KEYCODE_BACK) {
                var currentFocused: View? = findFocus()
                if (currentFocused === this) currentFocused = null
                val nextFocused = FocusFinder.getInstance().findNextFocus(this,
                    currentFocused, View.FOCUS_RIGHT)
                return nextFocused != null
                    && nextFocused !== this
                    && nextFocused.requestFocus(View.FOCUS_RIGHT)
            }
            return false
        }

        var handled = false
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_DPAD_LEFT -> if (!event.isAltPressed) {
                    handled = arrowScroll(View.FOCUS_LEFT)
                } else {
                    handled = fullScroll(View.FOCUS_LEFT)
                }
                KeyEvent.KEYCODE_DPAD_RIGHT -> if (!event.isAltPressed) {
                    handled = arrowScroll(View.FOCUS_RIGHT)
                } else {
                    handled = fullScroll(View.FOCUS_RIGHT)
                }
                KeyEvent.KEYCODE_SPACE -> pageScroll(if (event.isShiftPressed) View.FOCUS_LEFT else View.FOCUS_RIGHT)
            }
        }

        return handled
    }

    private fun inChild(x: Int, y: Int): Boolean {
        if (childCount > 0) {
            val scrollX = scrollX
            val child = getChildAt(0)
            return !(x < child.left - scrollX
                || x >= child.right - scrollX
                || y < child.top
                || y >= child.bottom)
        }
        return false
    }

    private fun initOrResetVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        } else {
            mVelocityTracker!!.clear()
        }
    }

    private fun initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
    }

    private fun recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker!!.recycle()
            mVelocityTracker = null
        }
    }

    override fun requestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        if (disallowIntercept) {
            recycleVelocityTracker()
        }
        super.requestDisallowInterceptTouchEvent(disallowIntercept)
    }


    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        /*
         * This method JUST determines whether we want to intercept the motion.
         * If we return true, onMotionEvent will be called and we do the actual
         * scrolling there.
         */

        /*
        * Shortcut the most recurring case: the user is in the dragging
        * state and he is moving his finger.  We want to intercept this
        * motion.
        */
        val action = ev.action
        if (action == MotionEvent.ACTION_MOVE && mIsBeingDragged) {
            return true
        }

        when (action and MotionEventCompat.ACTION_MASK) {
            MotionEvent.ACTION_MOVE -> {
                /*
                 * mIsBeingDragged == false, otherwise the shortcut would have caught it. Check
                 * whether the user has moved far enough from his original down touch.
                 */

                /*
                * Locally do absolute value. mLastMotionY is set to the y value
                * of the down event.
                */
                val activePointerId = mActivePointerId
                if (activePointerId == INVALID_POINTER) {
                    // If we don't have a valid id, the touch down wasn't on content.
                }

                val pointerIndex = MotionEventCompat.findPointerIndex(ev, activePointerId)
                if (pointerIndex == -1) {
                    Log.e(TAG, "Invalid pointerId=" + activePointerId
                        + " in onInterceptTouchEvent")
                }

                val x = MotionEventCompat.getX(ev, pointerIndex).toInt()
                val y = MotionEventCompat.getY(ev, pointerIndex).toInt()
                val xDiff = Math.abs(x - mLastMotionX)
                val yDiff = Math.abs(y - mLastMotionY)
                if (xDiff > mTouchSlop && xDiff > yDiff
                    && nestedScrollAxes and ViewCompat.SCROLL_AXIS_HORIZONTAL == 0) {
                    mIsBeingDragged = true
                    mLastMotionX = x
                    mLastMotionY = y
                    initVelocityTrackerIfNotExists()
                    mVelocityTracker!!.addMovement(ev)
                    mNestedXOffset = 0
                    val parent = parent
                    parent?.requestDisallowInterceptTouchEvent(true)
                } else {
                    mIsBeingDragged = false
                }
            }

            MotionEvent.ACTION_DOWN -> {
                val x = ev.x.toInt()
                val y = ev.y.toInt()
                if (!inChild(x, y)) {
                    mIsBeingDragged = false
                    recycleVelocityTracker()
                }

                /*
                 * Remember location of down touch.
                 * ACTION_DOWN always refers to pointer index 0.
                 */
                mLastMotionX = x
                mLastMotionY = y
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0)

                initOrResetVelocityTracker()
                mVelocityTracker!!.addMovement(ev)
                /*
                * If being flinged and user touches the screen, initiate drag;
                * otherwise don't.  mScroller.isFinished should be false when
                * being flinged.
                */
                mIsBeingDragged = !mScroller!!.isFinished
                startNestedScroll(ViewCompat.SCROLL_AXIS_HORIZONTAL)
            }

            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                /* Release the drag */
                mIsBeingDragged = false
                mActivePointerId = INVALID_POINTER
                recycleVelocityTracker()
                stopNestedScroll()
            }
            MotionEventCompat.ACTION_POINTER_UP -> onSecondaryPointerUp(ev)
        }

        /*
        * The only time we want to intercept motion events is if we are in the
        * drag mode.
        */
        return mIsBeingDragged
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        initVelocityTrackerIfNotExists()

        val vtev = MotionEvent.obtain(ev)

        val actionMasked = MotionEventCompat.getActionMasked(ev)

        if (actionMasked == MotionEvent.ACTION_DOWN) {
            mNestedXOffset = 0
        }
        vtev.offsetLocation(mNestedXOffset.toFloat(), 0f)

        when (actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                if (childCount == 0) {
                    return false
                }
                if (mIsBeingDragged == !mScroller!!.isFinished) {
                    val parent = parent
                    parent?.requestDisallowInterceptTouchEvent(true)
                }

                /*
                 * If being flinged and user touches, stop the fling. isFinished
                 * will be false if being flinged.
                 */
                if (!mScroller!!.isFinished) {
                    mScroller!!.abortAnimation()
                }

                // Remember where the motion event started
                mLastMotionX = ev.x.toInt()
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0)
                startNestedScroll(ViewCompat.SCROLL_AXIS_HORIZONTAL)
            }
            MotionEvent.ACTION_MOVE -> {
                val activePointerIndex = MotionEventCompat.findPointerIndex(ev,
                    mActivePointerId)
                if (activePointerIndex == -1) {
                    Log.e(TAG, "Invalid pointerId=$mActivePointerId in onTouchEvent")
                }

                val x = MotionEventCompat.getX(ev, activePointerIndex).toInt()
                var deltaX = mLastMotionX - x
                if (dispatchNestedPreScroll(deltaX, 0, mScrollConsumed, mScrollOffset)) {
                    deltaX -= mScrollConsumed[0]
                    vtev.offsetLocation(mScrollOffset[0].toFloat(), 0f)
                    mNestedXOffset += mScrollOffset[0]
                }
                if (!mIsBeingDragged && Math.abs(deltaX) > mTouchSlop) {
                    val parent = parent
                    parent?.requestDisallowInterceptTouchEvent(true)
                    mIsBeingDragged = true
                    if (deltaX > 0) {
                        deltaX -= mTouchSlop
                    } else {
                        deltaX += mTouchSlop
                    }
                }
                if (mIsBeingDragged) {
                    // Scroll to follow the motion event
                    mLastMotionX = x - mScrollOffset[0]

                    val oldX = scrollX
                    val range = scrollRange
                    val overscrollMode = ViewCompat.getOverScrollMode(this)
                    val canOverscroll = overscrollMode == ViewCompat.OVER_SCROLL_ALWAYS || overscrollMode == ViewCompat.OVER_SCROLL_IF_CONTENT_SCROLLS && range > 0

                    // Calling overScrollByCompat will call onOverScrolled, which
                    // calls onScrollChanged if applicable.
                    if (overScrollByCompat(deltaX, 0, scrollX, 0, range, 0, 0,
                        0, true) && !hasNestedScrollingParent()) {
                        // Break our velocity if we hit a scroll barrier.
                        mVelocityTracker!!.clear()
                    }

                    val scrolledDeltaX = scrollX - oldX
                    val unconsumedX = deltaX - scrolledDeltaX
                    if (dispatchNestedScroll(scrolledDeltaX, 0, unconsumedX, 0, mScrollOffset)) {
                        mLastMotionX -= mScrollOffset[0]
                        vtev.offsetLocation(mScrollOffset[0].toFloat(), 0f)
                        mNestedXOffset += mScrollOffset[0]
                    } else if (canOverscroll) {
                        ensureGlows()
                        val pulledToX = oldX + deltaX
                        if (pulledToX < 0) {
                            mEdgeGlowLeft!!.onPull(deltaX.toFloat() / width,
                                MotionEventCompat.getY(ev, activePointerIndex) / height)
                            if (!mEdgeGlowRight!!.isFinished) {
                                mEdgeGlowRight!!.onRelease()
                            }
                        } else if (pulledToX > range) {
                            mEdgeGlowRight!!.onPull(deltaX.toFloat() / width,
                                1f - MotionEventCompat.getY(ev, activePointerIndex) / height)
                            if (!mEdgeGlowLeft!!.isFinished) {
                                mEdgeGlowLeft!!.onRelease()
                            }
                        }
                        if (mEdgeGlowLeft != null && (!mEdgeGlowLeft!!.isFinished || !mEdgeGlowRight!!.isFinished)) {
                            ViewCompat.postInvalidateOnAnimation(this)
                        }
                    }
                }
            }
            MotionEvent.ACTION_UP -> if (mIsBeingDragged) {
                val velocityTracker = mVelocityTracker
                velocityTracker!!.computeCurrentVelocity(1000, mMaximumVelocity.toFloat())
                val initialVelocity = VelocityTrackerCompat.getXVelocity(velocityTracker,
                    mActivePointerId).toInt()

                if (Math.abs(initialVelocity) > mMinimumVelocity) {
                    flingWithNestedDispatch(-initialVelocity)
                }

                mActivePointerId = INVALID_POINTER
                endDrag()
            }
            MotionEvent.ACTION_CANCEL -> if (mIsBeingDragged && childCount > 0) {
                mActivePointerId = INVALID_POINTER
                endDrag()
            }
            MotionEventCompat.ACTION_POINTER_DOWN -> {
                val index = MotionEventCompat.getActionIndex(ev)
                mLastMotionX = MotionEventCompat.getX(ev, index).toInt()
                mActivePointerId = MotionEventCompat.getPointerId(ev, index)
            }
            MotionEventCompat.ACTION_POINTER_UP -> {
                onSecondaryPointerUp(ev)
                mLastMotionX = MotionEventCompat.getX(ev,
                    MotionEventCompat.findPointerIndex(ev, mActivePointerId)).toInt()
            }
        }

        if (mVelocityTracker != null) {
            mVelocityTracker!!.addMovement(vtev)
        }
        vtev.recycle()
        return true
    }

    private fun onSecondaryPointerUp(ev: MotionEvent) {
        val pointerIndex = ev.action and MotionEventCompat.ACTION_POINTER_INDEX_MASK shr MotionEventCompat.ACTION_POINTER_INDEX_SHIFT
        val pointerId = MotionEventCompat.getPointerId(ev, pointerIndex)
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            // TODO: Make this decision more intelligent.
            val newPointerIndex = if (pointerIndex == 0) 1 else 0
            mLastMotionX = MotionEventCompat.getX(ev, newPointerIndex).toInt()
            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex)
            if (mVelocityTracker != null) {
                mVelocityTracker!!.clear()
            }
        }
    }

    override fun onGenericMotionEvent(event: MotionEvent): Boolean {
        if (MotionEventCompat.getSource(event) and InputDeviceCompat.SOURCE_CLASS_POINTER != 0) {
            when (event.action) {
                MotionEventCompat.ACTION_SCROLL -> {
                    if (!mIsBeingDragged) {
                        val hscroll = MotionEventCompat.getAxisValue(event,
                            MotionEventCompat.AXIS_HSCROLL)
                        if (hscroll != 0f) {
                            val delta = (hscroll * horizontalScrollFactorCompat).toInt()
                            val range = scrollRange
                            val oldScrollX = scrollX
                            var newScrollX = oldScrollX - delta
                            if (newScrollX < 0) {
                                newScrollX = 0
                            } else if (newScrollX > range) {
                                newScrollX = range
                            }
                            if (newScrollX != oldScrollX) {
                                super.scrollTo(newScrollX, scrollY)
                                return true
                            }
                        }
                    }
                }
            }
        }
        return false
    }

    private val horizontalScrollFactorCompat: Float
        get() {
            if (mHorizontalScrollFactor == 0f) {
                val outValue = TypedValue()
                val context = context
                if (!context.theme.resolveAttribute(
                    android.R.attr.listPreferredItemHeight, outValue, true)) {
                    throw IllegalStateException(
                        "Expected theme to define listPreferredItemHeight.")
                }
                mHorizontalScrollFactor = outValue.getDimension(
                    context.resources.displayMetrics)
            }
            return mHorizontalScrollFactor
        }

    override fun onOverScrolled(scrollX: Int, scrollY: Int,
                                clampedX: Boolean, clampedY: Boolean) {
        super.scrollTo(scrollX, scrollY)
    }

    internal fun overScrollByCompat(deltaX: Int, deltaY: Int,
                                    scrollX: Int, scrollY: Int,
                                    scrollRangeX: Int, scrollRangeY: Int,
                                    maxOverScrollX: Int, maxOverScrollY: Int,
                                    isTouchEvent: Boolean): Boolean {
        var maxOverScrollX = maxOverScrollX
        var maxOverScrollY = maxOverScrollY
        val overScrollMode = ViewCompat.getOverScrollMode(this)
        val canScrollHorizontal = computeHorizontalScrollRange() > computeHorizontalScrollExtent()
        val canScrollVertical = computeVerticalScrollRange() > computeVerticalScrollExtent()
        val overScrollHorizontal = overScrollMode == ViewCompat.OVER_SCROLL_ALWAYS || overScrollMode == ViewCompat.OVER_SCROLL_IF_CONTENT_SCROLLS && canScrollHorizontal
        val overScrollVertical = overScrollMode == ViewCompat.OVER_SCROLL_ALWAYS || overScrollMode == ViewCompat.OVER_SCROLL_IF_CONTENT_SCROLLS && canScrollVertical

        var newScrollX = scrollX + deltaX
        if (!overScrollHorizontal) {
            maxOverScrollX = 0
        }

        var newScrollY = scrollY + deltaY
        if (!overScrollVertical) {
            maxOverScrollY = 0
        }

        // Clamp values if at the limits and record
        val left = -maxOverScrollX
        val right = maxOverScrollX + scrollRangeX
        val top = -maxOverScrollY
        val bottom = maxOverScrollY + scrollRangeY

        var clampedX = false
        if (newScrollX > right) {
            newScrollX = right
            clampedX = true
        } else if (newScrollX < left) {
            newScrollX = left
            clampedX = true
        }

        var clampedY = false
        if (newScrollY > bottom) {
            newScrollY = bottom
            clampedY = true
        } else if (newScrollY < top) {
            newScrollY = top
            clampedY = true
        }

        onOverScrolled(newScrollX, newScrollY, clampedX, clampedY)

        return clampedX || clampedY
    }

    private val scrollRange: Int
        get() {
            var scrollRange = 0
            if (childCount > 0) {
                val child = getChildAt(0)
                scrollRange = Math.max(0,
                    child.width - (width - paddingLeft - paddingRight))
            }
            return scrollRange
        }

    /**
     *
     *
     * Finds the next focusable component that fits in the specified bounds.
     *

     * @param topFocus look for a candidate is the one at the top of the bounds
     * *                 if topFocus is true, or at the bottom of the bounds if topFocus is
     * *                 false
     * *
     * @param left      the left offset of the bounds in which a focusable must be
     * *                 found
     * *
     * @param right   the right offset of the bounds in which a focusable must
     * *                 be found
     * *
     * @return the next focusable component in the bounds or null if none can
     * *         be found
     */
    private fun findFocusableViewInBounds(topFocus: Boolean, left: Int, right: Int): View {

        val focusables = getFocusables(View.FOCUS_FORWARD)
        var focusCandidate: View? = null

        /*
         * A fully contained focusable is one where its top is below the bound's
         * top, and its bottom is above the bound's bottom. A partially
         * contained focusable is one where some part of it is within the
         * bounds, but it also has some part that is not within bounds.  A fully contained
         * focusable is preferred to a partially contained focusable.
         */
        var foundFullyContainedFocusable = false

        val count = focusables.size
        for (i in 0..count - 1) {
            val view = focusables[i]
            val viewLeft = view.left
            val viewRight = view.right

            if (left < viewRight && viewLeft < right) {
                /*
                 * the focusable is in the target area, it is a candidate for
                 * focusing
                 */

                val viewIsFullyContained = left < viewLeft && viewRight < right

                if (focusCandidate == null) {
                    /* No candidate, take this one */
                    focusCandidate = view
                    foundFullyContainedFocusable = viewIsFullyContained
                } else {
                    val viewIsCloserToBoundary = topFocus && viewLeft < focusCandidate.left || !topFocus && viewRight > focusCandidate
                        .right

                    if (foundFullyContainedFocusable) {
                        if (viewIsFullyContained && viewIsCloserToBoundary) {
                            /*
                             * We're dealing with only fully contained views, so
                             * it has to be closer to the boundary to beat our
                             * candidate
                             */
                            focusCandidate = view
                        }
                    } else {
                        if (viewIsFullyContained) {
                            /* Any fully contained view beats a partially contained view */
                            focusCandidate = view
                            foundFullyContainedFocusable = true
                        } else if (viewIsCloserToBoundary) {
                            /*
                             * Partially contained view beats another partially
                             * contained view if it's closer
                             */
                            focusCandidate = view
                        }
                    }
                }
            }
        }

        return focusCandidate!!
    }

    /**
     *
     * Handles scrolling in response to a "page up/down" shortcut press. This
     * method will scroll the view by one page up or down and give the focus
     * to the topmost/bottommost component in the new visible area. If no
     * component is a good candidate for focus, this scrollview reclaims the
     * focus.

     * @param direction the scroll direction: [View.FOCUS_UP]
     * *                  to go one page up or
     * *                  [View.FOCUS_RIGHT] to go one page down
     * *
     * @return true if the key event is consumed by this method, false otherwise
     */
    fun pageScroll(direction: Int): Boolean {
        val toRight = direction == View.FOCUS_RIGHT
        val width = width

        if (toRight) {
            mTempRect.left = scrollX + width
            val count = childCount
            if (count > 0) {
                val view = getChildAt(count - 1)
                if (mTempRect.left + width > view.right) {
                    mTempRect.left = view.right - width
                }
            }
        } else {
            mTempRect.left = scrollX - width
            if (mTempRect.left < 0) {
                mTempRect.left = 0
            }
        }
        mTempRect.right = mTempRect.left + width

        return scrollAndFocus(direction, mTempRect.left, mTempRect.right)
    }

    /**
     *
     * Handles scrolling in response to a "home/end" shortcut press. This
     * method will scroll the view to the top or bottom and give the focus
     * to the topmost/bottommost component in the new visible area. If no
     * component is a good candidate for focus, this scrollview reclaims the
     * focus.

     * @param direction the scroll direction: [View.FOCUS_UP]
     * *                  to go the top of the view or
     * *                  [View.FOCUS_DOWN] to go the bottom
     * *
     * @return true if the key event is consumed by this method, false otherwise
     */
    fun fullScroll(direction: Int): Boolean {
        val right = direction == View.FOCUS_RIGHT
        val width = width

        mTempRect.left = 0
        mTempRect.right = width

        if (right) {
            val count = childCount
            if (count > 0) {
                val view = getChildAt(count - 1)
                mTempRect.right = view.right + paddingRight
                mTempRect.left = mTempRect.right - width
            }
        }

        return scrollAndFocus(direction, mTempRect.left, mTempRect.right)
    }

    /**
     *
     * Scrolls the view to make the area defined by `top` and
     * `bottom` visible. This method attempts to give the focus
     * to a component visible in this area. If no component can be focused in
     * the new visible area, the focus is reclaimed by this ScrollView.

     * @param direction the scroll direction: [View.FOCUS_UP]
     * *                  to go upward, [View.FOCUS_DOWN] to downward
     * *
     * @param left       the left offset of the new area to be made visible
     * *
     * @param right    the right offset of the new area to be made visible
     * *
     * @return true if the key event is consumed by this method, false otherwise
     */
    private fun scrollAndFocus(direction: Int, left: Int, right: Int): Boolean {
        var handled = true

        val width = width
        val containerLeft = scrollX
        val containerRight = containerLeft + width
        val toLeft = direction == View.FOCUS_LEFT

        var newFocused: View? = findFocusableViewInBounds(toLeft, left, right)
        if (newFocused == null) {
            newFocused = this
        }

        if (left >= containerLeft && right <= containerRight) {
            handled = false
        } else {
            val delta = if (toLeft) left - containerLeft else right - containerRight
            doScrollX(delta)
        }

        if (newFocused !== findFocus()) newFocused.requestFocus(direction)

        return handled
    }

    /**
     * Handle scrolling in response to an up or down arrow click.

     * @param direction The direction corresponding to the arrow key that was
     * *                  pressed
     * *
     * @return True if we consumed the event, false otherwise
     */
    fun arrowScroll(direction: Int): Boolean {

        var currentFocused: View? = findFocus()
        if (currentFocused === this) currentFocused = null

        val nextFocused = FocusFinder.getInstance().findNextFocus(this, currentFocused, direction)

        val maxJump = maxScrollAmount

        if (nextFocused != null && isWithinDeltaOfScreen(nextFocused, maxJump, height)) {
            nextFocused.getDrawingRect(mTempRect)
            offsetDescendantRectToMyCoords(nextFocused, mTempRect)
            val scrollDelta = computeScrollDeltaToGetChildRectOnScreen(mTempRect)
            doScrollX(scrollDelta)
            nextFocused.requestFocus(direction)
        } else {
            // no new focus
            var scrollDelta = maxJump

            if (direction == View.FOCUS_LEFT && scrollX < scrollDelta) {
                scrollDelta = scrollX
            } else if (direction == View.FOCUS_RIGHT) {
                if (childCount > 0) {
                    val daRight = getChildAt(0).right
                    val screenRight = scrollX + width - paddingRight
                    if (daRight - screenRight < maxJump) {
                        scrollDelta = daRight - screenRight
                    }
                }
            }
            if (scrollDelta == 0) {
                return false
            }
            doScrollX(if (direction == View.FOCUS_RIGHT) scrollDelta else -scrollDelta)
        }

        if (currentFocused != null && currentFocused.isFocused
            && isOffScreen(currentFocused)) {
            // previously focused item still has focus and is off screen, give
            // it up (take it back to ourselves)
            // (also, need to temporarily force FOCUS_BEFORE_DESCENDANTS so we are
            // sure to
            // get it)
            val descendantFocusability = descendantFocusability  // save
            setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS)
            requestFocus()
            setDescendantFocusability(descendantFocusability)  // restore
        }
        return true
    }

    /**
     * @return whether the descendant of this scroll view is scrolled off
     * *  screen.
     */
    private fun isOffScreen(descendant: View): Boolean {
        return !isWithinDeltaOfScreen(descendant, 0, width)
    }

    /**
     * @return whether the descendant of this scroll view is within delta
     * *  pixels of being on the screen.
     */
    private fun isWithinDeltaOfScreen(descendant: View, delta: Int, width: Int): Boolean {
        descendant.getDrawingRect(mTempRect)
        offsetDescendantRectToMyCoords(descendant, mTempRect)

        return mTempRect.right + delta >= scrollX && mTempRect.left - delta <= scrollX + width
    }

    /**
     * Smooth scroll by a X delta

     * @param delta the number of pixels to scroll by on the X axis
     */
    private fun doScrollX(delta: Int) {
        if (delta != 0) {
            if (isSmoothScrollingEnabled) {
                smoothScrollBy(delta, 0)
            } else {
                scrollBy(delta, 0)
            }
        }
    }

    /**
     * Like [View.scrollBy], but scroll smoothly instead of immediately.

     * @param dx the number of pixels to scroll by on the X axis
     * *
     * @param dy the number of pixels to scroll by on the Y axis
     */
    fun smoothScrollBy(dx: Int, dy: Int) {
        var dx = dx
        if (childCount == 0) {
            // Nothing to do.
            return
        }
        val duration = AnimationUtils.currentAnimationTimeMillis() - mLastScroll
        if (duration > ANIMATED_SCROLL_GAP) {
            val width = width - paddingLeft - paddingRight
            val right = getChildAt(0).width
            val maxX = Math.max(0, right - width)
            val scrollX = scrollX
            dx = Math.max(0, Math.min(scrollX + dx, maxX)) - scrollX

            mScroller!!.startScroll(scrollX, scrollY, dx, 0)
            ViewCompat.postInvalidateOnAnimation(this)
        } else {
            if (!mScroller!!.isFinished) {
                mScroller!!.abortAnimation()
            }
            scrollBy(dx, dy)
        }
        mLastScroll = AnimationUtils.currentAnimationTimeMillis()
    }

    /**
     * Like [.scrollTo], but scroll smoothly instead of immediately.

     * @param x the position where to scroll on the X axis
     * *
     * @param y the position where to scroll on the Y axis
     */
    fun smoothScrollTo(x: Int, y: Int) {
        smoothScrollBy(x - scrollX, y - scrollY)
    }

    override fun computeHorizontalScrollRange(): Int {
        val count = childCount
        val contentWidth = width - paddingLeft - paddingRight
        if (count == 0) {
            return contentWidth
        }

        var scrollRange = getChildAt(0).right
        val scrollX = scrollX
        val overscrollRight = Math.max(0, scrollRange - contentWidth)
        if (scrollX < 0) {
            scrollRange -= scrollX
        } else if (scrollX > overscrollRight) {
            scrollRange += scrollX - overscrollRight
        }

        return scrollRange
    }

    override fun computeHorizontalScrollOffset(): Int {
        return Math.max(0, super.computeHorizontalScrollOffset())
    }

    override fun measureChild(child: View, parentWidthMeasureSpec: Int, parentHeightMeasureSpec: Int) {
        val lp = child.layoutParams

        val childWidthMeasureSpec: Int
        val childHeightMeasureSpec: Int

        childHeightMeasureSpec = ViewGroup.getChildMeasureSpec(parentHeightMeasureSpec, paddingTop + paddingBottom, lp.height)

        childWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
    }

    override fun measureChildWithMargins(child: View, parentWidthMeasureSpec: Int, widthUsed: Int,
                                         parentHeightMeasureSpec: Int, heightUsed: Int) {
        val lp = child.layoutParams as ViewGroup.MarginLayoutParams

        val childHeightMeasureSpec = ViewGroup.getChildMeasureSpec(parentHeightMeasureSpec,
            paddingTop + paddingBottom + lp.topMargin + lp.bottomMargin
                + heightUsed, lp.height)
        val childWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(
            lp.leftMargin + lp.rightMargin, View.MeasureSpec.UNSPECIFIED)

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
    }

    override fun computeScroll() {
        if (mScroller!!.computeScrollOffset()) {
            val oldX = scrollX
            val oldY = scrollY
            val x = mScroller!!.currX
            val y = mScroller!!.currY

            if (oldX != x || oldY != y) {
                val range = scrollRange
                val overscrollMode = ViewCompat.getOverScrollMode(this)
                val canOverscroll = overscrollMode == ViewCompat.OVER_SCROLL_ALWAYS || overscrollMode == ViewCompat.OVER_SCROLL_IF_CONTENT_SCROLLS && range > 0

                overScrollByCompat(x - oldX, y - oldY, oldX, oldY, range, 0,
                    0, 0, false)

                if (canOverscroll) {
                    ensureGlows()
                    if (x <= 0 && oldX > 0) {
                        mEdgeGlowLeft!!.onAbsorb(mScroller!!.currVelocity.toInt())
                    } else if (x >= range && oldX < range) {
                        mEdgeGlowRight!!.onAbsorb(mScroller!!.currVelocity.toInt())
                    }
                }
            }
        }
    }

    /**
     * Scrolls the view to the given child.

     * @param child the View to scroll to
     */
    private fun scrollToChild(child: View) {
        child.getDrawingRect(mTempRect)

        /* Offset from child's local coordinates to ScrollView coordinates */
        offsetDescendantRectToMyCoords(child, mTempRect)

        val scrollDelta = computeScrollDeltaToGetChildRectOnScreen(mTempRect)

        if (scrollDelta != 0) {
            scrollBy(scrollDelta, 0)
        }
    }

    /**
     * If rect is off screen, scroll just enough to get it (or at least the
     * first screen size chunk of it) on screen.

     * @param rect      The rectangle.
     * *
     * @param immediate True to scroll immediately without animation
     * *
     * @return true if scrolling was performed
     */
    private fun scrollToChildRect(rect: Rect, immediate: Boolean): Boolean {
        val delta = computeScrollDeltaToGetChildRectOnScreen(rect)
        val scroll = delta != 0
        if (scroll) {
            if (immediate) {
                scrollBy(delta, 0)
            } else {
                smoothScrollBy(delta, 0)
            }
        }
        return scroll
    }

    /**
     * Compute the amount to scroll in the Y direction in order to get
     * a rectangle completely on the screen (or, if taller than the screen,
     * at least the first screen size chunk of it).

     * @param rect The rect.
     * *
     * @return The scroll delta.
     */
    protected fun computeScrollDeltaToGetChildRectOnScreen(rect: Rect): Int {
        if (childCount == 0) return 0

        val width = width
        var screenLeft = scrollX
        var screenRight = screenLeft + width

        val fadingEdge = horizontalFadingEdgeLength

        // leave room for top fading edge as long as rect isn't at very top
        if (rect.left > 0) {
            screenLeft += fadingEdge
        }

        // leave room for bottom fading edge as long as rect isn't at very bottom
        if (rect.right < getChildAt(0).width) {
            screenRight -= fadingEdge
        }

        var scrollXDelta = 0

        if (rect.right > screenRight && rect.left > screenLeft) {
            // need to move down to get it in view: move down just enough so
            // that the entire rectangle is in view (or at least the first
            // screen size chunk).

            if (rect.width() > width) {
                // just enough to get screen size chunk on
                scrollXDelta += rect.left - screenLeft
            } else {
                // get entire rect at bottom of screen
                scrollXDelta += rect.right - screenRight
            }

            // make sure we aren't scrolling beyond the end of our content
            val right = getChildAt(0).right
            val distanceToRight = right - screenRight
            scrollXDelta = Math.min(scrollXDelta, distanceToRight)

        } else if (rect.left < screenLeft && rect.right < screenRight) {
            // need to move up to get it in view: move up just enough so that
            // entire rectangle is in view (or at least the first screen
            // size chunk of it).

            if (rect.width() > width) {
                // screen size chunk
                scrollXDelta -= screenRight - rect.right
            } else {
                // entire rect at top
                scrollXDelta -= screenLeft - rect.left
            }

            // make sure we aren't scrolling any further than the top our content
            scrollXDelta = Math.max(scrollXDelta, -scrollX)
        }
        return scrollXDelta
    }

    override fun requestChildFocus(child: View, focused: View) {
        if (!mIsLayoutDirty) {
            scrollToChild(focused)
        } else {
            // The child may not be laid out yet, we can't compute the scroll yet
            mChildToScrollTo = focused
        }
        super.requestChildFocus(child, focused)
    }


    /**
     * When looking for focus in children of a scroll view, need to be a little
     * more careful not to give focus to something that is scrolled off screen.

     * This is more expensive than the default [ViewGroup]
     * implementation, otherwise this behavior might have been made the default.
     */
    override fun onRequestFocusInDescendants(direction: Int,
                                             previouslyFocusedRect: Rect?): Boolean {
        var direction = direction

        // convert from forward / backward notation to up / down / left / right
        // (ugh).
        if (direction == View.FOCUS_FORWARD) {
            direction = View.FOCUS_RIGHT
        } else if (direction == View.FOCUS_BACKWARD) {
            direction = View.FOCUS_LEFT
        }

        val nextFocus = (if (previouslyFocusedRect == null)
            FocusFinder.getInstance().findNextFocus(this, null, direction)
        else
            FocusFinder.getInstance().findNextFocusFromRect(this,
                previouslyFocusedRect, direction)) ?: return false

        if (isOffScreen(nextFocus)) {
            return false
        }

        return nextFocus.requestFocus(direction, previouslyFocusedRect)
    }

    override fun requestChildRectangleOnScreen(child: View, rectangle: Rect,
                                               immediate: Boolean): Boolean {
        // offset into coordinate space of this scroll view
        rectangle.offset(child.left - child.scrollX,
            child.top - child.scrollY)

        return scrollToChildRect(rectangle, immediate)
    }

    @SuppressLint("MissingSuperCall")
    override fun requestLayout() {
        mIsLayoutDirty = true
        super.requestLayout()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        mIsLayoutDirty = false
        // Give a child focus if it needs it
        if (mChildToScrollTo != null && isViewDescendantOf(mChildToScrollTo!!, this)) {
            scrollToChild(mChildToScrollTo!!)
        }
        mChildToScrollTo = null

        if (!mIsLaidOut) {
            if (mSavedState != null) {
                scrollTo(mSavedState!!.scrollPosition, scrollY)
                mSavedState = null
            } // mScrollY default value is "0"

            val childWidth = if (childCount > 0) getChildAt(0).measuredWidth else 0
            val scrollRange = Math.max(0,
                childWidth - (r - l - paddingRight - paddingLeft))

            // Don't forget to clamp
            if (scrollX > scrollRange) {
                scrollTo(scrollRange, scrollY)
            } else if (scrollX < 0) {
                scrollTo(0, scrollY)
            }
        }

        // Calling this with the present values causes it to re-claim them
        scrollTo(scrollX, scrollY)
        mIsLaidOut = true
    }

    @SuppressLint("MissingSuperCall")
    public override fun onAttachedToWindow() {
        mIsLaidOut = false
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val currentFocused = findFocus()
        if (null == currentFocused || this === currentFocused)
            return

        // If the currently-focused view was visible on the screen when the
        // screen was at the old height, then scroll the screen to make that
        // view visible with the new screen height.
        if (isWithinDeltaOfScreen(currentFocused, 0, oldw)) {
            currentFocused.getDrawingRect(mTempRect)
            offsetDescendantRectToMyCoords(currentFocused, mTempRect)
            val scrollDelta = computeScrollDeltaToGetChildRectOnScreen(mTempRect)
            doScrollX(scrollDelta)
        }
    }

    /**
     * Fling the scroll view

     * @param velocityX The initial velocity in the X direction. Positive
     * *                  numbers mean that the finger/cursor is moving down the screen,
     * *                  which means we want to scroll towards the top.
     */
    fun fling(velocityX: Int) {
        if (childCount > 0) {
            val width = width - paddingRight - paddingLeft
            val right = getChildAt(0).width

            mScroller!!.fling(scrollX, scrollY, velocityX, 0, 0, Math.max(0, right - width), 0,
                0, width / 2, 0)

            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    private fun flingWithNestedDispatch(velocityX: Int) {
        val scrollX = scrollX
        val canFling = (scrollX > 0 || velocityX > 0) && (scrollX < scrollRange || velocityX < 0)
        if (!dispatchNestedPreFling(velocityX.toFloat(), 0f)) {
            dispatchNestedFling(velocityX.toFloat(), 0f, canFling)
            if (canFling) {
                fling(velocityX)
            }
        }
    }

    private fun endDrag() {
        mIsBeingDragged = false

        recycleVelocityTracker()
        stopNestedScroll()

        if (mEdgeGlowLeft != null) {
            mEdgeGlowLeft!!.onRelease()
            mEdgeGlowRight!!.onRelease()
        }
    }

    /**
     * {@inheritDoc}

     *
     * This version also clamps the scrolling to the bounds of our child.
     */
    override fun scrollTo(x: Int, y: Int) {
        var x = x
        var y = y
        // we rely on the fact the View.scrollBy calls scrollTo.
        if (childCount > 0) {
            val child = getChildAt(0)
            x = clamp(x, width - paddingRight - paddingLeft, child.width)
            y = clamp(y, height - paddingBottom - paddingTop, child.height)
            if (x != scrollX || y != scrollY) {
                super.scrollTo(x, y)
            }
        }
    }

    private fun ensureGlows() {
        if (ViewCompat.getOverScrollMode(this) != ViewCompat.OVER_SCROLL_NEVER) {
            if (mEdgeGlowLeft == null) {
                val context = context
                mEdgeGlowLeft = EdgeEffectCompat(context)
                mEdgeGlowRight = EdgeEffectCompat(context)

            }
        } else {
            mEdgeGlowLeft = null
            mEdgeGlowRight = null
        }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        if (mEdgeGlowLeft != null) {
            val scrollX = scrollX
            if (!mEdgeGlowLeft!!.isFinished) {
                val restoreCount = canvas.save()
                val height = height - paddingTop - paddingBottom

                canvas.translate(Math.min(0, scrollX).toFloat(), paddingTop.toFloat())
                canvas.rotate(90f, 0f, height.toFloat())
                mEdgeGlowLeft!!.setSize(width, height)
                if (mEdgeGlowLeft!!.draw(canvas)) {
                    ViewCompat.postInvalidateOnAnimation(this)
                }
                canvas.restoreToCount(restoreCount)
            }
            if (!mEdgeGlowRight!!.isFinished) {
                val restoreCount = canvas.save()
                val width = width
                val height = height - paddingTop - paddingBottom

                canvas.translate((Math.max(scrollRange, scrollX) + width).toFloat(),
                    (-height + paddingTop).toFloat())
                canvas.rotate(90f, 0f, height.toFloat())
                mEdgeGlowRight!!.setSize(width, height)
                if (mEdgeGlowRight!!.draw(canvas)) {
                    ViewCompat.postInvalidateOnAnimation(this)
                }
                canvas.restoreToCount(restoreCount)
            }
        }
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val ss = state as SavedState
        super.onRestoreInstanceState(ss.superState)
        mSavedState = ss
        requestLayout()
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val ss = SavedState(superState)
        ss.scrollPosition = scrollX
        return ss
    }

    internal class SavedState : View.BaseSavedState {
        var scrollPosition: Int = 0

        constructor(superState: Parcelable) : super(superState) {}

        constructor(source: Parcel) : super(source) {
            scrollPosition = source.readInt()
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            super.writeToParcel(dest, flags)
            dest.writeInt(scrollPosition)
        }

        override fun toString(): String {
            return "HorizontalScrollView.SavedState{${Integer.toHexString(System.identityHashCode(this))} scrollPosition=${scrollPosition}}"
        }

        companion object {

            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(`in`: Parcel): SavedState {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    internal class AccessibilityDelegate : AccessibilityDelegateCompat() {
        override fun performAccessibilityAction(host: View, action: Int, arguments: Bundle): Boolean {
            if (super.performAccessibilityAction(host, action, arguments)) {
                return true
            }
            val nsvHost = host as NestHorizontalScrollView
            if (!nsvHost.isEnabled) {
                return false
            }
            when (action) {
                AccessibilityNodeInfoCompat.ACTION_SCROLL_FORWARD -> {
                    run {
                        val viewportWidth = nsvHost.width - nsvHost.paddingRight
                        -nsvHost.paddingLeft
                        val targetScrollX = Math.min(nsvHost.scrollX + viewportWidth,
                            nsvHost.scrollRange)
                        if (targetScrollX != nsvHost.scrollX) {
                            nsvHost.smoothScrollTo(targetScrollX, 0)
                            return true
                        }
                    }
                    return false
                }
                AccessibilityNodeInfoCompat.ACTION_SCROLL_BACKWARD -> {
                    run {
                        val viewportWidth = nsvHost.width - nsvHost.paddingRight
                        -nsvHost.paddingLeft
                        val targetScrollX = Math.max(nsvHost.scrollX - viewportWidth, 0)
                        if (targetScrollX != nsvHost.scrollX) {
                            nsvHost.smoothScrollTo(0, targetScrollX)
                            return true
                        }
                    }
                    return false
                }
            }
            return false
        }

        override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfoCompat) {
            super.onInitializeAccessibilityNodeInfo(host, info)
            val nsvHost = host as NestHorizontalScrollView
            info.className = ScrollView::class.java.name
            if (nsvHost.isEnabled) {
                val scrollRange = nsvHost.scrollRange
                if (scrollRange > 0) {
                    info.isScrollable = true
                    if (nsvHost.scrollX > 0) {
                        info.addAction(AccessibilityNodeInfoCompat.ACTION_SCROLL_BACKWARD)
                    }
                    if (nsvHost.scrollX < scrollRange) {
                        info.addAction(AccessibilityNodeInfoCompat.ACTION_SCROLL_FORWARD)
                    }
                }
            }
        }

        override fun onInitializeAccessibilityEvent(host: View, event: AccessibilityEvent) {
            super.onInitializeAccessibilityEvent(host, event)
            val nsvHost = host as NestHorizontalScrollView
            event.className = ScrollView::class.java.name
            val record = AccessibilityEventCompat.asRecord(event)
            val scrollable = nsvHost.scrollRange > 0
            record.isScrollable = scrollable
            record.scrollX = nsvHost.scrollX
            record.scrollY = nsvHost.scrollY
            record.maxScrollX = nsvHost.scrollRange
            record.maxScrollY = nsvHost.scrollY
        }
    }

    companion object {
        internal val ANIMATED_SCROLL_GAP = 250

        internal val MAX_SCROLL_FACTOR = 0.5f

        private val TAG = "NestedScrollView"

        /**
         * Sentinel value for no current active pointer.
         * Used by [.mActivePointerId].
         */
        private val INVALID_POINTER = -1

        private val ACCESSIBILITY_DELEGATE = AccessibilityDelegate()

        private val SCROLLVIEW_STYLEABLE = intArrayOf(android.R.attr.fillViewport)

        /**
         * Return true if child is a descendant of parent, (or equal to the parent).
         */
        private fun isViewDescendantOf(child: View, parent: View): Boolean {
            if (child === parent) {
                return true
            }

            val theParent = child.parent
            return theParent is ViewGroup && isViewDescendantOf(theParent as View, parent)
        }

        private fun clamp(n: Int, my: Int, child: Int): Int {
            if (my >= child || n < 0) {
                /* my >= child is this case:
             *                    |--------------- me ---------------|
             *     |------ child ------|
             * or
             *     |--------------- me ---------------|
             *            |------ child ------|
             * or
             *     |--------------- me ---------------|
             *                                  |------ child ------|
             *
             * n < 0 is this case:
             *     |------ me ------|
             *                    |-------- child --------|
             *     |-- mScrollX --|
             */
                return 0
            }
            if (my + n > child) {
                /* this case:
             *                    |------ me ------|
             *     |------ child ------|
             *     |-- mScrollX --|
             */
                return child - my
            }
            return n
        }
    }
}
