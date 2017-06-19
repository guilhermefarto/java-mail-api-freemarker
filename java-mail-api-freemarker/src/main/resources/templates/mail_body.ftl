<html>
<body>
<br />
<img src="${banner}" alt="Banner TOTVS" />
<br /><br />
Hello, <font size="+1"><b>${name}</b></font>.
<br /><br />
This is a sample mail with <b>JavaMail API</b> and <b>FreeMarker</b>.
<br /><br />
Please check the attached files:
<br /><br />
<ul>
	<#list attachments as attachment>
		<li><b>${attachment}</b></li>
	</#list>
</ul>
<br />
All the best,
<br /><br />
<b>${author}</b>
</body>
</html>