package asana.inotes.model;

import java.util.List;

public class ChecklistsModel {

    private long id;
    private String title;
    private List<ListsModel> items;
    private String date;

    public ChecklistsModel(long id, String title, List<ListsModel> items, String date) {
        this.id = id;
        this.title = title;
        this.items = items;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ListsModel> getItems() {
        return items;
    }

    public void setItems(List<ListsModel> items) {
        this.items = items;
    }

    public String getDate() { return this.date; }

    public void setDate(String date) { this.date = date; }
}
