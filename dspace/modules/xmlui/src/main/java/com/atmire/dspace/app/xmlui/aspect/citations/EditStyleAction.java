package com.atmire.dspace.app.xmlui.aspect.citations;

import static java.lang.Integer.parseInt;
import static org.apache.cocoon.environment.ObjectModelHelper.getRequest;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.dspace.app.xmlui.utils.ContextUtil.obtainContext;
import static org.jsoup.helper.StringUtil.isBlank;

import java.util.HashMap;
import java.util.Map;

import com.atmire.content.factory.CSLCitationServiceFactory;
import com.atmire.csl.CslStyle;
import com.atmire.csl.service.CSLCitationService;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.acting.AbstractAction;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.core.Context;

public class EditStyleAction extends AbstractAction {

    private static CSLCitationService cslCitationService = CSLCitationServiceFactory.getInstance().getCslCitationService();

    public Map act(Redirector redirector, SourceResolver resolver, Map objectModel, String source, Parameters parameters) throws Exception {

        Request request = getRequest(objectModel);
        Context context = obtainContext(objectModel);

        CslStyle style = cslCitationService.find(context, parseInt(request.getParameter("id")));

        String alias = request.getParameter("alias");
        if (isBlank(alias)) alias = style.getCslFileName();
        final boolean global = isNotEmpty(request.getParameter("global"));

        CslStyle existingStyle = cslCitationService.find(context, alias, global);
        if (existingStyle != null && !existingStyle.equals(style)) {
            return new HashMap() {
                {
                    put("outcome", "failure");
                    put("message", "xmlui.Citations.AddNewStyleAction.unique_error" + (global ? ".global" : ""));
                }
            };
        }

        style.setAlias(alias);
        if (global) {
            style.setSite(ContentServiceFactory.getInstance().getSiteService().findSite(context));
            style.setEperson(null);
        } else {
            style.setSite(null);
            style.setEperson(context.getCurrentUser());
        }

        context.commit();

        return null;
    }
}
