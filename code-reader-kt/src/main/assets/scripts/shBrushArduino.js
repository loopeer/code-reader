/**
 * SyntaxHighlighter
 * http://alexgorbatchev.com/SyntaxHighlighter
 *
 * SyntaxHighlighter is donationware. If you are using it, please donate.
 * http://alexgorbatchev.com/SyntaxHighlighter/donate.html
 *
 * @version
 * 3.0.83 (July 02 2010)
 *
 * @copyright
 * Copyright (C) 2004-2010 Alex Gorbatchev.
 *
 * @license
 * Dual licensed under the MIT and GPL licenses.
 */
;(function()
{
	// CommonJS
	typeof(require) != 'undefined' ? SyntaxHighlighter = require('shCore').SyntaxHighlighter : null;

	function Brush()
	{
		// Copyright 2006 Shin, YoungJin
		// Arduino addon 2016 Dubkov Ilya

		var datatypes =	'void boolean char unsigned signed byte int word long ' +
						'short double float string String'

		var keywords =	'break case class const private public protected default do ' +
						'else enum extern if for goto inline namespace new ' +
						'return sizeof static struct switch this true false typedef union ' +
						'using virtual void volatile while ' +
						'INPUT INPUT_PULLUP OUTPUT HIGH LOW LED_BUILTIN PROGMEM';

		var functions =
						'Serial Wire SPI Stream setup loop digitalWrite pinMode digitalRead analogWrite ' +
						'analogRead  analogReadResolution analogWriteResolution analogReference ' +
						'delay begin available println print write read min max abs constrain ' +
						'map pow sqrt random randomSeed millis micros delayMicroseconds attachInterrupt ' +
						'detachInterrupt interrupts noInterrupts pulseln shiftln shiftOut ' +
						'tone noTone sin cos tan isAlphaNumeric isAlpha isAscii isWhitespace isControl ' +
						'isDigit isGraph isLowerCase isPrintable isPunct isSpace isUpperCase ' +
						'isHexadecimalDigit lowByte highByte bitRead bitWrite bitSet bitClear bit ' +
						'char byte int long float word ';



		this.regexList = [
			{ regex: SyntaxHighlighter.regexLib.singleLineCComments,	css: 'comments' },			// one line comments
			{ regex: SyntaxHighlighter.regexLib.multiLineCComments,		css: 'comments' },			// multiline comments
			{ regex: SyntaxHighlighter.regexLib.doubleQuotedString,		css: 'string' },			// strings
			{ regex: SyntaxHighlighter.regexLib.singleQuotedString,		css: 'string' },			// strings
			{ regex: /^ *#.*/gm,										css: 'preprocessor' },
			{ regex: new RegExp(this.getKeywords(datatypes), 'gm'),		css: 'color1 bold' },
			{ regex: new RegExp(this.getKeywords(functions), 'gm'),		css: 'functions bold' },
			{ regex: new RegExp(this.getKeywords(keywords), 'gm'),		css: 'keyword bold' }
			];
	};

	Brush.prototype	= new SyntaxHighlighter.Highlighter();
	Brush.aliases	= ['arduino', 'Arduino'];

	SyntaxHighlighter.brushes.Arduino = Brush;

	// CommonJS
	typeof(exports) != 'undefined' ? exports.Brush = Brush : null;
})();