<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

<xsl:template match="/">
<html>
  <head>
    <title>Merge of serveral files</title>
  </head>

  <body>
    <h1>Merge of serveral files</h1>
	
    <xsl:apply-templates select="dirlist"/>
  </body>
</html>
</xsl:template>

<xsl:template match="file">
	<p><xsl:copy-of select="document(@url)/item"/></p>
</xsl:template>

</xsl:stylesheet>

