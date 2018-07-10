package net.americanairguns.classifiedads.UIAdapters;

public class DrawerItem {
    private String title;
    private int icon;
    private Boolean isClickable, isHeader;

    public DrawerItem(){}

    public DrawerItem(String title, Integer icon, Boolean isHeader, Boolean isClickable){
        this.title = title;
        this.icon = icon;
        this.isHeader = isHeader;
        this.isClickable = isClickable;
    }

    public DrawerItem(String title, Integer icon) {
        this(title, icon, false, true);
    }

    public DrawerItem(String title, Boolean isHeader, Boolean isClickable){
        this(title, -1, isHeader, isClickable);
    }

    public DrawerItem(String title, Boolean isHeader){
        this(title, -1, isHeader, true);
    }

    public DrawerItem(String title){
        this(title, -1, true, true);
    }

    public String getTitle(){
        return this.title;
    }

    public int getIcon(){
        return this.icon;
    }

    public Boolean getIsHeader() {
        return this.isHeader;
    }

    public Boolean getIsClickable() {
        return this.isClickable;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setIcon(int icon){
        this.icon = icon;
    }

    public void setIsHeader(Boolean isHeader) {
        this.isHeader = isHeader;
    }

    public void setIsClickable(Boolean isClickable) {
        this.isClickable = isClickable;
    }
}
