<?xml version="1.0" encoding="iso-8859-1"?>

<!-- XSLT stylesheet for transforming the technical reports
     to LaTeX for printing -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

<xsl:output method="text"
			encoding="iso-8859-1" />

<xsl:template match="/">\documentclass[a4paper,10pt]{report}
\usepackage[T1]{fontenc}
\usepackage[latin1]{inputenc}

\author{<xsl:value-of select="report/front/author"/>}
\title{<xsl:value-of select="report/front/title"/>}

\begin{document}

\maketitle

\tableofcontents

\setlength{\parindent}{0pt}
\setlength{\parskip}{1ex plus 0.5ex minus 0.2ex}

<xsl:apply-templates select="report/body"/>
<xsl:apply-templates select="report/back"/>

\end{document}
</xsl:template>

<xsl:template match="chapter">
\chapter{<xsl:value-of select="title"/>}
<xsl:apply-templates/>
</xsl:template>

<xsl:template match="section">
\section{<xsl:value-of select="title"/>}
<xsl:apply-templates/>
</xsl:template>

<xsl:template match="subsection">
\subsection{<xsl:value-of select="title"/>}
<xsl:apply-templates/>
</xsl:template>

<xsl:template match="p">
<xsl:apply-templates/>
</xsl:template>

<xsl:template match="em">\emph{<xsl:apply-templates/>}</xsl:template>

<!-- Insert footnote reference and text -->
<xsl:template match="footnote">\footnote{<xsl:apply-templates/>}</xsl:template>

<xsl:template match="title"/>

<!-- Insert a bibliographic citation -->
<xsl:template match="cite">\cite{<xsl:value-of select="@ref"/>}</xsl:template>

<!-- Build the bibliography -->
<xsl:template match="bibliography">
\begin{thebibliography}{99}
<xsl:apply-templates/>
\end{thebibliography}
</xsl:template>

<!-- Each bibliographic item -->
<xsl:template match="bibitem">
\bibitem{<xsl:value-of select="@id"/>} <xsl:apply-templates/>
</xsl:template>

</xsl:stylesheet>
