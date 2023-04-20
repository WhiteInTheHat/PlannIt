package com.example.plannit;

import android.view.MenuItem;

/**
 * This is an interface that declares what methods need to be implemented for the RecyclerView
 */
public interface recyclerViewInterface {

    /**
     * onItemClick is needed so that we can click on a note, it will take us somewhere else
     * @param position - This returns the posistion of the item that was clicked in the array
     */
    void onItemClick(int position);
    void onRvMenuItem(int position, MenuItem menuItem, Note note);
}
