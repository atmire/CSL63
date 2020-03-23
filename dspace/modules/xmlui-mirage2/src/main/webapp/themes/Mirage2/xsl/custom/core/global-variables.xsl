<xsl:stylesheet xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
	xmlns:dri="http://di.tamu.edu/DRI/1.0/"
	xmlns:mets="http://www.loc.gov/METS/"
	xmlns:dim="http://www.dspace.org/xmlns/dspace/dim"
    xmlns:xlink="http://www.w3.org/TR/xlink/"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:util="org.dspace.app.xmlui.utils.XSLUtils"
	xmlns:xalan="http://xml.apache.org/xalan"
    xmlns:encoder="xalan://java.net.URLEncoder"
	exclude-result-prefixes="xalan encoder dri mets xlink xsl util">

    <xsl:output indent="yes"/>

    <!--<xsl:variable name="discoveryResultCount" select="//dri:div[contains(@id,'div.search-results')]/@itemsTotal"/>-->
    <xsl:variable name="discoveryResultCount">
        <xsl:choose>
            <xsl:when test="//dri:div[@id='aspect.discovery.SimpleSearch.div.search-results'
                or @id='aspect.artifactbrowser.ConfigurableBrowse.div.browse-by-title-results']">
                <xsl:value-of select="//dri:div[@id='aspect.discovery.SimpleSearch.div.search-results'
                    or @id='aspect.artifactbrowser.ConfigurableBrowse.div.browse-by-title-results']/@itemsTotal"/>
            </xsl:when>
            <xsl:when test="//dri:field[@id='aspect.discovery.CommunityRecentSubmissions.field.totalItems'
                or @id='aspect.discovery.CollectionRecentSubmissions.field.totalItems'
                or @id='aspect.discovery.SiteRecentSubmissions.field.totalItems']">
                <xsl:value-of select="//dri:field[@id='aspect.discovery.CommunityRecentSubmissions.field.totalItems'
                    or @id='aspect.discovery.CollectionRecentSubmissions.field.totalItems'
                    or @id='aspect.discovery.SiteRecentSubmissions.field.totalItems']/dri:value/text()"/>
            </xsl:when>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="basketCount">
            <xsl:if test="//dri:div[@id='aspect.exporter.ViewExportBasketTransformer.div.basket-items-overview']">
                <xsl:value-of select="//dri:div[@id='aspect.exporter.ViewExportBasketTransformer.div.basket-items']/@itemsTotal"/>
            </xsl:if>
    </xsl:variable>
    <xsl:variable name="maxExporterResults" select="$pagemeta/dri:metadata[@element='max-exported-results']"/>


</xsl:stylesheet>