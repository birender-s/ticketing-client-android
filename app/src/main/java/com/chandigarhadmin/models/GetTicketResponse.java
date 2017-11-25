package com.chandigarhadmin.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.chandigarhadmin.utils.Constant;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Comparator;
import java.util.List;

/**
 * Created by harendrasinghbisht on 23/09/17.
 */

public class GetTicketResponse implements Parcelable, Comparator<GetTicketResponse> {
    @SerializedName("assigned_since")
    @Expose
    private String assignedSince;
    @SerializedName("assignee")
    @Expose
    private String assignee;
    @SerializedName("branch")
    @Expose
    private String branch;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("key")
    @Expose
    private String key;
    @SerializedName("priority")
    @Expose
    private String priority;
    @SerializedName("reporter")
    @Expose
    private String reporter;
    @SerializedName("reporter_email")
    @Expose
    private String reporterEmail;
    @SerializedName("source")
    @Expose
    private String source;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("subject")
    @Expose
    private String subject;
    @SerializedName("unique_id")
    @Expose
    private UniqueId uniqueId;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("watcher_list")
    @Expose
    private List<Object> watcherList = null;

    public GetTicketResponse() {

    }

    protected GetTicketResponse(Parcel in) {
        assignedSince = in.readString();
        assignee = in.readString();
        branch = in.readString();
        createdAt = in.readString();
        id = in.readString();
        key = in.readString();
        priority = in.readString();
        reporter = in.readString();
        reporterEmail = in.readString();
        source = in.readString();
        status = in.readString();
        subject = in.readString();
        uniqueId = in.readParcelable(UniqueId.class.getClassLoader());
        updatedAt = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(assignedSince);
        dest.writeString(assignee);
        dest.writeString(branch);
        dest.writeString(createdAt);
        dest.writeString(id);
        dest.writeString(key);
        dest.writeString(priority);
        dest.writeString(reporter);
        dest.writeString(reporterEmail);
        dest.writeString(source);
        dest.writeString(status);
        dest.writeString(subject);
        dest.writeParcelable(uniqueId, flags);
        dest.writeString(updatedAt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GetTicketResponse> CREATOR = new Creator<GetTicketResponse>() {
        @Override
        public GetTicketResponse createFromParcel(Parcel in) {
            return new GetTicketResponse(in);
        }

        @Override
        public GetTicketResponse[] newArray(int size) {
            return new GetTicketResponse[size];
        }
    };

    public String getAssignedSince() {
        return assignedSince;
    }

    public void setAssignedSince(String assignedSince) {
        this.assignedSince = assignedSince;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getReporterEmail() {
        return reporterEmail;
    }

    public void setReporterEmail(String reporterEmail) {
        this.reporterEmail = reporterEmail;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public UniqueId getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(UniqueId uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Object> getWatcherList() {
        return watcherList;
    }

    public void setWatcherList(List<Object> watcherList) {
        this.watcherList = watcherList;
    }

    /*@Override
    public int compareTo(@NonNull GetTicketResponse getTicketResponse) {

    }*/

    @Override
    public int compare(GetTicketResponse t1, GetTicketResponse t2) {
        //if both tickets are updated
        if (null != t1.getUpdatedAt() && null != t2.getUpdatedAt()) {
            String updatedDate1 = t1.getUpdatedAt().substring(0, t1.getUpdatedAt().lastIndexOf("+"));
            String updatedDate2 = t2.getUpdatedAt().substring(0, t2.getUpdatedAt().lastIndexOf("+"));
            if (Constant.getDate(updatedDate1).after(Constant.getDate(updatedDate2))) {
                return 1;
            } else {
                return -1;
            }
            //if only first ticket updated
        } else if (null != t1.getUpdatedAt() && null == t2.getUpdatedAt()) {
            String updatedDate1 = t1.getUpdatedAt().substring(0, t1.getUpdatedAt().lastIndexOf("+"));
            String createdDate2 = t2.getCreatedAt().substring(0, t2.getCreatedAt().lastIndexOf("+"));
            if (Constant.getDate(updatedDate1).after(Constant.getDate(createdDate2))) {
                return 1;
            } else {
                return -1;
            }
//if only second ticket updated
        } else if (null == t1.getUpdatedAt() && null != t2.getUpdatedAt()) {
            String createdDate1 = t1.getCreatedAt().substring(0, t1.getCreatedAt().lastIndexOf("+"));
            String updatedDate2 = t2.getUpdatedAt().substring(0, t2.getUpdatedAt().lastIndexOf("+"));
            if (Constant.getDate(createdDate1).after(Constant.getDate(updatedDate2))) {
                return 1;
            } else {
                return -1;
            }
//if only both ticket not updated
        } else {
            if (Integer.parseInt(t1.getId()) > Integer.parseInt(t2.getId())) {
                return 1;
            }
        }
        return 0;
    }
}
