package asana.inotes.model;

public class ListsModel {

    private String content;
    private String checked;

    public ListsModel(String content, String checked) {
        this.content = content;
        this.checked = checked;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String isChecked() {
        return checked;
    }

    public void setChecked(String checked) {
        this.checked = checked;
    }
}
