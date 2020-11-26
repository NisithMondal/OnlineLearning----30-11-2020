package com.nisith.onlinelearning.Model;

public class DrawerMenu {
    private String menuHeaderDocumentId;
    private String menuItemDocumentId;
    private String menuItemTitle;
    public DrawerMenu(){}

    public DrawerMenu(String menuHeaderDocumentId, String menuItemDocumentId, String menuItemTitle) {
        this.menuHeaderDocumentId = menuHeaderDocumentId;
        this.menuItemDocumentId = menuItemDocumentId;
        this.menuItemTitle = menuItemTitle;
    }

    public String getMenuHeaderDocumentId() {
        return menuHeaderDocumentId;
    }

    public String getMenuItemDocumentId() {
        return menuItemDocumentId;
    }

    public String getMenuItemTitle() {
        return menuItemTitle;
    }
}
