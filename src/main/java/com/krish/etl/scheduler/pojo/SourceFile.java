package com.krish.etl.scheduler.pojo;

import java.util.Date;
import java.util.UUID;

public class SourceFile {
    private UUID fileId;
    private String fileCode;
    private String fileName;
    private String filePath;
    private String filePattern;


    private String fileLocation;
    private UUID fileuuid;
    private String modified_by;
    private Date modifiedDate;
    private Date availableFrom;
    private Date availableTo;
    private Boolean isAvailable;
    private Boolean isReadable;
    private Boolean isWritable;
    private Boolean enabled;
    private String contact;
    private long size;

    public UUID getFileId() {
        return fileId;
    }

    public void setFileId(UUID fileId) {
        this.fileId = fileId;
    }

    public String getFileCode() {
        return fileCode;
    }

    public void setFileCode(String fileCode) {
        this.fileCode = fileCode;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePattern() {
        return filePattern;
    }

    public void setFilePattern(String filePattern) {
        this.filePattern = filePattern;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public UUID getFileuuid() {
        return fileuuid;
    }

    public void setFileuuid(UUID fileuuid) {
        this.fileuuid = fileuuid;
    }

    public String getModified_by() {
        return modified_by;
    }

    public void setModified_by(String modified_by) {
        this.modified_by = modified_by;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Date getAvailableFrom() {
        return availableFrom;
    }

    public void setAvailableFrom(Date availableFrom) {
        this.availableFrom = availableFrom;
    }

    public Date getAvailableTo() {
        return availableTo;
    }

    public void setAvailableTo(Date availableTo) {
        this.availableTo = availableTo;
    }

    public Boolean getAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
    }

    public Boolean getReadable() {
        return isReadable;
    }

    public void setReadable(Boolean readable) {
        isReadable = readable;
    }

    public Boolean getWritable() {
        return isWritable;
    }

    public void setWritable(Boolean writable) {
        isWritable = writable;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "SourceFile{" +
                "fileId=" + fileId +
                ", fileCode='" + fileCode + '\'' +
                ", fileName='" + fileName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", filePattern='" + filePattern + '\'' +
                ", fileLocation='" + fileLocation + '\'' +
                ", fileuuid='" + fileuuid + '\'' +
                ", modified_by='" + modified_by + '\'' +
                ", modifiedDate=" + modifiedDate +
                ", availableFrom=" + availableFrom +
                ", availableTo=" + availableTo +
                ", isAvailable=" + isAvailable +
                ", isReadable=" + isReadable +
                ", isWritable=" + isWritable +
                ", enabled=" + enabled +
                ", contact='" + contact + '\'' +
                '}';
    }
}
