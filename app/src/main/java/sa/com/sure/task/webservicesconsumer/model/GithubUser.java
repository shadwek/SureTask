package sa.com.sure.task.webservicesconsumer.model;

import android.graphics.Bitmap;

/**
 * Created by HussainHajjar on 5/8/2017.
 */

public class GithubUser {

    // query used to get small avatar images (80 * 80)
    private final static String AVATAR_SIZE_QUERY = "&s=80";

    private long tableId;
    private int id;
    private String login;
    private String avatar_url;
    private String html_url;
    private Bitmap avatar;
    private boolean isStarted;

    public GithubUser(){}

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getAvatar_url() {
        return avatar_url + AVATAR_SIZE_QUERY;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getHtml_url() {
        return html_url;
    }

    public void setHtml_url(String html_url) {
        this.html_url = html_url;
    }

    public Bitmap getAvatar() {
        return avatar;
    }

    public void setAvatar(Bitmap avatar) {
        this.avatar = avatar;
    }

    public void setStarted(boolean started){ this.isStarted = started; }

    public boolean getStarted(){ return this.isStarted; }

    public long getTableId() { return tableId; }

    public void setTableId(long tableId) { this.tableId = tableId; }
}