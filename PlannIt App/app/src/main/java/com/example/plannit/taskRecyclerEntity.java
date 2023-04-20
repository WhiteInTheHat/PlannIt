package com.example.plannit;

public class taskRecyclerEntity {
    private Task task;
    private boolean showMenu = false;

    public taskRecyclerEntity(Task task, boolean showMenu) {
        this.task = task;
        this.showMenu = showMenu;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public boolean isShowMenu() {
        return showMenu;
    }

    public void setShowMenu(boolean showMenu) {
        this.showMenu = showMenu;
    }


}