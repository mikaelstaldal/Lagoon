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

<lsp:processing-instruction name="{$pi}">Create a processing instrunction in output like this</lsp:processing-instruction>

<a href="{$hej}">{$foo}</a>

<lsp:raw>
<a href="{$hej}">{$foo}</a>
</lsp:raw>

<lsp:if test="$bool">
<p>The parameter $bool was set to a non-empty string</p>
</lsp:if>

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
