;(function()
{
	// CommonJS
	typeof(require) != 'undefined' ? SyntaxHighlighter = require('shCore').SyntaxHighlighter : null;

	function Brush()
	{

		this.regexList = [
			{ regex: /#.*$/gm,		                                    css: 'color1' },		// strings
//			{ regex: new RegExp('/{(.*)/}', 'g'),		                                    css: 'keyword' },		// strings
//			{ regex: new XRegExp('(?<=/{)[^}]*(?=/})', 'gm'),		                        css: 'color1' },		// strings
			{ regex: /-.+? /g,				                            css: 'keyword' },			// numbers
			];

		this.forHtmlScript({
			left	: /(&lt;|<)%[@!=]?/g,
			right	: /%(&gt;|>)/g
		});
	};

	Brush.prototype	= new SyntaxHighlighter.Highlighter();
	Brush.aliases	= ['pro'];

	SyntaxHighlighter.brushes.Pro = Brush;

	// CommonJS
	typeof(exports) != 'undefined' ? exports.Brush = Brush : null;
})();
