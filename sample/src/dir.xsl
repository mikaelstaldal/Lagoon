<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

<xsl:template match="/">
<html>
  <head>
    <title>Directory listing</title>
  </head>

  <body>
    <h1>Directory listing</h1>
	<ul>
		<xsl:apply-templates select="dirlist"/>
	</ul>
  </body>
</html>
</xsl:template>

<xsl:template match="file">
	<li><a href="{@filename}"><xsl:value-of select="@url"/></a>
	Version: <xsl:value-of select="document(@url)/xsl:stylesheet/@version"/></li>
</xsl:template>

</xsl:stylesheet>
