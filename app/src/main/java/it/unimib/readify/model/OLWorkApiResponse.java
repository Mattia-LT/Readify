package it.unimib.readify.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class OLWorkApiResponse implements Parcelable {

    private OLDescription olDescription;
    private String title;
    private List<Integer> covers;
    private String first_publish_date;
    private String key;
    private List<OLAuthor> olAuthors;
    private List<String> subjects;

    public OLWorkApiResponse() {

    }

    public OLWorkApiResponse(OLDescription olDescription, String title, List<Integer> covers, String first_publish_date, String key, List<OLAuthor> olAuthors, List<String> subjects) {
        this.olDescription = olDescription;
        this.title = title;
        this.covers = covers;
        this.first_publish_date = first_publish_date;
        this.key = key;
        this.olAuthors = olAuthors;
        this.subjects = subjects;
    }

    public OLDescription getOlDescription() {
        return olDescription;
    }

    public void setOlDescription(OLDescription olDescription) {
        this.olDescription = olDescription;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Integer> getCovers() {
        return covers;
    }

    public void setCovers(List<Integer> covers) {
        this.covers = covers;
    }

    public String getFirst_publish_date() {
        return first_publish_date;
    }

    public void setFirst_publish_date(String first_publish_date) {
        this.first_publish_date = first_publish_date;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<OLAuthor> getOlAuthors() {
        return olAuthors;
    }

    public void setOlAuthors(List<OLAuthor> olAuthors) {
        this.olAuthors = olAuthors;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }

    @Override
    public String toString() {
        return "OLWorkApiResponse{" +
                "olDescription=" + olDescription +
                ", title='" + title + '\'' +
                ", covers=" + covers +
                ", first_publish_date='" + first_publish_date + '\'' +
                ", key='" + key + '\'' +
                ", olAuthors=" + olAuthors +
                ", subjects=" + subjects +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.olDescription, flags);
        dest.writeString(this.title);
        dest.writeList(this.covers);
        dest.writeString(this.first_publish_date);
        dest.writeString(this.key);
        dest.writeTypedList(this.olAuthors);
        dest.writeStringList(this.subjects);
    }

    public void readFromParcel(Parcel source) {
        this.olDescription = source.readParcelable(OLDescription.class.getClassLoader());
        this.title = source.readString();
        this.covers = new ArrayList<Integer>();
        source.readList(this.covers, Integer.class.getClassLoader());
        this.first_publish_date = source.readString();
        this.key = source.readString();
        this.olAuthors = source.createTypedArrayList(OLAuthor.CREATOR);
        this.subjects = source.createStringArrayList();
    }

    protected OLWorkApiResponse(Parcel in) {
        this.olDescription = in.readParcelable(OLDescription.class.getClassLoader());
        this.title = in.readString();
        this.covers = new ArrayList<Integer>();
        in.readList(this.covers, Integer.class.getClassLoader());
        this.first_publish_date = in.readString();
        this.key = in.readString();
        this.olAuthors = in.createTypedArrayList(OLAuthor.CREATOR);
        this.subjects = in.createStringArrayList();
    }

    public static final Parcelable.Creator<OLWorkApiResponse> CREATOR = new Parcelable.Creator<OLWorkApiResponse>() {
        @Override
        public OLWorkApiResponse createFromParcel(Parcel source) {
            return new OLWorkApiResponse(source);
        }

        @Override
        public OLWorkApiResponse[] newArray(int size) {
            return new OLWorkApiResponse[size];
        }
    };
}
