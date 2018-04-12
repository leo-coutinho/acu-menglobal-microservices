package au.com.acumen.faces.model;

import java.util.LinkedHashMap;

public class Images extends LinkedHashMap {
    private Double quality;
    private String imageBase64;

    public Images() {
    }


    public Double getQuality() {
        return quality;
    }

    public void setQuality(Double quality) {
        this.quality = quality;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }
}
