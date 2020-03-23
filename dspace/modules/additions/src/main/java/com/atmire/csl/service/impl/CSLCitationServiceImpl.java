package com.atmire.csl.service.impl;

import static org.apache.poi.util.IOUtils.copy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.atmire.csl.CslStyle;
import com.atmire.csl.DSpaceListItemDataProvider;
import com.atmire.csl.dao.CslStyleDAO;
import com.atmire.csl.service.CSLCitationService;
import de.undercouch.citeproc.CSL;
import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.factory.AuthorizeServiceFactory;
import org.dspace.content.Item;
import org.dspace.content.Site;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.services.factory.DSpaceServicesFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class CSLCitationServiceImpl implements CSLCitationService {

    private static final String CSL_FILE_DIR = DSpaceServicesFactory.getInstance().getConfigurationService().getProperty("csl.file.dir",
            DSpaceServicesFactory.getInstance().getConfigurationService().getProperty("dspace.dir") + "/csl-files");

    @Autowired
    private CslStyleDAO cslStyleDAO;

    @Override
    public CslStyle create(Context context, String alias, Site site,
                           String cslFileName, InputStream cslFileInputStream) throws SQLException, IOException, AuthorizeException {

        if (find(context, alias, site) != null) {
            throw new IllegalArgumentException("global csl style already exists for alias: " + alias);
        }

        if (!allowAddPersonalStyle(context)) {
            throw new AuthorizeException("add new personal style not allowed");
        }

        CslStyle cslStyle = createCslStyle(context, alias, cslFileName, cslFileInputStream);

        cslStyle.setSite(site);

        return cslStyle;
    }

    @Override
    public CslStyle create(Context context, String alias, EPerson eperson,
                           String cslFileName, InputStream cslFileInputStream) throws SQLException, IOException, AuthorizeException {

        if (find(context, alias, eperson) != null) {
            throw new IllegalArgumentException("csl style already exists for alias: " + alias + " and user with email: " + context.getCurrentUser().getEmail());
        }

        if (!allowAddPersonalStyle(context)) {
            throw new AuthorizeException("add new personal style not allowed");
        }

        CslStyle cslStyle = createCslStyle(context, alias, cslFileName, cslFileInputStream);

        cslStyle.setEperson(eperson);

        return cslStyle;
    }

    protected CslStyle createCslStyle(Context context, String alias, String cslFileName, InputStream cslFileInputStream)
            throws SQLException, IOException {

        File directory = new File(CSL_FILE_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String cslFilePath = CSL_FILE_DIR + (CSL_FILE_DIR.endsWith("/") ? "" : "/") + UUID.randomUUID() + "_" + cslFileName;

        CslStyle cslStyle = new CslStyle();
        cslStyle.setAlias(alias);
        cslStyle.setCslFileName(cslFileName);
        cslStyle.setCslFilePath(cslFilePath);

        File file = new File(cslFilePath);
        if (!file.createNewFile()) {
            throw new IOException("file already exists: " + cslFilePath);
        }

        FileOutputStream outputStream = new FileOutputStream(file);
        copy(cslFileInputStream, outputStream);
        outputStream.close();

        return cslStyleDAO.create(context, cslStyle);
    }

    @Override
    public CslStyle find(Context context, Integer id) throws SQLException {
        return cslStyleDAO.findByID(context, CslStyle.class, id);
    }

    @Override
    public CslStyle find(Context context, String alias, Site site) throws SQLException {
        return cslStyleDAO.find(context, alias, site);
    }

    @Override
    public CslStyle find(Context context, String alias, EPerson eperson) throws SQLException {
        return cslStyleDAO.find(context, alias, eperson);
    }

    @Override
    public CslStyle find(Context context, String alias, boolean global) throws SQLException {

        if (global)
            return cslStyleDAO.find(context, alias, ContentServiceFactory.getInstance().getSiteService().findSite(context));
        else
            return cslStyleDAO.find(context, alias, context.getCurrentUser());
    }

    @Override
    public List<CslStyle> findBySite(Context context, Site site) throws SQLException {
        return cslStyleDAO.findBySite(context, site);
    }

    @Override
    public List<CslStyle> findByEPerson(Context context, EPerson eperson) throws SQLException {
        if(eperson != null)
        {
            return cslStyleDAO.findByEPerson(context, eperson);
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<CslStyle> findBySiteOrEPerson(Context context, Site site, EPerson eperson) throws SQLException {
        return cslStyleDAO.findBySiteOrEPerson(context, site, eperson);
    }

    @Override
    public void delete(Context context, CslStyle cslStyle) throws SQLException {
        cslStyleDAO.delete(context, cslStyle);
    }

    public boolean isAuthorized(Context context, CslStyle cslStyle) throws SQLException {

        return context.getCurrentUser() != null && (
                cslStyle.isGlobal() ||
                        context.getCurrentUser().equals(cslStyle.getEperson()) ||
                        AuthorizeServiceFactory.getInstance().getAuthorizeService().isAdmin(context)
        );
    }

    @Override
    public boolean allowAddPersonalStyle(Context context) throws SQLException {
        return findByEPerson(context, context.getCurrentUser()).size() <
                DSpaceServicesFactory.getInstance().getConfigurationService().getIntProperty("citations.personal_styles_limit", 10);
    }

    public String getCitationEntry(List<Item> items, CslStyle cslStyle, String format)
            throws IOException, ParseException {
        String cslFileContent = getCslFileContent(cslStyle);
        return  getCitationEntry(items, cslFileContent, format);
    }


    public String getCitationEntry(List<Item> items, String cslFileContent, String format)
            throws IOException, ParseException {
        List<String> uuids = new LinkedList<>();
        for (Item item : items) {
            uuids.add(String.valueOf(item.getID()));
        }

        DSpaceListItemDataProvider dSpaceListItemDataProvider = DSpaceServicesFactory.getInstance().getServiceManager().getServiceByName("DSpaceListItemDataProvider", DSpaceListItemDataProvider.class);
        dSpaceListItemDataProvider.processItems(items);
        CSL citeproc = new CSL(dSpaceListItemDataProvider, cslFileContent);
        citeproc.setOutputFormat(format);
        citeproc.registerCitationItems(uuids.toArray(new String[uuids.size()]));
        return citeproc.makeBibliography().makeString();
    }

    public String getCitationEntry(Item item, CslStyle cslStyle, String format)
            throws IOException, ParseException {
        List<Item> list = new LinkedList<>();
        list.add(item);

        return getCitationEntry(list, cslStyle, format);
    }


    private String getCslFileContent(CslStyle cslStyle) throws IOException {
        String cslFilePath = cslStyle.getCslFilePath();
        return new String(Files.readAllBytes(Paths.get(cslFilePath)));
    }
}
