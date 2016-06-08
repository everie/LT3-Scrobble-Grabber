import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Hans on 27-05-2016.
 */
public class Crypt
{

    private Settings s = new Settings();

    private byte[] keySpec = s.getKey().getBytes(StandardCharsets.UTF_8);
    private SecretKey key64 = new SecretKeySpec(keySpec, s.getKeyType());
    private Cipher cipher;

    public Container decrypt() {
        try
        {
            cipher = Cipher.getInstance(s.getKeyType());
            cipher.init(Cipher.ENCRYPT_MODE, key64);

            cipher.init(Cipher.DECRYPT_MODE, key64);
            CipherInputStream cipherInputStream = new CipherInputStream(new BufferedInputStream(new FileInputStream(s.getFileName())), cipher);
            ObjectInputStream inputStream = new ObjectInputStream(cipherInputStream);
            SealedObject sealedObjectIn = (SealedObject) inputStream.readObject();

            return (Container) sealedObjectIn.getObject(cipher);

        } catch (NoSuchPaddingException|NoSuchAlgorithmException|InvalidKeyException|IOException|ClassNotFoundException|BadPaddingException|IllegalBlockSizeException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void encrypt(Container c) {
        try
        {
            cipher = Cipher.getInstance(s.getKeyType());
            cipher.init(Cipher.ENCRYPT_MODE, key64);

            SealedObject sealedObjectOut = new SealedObject(c, cipher);

            CipherOutputStream cipherOutputStream = new CipherOutputStream(new BufferedOutputStream(new FileOutputStream(s.getFileName())), cipher);
            ObjectOutputStream outputStream = new ObjectOutputStream(cipherOutputStream);
            outputStream.writeObject(sealedObjectOut);
            outputStream.close();
        } catch (IOException|InvalidKeyException|IllegalBlockSizeException|NoSuchPaddingException|NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
