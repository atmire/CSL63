package com.atmire.app.xmlui.aspect.citations;

import org.springframework.beans.factory.BeanNameAware;

public class CitationFormat implements BeanNameAware {

    private String name;
    private String format;
    private String fileExtension;

    public CitationFormat() {
    }

    public String getName() {
        return name;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(final String format) {
        this.format = format;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(final String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public void setBeanName(final String name) {
        this.name = name;
    }
}
