<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" indent="yes" omit-xml-declaration="yes"/>
	
	<xsl:template match="/">
		<xsl:apply-templates select="//repository[not(contains(@location, 'download.oracle.com')) and not(contains(@location, 'totallyirrelevant.zip')) and not(contains(@location, 'tools/ajdt'))]"/>
	</xsl:template>
	
	<xsl:template match="repository">
		<site>
			<xsl:attribute name="url">
				<xsl:value-of select="@location"/>
			</xsl:attribute>
			<xsl:attribute name="selected">true</xsl:attribute>
		</site>
	</xsl:template>
	
</xsl:stylesheet>