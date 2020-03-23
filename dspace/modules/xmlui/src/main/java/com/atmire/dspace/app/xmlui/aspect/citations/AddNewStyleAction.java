package com.atmire.dspace.app.xmlui.aspect.citations;

import static org.apache.cocoon.environment.ObjectModelHelper.getRequest;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;
import static org.dspace.app.xmlui.utils.ContextUtil.obtainContext;
import static org.jsoup.helper.StringUtil.isBlank;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.atmire.content.factory.CSLCitationServiceFactory;
import com.atmire.csl.service.CSLCitationService;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.acting.AbstractAction;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.servlet.multipart.Part;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.core.Context;

public class AddNewStyleAction extends AbstractAction {

    private static CSLCitationService cslCitationService = CSLCitationServiceFactory.getInstance().getCslCitationService();

    public Map act(Redirector redirector, SourceResolver resolver, Map objectModel, String source, Parameters parameters) throws SQLException, IOException, AuthorizeException {

        Request request = getRequest(objectModel);
        Context context = obtainContext(objectModel);
        Part filePart = (Part) request.get("file");

        if (filePart == null || filePart.getSize() <= 0) {
            return new HashMap() {
                {
                    put("outcome", "failure");
                    put("message", "xmlui.Citations.AddNewStyleAction.no_file");
                }
            };
        }

        String fileName = filePart.getUploadName();
        String extension =  substringAfterLast(fileName, ".");

        if (!"csl".equals(extension)) {
            return new HashMap() {
                {
                    put("outcome", "failure");
                    put("message", "xmlui.Citations.AddNewStyleAction.extension_error");
                }
            };
        }

        InputStream inputStream = filePart.getInputStream();

        String alias = request.getParameter("alias");
        if (isBlank(alias)) alias = fileName;
        final boolean global = isNotEmpty(request.getParameter("global"));

        if (cslCitationService.find(context, alias, global) != null) {
            return new HashMap() {
                {
                    put("outcome", "failure");
                    put("message", "xmlui.Citations.AddNewStyleAction.unique_error" + (global ? ".global" : ""));
                }
            };
        }

        if (global) {
            cslCitationService.create(context, alias, ContentServiceFactory.getInstance().getSiteService().findSite(context), fileName, inputStream);
        } else {
            cslCitationService.create(context, alias, context.getCurrentUser(), fileName, inputStream);
        }

        context.commit();

        return null;
    }
}
