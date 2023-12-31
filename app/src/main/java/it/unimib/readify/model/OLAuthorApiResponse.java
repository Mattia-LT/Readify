package it.unimib.readify.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OLAuthorApiResponse implements Parcelable {

    @SerializedName("fuller_name")
    private String fullerName;
    private String name;
    @SerializedName("birth_date")
    private String birthDate;
    @SerializedName("death_date")
    private String deathDate;
    private List<Integer> photos;

    public OLAuthorApiResponse(String fullerName, String name, String birthDate, String deathDate, List<Integer> photos) {
        this.fullerName = fullerName;
        this.name = name;
        this.birthDate = birthDate;
        this.deathDate = deathDate;
        this.photos = photos;
    }

    public String getFullerName() {
        return fullerName;
    }

    public void setFullerName(String fullerName) {
        this.fullerName = fullerName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(String deathDate) {
        this.deathDate = deathDate;
    }

    public List<Integer> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Integer> photos) {
        this.photos = photos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OLAuthorApiResponse olAuthorApiResponse = (OLAuthorApiResponse) o;
        return Objects.equals(fullerName, olAuthorApiResponse.fullerName) && Objects.equals(name, olAuthorApiResponse.name) && Objects.equals(birthDate, olAuthorApiResponse.birthDate) && Objects.equals(deathDate, olAuthorApiResponse.deathDate) && Objects.equals(photos, olAuthorApiResponse.photos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullerName, name, birthDate, deathDate, photos);
    }

    @Override
    public String toString() {
        return "OLAuthorApiResponse{" +
                "fuller_name='" + fullerName + '\'' +
                ", name='" + name + '\'' +
                ", birth_date='" + birthDate + '\'' +
                ", death_date='" + deathDate + '\'' +
                ", photos=" + photos +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.fullerName);
        dest.writeString(this.name);
        dest.writeString(this.birthDate);
        dest.writeString(this.deathDate);
        dest.writeList(this.photos);
    }

    public void readFromParcel(Parcel source) {
        this.fullerName = source.readString();
        this.name = source.readString();
        this.birthDate = source.readString();
        this.deathDate = source.readString();
        this.photos = new ArrayList<Integer>();
        source.readList(this.photos, Integer.class.getClassLoader());
    }

    protected OLAuthorApiResponse(Parcel in) {
        this.fullerName = in.readString();
        this.name = in.readString();
        this.birthDate = in.readString();
        this.deathDate = in.readString();
        this.photos = new ArrayList<Integer>();
        in.readList(this.photos, Integer.class.getClassLoader());
    }

    public static final Creator<OLAuthorApiResponse> CREATOR = new Creator<OLAuthorApiResponse>() {
        @Override
        public OLAuthorApiResponse createFromParcel(Parcel source) {
            return new OLAuthorApiResponse(source);
        }

        @Override
        public OLAuthorApiResponse[] newArray(int size) {
            return new OLAuthorApiResponse[size];
        }
    };
}
