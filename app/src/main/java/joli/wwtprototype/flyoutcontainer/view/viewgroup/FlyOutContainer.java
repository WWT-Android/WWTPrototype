package joli.wwtprototype.flyoutcontainer.view.viewgroup;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.Scroller;


/**
 * Created by Jasmine on 5/9/2016.
 * Reference: https://www.youtube.com/watch?v=YeR7McJIltk
 */
public class FlyOutContainer extends LinearLayout {


    /* References to groups contained in this view */
    private View menu;          // flyout menu view
    private View content;       // main content view

    /* Constants */
    protected int currentContentOffset = 0;
    protected static final int menuMargin = 70;    // amount of main content visible
                                                    // when menu is open (pix)
    public enum MenuState{
        CLOSED, OPEN, CLOSING, OPENING;
    }

    /* Position Information Attributes*/
    protected int currentContentOffSet = 0;
    protected MenuState menuCurrentState = MenuState.CLOSED;

    /* Animation objects */
    protected Scroller menuAnimationScroller = new Scroller(this.getContext(), new LinearInterpolator());     // keeps track of view in UI (Interpolator determines speed of animation)
    protected Runnable menuAnimationRunnable = new AnimationRunnable();     // dispatch update view of UI
    protected Handler menuAnimationHandler = new Handler(); // executes the runnable

    /* Animation Constants */
    private static final int menuAnimationDuration = 1000;
    private static final int menuAnimationPollingInterval = 16;

    /* Constructors */
    public FlyOutContainer(Context context, AttributeSet attrs){
        super(context, attrs);
    }
    public FlyOutContainer(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }
    public FlyOutContainer(Context context){
        super(context);
    }

    @Override
    protected void onAttachedToWindow(){
        super.onAttachedToWindow();

        this.menu = this.getChildAt(0);
        this.content = this.getChildAt(1);

        this.menu.setVisibility(View.GONE);     // gone when menu is closed
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom){
        // if size of flyout container has changed since last call
        if(changed)
            this.calculateChildDimensions();

        this.menu.layout(left, top, right - menuMargin, bottom);

        this.content.layout(left + this.currentContentOffset, top, right
                + this.currentContentOffset, bottom);
    }

    public void toggleMenu() {
        switch (this.menuCurrentState) {
            case CLOSED:
                this.menuCurrentState = MenuState.OPENING;
                this.menu.setVisibility(View.VISIBLE);
                this.menuAnimationScroller.startScroll(0, 0, this.getMenuWidth(), 0, menuAnimationDuration);
                //this.menu.setVisibility(View.VISIBLE);
                //this.currentContentOffset = this.getMenuWidth();
                //this.content.offsetLeftAndRight(currentContentOffset);
                //this.menuCurrentState = MenuState.OPEN;
                break;
            case OPEN:
                this.menuCurrentState = MenuState.CLOSING;
                this.menu.setVisibility(View.VISIBLE);
                this.menuAnimationScroller.startScroll(this.currentContentOffset, 0, -this.currentContentOffset, 0, menuAnimationDuration);
                //this.content.offsetLeftAndRight(-currentContentOffset);
                //this.currentContentOffset = 0;
                //this.menuCurrentState = MenuState.CLOSED;
                //this.menu.setVisibility(View.GONE);
                break;
            default:
                return;

        }

        this.menuAnimationHandler.postDelayed(this.menuAnimationRunnable,
                menuAnimationPollingInterval);

        this.invalidate();      // tell android to redraw container (there's been a change)
    }

    private int getMenuWidth() {
        return this.menu.getLayoutParams().width;
    }

    /* sets height and width of content and menu */
    private void calculateChildDimensions() {
        this.content.getLayoutParams().height = this.getHeight();
        this.content.getLayoutParams().width = this.getWidth();

        this.menu.getLayoutParams().width = this.getWidth() - menuMargin;
        this.menu.getLayoutParams().height = this.getHeight();
    }

    private void adjustContentPosition(boolean isAnimationOngoing) {
        int scrollerOffset = this.menuAnimationScroller.getCurrX();

        this.content.offsetLeftAndRight(scrollerOffset
                - this.currentContentOffset);

        this.currentContentOffset = scrollerOffset;

        this.invalidate();

        if (isAnimationOngoing)
            this.menuAnimationHandler.postDelayed(this.menuAnimationRunnable,
                    menuAnimationPollingInterval);
        else
            this.onMenuTransitionComplete();
    }

    private void onMenuTransitionComplete() {
        switch (this.menuCurrentState) {
            case OPENING:
                this.menuCurrentState = MenuState.OPEN;
                break;
            case CLOSING:
                this.menuCurrentState = MenuState.CLOSED;
                this.menu.setVisibility(View.GONE);
                break;
            default:
                return;
        }
    }

    protected class SmoothInterpolator implements Interpolator{

        @Override
        public float getInterpolation(float t) {
            return (float)Math.pow(t-1, 5) + 1;
        }

    }

    protected class AnimationRunnable implements Runnable {

        @Override
        public void run() {
            FlyOutContainer.this
                    .adjustContentPosition(FlyOutContainer.this.menuAnimationScroller
                            .computeScrollOffset());
        }

    }
}

