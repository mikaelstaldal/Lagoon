<?xml version="1.0" encoding="iso-8859-1"?>

<!-- Lagoon Server Page -->

<html xmlns:lsp="http://staldal.nu/LSP/core" 
	  xmlns:test="http://staldal.nu/LSP/test"
	  xmlns:stringtest="http://staldal.nu/LSP/string"
	  xmlns:foo="http://foo.com">
<head>
<title>LSP test</title>
</head>
<body>
<h1>LSP test</h1>

<!-- Comments are ignored -->

<?pi param="processing instructions are ignored"?>

<lsp:processing-instruction name="{$pi}">Create a processing instrunction 
in output like this</lsp:processing-instruction>

<a href="{$hej}">Foo: <lsp:value-of select="$foo">FOOTEST</lsp:value-of> bar</a>

<lsp:root>
<a href="{$hej}"><lsp:value-of select="$foo"/>bar<lsp:value-of select="$hej">HEJVAR</lsp:value-of></a>
</lsp:root>

<lsp:if test="$bool">
<p>The parameter $bool was set to a non-empty string, namly "<lsp:value-of select="$bool"/>"</p>
</lsp:if>

<hr />
<h2>lsp:if</h2>

<lsp:if test="5 &lt; 7">
<p>5 is less than 7</p>
</lsp:if>

<lsp:if test="not(5 &lt; 7)">
<p>5 is not less than 7</p>
</lsp:if>

<hr />
<h2>lsp:choose</h2>

<lsp:choose>
<lsp:when test="5 &lt; 7">
	<p>5 is less than 7</p>
</lsp:when>
</lsp:choose>

<lsp:choose>
<lsp:when test="5 &lt; 7">
	<p>5 is less than 7</p>
</lsp:when>
<lsp:when test="5 &lt; 6">
	<p>5 is less than 6</p>
</lsp:when>
</lsp:choose>

<!-- error
<lsp:choose>
<lsp:when test="5 &lt; 7">
	<p>5 is less than 7</p>
</lsp:when>
<lsp:otherwise>
	<p>Otherwise</p>
</lsp:otherwise>
<lsp:when test="5 &lt; 6">
	<p>5 is less than 6</p>
</lsp:when>
</lsp:choose>
-->

<lsp:choose>
<lsp:when test="5 &gt; 7">
	<p>5 is greater than 7</p>
</lsp:when>
<lsp:when test="5 &gt; 6">
	<p>5 is greater than 6</p>
</lsp:when>
<lsp:otherwise>
	<p>Otherwise</p>
</lsp:otherwise>
</lsp:choose>

<lsp:choose>
<lsp:when test="5 &gt; 7">
	<p>5 is greater than 7</p>
</lsp:when>
<lsp:when test="5 &gt; 6">
	<p>5 is greater than 6</p>
</lsp:when>
</lsp:choose>

<hr/>
<h2>Expressions</h2>
<p>5+6.8*7.1-2: "<lsp:value-of select="5+6.8*7.1-2"/>"</p>
<p>-5 mod 2: "<lsp:value-of select="-5 mod 2"/>"</p>
<p>round(11.2): "<lsp:value-of select="round(11.2)"/>"</p>
<p>0 div 0: "<lsp:value-of select="0 div 0"/>"</p>
<p>concat('foo','bar'): "<lsp:value-of select="concat('foo','bar')"/>"</p>
<p>concat('foo','bar','apa'): "<lsp:value-of select="concat('foo','bar','apa')"/>"</p>
<p>substring('12345',2,3): "<lsp:value-of select="substring('12345',2,3)"/>"</p>
<p>substring('12345',2): "<lsp:value-of select="substring('12345',2)"/>"</p>
<p>substring('12345',1.5, 2.6): "<lsp:value-of select="substring('12345',1.5,2.6)"/>"</p>
<p>substring('12345',0,3): "<lsp:value-of select="substring('12345',0,3)"/>"</p>
<p>substring('12345',0 div 0,3): "<lsp:value-of select="substring('12345',0 div 0,3)"/>"</p>
<p>substring('12345',1,0 div 0): "<lsp:value-of select="substring('12345',1,0 div 0)"/>"</p>
<p>concat(substring('12345',0,3),' ',translate('foo','fo','FOA')): "<lsp:value-of select="concat(substring('12345',0,3),' ',translate('foo','fo','FOA'))"/>"</p>
<p>5.2 > 11: "<lsp:value-of select="5.2 > 11"/>"</p>
<p>(5.2 &lt; 11) and (string-length('foo') = 3): "<lsp:value-of select="(5.2 &lt; 11) and (string-length('foo') = 3)"/>"</p>
<p>not(true()): "<lsp:value-of select="not(true())"/>"</p>
<p>$a: "<lsp:value-of select="$a"/>"</p>
<!-- <p>$a.b: "<lsp:value-of select="$a.b"/>"</p> -->

<hr/>
<h2>for-each</h2>

<!-- <h4>A list of <lsp:value-of select="count($foo)"/> elements</h4> -->

<ul>
<lsp:for-each select="seq(5.6,15.4,0.5)" var="i" status="stat">
<li><lsp:value-of select="$stat.index"/>: <lsp:value-of select="$i"/> 
 first:<lsp:value-of select="$stat.first"/>     
 last:<lsp:value-of select="$stat.last"/>     
 even:<lsp:value-of select="$stat.even"/>     
 odd:<lsp:value-of select="$stat.odd"/>     
</li>
</lsp:for-each>
</ul>


<hr/>
<h2>lsp:let</h2>

<lsp:let foo="'FOO'" bar="5+6.8*7.1-2" baz="concat('hej','san')">
<ul>
<li>foo: <lsp:value-of select="$foo"/></li>
<li>bar: <lsp:value-of select="$bar"/></li>
<li>baz: <lsp:value-of select="$baz"/></li>
</ul>
</lsp:let>

<ul>
<li>foo: <lsp:value-of select="$foo"/></li>
<li>bar: <lsp:value-of select="$bar"/></li>
<li>baz: <lsp:value-of select="$baz"/></li>
</ul>

<hr/>
<h2>include</h2>

<lsp:include file="includehere.xml"/>
<lsp:include file="/include.xml"/>

<hr/>
<h2>import</h2>

<lsp:import file="importhere.lsp"/>
<lsp:import file="/import.lsp"/>

<hr/>

<h2>Part</h2>

<lsp:import file="part:theSecondPart"/>
<lsp:include file="part:thePart"/>

<h2>ExtElement</h2>

<p>
<test:foo>
	<lsp:if test="5 &gt; 3"><bar>apa</bar></lsp:if>
</test:foo>
</p>

<!--
<p>
<stringtest:hej>apa</stringtest:hej>
</p>
-->

<p>
<test:foo>
	<lsp:if test="2 &gt; 3"><bar>apa</bar></lsp:if>
</test:foo>
</p>

<h2>Element element</h2>

<lsp:element name="foo">FOO</lsp:element>
<lsp:element name="bar"><b>BAR</b></lsp:element>
<lsp:element name="apa" namespace="http://foo.com/hej">PREFIX</lsp:element>
<lsp:element name="gurka" namespace="http://foo.com">PREFIX igen</lsp:element>

<h2>ExtFunc</h2>

<!--
<p><lsp:value-of select="test:func(1, 2, 3, 'one', 'two', 'three')"/></p>
<p><lsp:value-of select="test:foo(1)"/></p>
<p><lsp:value-of select="test:bar()"/></p> -->
<!-- <p><lsp:value-of select="stringtest:func(1, 2, 3, 'one', 'two', 'three')"/></p> -->

<p>---End of LSP Test---</p>

</body>
</html>
