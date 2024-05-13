package it.unimib.readify.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/*
usata per registrare la risposta dell'api alla nostra richiesta
 */
public class OLSearchApiResponse implements Parcelable {

    private int numFound;
    private int start;
    private boolean numFoundExact;
    private List<OLDocs> docs;
    private String q;
    private int offset;
    private List<OLWorkApiResponse> workList;

    public OLSearchApiResponse() {}

    public OLSearchApiResponse(int numFound, int start, boolean numFoundExact, List<OLDocs> docs, String q, int offset) {
        this.numFound = numFound;
        this.start = start;
        this.numFoundExact = numFoundExact;
        this.docs = docs;
        this.q = q;
        this.offset = offset;
        this.workList = new ArrayList<>();
    }

    public int getNumFound() {
        return numFound;
    }

    public void setNumFound(int numFound) {
        this.numFound = numFound;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public boolean isNumFoundExact() {
        return numFoundExact;
    }

    public void setNumFoundExact(boolean numFoundExact) {
        this.numFoundExact = numFoundExact;
    }

    public List<OLDocs> getDocs() {
        return docs;
    }

    public void setDocs(List<OLDocs> docs) {
        this.docs = docs;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public List<OLWorkApiResponse> getWorkList() {
        return workList;
    }

    public void setWorkList(List<OLWorkApiResponse> workList) {
        this.workList = workList;
    }

    @NonNull
    @Override
    public String toString() {
        return "OLSearchApiResponse{" +
                "numFound=" + numFound +
                ", start=" + start +
                ", numFoundExact=" + numFoundExact +
                ", docs=" + docs +
                ", q='" + q + '\'' +
                ", offset=" + offset +
                ", workList=" + workList +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.numFound);
        dest.writeInt(this.start);
        dest.writeByte(this.numFoundExact ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.docs);
        dest.writeString(this.q);
        dest.writeInt(this.offset);
        dest.writeTypedList(this.workList);
    }

    public void readFromParcel(Parcel source) {
        this.numFound = source.readInt();
        this.start = source.readInt();
        this.numFoundExact = source.readByte() != 0;
        this.docs = source.createTypedArrayList(OLDocs.CREATOR);
        this.q = source.readString();
        this.offset = source.readInt();
        this.workList = source.createTypedArrayList(OLWorkApiResponse.CREATOR);
    }

    protected OLSearchApiResponse(Parcel in) {
        this.numFound = in.readInt();
        this.start = in.readInt();
        this.numFoundExact = in.readByte() != 0;
        this.docs = in.createTypedArrayList(OLDocs.CREATOR);
        this.q = in.readString();
        this.offset = in.readInt();
        this.workList = in.createTypedArrayList(OLWorkApiResponse.CREATOR);
    }

    public static final Creator<OLSearchApiResponse> CREATOR = new Creator<OLSearchApiResponse>() {
        @Override
        public OLSearchApiResponse createFromParcel(Parcel source) {
            return new OLSearchApiResponse(source);
        }

        @Override
        public OLSearchApiResponse[] newArray(int size) {
            return new OLSearchApiResponse[size];
        }
    };
}
