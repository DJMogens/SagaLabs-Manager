package sagalabsmanagerclient.controllers;

import javafx.beans.property.SimpleObjectProperty;
import sagalabsmanagerclient.View;

public abstract class Controller {
    private View view = null;
    private SimpleObjectProperty<View> viewSimpleObjectProperty = new SimpleObjectProperty<>(null);

    public void setView(View view) {
        this.viewSimpleObjectProperty.set(view);
    }
    public void closeRefreshingThreads() {
        if(this instanceof MenuController) {
            ((MenuController) this).stopRefreshing();
        }
    }
    public SimpleObjectProperty<View> getViewSimpleObjectProperty() {
        return viewSimpleObjectProperty;
    }
}
