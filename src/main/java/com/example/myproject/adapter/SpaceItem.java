package com.example.myproject.adapter;


import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpaceItem extends RecyclerView.ItemDecoration {
    private int space;
    public SpaceItem(int space) {
        this.space=space;
    }

    public int getSpace() {
        return space;
    }

    public void setSpace(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left=space;
        outRect.right=space;
        outRect.bottom=space;
        //注释这两行是为了上下间距相同
//       if(parent.getChildAdapterPosition(view)==0){
        outRect.top=0;
//       }
    }
}
