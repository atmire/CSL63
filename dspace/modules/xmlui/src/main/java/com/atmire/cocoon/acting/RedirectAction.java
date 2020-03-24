package com.atmire.cocoon.acting;

import static org.apache.cocoon.environment.ObjectModelHelper.getRequest;
import static org.apache.cocoon.environment.http.HttpEnvironment.HTTP_RESPONSE_OBJECT;

import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.acting.AbstractAction;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;

public class RedirectAction extends AbstractAction {

    public Map act(Redirector redirector, SourceResolver resolver, Map objectModel, String source, Parameters parameters) throws Exception {

        ((HttpServletResponse) objectModel.get(HTTP_RESPONSE_OBJECT)).sendRedirect(
                getRequest(objectModel).getContextPath() + "/" + parameters.getParameter("path")
        );

        return null;
    }
}
