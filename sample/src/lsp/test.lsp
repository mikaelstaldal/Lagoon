<?xml version="1.0" encoding="iso-8859-1"?>

<!-- Lagoon Server Page -->

<html xmlns:lsp="http://staldal.nu/LSP/core">
<head>
<title>LSP test</title>
</head>
<body>
<h1>LSP test</h1>

<!-- Comments are ignored -->

<?pi param="processing instructions are ignored"?>

<lsp:processing-instruction name="{$pi}">Create a processing instrunction 
in output like this</lsp:processing-instruction>

<a href="{$hej}">Foo: {$foo} bar</a>

<lsp:raw>
<a href="{$hej}">{$foo}</a>
</lsp:raw>

<lsp:root>
<a href="{$hej}">{$foo}bar{$hej}</a>
</lsp:root>

<lsp:if test="$bool">
<p>The parameter $bool was set to a non-empty string, namly "{$bool}"</p>
</lsp:if>

<lsp:if test="5 &lt; 7">
<p>5 is less than 7</p>
</lsp:if>

<lsp:if test="not(5 &lt; 7)">
<p>5 is not less than 7</p>
</lsp:if>

<hr/>
<h2>Expressions</h2>
<p>5+6.8*7.1-2: "{5+6.8*7.1-2}"</p>
<p>-5 mod 2: "{-5 mod 2}"</p>
<p>round(11.2): "{round(11.2)}"</p>
<p>0 div 0: "{0 div 0}"</p>
<p>concat('foo','bar'): "{concat('foo','bar')}"</p>
<p>concat('foo','bar','apa'): "{concat('foo','bar','apa')}"</p>
<p>substring('12345',2,3): "{substring('12345',2,3)}"</p>
<p>substring('12345',2): "{substring('12345',2)}"</p>
<p>substring('12345',1.5, 2.6): "{substring('12345',1.5,2.6)}"</p>
<p>substring('12345',0,3): "{substring('12345',0,3)}"</p>
<p>substring('12345',0 div 0,3): "{substring('12345',0 div 0,3)}"</p>
<p>substring('12345',1,0 div 0): "{substring('12345',1,0 div 0)}"</p>
<p>concat(substring('12345',0,3),' ',translate('foo','fo','FOA')): "{concat(substring('12345',0,3),' ',translate('foo','fo','FOA'))}"</p>
<p>5.2 > 11: "{5.2 > 11}"</p>
<p>(5.2 &lt; 11) and (string-length('foo') = 3): "{(5.2 &lt; 11) and (string-length('foo') = 3)}"</p>
<p>not(true()): "{not(true())}"</p>

<hr/>
<h2>include</h2>

<lsp:include file="includehere.xml"/>
<lsp:include file="/include.xml"/>

<hr/>
<h2>import</h2>

<lsp:import file="importhere.lsp"/>
<lsp:import file="/import.lsp"/>

<hr/>

<p>---End of LSP Test---</p>

</body>
</html>
