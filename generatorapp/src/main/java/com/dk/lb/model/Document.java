package com.dk.lb.model;

import java.util.List;
import java.util.Map;

public class Document {
    // 1 means invoice document
    // 2 means payment document
    int documentType;
    long documentId;
    List<String> customFields;

    public int getDocumentType() {
        return documentType;
    }

    public void setDocumentType(int documentType) {
        this.documentType = documentType;
    }

    public long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(long documentId) {
        this.documentId = documentId;
    }

    public List<String> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(List<String> customFields) {
        this.customFields = customFields;
    }
}
