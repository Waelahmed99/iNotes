package asana.inotes.model;

public class NotesModel {

    private long id;
    private String title;
    private String content;
    private String label;
    private String date;

    public NotesModel(long id, String title, String content, String label, String date) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.label = label;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
