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


    <xsl:template match="dri:div[contains(@id,'citation-controls-gear')]"/>

    <xsl:template match="dri:div/dri:list/dri:list" mode="eperson-citations">
        <li class="eperson-citations gear-head dropdown-header">
            <i18n:text>xmlui.citations.export.eperson-citations.head</i18n:text>
        </li>
        <xsl:apply-templates select="dri:item" mode="citation"/>
    </xsl:template>

    <xsl:template match="dri:div/dri:list/dri:list" mode="global-citations">
        <li class="global-citations gear-head dropdown-header">
            <i18n:text>xmlui.citations.export.global-citations.head</i18n:text>
        </li>
        <xsl:apply-templates select="dri:item" mode="citation"/>
    </xsl:template>

    <xsl:template match="dri:item" mode="citation">
        <li class="global-citation citation-style">
            <a>
                <xsl:attribute name="href">
                    <xsl:value-of select="dri:xref/@target"/>
                </xsl:attribute>
                <span class="glyphicon glyphicon-ok btn-xs active">
                    <xsl:attribute name="class">
                        <xsl:text>glyphicon glyphicon-ok btn-xs active invisible</xsl:text>
                    </xsl:attribute>
                </span>
                <i18n:text>
                    <xsl:value-of select="dri:xref"/>
                </i18n:text>
            </a>
        </li>
    </xsl:template>


    <xsl:template name="citation-help-link">
        <li>
            <a href="#" id="help" data-toggle="modal" data-target="#citationHelpModal">
                <span class="glyphicon glyphicon-ok btn-xs active">
                    <xsl:attribute name="class">
                        <xsl:text>glyphicon glyphicon-ok btn-xs active invisible</xsl:text>
                    </xsl:attribute>
                </span>
                <i18n:text>xmlui.export.help</i18n:text>
            </a>
        </li>
    </xsl:template>

        <xsl:template name="citation-help-modal">
        <div class="modal fade" id="citationHelpModal" tabindex="-1" role="dialog" aria-labelledby="citationHelpModal">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h4 class="modal-title" id="myModalLabel">
                            <i18n:text>xmlui.citation.export.help-title</i18n:text>
                        </h4>
                    </div>
                    <div class="modal-body">
                        <i18n:translate>
                        <i18n:text>xmlui.citation.export.help-text</i18n:text>
                            <i18n:param>
                                <xsl:value-of
                                        select="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='citation-export'][@qualifier='max-results']"/>
                            </i18n:param>
                        </i18n:translate>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                    </div>
                </div>
            </div>
        </div>
    </xsl:template>

    <xsl:template name="citation-format-modal">
        <div class="modal fade" id="citationFormatModal" tabindex="-1" role="dialog"
             aria-labelledby="citationFormatModal">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h4 class="modal-title">
                            <i18n:text>xmlui.citation.export.format-title</i18n:text>
                        </h4>
                    </div>
                    <div class="modal-body">
                        <xsl:apply-templates select="//dri:field[@n='citation-format-radio']"/>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default citation-export-button" data-dismiss="modal">Export</button>
                    </div>
                </div>
            </div>
        </div>
    </xsl:template>


</xsl:stylesheet>