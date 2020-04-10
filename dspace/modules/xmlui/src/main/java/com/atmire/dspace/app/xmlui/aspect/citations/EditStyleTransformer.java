package com.atmire.dspace.app.xmlui.aspect.citations;

import static com.atmire.dspace.app.xmlui.aspect.citations.CitationsTransformer.T_add_new_style_alias_help;
import static com.atmire.dspace.app.xmlui.aspect.citations.CitationsTransformer.T_add_new_style_alias_label;
import static com.atmire.dspace.app.xmlui.aspect.citations.CitationsTransformer.T_add_new_style_file_label;
import static com.atmire.dspace.app.xmlui.aspect.citations.CitationsTransformer.T_add_new_style_global_help;
import static com.atmire.dspace.app.xmlui.aspect.citations.CitationsTransformer.T_add_new_style_global_label;
import static com.atmire.dspace.app.xmlui.aspect.citations.CitationsTransformer.T_add_new_style_limit_reached;
import static java.lang.Integer.parseInt;
import static org.apache.cocoon.environment.ObjectModelHelper.getRequest;
import static org.dspace.app.xmlui.wing.element.List.TYPE_FORM;

import java.io.IOException;
import java.sql.SQLException;

import com.atmire.content.factory.CSLCitationServiceFactory;
import com.atmire.csl.CslStyle;
import com.atmire.csl.service.CSLCitationService;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.ResourceNotFoundException;
import org.apache.cocoon.environment.Request;
import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.dspace.app.xmlui.wing.Message;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.Body;
import org.dspace.app.xmlui.wing.element.CheckBox;
import org.dspace.app.xmlui.wing.element.Division;
import org.dspace.app.xmlui.wing.element.List;
import org.dspace.app.xmlui.wing.element.PageMeta;
import org.dspace.app.xmlui.wing.element.Text;
import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.factory.AuthorizeServiceFactory;
import org.xml.sax.SAXException;

public class EditStyleTransformer extends AbstractDSpaceTransformer {

    protected static final Message T_title = message("xmlui.Citations.EditStyleTransformer.title");
    protected static final Message T_dspace_home = message("xmlui.general.dspace_home");
    protected static final Message T_trail = message("xmlui.Citations.EditStyleTransformer.trail");

    protected static final Message T_head = message("xmlui.Citations.EditStyleTransformer.head");
    protected static final Message T_edit_style_button = message("xmlui.Citations.EditStyleTransformer.edit_style_button");
    protected static final Message T_cancel_button = message("xmlui.Citations.EditStyleTransformer.cancel_button");

    private static CSLCitationService cslCitationService = CSLCitationServiceFactory.getInstance().getCslCitationService();

    @Override
    public void addBody(Body body) throws SAXException, WingException,
            SQLException, IOException, AuthorizeException, ProcessingException {

        Request request = getRequest(objectModel);

        CslStyle cslStyle = getCslStyle();

        Division editStyleDiv = body.addInteractiveDivision("edit_style_div",  request.getContextPath() + "/citations", "post", "");
        editStyleDiv.addHidden("id").setValue(cslStyle.getId());
        editStyleDiv.setHead(T_head);

        List editStyleList = editStyleDiv.addList("edit_style_list", TYPE_FORM);

        editStyleList.addLabel(T_add_new_style_file_label);
        editStyleList.addItem();
        editStyleList.addItem().addXref(request.getContextPath() + "/citations/style/" + cslStyle.getId(), cslStyle.getCslFileName());

        Text alias = editStyleList.addItem().addText("alias");
        alias.setLabel(T_add_new_style_alias_label);
        alias.setHelp(T_add_new_style_alias_help);
        alias.setValue(cslStyle.getAlias());

        if (AuthorizeServiceFactory.getInstance().getAuthorizeService().isAdmin(context)) {
            CheckBox global = editStyleList.addItem().addCheckBox("global");
            global.setLabel(T_add_new_style_global_label);
            global.setHelp(T_add_new_style_global_help);
            global.addOption("global");

            if (cslStyle.isGlobal()) {
                global.setOptionSelected("global");

                if (!cslCitationService.allowAddPersonalStyle(context)) {
                    global.setReadonly(true);
                    global.setHelp(T_add_new_style_limit_reached);
                }
            }
        }

        editStyleDiv.addPara().addButton("edit").setValue(T_edit_style_button);
        editStyleDiv.addPara().addButton("cancel").setValue(T_cancel_button);
    }

    protected CslStyle getCslStyle() throws ResourceNotFoundException {

        try {
            CslStyle style = cslCitationService.find(context, parseInt(parameters.getParameter("edit_id")));
            if (cslCitationService.isAuthorized(context, style)) {
                return style;
            }
        } catch (Exception e) {
            getLogger().error(e);
        }

        throw new ResourceNotFoundException("no citation style could be found");
    }

    @Override
    public void addPageMeta(PageMeta pageMeta) throws WingException {

        pageMeta.addMetadata("title").addContent(T_title);

        pageMeta.addTrailLink(contextPath + "/", T_dspace_home);
        pageMeta.addTrailLink(null, T_trail);
    }
}
