<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xmi="http://www.omg.org/XMI"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:git="http://www.eclipse.org/oomph/setup/git/1.0" 
  xmlns:jdt="http://www.eclipse.org/oomph/setup/jdt/1.0" 
  xmlns:mylyn="http://www.eclipse.org/oomph/setup/mylyn/1.0" 
  xmlns:pde="http://www.eclipse.org/oomph/setup/pde/1.0" 
  xmlns:predicates="http://www.eclipse.org/oomph/predicates/1.0" 
  xmlns:workingsets="http://www.eclipse.org/oomph/workingsets/1.0"
  xmlns:setup="http://www.eclipse.org/oomph/setup/1.0"
  xmlns:setup.p2="http://www.eclipse.org/oomph/setup/p2/1.0"
  xmlns:setup.targlets="http://www.eclipse.org/oomph/setup/targlets/1.0"
  xmlns:setup.workingsets="http://www.eclipse.org/oomph/setup/workingsets/1.0"> 
    
	<xsl:output method="xml" indent="yes" omit-xml-declaration="no"/>

	<!-- Match the root and start processing -->
	<xsl:template match="/">
		<xsl:apply-templates select="//setupTask[@id='tp']"/>
	</xsl:template>

	<!-- Extract only requirement and first repositoryList from the TargletTask -->
	<xsl:template match="setupTask[@id='tp']">
		<setup:Macro xmi:version="2.0" label="CDO Target Platform">
  		<setupTask>
  			<xsl:copy-of select="@*"/>
  			<xsl:apply-templates select="targlet[@name='CDO']"/>
  		</setupTask>
		</setup:Macro>
	</xsl:template>

	<xsl:template match="targlet[@name='CDO']">
		<targlet>
			<!-- Copy all attributes except activeRepositoryList -->
			<xsl:for-each select="@*">
				<xsl:if test="name() != 'activeRepositoryList'">
					<xsl:attribute name="{name()}"><xsl:value-of select="."/></xsl:attribute>
				</xsl:if>
			</xsl:for-each>
      
			<!-- Copy requirement children except those with 'releng' in their name -->
			<xsl:for-each select="requirement[not(contains(@name, 'releng'))]">
				<xsl:copy-of select="."/>
			</xsl:for-each>
			
      <!-- Copy first repositoryList, but filter repositories and add new first repository -->
			<xsl:for-each select="repositoryList[1]">
				<repositoryList>
					<!-- Copy all attributes except name -->
					<xsl:for-each select="@*">
						<xsl:if test="name() != 'name'">
							<xsl:attribute name="{name()}"><xsl:value-of select="."/></xsl:attribute>
						</xsl:if>
					</xsl:for-each>
			
      		<!-- Add a placeholder repository element for CDO itself -->
					<repository url="https://download.eclipse.org/modeling/emf/cdo/updates"/>
			
      		<!-- Copy filtered repositories, only attributes, no children -->
					<xsl:for-each select="repository[not(contains(@url, 'oracle')) and not(contains(@url, 'aspectj'))]">
						<repository>
							<xsl:for-each select="@*">
								<xsl:attribute name="{name()}"><xsl:value-of select="."/></xsl:attribute>
							</xsl:for-each>
						</repository>
					</xsl:for-each>
				</repositoryList>
			</xsl:for-each>
		</targlet>
	</xsl:template>

</xsl:stylesheet>