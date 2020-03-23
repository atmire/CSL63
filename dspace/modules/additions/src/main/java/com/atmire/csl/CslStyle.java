/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 * <p>
 * http://www.dspace.org/license/
 */
package com.atmire.csl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.dspace.content.Site;
import org.dspace.eperson.EPerson;

@Entity
@Table(name="csl_style")
public class CslStyle {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "csl_style_seq")
    @SequenceGenerator(name="csl_style_seq", sequenceName="csl_style_seq", allocationSize = 1, initialValue = 1)
    private Integer id;

    @Column(name = "alias", nullable = false, length = 256)
    private String alias;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site")
    private Site site;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eperson")
    private EPerson eperson;

    @Column(name = "cslFileName", nullable = false, length = 256)
    private String cslFileName;

    @Column(name = "cslFile", nullable = false, length = 256)
    private String cslFile;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public EPerson getEperson() {
        return eperson;
    }

    public void setEperson(EPerson eperson) {
        this.eperson = eperson;
    }

    public String getCslFileName() {
        return cslFileName;
    }

    public void setCslFileName(String cslFileName) {
        this.cslFileName = cslFileName;
    }

    public String getCslFilePath() {
        return cslFile;
    }

    public void setCslFilePath(String cslFile) {
        this.cslFile = cslFile;
    }

    public boolean isGlobal() {
        return getEperson() == null;
    }
}
