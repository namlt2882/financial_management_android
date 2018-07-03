package project.baonq.ui.DBModel;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "notification")
public class Notification {
    @Id(autoincrement = true)
    private Long id;

    private int server_id;

    private String title;

    private String content;

    private boolean is_system_notification;

    private String insert_date;

    private String last_update;

    private  int status;

    @Generated(hash = 1602731825)
    public Notification(Long id, int server_id, String title, String content,
            boolean is_system_notification, String insert_date, String last_update,
            int status) {
        this.id = id;
        this.server_id = server_id;
        this.title = title;
        this.content = content;
        this.is_system_notification = is_system_notification;
        this.insert_date = insert_date;
        this.last_update = last_update;
        this.status = status;
    }

    @Generated(hash = 1855225820)
    public Notification() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getServer_id() {
        return this.server_id;
    }

    public void setServer_id(int server_id) {
        this.server_id = server_id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean getIs_system_notification() {
        return this.is_system_notification;
    }

    public void setIs_system_notification(boolean is_system_notification) {
        this.is_system_notification = is_system_notification;
    }

    public String getInsert_date() {
        return this.insert_date;
    }

    public void setInsert_date(String insert_date) {
        this.insert_date = insert_date;
    }

    public String getLast_update() {
        return this.last_update;
    }

    public void setLast_update(String last_update) {
        this.last_update = last_update;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
