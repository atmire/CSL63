package com.atmire.dspace.app.xmlui.aspect.citations;

import static java.lang.Integer.parseInt;
import static org.apache.cocoon.environment.ObjectModelHelper.getRequest;
import static org.apache.cocoon.environment.ObjectModelHelper.getResponse;
import static org.dspace.app.xmlui.utils.AuthenticationUtil.interruptRequest;
import static org.dspace.app.xmlui.utils.ContextUtil.obtainContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.atmire.content.factory.CSLCitationServiceFactory;
import com.atmire.csl.CslStyle;
import com.atmire.csl.service.CSLCitationService;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.ResourceNotFoundException;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Response;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.environment.http.HttpEnvironment;
import org.apache.cocoon.reading.AbstractReader;
import org.apache.log4j.Logger;
import org.apache.log4j.lf5.util.StreamUtils;
import org.dspace.core.Context;
import org.xml.sax.SAXException;

public class CslCitationReader extends AbstractReader {

    private static final Logger log = Logger.getLogger(CslCitationReader.class);

    private static final String AUTH_REQUIRED_HEADER = "xmlui.Citations.CslCitationReader.auth_header";
    private static final String AUTH_REQUIRED_MESSAGE = "xmlui.Citations.CslCitationReader.auth_message";

    private static CSLCitationService cslCitationService = CSLCitationServiceFactory.getInstance().getCslCitationService();

    protected Response response;
    protected InputStream fileInputStream;
    protected String fileName;
    protected long fileSize;

    @Override
    public void setup(SourceResolver resolver, Map objectModel, String src, Parameters par)
            throws ProcessingException, SAXException, IOException {

        super.setup(resolver, objectModel, src, par);

        try {
            Request request = getRequest(objectModel);
            Context context = obtainContext(objectModel);

            this.response = getResponse(objectModel);

            if (context.getCurrentUser() == null) {

                interruptRequest(objectModel, AUTH_REQUIRED_HEADER, AUTH_REQUIRED_MESSAGE, null);
                ((HttpServletResponse) objectModel.get(HttpEnvironment.HTTP_RESPONSE_OBJECT))
                        .sendRedirect(request.getContextPath() + "/login");
                return;
            }

            CslStyle style = getCslStyle(context, par);
            File file = new File(style.getCslFilePath());

            fileInputStream = new FileInputStream(file);
            fileName = style.getCslFileName();
            fileSize = file.length();

        } catch (SQLException e) {
            throw new ProcessingException("Unable to read citation style", e);
        }
    }

    protected CslStyle getCslStyle(Context context, Parameters par) throws ResourceNotFoundException {

        try {
            CslStyle style = cslCitationService.find(context, parseInt(par.getParameter("id")));
            if (cslCitationService.isAuthorized(context, style)) {
                return style;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        throw new ResourceNotFoundException("no citation style could be found");
    }

    @Override
    public void generate() throws IOException {
        if (fileInputStream == null) {
            return;
        }
        try {
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.setHeader("Content-Length", String.valueOf(fileSize));
            StreamUtils.copy(fileInputStream, out);
        } finally {
            try {
                fileInputStream.close();
                out.close();
            } catch (IOException ioe) {
                log.warn("Caught IO exception when closing a stream: " + ioe.getMessage(), ioe);
            }
        }
    }
}
