/**
 * Created by Hans on 27-05-2016.
 */
public class Settings
{
    String key = "#NP4ever~NOvril!";
    String keyType = "Blowfish";

    private int width = 500;
    private int height = 300;

    private int loadWidth = 400;
    private int loadHeight = 60;

    private String api = "c4f03082dfb1d84ebb558ec0439e3b88";
    private int limit = 200;

    private int readFrequency = 2; // pr second

    private String fileName = "scrobbles.lt3";

    public String getKey()
    {
        return key;
    }

    public String getKeyType()
    {
        return keyType;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public String getApi()
    {
        return api;
    }

    public int getLimit()
    {
        return limit;
    }

    public String getFileName()
    {
        return fileName;
    }

    public int getLoadWidth()
    {
        return loadWidth;
    }

    public int getLoadHeight()
    {
        return loadHeight;
    }

    public int getReadFrequency()
    {
        return readFrequency;
    }
}
