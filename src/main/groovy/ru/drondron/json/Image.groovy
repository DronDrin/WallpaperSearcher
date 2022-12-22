package ru.drondron.json

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
class Image {
    String id
    String purity
    @JsonProperty("file_type")
    String fileType
    String path
    @JsonProperty("dimension_x")
    int dimensionX
    @JsonProperty("dimension_y")
    int dimensionY

    Image(String id, String purity, int dimensionX, int dimensionY, String fileType, String path) {
        this.id = id
        this.purity = purity
        this.dimensionX = dimensionX
        this.dimensionY = dimensionY
        this.fileType = fileType
        this.path = path
    }

    Image() {
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.class) return false

        Image image = (Image) o

        if (dimensionX != image.dimensionX) return false
        if (dimensionY != image.dimensionY) return false
        if (fileType != image.fileType) return false
        if (id != image.id) return false
        if (path != image.path) return false
        if (purity != image.purity) return false

        return true
    }

    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (purity != null ? purity.hashCode() : 0)
        result = 31 * result + (fileType != null ? fileType.hashCode() : 0)
        result = 31 * result + (path != null ? path.hashCode() : 0)
        result = 31 * result + dimensionX
        result = 31 * result + dimensionY
        return result
    }

    @Override
    String toString() {
        return "Image{" +
                "id='" + id + '\'' +
                ", purity='" + purity + '\'' +
                ", fileType='" + fileType + '\'' +
                ", path='" + path + '\'' +
                ", dimensionX=" + dimensionX +
                ", dimensionY=" + dimensionY +
                '}';
    }
}
