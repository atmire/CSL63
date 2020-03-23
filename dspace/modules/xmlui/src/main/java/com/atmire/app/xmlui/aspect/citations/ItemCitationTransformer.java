package com.atmire.app.xmlui.aspect.citations;

import java.io.IOException;
import java.sql.SQLException;

import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.dspace.app.xmlui.utils.HandleUtil;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.Body;
import org.dspace.app.xmlui.wing.element.Division;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.services.ConfigurationService;
import org.dspace.services.factory.DSpaceServicesFactory;
import org.xml.sax.SAXException;

public class ItemCitationTransformer extends AbstractDSpaceTransformer {

    private ConfigurationService configurationService = DSpaceServicesFactory.getInstance().getConfigurationService();


    public void addBody(Body body) throws SAXException, WingException,
            SQLException, IOException, AuthorizeException {

        DSpaceObject dso = HandleUtil.obtainHandle(objectModel);
        if (!(dso instanceof Item))
        {
            return;
        }

        Division division = body.addDivision("item-view","primary");

        division.addHidden("citation-export-base").setValue(
                configurationService.getProperty("dspace.url") + "/discover/citation-export?itemUuid=" + dso.getID());

        ExportCitationUI exportCitationUI = new ExportCitationUI();
        exportCitationUI.addCitationUI(context, division);
    }

}
