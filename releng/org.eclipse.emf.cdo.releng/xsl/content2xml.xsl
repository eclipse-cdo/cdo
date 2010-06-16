<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" indent="yes"/>

	<xsl:template match="/">
		<xsl:apply-templates select="repository"/>
	</xsl:template>

	<xsl:template match="repository">
		<release tag="" integration="true">
			<xsl:attribute name="repository">
				<xsl:value-of select="@name"/>
			</xsl:attribute>
			<xsl:apply-templates select="//provided[@namespace='org.eclipse.update.feature']">
				<xsl:sort select="@name"/>
			</xsl:apply-templates>
			<xsl:apply-templates select="//provided[@namespace='osgi.bundle']">
				<xsl:sort select="@name"/>
			</xsl:apply-templates>
		</release>
	</xsl:template>

	<xsl:template match="provided">
		<element>
			<xsl:attribute name="name">
				<xsl:value-of select="@name"/>
			</xsl:attribute>
			<xsl:attribute name="version">
				<xsl:value-of select="@version"/>
			</xsl:attribute>
			<xsl:attribute name="type">
				<xsl:value-of select="@namespace"/>
			</xsl:attribute>
		</element>
	</xsl:template>

</xsl:stylesheet>