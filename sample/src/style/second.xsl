<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

<!-- Output as XHTML -->
<xsl:output method="xml" encoding="iso-8859-1"/>

<xsl:template match="/">
  <final>
    <xsl:apply-templates select="main"/>
  </final>
</xsl:template>

<xsl:template match="bar">
	<apa><xsl:apply-templates/></apa>
</xsl:template>

</xsl:stylesheet>
