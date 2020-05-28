package com.atmire.dspace.app.xmlui.aspect.citations;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.dspace.app.xmlui.wing.element.List.TYPE_FORM;
import static org.dspace.app.xmlui.wing.element.Row.ROLE_DATA;
import static org.dspace.app.xmlui.wing.element.Row.ROLE_HEADER;

import java.sql.SQLException;

import com.atmire.content.factory.CSLCitationServiceFactory;
import com.atmire.csl.CslStyle;
import com.atmire.csl.service.CSLCitationService;
import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.dspace.app.xmlui.wing.Message;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.Body;
import org.dspace.app.xmlui.wing.element.Button;
import org.dspace.app.xmlui.wing.element.CheckBox;
import org.dspace.app.xmlui.wing.element.Division;
import org.dspace.app.xmlui.wing.element.File;
import org.dspace.app.xmlui.wing.element.List;
import org.dspace.app.xmlui.wing.element.PageMeta;
import org.dspace.app.xmlui.wing.element.Row;
import org.dspace.app.xmlui.wing.element.Table;
import org.dspace.app.xmlui.wing.element.Text;
import org.dspace.authorize.factory.AuthorizeServiceFactory;
import org.dspace.content.factory.ContentServiceFactory;

public class CitationsTransformer extends AbstractDSpaceTransformer {

    protected static final Message T_title = message("xmlui.Citations.CitationsTransformer.title");
    protected static final Message T_dspace_home = message("xmlui.general.dspace_home");
    protected static final Message T_trail = message("xmlui.Citations.CitationsTransformer.trail");

    protected static final Message T_overview_head = message("xmlui.Citations.CitationsTransformer.overview.head");
    protected static final Message T_overview_explanation = message("xmlui.Citations.CitationsTransformer.overview.explanation");
    protected static final Message T_overview_table_header_id = message("xmlui.Citations.CitationsTransformer.overview.table.header.id");
    protected static final Message T_overview_table_header_citation_file = message("xmlui.Citations.CitationsTransformer.overview.table.header.citation_file");
    protected static final Message T_overview_table_header_alias = message("xmlui.Citations.CitationsTransformer.overview.table.header.alias");
    protected static final Message T_overview_table_header_global = message("xmlui.Citations.CitationsTransformer.overview.table.header.global");
    protected static final Message T_overview_delete = message("xmlui.Citations.CitationsTransformer.overview.delete");
    protected static final Message T_overview_edit = message("xmlui.Citations.CitationsTransformer.overview.edit");

    protected static final Message T_add_new_style_head = message("xmlui.Citations.CitationsTransformer.add_new_style.head");
    protected static final Message T_add_new_style_file_label = message("xmlui.Citations.CitationsTransformer.add_new_style.file.label");
    protected static final Message T_add_new_style_help = message("xmlui.Citations.CitationsTransformer.add_new_style.file.help");
    protected static final Message T_add_new_style_alias_label = message("xmlui.Citations.CitationsTransformer.add_new_style.text.label");
    protected static final Message T_add_new_style_alias_help = message("xmlui.Citations.CitationsTransformer.add_new_style.text.help");
    protected static final Message T_add_new_style_global_label = message("xmlui.Citations.CitationsTransformer.add_new_style.global.label");
    protected static final Message T_add_new_style_global_help = message("xmlui.Citations.CitationsTransformer.add_new_style.global.help");
    protected static final Message T_add_new_style_button = message("xmlui.Citations.CitationsTransformer.add_new_style.button");
    protected static final Message T_add_new_style_limit_reached = message("xmlui.Citations.CitationsTransformer.add_new_style.limit_reached");

    private static CSLCitationService cslCitationService = CSLCitationServiceFactory.getInstance().getCslCitationService();

    @Override
    public void addPageMeta(PageMeta pageMeta) throws WingException {
        pageMeta.addMetadata("title").addContent(T_title);

        pageMeta.addTrailLink(contextPath + "/", T_dspace_home);
        pageMeta.addTrailLink(null, T_trail);
    }

    @Override
    public void addBody(Body body) throws WingException, SQLException {

        java.util.List<CslStyle> cslStyles = cslCitationService.findBySiteOrEPerson(context, ContentServiceFactory.getInstance().getSiteService().findSite(context), context.getCurrentUser());

        Division overviewDiv = body.addInteractiveDivision("overviewDiv", "citations", "post", "");

        overviewDiv.setHead(T_overview_head);
        overviewDiv.addPara(T_overview_explanation);

        if (isNotEmpty(cslStyles)) {
            Table overviewTable = overviewDiv.addTable("overviewTable", cslStyles.size() + 1, 6);
            Row overviewTableHeader = overviewTable.addRow(ROLE_HEADER);
            overviewTableHeader.addCellContent("");
            overviewTableHeader.addCellContent(T_overview_table_header_id);
            overviewTableHeader.addCellContent(T_overview_table_header_citation_file);
            overviewTableHeader.addCellContent(T_overview_table_header_alias);
            overviewTableHeader.addCellContent(T_overview_table_header_global);

            for (CslStyle cslStyle : cslStyles) {
                Row row = overviewTable.addRow(ROLE_DATA);

                CheckBox checkBox = row.addCell().addCheckBox("delete_id");
                checkBox.addOption(cslStyle.getId());
                checkBox.setDisabled(cslStyle.isGlobal() && !AuthorizeServiceFactory.getInstance().getAuthorizeService().isAdmin(context));

                row.addCellContent(cslStyle.getId() + "");
                row.addCell().addXref("citations/style/" + cslStyle.getId(), cslStyle.getCslFileName());
                row.addCellContent(cslStyle.getAlias());
                row.addCell(null, null, "").addHighlight("glyphicon " + (cslStyle.isGlobal() ? "glyphicon-ok" : "glyphicon-remove")).addContent(" ");

                Button button = row.addCell().addButton("edit_" + cslStyle.getId());
                button.setValue(T_overview_edit);
                button.setDisabled(cslStyle.isGlobal() && !AuthorizeServiceFactory.getInstance().getAuthorizeService().isAdmin(context));
            }

            overviewDiv.addPara().addButton("delete").setValue(T_overview_delete);
        }

        Division addNewStyleDiv = body.addInteractiveDivision("add_new_style_div", "citations", "multipart", "");
        addNewStyleDiv.setHead(T_add_new_style_head);

        List addNewStyleList = addNewStyleDiv.addList("add_new_style_list", TYPE_FORM);

        File file = addNewStyleList.addItem().addFile("file");
        file.setLabel(T_add_new_style_file_label);
        file.setHelp(T_add_new_style_help);
        file.setRequired();

        Text text = addNewStyleList.addItem().addText("alias");
        text.setLabel(T_add_new_style_alias_label);
        text.setHelp(T_add_new_style_alias_help);
        text.setRequired();

        boolean allowAddPersonalStyle = cslCitationService.allowAddPersonalStyle(context);
        boolean disableAddButton = !allowAddPersonalStyle;

        try {
            if (AuthorizeServiceFactory.getInstance().getAuthorizeService().isAdmin(context)) {
                disableAddButton = false;

                CheckBox global = addNewStyleList.addItem().addCheckBox("global");
                global.addOption("global");
                global.setLabel(T_add_new_style_global_label);
                global.setHelp(T_add_new_style_global_help);
                global.setRequired();

                if (!allowAddPersonalStyle) {
                    global.setReadonly(true);
                    global.setOptionSelected("global");
                    global.setHelp(T_add_new_style_limit_reached);
                }
            }
        } catch (SQLException e) {
            getLogger().error(e);
        }

        Button addButton = addNewStyleDiv.addPara().addButton("add");
        addButton.setValue(T_add_new_style_button);

        if (disableAddButton) {
            addButton.setDisabled(true);
            addNewStyleDiv.addPara(T_add_new_style_limit_reached);
        }
    }

}
