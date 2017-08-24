;(function()
{
	// CommonJS
	typeof(require) != 'undefined' ? SyntaxHighlighter = require('shCore').SyntaxHighlighter : null;

	function Brush()
	{
		this.regexList = [
			{ regex: SyntaxHighlighter.regexLib.doubleQuotedString,		css: 'value' },		// strings
			{ regex: SyntaxHighlighter.regexLib.singleQuotedString,		css: 'value' },		// strings
			];

		this.forHtmlScript({
			left	: /(&lt;|<)%[@!=]?/g, 
			right	: /%(&gt;|>)/g 
		});
	};

	Brush.prototype	= new SyntaxHighlighter.Highlighter();
	Brush.aliases	= ['json'];

	SyntaxHighlighter.brushes.Json = Brush;

	// CommonJS
	typeof(exports) != 'undefined' ? exports.Brush = Brush : null;
})();
