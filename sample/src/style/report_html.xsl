<?xml version="1.0" encoding="iso-8859-1"?>

<!-- XSLT stylesheet for transforming the technical reports
     to HTML for screen viewing -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

<xsl:import href="/imp.xsl"/>

<xsl:import href="imphere.xsl"/>

<xsl:include href="/inc.xsl"/>

<xsl:include href="inchere.xsl"/>

<!-- Output as HTML -->
<xsl:output method="html" encoding="iso-8859-1"/>

<xsl:template match="/">
  <!-- Build the main structure of the produced HTML -->
  <html>
    <head>
        <title><xsl:value-of select="$myparam"/></title>
        <meta name="Author" content="{$theparam}"/>

		<!-- <xsl:copy-of select="document('part:thePart')"/> -->

		<xsl:copy-of select="document('includehere.xml')"/>

		<xsl:copy-of select="document('/include.xml')"/>

        <!-- The produced HTML will have an embeeded CSS stylesheet -->
        <style type="text/css">
            h1.main { margin-bottom: 0.2em; }
            p.author { font-style: italic; margin-top: 0; }
            ul.toc { list-style: none; margin-top: 0.2em; }
            p.toc { margin-bottom: 0; font-weight: bolder; }
        </style>
    </head>
    <body>
        <h1 class="main"><xsl:value-of select="report/front/title"/></h1>
        <p class="author">by <xsl:value-of select="report/front/author"/>
        </p>
        <hr/>
        <h2>Table of Contents</h2>
        <!-- Build a TOC from chapters, sections and subsections,
             with hypertext links -->
        <xsl:for-each select="report/body/chapter">
            <xsl:variable name="chapnum">
                <xsl:number count="chapter" format="1"/>
            </xsl:variable>
            <p class="toc"><a href="#chap{$chapnum}">
            Chapter <xsl:value-of select="$chapnum"/> - 
            <xsl:value-of select="title"/></a></p>
            <ul class="toc">
            <xsl:for-each select="section">
                <xsl:variable name="sectnum">
                    <xsl:number count="chapter|section"
                                level="multiple" format="1.1"/>
                </xsl:variable>
                <li><a href="#sect{$sectnum}">
                <xsl:value-of select="$sectnum"/><xsl:text> </xsl:text>
                <xsl:value-of select="title"/></a></li>
                <ul class="toc">
                <xsl:for-each select="subsection">
                    <xsl:variable name="subsectnum">
                        <xsl:number count="chapter|section|subsection"
                                    level="multiple" format="1.1.1"/>
                    </xsl:variable>
                    <li><a href="#subsect{$subsectnum}">
                    <xsl:value-of select="$subsectnum"/>
                    <xsl:text> </xsl:text>
                    <xsl:value-of select="title"/></a></li>
                </xsl:for-each>
                </ul>
            </xsl:for-each>
            </ul>
        </xsl:for-each>
        <hr/>
        <!-- The main part of the report -->
        <xsl:apply-templates select="report/body"/>
        <hr/>
        <xsl:if test="report/body//footnote">
            <h2>Footnotes</h2>
            <!-- Put the text of all footnotes here -->
            <xsl:for-each select="report/body//footnote">
                <xsl:variable name="footnotenum">
                    <xsl:number count="footnote" level="any"/>
                </xsl:variable>
                <a name="footnote{$footnotenum}"><p>
                    <sup><xsl:value-of select="$footnotenum"/></sup>
                <xsl:text> </xsl:text><xsl:apply-templates/></p></a>
            </xsl:for-each>
            <hr/>
        </xsl:if>
        <xsl:apply-templates select="report/back"/>
    </body>
  </html>
</xsl:template>

<xsl:template match="chapter">
    <xsl:variable name="chapnum">
        <xsl:number count="chapter" format="1"/>
    </xsl:variable>
    <a name="chap{$chapnum}"><h2>Chapter <xsl:value-of select="$chapnum"/>
        <br/>
    <xsl:value-of select="title"/></h2></a>
    <xsl:apply-templates/>
</xsl:template>

<xsl:template match="section">
    <xsl:variable name="sectnum">
        <xsl:number count="chapter|section"
                    level="multiple" format="1.1"/>
    </xsl:variable>
    <a name="sect{$sectnum}"><h3><xsl:value-of select="$sectnum"/>
    <xsl:text> </xsl:text><xsl:value-of select="title"/></h3></a>
    <xsl:apply-templates/>
</xsl:template>

<xsl:template match="subsection">
    <xsl:variable name="subsectnum">
        <xsl:number count="chapter|section|subsection"
                    level="multiple" format="1.1.1"/>
    </xsl:variable>
    <a name="subsect{$subsectnum}"><h4><xsl:value-of select="$subsectnum"/>
    <xsl:text> </xsl:text><xsl:value-of select="title"/></h4></a>
    <xsl:apply-templates/>
</xsl:template>

<xsl:template match="p">
    <p><xsl:apply-templates/></p>
</xsl:template>

<xsl:template match="em">
    <em><xsl:apply-templates/></em>
</xsl:template>

<!-- Insert a footnote reference -->
<xsl:template match="footnote">
    <xsl:variable name="footnotenum">
        <xsl:number count="footnote" level="any"/>
    </xsl:variable>
    <sup><a href="#footnote{$footnotenum}">
    <xsl:value-of select="$footnotenum"/></a></sup>
</xsl:template>

<xsl:template match="title"/>

<!-- Insert a bibliographic citation, with a hypertext link -->
<xsl:template match="cite">
    <xsl:variable name="cref">
        <xsl:value-of select="@ref"/>
    </xsl:variable>
    <a href="#{@ref}">[<xsl:for-each select="//bibitem[@id=$cref]">
        <xsl:number/></xsl:for-each>]</a>
</xsl:template>

<!-- Build the bibliography -->
<xsl:template match="bibliography">
    <h2>Bibliography</h2>
    <table>
    <xsl:apply-templates/>
    </table>
</xsl:template>

<!-- Each bibliographic item -->
<xsl:template match="bibitem">
    <tr>
    <td><a name="{@id}">[<xsl:number/>]</a></td>
    <td>
    <xsl:choose>
    <xsl:when test="@href">
        <a href="{@href}"><xsl:apply-templates/></a>
    </xsl:when>
    <xsl:otherwise><xsl:apply-templates/></xsl:otherwise>
    </xsl:choose>
    </td>
    </tr>
</xsl:template>

</xsl:stylesheet>
