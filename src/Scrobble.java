import java.io.Serializable;

/**
 * Created by Hans on 26-05-2016.
 */
public class Scrobble implements Serializable
{
    String artist;
    String track;
    String album;
    long date;

    public Scrobble(String _artist, String _track, String _album, long _date) {
        this.artist = _artist;
        this.track = _track;
        this.album = _album;
        this.date = _date;
    }

    public String getArtist()
    {
        return artist;
    }

    public String getTrack()
    {
        return track;
    }

    public String getAlbum()
    {
        return album;
    }

    public long getDate()
    {
        return date;
    }

    public String toString() {
        return artist + " - " + track;
    }
}
