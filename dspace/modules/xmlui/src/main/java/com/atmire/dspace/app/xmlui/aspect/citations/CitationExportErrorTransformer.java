/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 * <p>
 * http://www.dspace.org/license/
 */
package com.atmire.dspace.app.xmlui.aspect.citations;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static org.apache.cocoon.environment.ObjectModelHelper.getRequest;
import static org.apache.cocoon.environment.http.HttpEnvironment.HTTP_RESPONSE_OBJECT;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.http.HttpServletResponse;

import org.apache.cocoon.ResourceNotFoundException;
import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.dspace.app.xmlui.wing.Message;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.Body;
import org.dspace.app.xmlui.wing.element.Division;
import org.dspace.app.xmlui.wing.element.PageMeta;
import org.dspace.authorize.AuthorizeException;
import org.xml.sax.SAXException;

public class CitationExportErrorTransformer extends AbstractDSpaceTransformer {

    private static final Message T_title =
            message("xmlui.CitationExportError.title");

    private static final Message T_head =
            message("xmlui.CitationExportError.head");

    private static final Message T_para =
            message("xmlui.CitationExportError.para");

    private static final Message T_go_home =
            message("xmlui.general.go_home");

    private static final Message T_return =
            message("xmlui.general.return");

    private static final Message T_dspace_home =
            message("xmlui.general.dspace_home");

    @Override
    public void addBody(Body body) throws SAXException, WingException, SQLException, IOException, AuthorizeException,
            ResourceNotFoundException {

        Division division = body.addDivision("citation-export-error", "primary");

        division.setHead(T_head);
        division.addPara(T_para);

        String referer = getRequest(objectModel).getHeader("referer");
        if (isNotBlank(referer)) {
            division.addPara().addXref(referer, T_return);
        } else {
            division.addPara().addXref(contextPath + "/", T_go_home);
        }

        ((HttpServletResponse) objectModel.get(HTTP_RESPONSE_OBJECT)).setStatus(SC_INTERNAL_SERVER_ERROR);
    }

    @Override
    public void addPageMeta(PageMeta pageMeta) throws WingException {

        pageMeta.addMetadata("title").addContent(T_title);
        pageMeta.addTrailLink(contextPath + "/", T_dspace_home);
    }
}
