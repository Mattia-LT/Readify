package it.unimib.readify.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OLWorkApiResponse implements Parcelable {

    private OLDescription description;
    private String title;
    private List<Integer> covers;
    @SerializedName("first_publish_date")
    private String firstPublishDate;
    private String key;
    private List<OLAuthorKeys> authors;
    private List<String> subjects;
    private List<OLAuthorApiResponse> authorList;

    public OLWorkApiResponse() {}

    public OLWorkApiResponse(List<Integer> covers) {
        this.covers = covers;
    }

    public OLWorkApiResponse(OLDescription description, String title, List<Integer> covers, String firstPublishDate, String key, List<OLAuthorKeys> authors, List<String> subjects) {
        this.description = description;
        this.title = title;
        this.covers = covers;
        this.firstPublishDate = firstPublishDate;
        this.key = key;
        this.authors = authors;
        this.subjects = subjects;
    }

    public OLDescription getDescription() {
        return description;
    }

    public void setDescription(OLDescription description) {
        this.description = description;
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

    public String getFirstPublishDate() {
        return firstPublishDate;
    }

    public void setFirstPublishDate(String firstPublishDate) {
        this.firstPublishDate = firstPublishDate;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<OLAuthorKeys> getAuthors() {
        return authors;
    }

    public void setAuthors(List<OLAuthorKeys> authors) {
        this.authors = authors;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }

    public List<OLAuthorApiResponse> getAuthorList() {
        return authorList;
    }

    public void setAuthorList(List<OLAuthorApiResponse> authorList) {
        this.authorList = authorList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OLWorkApiResponse that = (OLWorkApiResponse) o;
        return Objects.equals(description, that.description) && Objects.equals(title, that.title) && Objects.equals(covers, that.covers) && Objects.equals(firstPublishDate, that.firstPublishDate) && Objects.equals(key, that.key) && Objects.equals(authors, that.authors) && Objects.equals(subjects, that.subjects) && Objects.equals(authorList, that.authorList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, title, covers, firstPublishDate, key, authors, subjects, authorList);
    }

    @Override
    public String toString() {
        return "OLWorkApiResponse{" +
                "description=" + description +
                ", title='" + title + '\'' +
                ", covers=" + covers +
                ", first_publish_date='" + firstPublishDate + '\'' +
                ", key='" + key + '\'' +
                ", authors=" + authors +
                ", subjects=" + subjects +
                ", authorList=" + authorList +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.description, flags);
        dest.writeString(this.title);
        dest.writeList(this.covers);
        dest.writeString(this.firstPublishDate);
        dest.writeString(this.key);
        dest.writeTypedList(this.authors);
        dest.writeStringList(this.subjects);
        dest.writeTypedList(this.authorList);
    }

    public void readFromParcel(Parcel source) {
        this.description = source.readParcelable(OLDescription.class.getClassLoader());
        this.title = source.readString();
        this.covers = new ArrayList<Integer>();
        source.readList(this.covers, Integer.class.getClassLoader());
        this.firstPublishDate = source.readString();
        this.key = source.readString();
        this.authors = source.createTypedArrayList(OLAuthorKeys.CREATOR);
        this.subjects = source.createStringArrayList();
        this.authorList = source.createTypedArrayList(OLAuthorApiResponse.CREATOR);
    }

    protected OLWorkApiResponse(Parcel in) {
        this.description = in.readParcelable(OLDescription.class.getClassLoader());
        this.title = in.readString();
        this.covers = new ArrayList<Integer>();
        in.readList(this.covers, Integer.class.getClassLoader());
        this.firstPublishDate = in.readString();
        this.key = in.readString();
        this.authors = in.createTypedArrayList(OLAuthorKeys.CREATOR);
        this.subjects = in.createStringArrayList();
        this.authorList = in.createTypedArrayList(OLAuthorApiResponse.CREATOR);
    }

    public static final Creator<OLWorkApiResponse> CREATOR = new Creator<OLWorkApiResponse>() {
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
