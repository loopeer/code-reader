;(function()
{
	// CommonJS
	typeof(require) != 'undefined' ? SyntaxHighlighter = require('shCore').SyntaxHighlighter : null;

	function Brush()
	{
		var keywords =	'import class let private set override func super. self. if return var' +
        		        ' public required else in guard ->';

		var values   =	'Bool false true nil String contains count Double Long Float max min ' +
		                'AnyObject description Int Array ';

		this.regexList = [
			{ regex: SyntaxHighlighter.regexLib.singleLineCComments,	css: 'color1' },		// one line comments
			{ regex: SyntaxHighlighter.regexLib.doubleQuotedString,		css: 'string' },		// strings
			{ regex: SyntaxHighlighter.regexLib.singleQuotedString,		css: 'string' },		// strings
			{ regex: /\/\*([^\*][\s\S]*)?\*\//gm,						css: 'comments' },	 	// multiline comments
            { regex: /\/\*(?!\*\/)\*[\s\S]*?\*\//gm,					css: 'preprocessor' },	// documentation comments
			{ regex: /\b([\d]+(\.[\d]+)?|0x[a-f0-9]+)\b/gi,				css: 'value' },			// numbers
		    { regex: /[\+, \-, \=, \*\b, \/]/g,                         css: 'keyword' },
			{ regex: /(?!\@interface\b)\@[\$\w]+\b/g,					css: 'keyword' },
			{ regex: new RegExp(this.getKeywords(keywords), 'gm'),		css: 'keyword' },
			{ regex: new RegExp(this.getKeywords(values), 'gm'),		css: 'value' }	       	// java keyword

			];

		this.forHtmlScript({
			left	: /(&lt;|<)%[@!=]?/g, 
			right	: /%(&gt;|>)/g 
		});
	};

	Brush.prototype	= new SyntaxHighlighter.Highlighter();
	Brush.aliases	= ['swift'];

	SyntaxHighlighter.brushes.Swift = Brush;

	// CommonJS
	typeof(exports) != 'undefined' ? exports.Brush = Brush : null;
})();
