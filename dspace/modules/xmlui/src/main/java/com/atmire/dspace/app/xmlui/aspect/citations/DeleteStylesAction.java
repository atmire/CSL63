package com.atmire.dspace.app.xmlui.aspect.citations;

import static java.lang.Integer.parseInt;
import static org.apache.cocoon.environment.ObjectModelHelper.getRequest;
import static org.apache.commons.lang.ArrayUtils.isEmpty;
import static org.dspace.app.xmlui.utils.ContextUtil.obtainContext;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.atmire.content.factory.CSLCitationServiceFactory;
import com.atmire.csl.service.CSLCitationService;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.acting.AbstractAction;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.dspace.core.Context;

public class DeleteStylesAction extends AbstractAction {

    private static CSLCitationService cslCitationService = CSLCitationServiceFactory.getInstance().getCslCitationService();

    public Map act(Redirector redirector, SourceResolver resolver, Map objectModel, String source, Parameters parameters) throws SQLException, IOException, ProcessingException {

        Request request = getRequest(objectModel);
        Context context = obtainContext(objectModel);

        if (isEmpty(request.getParameterValues("delete_id"))) {
            return new HashMap() {
                {
                    put("outcome", "failure");
                    put("message", "xmlui.Citations.DeleteStylesAction.no_styles_selected");
                }
            };
        }

        for (String id : request.getParameterValues("delete_id")) {
            cslCitationService.delete(context, cslCitationService.find(context, parseInt(id)));
        }

        context.commit();

        return new HashMap() {
            {
                put("outcome", "success");
                put("message", "xmlui.Citations.DeleteStylesAction.notice");
            }
        };
    }
}
