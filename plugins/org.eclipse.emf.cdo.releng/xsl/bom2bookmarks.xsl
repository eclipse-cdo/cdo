<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:md="http://www.eclipse.org/buckminster/MetaData-1.0" exclude-result-prefixes="md">
	<xsl:output method="xml" indent="yes" omit-xml-declaration="yes"/>
	
	<xsl:template match="/">
		<xsl:apply-templates select="//md:resolution"/>
	</xsl:template>
	
	<xsl:template match="md:resolution">
		<site>
			<xsl:attribute name="url">
				<xsl:value-of select="@repository"/>
			</xsl:attribute>
			<xsl:attribute name="selected">true</xsl:attribute>
			<xsl:attribute name="name">
				<xsl:value-of select="@repository"/>
			</xsl:attribute>
		</site>
	</xsl:template>
	
</xsl:stylesheet>