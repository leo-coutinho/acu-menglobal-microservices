package au.com.acumen.faces.utils;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

//import sun.misc.BASE64Decoder;
//import sun.misc.BASE64Encoder;

@Component
public class Base64EncodeDecode {

    public Base64EncodeDecode() {
    }

    // String type can be --> GIF,PNG,JPG
// This method returns a String representation of an Image,
// ready to be saved to a database as a Blob
    public String encodeToString(BufferedImage image, String type) {
        String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, type, bos);
            byte[] imageBytes = bos.toByteArray();

//            BASE64Encoder encoder = new BASE64Encoder();
            imageString = Base64.getEncoder().encodeToString(imageBytes);

            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageString;
    }
// Below is a method on how to decode base64 string to image.
    public BufferedImage decodeToImage(String imageString) {

        BufferedImage image = null;
        byte[] imageByte;
        try {
//            BASE64Decoder decoder = new BASE64Decoder();

            imageByte = Base64.getDecoder().decode(imageString);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(bis);
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }


}
