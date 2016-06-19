import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hans on 26-05-2016.
 */
public class Container implements Serializable {

    private String user;

    private int totalTracks;
    private int totalPages;
    private int atPage;
    private List<Scrobble> scrobbles = new ArrayList<>();

    public Container(String _user, int _totalTracks, int _totalPages) {
        this.user = _user;
        this.totalPages = _totalPages;
        this.totalTracks = _totalTracks;
        this.atPage = 0;
    }

    public String getUser() {
        return user;
    }

    public int getTotalTracks() {
        return totalTracks;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getAtPage() {
        return atPage;
    }

    public void setAtPage(int atPage) {
        this.atPage = atPage;
    }

    public List<Scrobble> getScrobbles() {
        return scrobbles;
    }

    public void setTotalTracks(int totalTracks) {
        this.totalTracks = totalTracks;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
