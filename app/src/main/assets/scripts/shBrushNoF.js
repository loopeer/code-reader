;(function()
{
	// CommonJS
	typeof(require) != 'undefined' ? SyntaxHighlighter = require('shCore').SyntaxHighlighter : null;

	function Brush()
	{
		var keywords =	'applicationId parseInt versionCode versionName multiDexEnabled keyAlias ' +
						'storeFile storePassword keyPassword signingConfig manifestPlaceholders ' +
						'minifyEnabled targetCompatibility sourceCompatibility VERSION_1_8';

		this.regexList = [
			{ regex: SyntaxHighlighter.regexLib.singleLineCComments,	css: 'color1' },		// one line comments
			{ regex: SyntaxHighlighter.regexLib.doubleQuotedString,		css: 'value' },		// strings
			{ regex: SyntaxHighlighter.regexLib.singleQuotedString,		css: 'value' },		// strings
			];

		this.forHtmlScript({
			left	: /(&lt;|<)%[@!=]?/g,
			right	: /%(&gt;|>)/g
		});
	};

	Brush.prototype	= new SyntaxHighlighter.Highlighter();
	Brush.aliases	= ['bat', 'txt'];

	SyntaxHighlighter.brushes.NoF = Brush;

	// CommonJS
	typeof(exports) != 'undefined' ? exports.Brush = Brush : null;
})();
