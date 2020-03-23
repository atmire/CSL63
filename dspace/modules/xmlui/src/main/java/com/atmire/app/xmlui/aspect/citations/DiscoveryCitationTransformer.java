package com.atmire.app.xmlui.aspect.citations;

import static com.atmire.app.xmlui.aspect.citations.CitationExportReader.getMaxResults;
import static org.dspace.app.xmlui.utils.ContextUtil.obtainContext;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.commons.lang3.StringUtils;
import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.dspace.app.xmlui.utils.HandleUtil;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.Body;
import org.dspace.app.xmlui.wing.element.Division;
import org.dspace.app.xmlui.wing.element.PageMeta;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.DSpaceObject;
import org.dspace.core.Context;
import org.dspace.services.ConfigurationService;
import org.dspace.services.factory.DSpaceServicesFactory;
import org.xml.sax.SAXException;

public class DiscoveryCitationTransformer extends AbstractDSpaceTransformer {

    private ConfigurationService configurationService = DSpaceServicesFactory.getInstance().getConfigurationService();

    @Override
    public void addPageMeta(PageMeta pageMeta) throws WingException, SQLException {

        Context context = obtainContext(objectModel);

        pageMeta.addMetadata("citation-export", "max-results").addContent(
                getMaxResults(context)
        );
    }

    public void addBody(Body body) throws SAXException, WingException,
            SQLException, IOException, AuthorizeException {

        Division search = body.addDivision("search", "primary");
        Division results = search.addDivision("search-results", "primary");

        addCitationUrl(results);

        ExportCitationUI exportCitationUI = new ExportCitationUI();
        exportCitationUI.addCitationUI(context, results);
    }

    private void addCitationUrl(final Division results)
            throws WingException, SQLException {


        DSpaceObject dso = HandleUtil.obtainHandle(objectModel);


        String pageURLMask = generateURL("", ObjectModelHelper.getRequest(objectModel).getParameters());
        Map<String, String[]> filterQueryParams = getParameterFilterQueries();
        if (filterQueryParams != null) {
            StringBuilder maskBuilder = new StringBuilder(pageURLMask);
            for (String filterQueryParam : filterQueryParams.keySet()) {
                String[] filterQueryValues = filterQueryParams.get(filterQueryParam);
                if (filterQueryValues != null) {
                    for (String filterQueryValue : filterQueryValues) {
                        maskBuilder.append("&").append(filterQueryParam).append("=")
                                   .append(encodeForURL(filterQueryValue));
                    }
                }
            }

            pageURLMask = maskBuilder.toString();
        }


        String citationExportUrl = configurationService
                .getProperty("dspace.url") + "/discover/citation-export?list=discovery";


        if (dso != null) {
            citationExportUrl += "&handle=" + dso.getHandle();
        }

        if (StringUtils.isNotBlank(pageURLMask.replace("?", ""))) {
            citationExportUrl += "&" + pageURLMask.replace("?", "");
        }

        results.addHidden("citation-export-base").setValue(citationExportUrl);
    }

    private Map<String, String[]> getParameterFilterQueries() {
        try {
            Map<String, String[]> result = new HashMap<>();
            result.put("fq", ObjectModelHelper.getRequest(objectModel).getParameterValues("fq"));
            return result;
        } catch (Exception e) {
            return null;
        }
    }

}
