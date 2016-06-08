import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Hans on 26-05-2016.
 */
public class JSON
{
    private Settings s = new Settings();

    public String apiToString(String in) throws IOException
    {
        URL url = new URL(in);
        BufferedReader streamReader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
        StringBuilder responseStrBuilder = new StringBuilder();

        String inputStr;
        while ((inputStr = streamReader.readLine()) != null)
        {
            responseStrBuilder.append(inputStr);
        }
        String input = responseStrBuilder.toString();

        streamReader.close();
        return input;
    }

    public List<Scrobble> readPage(int page, String user, Container c) throws IOException, JSONException {
        int readPage = c.getTotalPages() - page;
        String input = apiToString("http://ws.audioscrobbler.com/2.0/?method=user.getrecenttracks&user=" + user + "&api_key=" + s.getApi() + "&format=json&limit=" + s.getLimit() + "&page=" + readPage);

        JSONObject obj = new JSONObject(input);
        JSONObject recent = obj.getJSONObject("recenttracks");
        JSONArray tracks = recent.getJSONArray("track");

        JSONObject attr = recent.getJSONObject("@attr");

        List<Scrobble> scrobbles = new ArrayList<>();

        c.setTotalTracks(attr.getInt("total"));
        int pages = attr.getInt("totalPages");
        if (c.getTotalPages() != pages) {
            c.setTotalPages(pages);
        } else
        {
            for (int i = 0; i < tracks.length(); i++)
            {
                JSONObject track = tracks.getJSONObject(i);
                boolean isFirst = true;
                try {
                    track.getJSONObject("@attr").getBoolean("nowplaying");
                } catch (JSONException e) {
                    isFirst = false;
                }
                if (!isFirst)
                {
                    String artist = track.getJSONObject("artist").getString("#text");
                    String name = track.getString("name");
                    String album = track.getJSONObject("album").getString("#text");
                    long date = track.getJSONObject("date").getLong("uts");

                    scrobbles.add(new Scrobble(artist, name, album, date));
                }
            }
        }

        return scrobbles;
    }

    public String getDateFromTS(long ts)
    {
        java.util.Date _date = new java.util.Date(ts * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
        return sdf.format(_date);
    }

    public String showDuration(long dur) {
        String ret;

        long hour = 3600000;
        long day = hour * 24;

        if (dur >= day) {
            ret = String.format("%dd, %dh, %dm, %ds",
                    TimeUnit.MILLISECONDS.toDays(dur),
                    TimeUnit.MILLISECONDS.toHours(dur) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(dur)),
                    TimeUnit.MILLISECONDS.toMinutes(dur) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(dur)),
                    TimeUnit.MILLISECONDS.toSeconds(dur) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(dur))
            );
        } else if (dur > hour && dur < day) {
            ret = String.format("%dh, %dm, %ds",
                    TimeUnit.MILLISECONDS.toHours(dur),
                    TimeUnit.MILLISECONDS.toMinutes(dur) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(dur)),
                    TimeUnit.MILLISECONDS.toSeconds(dur) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(dur))
            );
        } else {
            ret = String.format("%dm, %ds",
                    TimeUnit.MILLISECONDS.toMinutes(dur),
                    TimeUnit.MILLISECONDS.toSeconds(dur) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(dur))
            );
        }
        return ret;
    }
}
