<?xml version="1.0" encoding="iso-8859-1"?>

<!-- XSLT stylesheet for transforming the technical reports
     to XSL:FO for printing -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                version="1.0">

<xsl:template match="/">
  <!-- Build the main structure of the produced XSL:FO -->
  <fo:root>
     <fo:layout-master-set>
      <fo:simple-page-master master-name="one" margin-left="3cm" 
                                               margin-right="3cm" 
                                               margin-top="2cm" 
                                               margin-bottom="2cm">
       <fo:region-before extent="12pt"/>
       <fo:region-body margin-top="20pt" margin-bottom="20pt"/>
       <fo:region-after extent="12pt"/>
      </fo:simple-page-master>
     </fo:layout-master-set>

    <fo:page-sequence master-reference="one">
<!--    <fo:sequence-specification>
    <fo:sequence-specifier-single master-name="one"/>
    </fo:sequence-specification> -->
        <fo:flow flow-name="xsl-region-body">
        <fo:block font-size="24pt" text-align="center" space-after="6pt">
            <xsl:value-of select="report/front/title"/>
        </fo:block>
        <fo:block text-align="center">
            by <xsl:value-of select="report/front/author"/>
        </fo:block>
        </fo:flow>
    </fo:page-sequence>

    <fo:page-sequence master-reference="one">
<!--        <fo:sequence-specification>
        <fo:sequence-specifier-repeating page-master-first="one"
                                         page-master-repeating="one"/>
        </fo:sequence-specification> -->

        <fo:static-content flow-name="xsl-region-before">
            <fo:block font-size="10pt">
                <xsl:value-of select="report/front/title"/>
            </fo:block>
        </fo:static-content>

        <fo:static-content flow-name="xsl-region-after">
            <fo:block font-size="10pt" text-align="center">
                <fo:page-number/>
            </fo:block>
        </fo:static-content>

        <fo:flow flow-name="xsl-region-body" font-size="12pt" font-family="serif">
        <fo:block font-size="20pt" space-after="24pt">Table of Contents
        </fo:block>
        <!-- Build a TOC from chapters, sections and subsections,
             with page references -->
        <xsl:for-each select="report/body/chapter">
            <xsl:variable name="chapnum">
                <xsl:number count="chapter" format="1"/>
            </xsl:variable>
            <fo:block>
            Chapter <xsl:value-of select="$chapnum"/> -
            <xsl:value-of select="title"/>...
            <fo:page-number-citation ref-id="chap{$chapnum}"/>
            </fo:block>
            <fo:list-block>
            <xsl:for-each select="section">
                <xsl:variable name="sectnum">
                    <xsl:number count="chapter|section"
                                level="multiple" format="1.1"/>
                </xsl:variable>
                <fo:list-item><fo:list-item-label><fo:block/>
                </fo:list-item-label><fo:list-item-body>
                <fo:block>
                <xsl:value-of select="$sectnum"/><xsl:text> </xsl:text>
                <xsl:value-of select="title"/>...
                <fo:page-number-citation ref-id="sect{$sectnum}"/>
                </fo:block></fo:list-item-body></fo:list-item>
                <fo:list-item><fo:list-item-label><fo:block/>
                </fo:list-item-label><fo:list-item-body>
                <fo:list-block>
                <xsl:for-each select="subsection">
                    <xsl:variable name="subsectnum">
                        <xsl:number count="chapter|section|subsection"
                                    level="multiple" format="1.1.1"/>
                    </xsl:variable>
                    <fo:list-item><fo:list-item-label><fo:block/>
                    </fo:list-item-label><fo:list-item-body>
                    <fo:block>
                    <xsl:value-of select="$subsectnum"/><xsl:text> </xsl:text>
                    <xsl:value-of select="title"/>...
                    <fo:page-number-citation ref-id="subsect{$subsectnum}"/>
                    </fo:block></fo:list-item-body></fo:list-item>
                </xsl:for-each>
                </fo:list-block>
                </fo:list-item-body></fo:list-item>
            </xsl:for-each>
            </fo:list-block>
        </xsl:for-each>

        <!-- The main part of the report -->
        <xsl:apply-templates select="report/body"/>
        <xsl:apply-templates select="report/back"/>
        </fo:flow>
    </fo:page-sequence>
  </fo:root>
</xsl:template>

<xsl:template match="chapter">
    <xsl:variable name="chapnum">
        <xsl:number count="chapter" format="1"/>
    </xsl:variable>
    <fo:block break-before="page" font-size="20pt" id="chap{$chapnum}">
        Chapter <xsl:value-of select="$chapnum"/>
    </fo:block>
    <fo:block font-size="20pt" space-after="24pt">
        <xsl:value-of select="title"/></fo:block>
    <xsl:apply-templates/>
</xsl:template>

<xsl:template match="section">
    <xsl:variable name="sectnum">
        <xsl:number count="chapter|section"
                    level="multiple" format="1.1"/>
    </xsl:variable>
    <fo:block font-size="14pt" font-weight="bold" id="sect{$sectnum}">
        <xsl:value-of select="$sectnum"/><xsl:text> </xsl:text>
        <xsl:value-of select="title"/>
    </fo:block>
    <xsl:apply-templates/>
</xsl:template>

<xsl:template match="subsection">
    <xsl:variable name="subsectnum">
        <xsl:number count="chapter|section|subsection"
                    level="multiple" format="1.1.1"/>
    </xsl:variable>
    <fo:block font-weight="bold" id="subsect{$subsectnum}">
        <xsl:value-of select="$subsectnum"/><xsl:text> </xsl:text>
        <xsl:value-of select="title"/>
    </fo:block>
    <xsl:apply-templates/>
</xsl:template>

<xsl:template match="p">
    <fo:block><xsl:apply-templates/></fo:block>
</xsl:template>

<xsl:template match="em">
    <fo:inline font-style="italic">
        <xsl:apply-templates/>
    </fo:inline>
</xsl:template>

<!-- Insert footnote reference and text
     (XSL:FO will place it in the page footer automatically) -->
<xsl:template match="footnote">
    <xsl:variable name="footnotenum">
        <xsl:number count="footnote" level="any"/>
    </xsl:variable>
    <fo:footnote><fo:inline>
    <xsl:value-of select="$footnotenum"/>
    </fo:inline><fo:footnote-body>
    <fo:block><xsl:value-of select="$footnotenum"/> <xsl:apply-templates/>
    </fo:block>
    </fo:footnote-body></fo:footnote>
</xsl:template>

<xsl:template match="title"/>

<!-- Insert a bibliographic citation -->
<xsl:template match="cite">
    <xsl:variable name="cref">
        <xsl:value-of select="@ref"/>
    </xsl:variable>
    [<xsl:for-each select="//bibitem[@id=$cref]">
        <xsl:number/></xsl:for-each>]
</xsl:template>

<!-- Build the bibliography -->
<xsl:template match="bibliography">
    <fo:block font-size="20pt" space-after="24pt" break-before="page">
        Bibliography
    </fo:block>
    <fo:list-block><xsl:apply-templates/></fo:list-block>
</xsl:template>

<!-- Each bibliographic item -->
<xsl:template match="bibitem">
<fo:list-item>
    <fo:list-item-label id="{@id}"><fo:block>[<xsl:number/>]</fo:block>
    </fo:list-item-label>
    <fo:list-item-body><fo:block><xsl:apply-templates/></fo:block>
    </fo:list-item-body>
</fo:list-item>
</xsl:template>

</xsl:stylesheet>
