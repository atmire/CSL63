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

    <xsl:template name="renderGearButtonExport">
        <button class="btn btn-default dropdown-toggle pull-right" data-toggle="dropdown">
            <span class="glyphicon glyphicon-cog" aria-hidden="true"/>
        </button>
    </xsl:template>

</xsl:stylesheet>