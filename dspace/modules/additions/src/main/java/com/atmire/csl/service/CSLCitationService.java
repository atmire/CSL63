package com.atmire.csl.service;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import com.atmire.csl.CslStyle;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Item;
import org.dspace.content.Site;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;

public interface CSLCitationService {

    CslStyle create(Context context, String alias, Site site, String cslFileName, InputStream cslFileInputStream) throws SQLException, IOException, AuthorizeException;

    CslStyle create(Context context, String alias, EPerson eperson, String cslFileName, InputStream cslFileInputStream) throws SQLException, IOException, AuthorizeException;

    CslStyle find(Context context, Integer id) throws SQLException;

    CslStyle find(Context context, String alias, Site site) throws SQLException;

    CslStyle find(Context context, String alias, EPerson eperson) throws SQLException;

    CslStyle find(Context context, String alias, boolean global) throws SQLException;

    List<CslStyle> findBySite(Context context, Site site) throws SQLException;

    List<CslStyle> findByEPerson(Context context, EPerson eperson) throws SQLException;

    List<CslStyle> findBySiteOrEPerson(Context context, Site site, EPerson eperson) throws SQLException;

    void delete(Context context, CslStyle cslStyle) throws SQLException;

    boolean isAuthorized(Context context, CslStyle cslStyle) throws SQLException;

    boolean allowAddPersonalStyle(Context context) throws SQLException;

    String getCitationEntry(List<Item> items, CslStyle cslStyle, String format)
            throws IOException, ParseException;

    String getCitationEntry(List<Item> items, String cslFileContent, String format)
            throws IOException, ParseException;

    String getCitationEntry(Item items, CslStyle cslStyle, String format)
            throws IOException, ParseException;
}
