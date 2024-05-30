package constellation.drawing.entities;

import constellation.util.CxFileUtilities;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

import static constellation.util.CxFileUtilities.readFileContents;

/**
 * Represents a user image
 * @author lawrence.daniels@gmail.com
 */
public class UserImage {
    private final byte[] content;
    private final BufferedImage image;
    private String name;

    /**
     * Creates a new user name
     * @param name    the name of the image
     * @param content the image content
     * @param image   the image itself
     */
    public UserImage(final String name,
                     final byte[] content,
                     final BufferedImage image) {
        this.name = name;
        this.content = content;
        this.image = image;
    }

    /**
     * Creates a new user image which is read from the given image file
     * @param name    the reference name for the image
     * @param content the given binary content
     * @return a {@link UserImage user image}
     * @throws IOException
     */
    public static UserImage createUserImage(final String name, final byte[] content)
            throws IOException {
        // convert the content into an image
        final BufferedImage image = CxFileUtilities.readImage(content);

        // return the user image
        return new UserImage(name, content, image);
    }

    /**
     * Creates a new user image which is read from the given image file
     * @param name the reference name for the image
     * @param file the given image {@link File file}
     * @return a {@link UserImage user image}
     * @throws IOException
     */
    public static UserImage createUserImage(final String name, final File file)
            throws IOException {
        // get the image content
        final byte[] content = readFileContents(file);

        // convert the content into an image
        final BufferedImage image = CxFileUtilities.readImage(content);

        // return the user image
        return new UserImage(name, content, image);
    }

    /**
     * Creates a new user image which is read from the given Base64 encoded data
     * @param name       the reference name for the image
     * @param base64data the given Base64 encoded data
     * @return a {@link UserImage user image}
     */
    public static UserImage createUserImage(final String name, final String base64data)
            throws IOException {
        // convert the Base64 encoded data to binary image data
        final byte[] content = Base64.getDecoder().decode(base64data.getBytes());

        // convert the content into an image
        final BufferedImage image = CxFileUtilities.readImage(content);

        // return the user image
        return new UserImage(name, content, image);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return the content
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * Returns the width of the image
     * @return the width of the image
     */
    public int getWidth() {
        return image.getWidth();
    }

    /**
     * Returns the height of the image
     * @return the height of the image
     */
    public int getHeight() {
        return image.getHeight();
    }

    /**
     * Returns the content as Base64 encoded data
     * @return the Base64 encoded data
     */
    public String getContentAsBase64() {
        return new String(Base64.getEncoder().encode(content));
    }

    /**
     * Returns the image
     * @return the image
     */
    public BufferedImage getImage() {
        return image;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return name;
    }

}
