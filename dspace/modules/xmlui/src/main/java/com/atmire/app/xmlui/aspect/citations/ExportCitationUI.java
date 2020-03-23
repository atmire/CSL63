package com.atmire.app.xmlui.aspect.citations;

import java.sql.SQLException;

import com.atmire.content.factory.CSLCitationServiceFactory;
import com.atmire.csl.CslStyle;
import com.atmire.csl.service.CSLCitationService;
import org.dspace.app.xmlui.wing.Message;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.Division;
import org.dspace.app.xmlui.wing.element.List;
import org.dspace.app.xmlui.wing.element.Radio;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.core.Context;
import org.dspace.services.factory.DSpaceServicesFactory;


public class ExportCitationUI {

    private CSLCitationService cslCitationService = CSLCitationServiceFactory.getInstance()
                                                                             .getCslCitationService();

    private static String T_citation_format_name = "xmlui.citation.format.";

    private java.util.List<CslStyle> globalList;
    private java.util.List<CslStyle> ePersonList;


    public void addCitationUI(Context context, Division division)
            throws WingException, SQLException {

        globalList = cslCitationService.findBySite(context, ContentServiceFactory.getInstance().getSiteService().findSite(context));
        ePersonList = cslCitationService.findByEPerson(context, context.getCurrentUser());

        Division citationControls = division.addDivision("citation-controls-gear", "controls-citation-wrapper");
        List citationList = citationControls.addList("citation-options");

        addCitationList(citationList);

        List formatList = citationControls.addList("citation-formats");
        addFormatList(formatList);
    }


    private void addFormatList(List formatList) throws WingException {

        java.util.List<CitationFormat> formats = DSpaceServicesFactory.getInstance().getServiceManager()
                                                                      .getServiceByName("citationFormatList",
                                                                                        java.util.List.class);

        Radio radio = formatList.addItem().addRadio("citation-format-radio", "citation-format-radio");

        if (!formats.isEmpty()) {
            radio.addOption(true, formats.get(0).getName(),
                            new Message("default", T_citation_format_name + formats.get(0).getName()));
        }

        if (formats.size() > 1) {
            for (int i = 1; i< formats.size(); i++) {
                radio.addOption(formats.get(i).getName(),
                                new Message("default", T_citation_format_name + formats.get(i).getName()));            }
        }

    }


    private void addCitationList(List citationList) throws WingException {

        if (!globalList.isEmpty()) {
            List globalCitations = citationList.addList("global-citations");
            for (CslStyle citationStyle : globalList) {
                globalCitations.addItemXref(String.valueOf(citationStyle.getId()), citationStyle.getAlias());
            }
        }

        if (!ePersonList.isEmpty()) {
            List ePersonCitations = citationList.addList("eperson-citations");
            for (CslStyle citationStyle : ePersonList) {
                ePersonCitations.addItemXref(String.valueOf(citationStyle.getId()), citationStyle.getAlias());
            }
        }
    }

}
