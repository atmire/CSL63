package com.atmire.app.xmlui.aspect.citations;

import static org.apache.cocoon.environment.ObjectModelHelper.getRequest;
import static org.apache.cocoon.environment.http.HttpEnvironment.HTTP_RESPONSE_OBJECT;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletResponse;

import com.atmire.content.factory.CSLCitationServiceFactory;
import com.atmire.csl.CslStyle;
import com.atmire.csl.service.CSLCitationService;
import com.atmire.discovery.export.DiscoveryExportItemSearchService;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Response;
import org.apache.cocoon.reading.AbstractReader;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.dspace.app.xmlui.aspect.discovery.DiscoveryUIUtils;
import org.dspace.app.xmlui.utils.ContextUtil;
import org.dspace.authorize.factory.AuthorizeServiceFactory;
import org.dspace.content.Item;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.core.Context;
import org.dspace.services.factory.DSpaceServicesFactory;

public class CitationExportReader extends AbstractReader {

    public static Logger log = Logger.getLogger(CitationExportReader.class);

    public void generate() throws IOException {
        try {
            Context context = ContextUtil.obtainContext(objectModel);
            Request request = ObjectModelHelper.getRequest(objectModel);
            Response response = ObjectModelHelper.getResponse(objectModel);


            String format = request.getParameter("format");
            CitationFormat citationFormat = DSpaceServicesFactory.getInstance().getServiceManager()
                                                                 .getServiceByName(format, CitationFormat.class);

            int cslStyleId = Integer.parseInt(request.getParameter("citationStyle"));


            CSLCitationService cslCitationService = CSLCitationServiceFactory.getInstance().getCslCitationService();
            CslStyle cslStyle = cslCitationService.find(context, cslStyleId);

            String itemUuid = request.getParameter("itemUuid");

            String exportFile = "citation-export." + citationFormat.getFileExtension();

            if (StringUtils.isNotBlank(itemUuid)) {
                provideItemCitation(context, citationFormat.getFormat(), cslCitationService, cslStyle, itemUuid,
                                    response,
                                    exportFile);
            } else {

                Map<String, String> filterParameters = request.getParameters();
                String[] filterQueries = DiscoveryUIUtils.getFilterQueries(request, context);
                provideSearchCitation(context, filterParameters, filterQueries, citationFormat.getFormat(),
                                      cslCitationService,
                                      cslStyle, response, exportFile);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);

            ((HttpServletResponse) objectModel.get(HTTP_RESPONSE_OBJECT)).sendRedirect(
                    getRequest(objectModel).getContextPath() + "/discover/citation-export/error"
            );
        }
    }

    private void provideSearchCitation(final Context context, final Map<String, String> filterParameters,
                                       final String[] filterQueries, final String format,
                                       final CSLCitationService cslCitationService, final CslStyle cslStyle,
                                       Response response, String exportFileName) throws IOException, ParseException, SQLException {

        int maxResults = getMaxResults(context);

        DiscoveryExportItemSearchService discoveryExportItemSearchService =
                new DiscoveryExportItemSearchService();

        List<Item> items = discoveryExportItemSearchService
                .getItems(context, filterParameters, filterQueries, maxResults);

        String citationEntry = cslCitationService.getCitationEntry(items, cslStyle, format);
        if (StringUtils.isNotBlank(citationEntry)) {
            serveExportStream(context, response, exportFileName, citationEntry);
        } else {
            log.warn("no file to export");
        }
    }

    public static int getMaxResults(Context context) throws SQLException {

        String config;

        if (context.getCurrentUser() == null) {
            config = "discovery.citation.export.maxResults.anonymous";
        } else {
            if (AuthorizeServiceFactory.getInstance().getAuthorizeService().isAdmin(context)) {
                config = "discovery.citation.export.maxResults.admin";
            } else {
                config = "discovery.citation.export.maxResults.eperson";
            }
        }

        return DSpaceServicesFactory.getInstance().getConfigurationService().getIntProperty(config, 500);
    }


    private void provideItemCitation(final Context context, final String format,
                                     final CSLCitationService cslCitationService, final CslStyle cslStyle,
                                     final String itemUuid, Response response, String exportFileName)
            throws SQLException, IOException, ParseException {
        Item item = ContentServiceFactory.getInstance().getItemService()
                                         .find(context, UUID.fromString(itemUuid));

        if (item != null) {
            String citationEntry = cslCitationService.getCitationEntry(item, cslStyle, format);
            serveExportStream(context, response, exportFileName, citationEntry);

        } else {
            log.warn("no file to export");
        }
    }

    private void serveExportStream(Context context, Response response, String exportFileName,
                                   String citationString) throws IOException {

        log.info(org.dspace.core.LogManager.getHeader(context, "CitationExportReader",
                                                      "exported_file: " + exportFileName));
        response.setContentType("application/text; charset=UTF-8");
        response.setHeader("Content-Disposition",
                           "attachment; filename=" + exportFileName);
        response.setContentLength(citationString.getBytes(StandardCharsets.UTF_8).length);


        try (OutputStreamWriter writer =
                     new OutputStreamWriter(out, StandardCharsets.UTF_8)) {
            writer.write(citationString);
        }
    }


}
