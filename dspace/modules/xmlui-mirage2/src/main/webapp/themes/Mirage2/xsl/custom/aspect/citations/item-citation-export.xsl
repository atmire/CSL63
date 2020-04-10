<xsl:stylesheet
        xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
        xmlns:dri="http://di.tamu.edu/DRI/1.0/"
        xmlns:mets="http://www.loc.gov/METS/"
        xmlns:dim="http://www.dspace.org/xmlns/dspace/dim"
        xmlns:xlink="http://www.w3.org/TR/xlink/"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
        xmlns="http://www.w3.org/1999/xhtml"
        xmlns:util="org.dspace.app.xmlui.utils.XSLUtils"
        xmlns:xalan="http://xml.apache.org/xalan"
        xmlns:encoder="xalan://java.net.URLEncoder"
        exclude-result-prefixes="xalan encoder i18n dri mets dim  xlink xsl util">

    <xsl:output indent="yes"/>

    <xsl:template name="itemCitationExportButton">
        <xsl:if test="$document/dri:body//dri:div[@n='citation-controls-gear']">

            <div class="btn-group sort-export-menu pull-right">
                <button type="button" class="btn btn-default dropdown-toggle export-button"
                        data-toggle="dropdown">
                    <span class="export-text">
                        <i18n:text>xmlui.dri2xhtml.METS-1.0.citations</i18n:text>
                    </span>
                </button>

                <ul class="dropdown-menu" role="menu">
                    <xsl:call-template name="exportCitationOptions"/>
                </ul>
            </div>
        </xsl:if>

    </xsl:template>

    <xsl:template name="exportCitationOptions">
        <xsl:apply-templates
                select="$document/dri:body//dri:div[@n='citation-controls-gear']/dri:list/dri:list[@n='eperson-citations']"
                mode="eperson-citations"/>
        <xsl:if test="$document/dri:body//dri:div[@n='citation-controls-gear']/dri:list/dri:list[@n='eperson-citations']
                and $document/dri:body//dri:div[@n='citation-controls-gear']/dri:list/dri:list[@n='global-citations']">
            <li class="single-search divider"/>
        </xsl:if>
        <xsl:apply-templates
                select="$document/dri:body//dri:div[@n='citation-controls-gear']/dri:list/dri:list[@n='global-citations']"
                mode="global-citations"/>
        <xsl:if test="$document/dri:body//dri:div[@n='citation-controls-gear']/dri:list/dri:list[@n='eperson-citations']
                or $document/dri:body//dri:div[@n='citation-controls-gear']/dri:list/dri:list[@n='global-citations']">
            <li class="single-search divider"/>
        </xsl:if>
        <xsl:call-template name="item-citation-help-link"/>
    </xsl:template>

    <xsl:template name="item-citation-help-link">
        <li>
            <a href="#" id="item-citation-help" data-toggle="modal" data-target="#itemCitationHelpModal">
                <span class="glyphicon glyphicon-ok btn-xs active">
                    <xsl:attribute name="class">
                        <xsl:text>glyphicon glyphicon-ok btn-xs active invisible</xsl:text>
                    </xsl:attribute>
                </span>
                <i18n:text>xmlui.export.help</i18n:text>
            </a>
        </li>
    </xsl:template>

    <xsl:template name="item-citation-help-modal">
        <div class="modal fade" id="itemCitationHelpModal" tabindex="-1" role="dialog"
             aria-labelledby="itemCitationHelpModal">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h4 class="modal-title">
                            <i18n:text>xmlui.citation.export.help-title</i18n:text>
                        </h4>
                    </div>
                    <div class="modal-body">
                        <i18n:text>xmlui.citation.export.item.help-text</i18n:text>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                    </div>
                </div>
            </div>
        </div>
    </xsl:template>



</xsl:stylesheet>