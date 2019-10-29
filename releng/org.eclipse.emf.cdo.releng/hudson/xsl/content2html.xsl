<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" omit-xml-declaration="yes" indent="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:template match="/">
		<html xmlns="http://www.w3.org/1999/xhtml">
			<xsl:apply-templates select="repository"/>
		</html>
	</xsl:template>

	<xsl:template match="repository">
		<head>
			<title>
				<xsl:value-of select="@name"/>
			</title>
		</head>
		<body>
			<h1>
				<xsl:value-of select="@name"/>
			</h1>
			<p>
				<em>For information about CDO or Net4j, see their
					<a href="http://www.eclipse.org/cdo">homepage</a> or
					<a href="http://wiki.eclipse.org/CDO">wiki</a>.
					<br/> For information about installing or updating Eclipse software, see the
					<a
						href="http://help.eclipse.org/2019-09/index.jsp?topic=/org.eclipse.platform.doc.user/tasks/tasks-124.htm">
						Eclipse Platform Help</a>.
					<br/> Some plugins require third party software from p2 repositories listed in this
					<a href="bookmarks.xml">bookmarks.xml</a> file.</em>

			</p>
			<table border="0">
				<tr>
					<td colspan="2">
						<hr/>
						<h2>Features</h2>
					</td>
				</tr>
				<xsl:apply-templates select="//provided[@namespace='org.eclipse.update.feature']">
					<xsl:sort select="@name"/>
				</xsl:apply-templates>
				<tr>
					<td colspan="2">
						<hr/>
						<h2>Plugins</h2>
					</td>
				</tr>
				<xsl:apply-templates select="//provided[@namespace='osgi.bundle']">
					<xsl:sort select="@name"/>
				</xsl:apply-templates>
			</table>
		</body>
	</xsl:template>

	<xsl:template match="provided">
		<tr>
			<td>
				<xsl:value-of select="@name"/>
			</td>
			<td>
				<xsl:value-of select="@version"/>
			</td>
		</tr>
	</xsl:template>

</xsl:stylesheet>