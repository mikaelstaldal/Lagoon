<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

<!-- Output as XHTML -->
<xsl:output method="xml" encoding="iso-8859-1"/>

<xsl:template match="/">
  <main>
    <xsl:apply-templates select="root"/>
  </main>
</xsl:template>

<xsl:template match="foo">
	<bar>FOO: <xsl:apply-templates/> :OOF</bar>
</xsl:template>

</xsl:stylesheet>
