package cherish.expensetracker.custom;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;


public class RecyclerViewItemDecorator extends RecyclerView.ItemDecoration {

    private final float mVerticalSpaceHeight;

    public RecyclerViewItemDecorator(float mVerticalSpaceHeight) {
        this.mVerticalSpaceHeight = mVerticalSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        outRect.bottom = (int) mVerticalSpaceHeight;
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = (int) mVerticalSpaceHeight;
        }
    }

}
