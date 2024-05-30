package constellation.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;

/**
 * Constellation File Utilities
 * @author lawrence.daniels@gmail.com
 */
public class CxFileUtilities {

    /**
     * Retrieves the image from the given file
     * @param imageFile the given image {@link File file}
     * @return the {@link BufferedImage image}
     */
    public static BufferedImage readImage(final File imageFile)
            throws IOException {
        try (FileInputStream fis = new FileInputStream(imageFile)) {
            // open the image file

            // return the image
            return ImageIO.read(fis);
        }
    }

    /**
     * Retrieves the image from the given binary data
     * @param imageContent the given binary data
     * @return the {@link BufferedImage image}
     */
    public static BufferedImage readImage(final byte[] imageContent)
            throws IOException {
        ByteArrayInputStream in = null;
        try {
            // open the image file
            in = new ByteArrayInputStream(imageContent);

            // return the image
            return ImageIO.read(in);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (final IOException e) {
                }
            }
        }
    }

    /**
     * Retrieves the image from the given Base64 encoded string
     * @param base64Data the given Base64 encoded string
     * @return the {@link BufferedImage image}
     */
    public static BufferedImage readImage(final String base64Data)
            throws IOException {
        // convert the Base64 encoded data to binary image data
        final byte[] content = Base64.getDecoder().decode(base64Data);

        // convert the content into an image
        return CxFileUtilities.readImage(content);
    }

    /**
     * Reads the image content from the given file
     * @param file the given {@link File file}
     * @return the binary image data
     * @throws IOException
     */
    public static byte[] readFileContents(final File file)
            throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream((int) file.length());
        FileInputStream fis = null;
        try {
            // open the file for reading
            fis = new FileInputStream(file);

            // create a buffer for reading
            byte[] buf = new byte[65535];

            // read the content
            int count;
            while ((count = fis.read(buf)) != -1) {
                baos.write(buf, 0, count);
            }

            // return the image content
            return baos.toByteArray();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (final IOException e) {
                }
            }
        }
    }

}
