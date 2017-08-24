/**
 * SyntaxHighlighter Verilog Brush
 * http://hdelossantos.com/
 *
 * SyntaxHighlighter is donationware. If you are using it, please donate.
 * http://alexgorbatchev.com/wiki/SyntaxHighlighter:Donate
 *
 * @version
 * 1.0.0 (May 20, 2010)
 * 
 * @copyright
 * Copyright (C) 2010 Hanly De Los Santos.
 *
 * @license
 * This file is a SyntaxHighlighter brush and is licensed under
 * the same license as SyntaxHighlighter.
 * 
 * SyntaxHighlighter is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SyntaxHighlighter is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SyntaxHighlighter.  If not, see <http://www.gnu.org/copyleft/lesser.html>.
 */
SyntaxHighlighter.brushes.Verilog = function() {
	var keywords = 'always end ifnone or rpmos tranif1 and endcase ' +
			  'initial output rtran tri assign endmodule inout ' +
			  'parameter rtranif0 tri0 begin endfunction input ' +
			  'pmos rtranif1 tri1 buf endprimitive integer ' +
			  'posedge scalared triand bufif0 endspecify join ' +
			  'primitive small trior bufif1 endtable large pull0 ' +
			  'specify trireg case endtask macromodule pull1 ' +
			  'specparam vectored casex event medium pullup ' +
			  'strong0 wait casez for module pulldown strong1 ' +
			  'wand cmos force nand rcmos supply0 weak0 deassign ' +
			  'forever negedge real supply1 weak1 default for ' +
			  'nmos realtime table while defparam function nor ' +
			  'reg task wire disable highz0 not release time wor ' +
			  'edge highz1 notif0 repeat tran xnor else if ' +
			  'notif1 rnmos tranif0 xor ' +
			  'alias always always_comb always_ff always_latch and assert assume automatic before bind bins binsof bit break byte cell chandle class  clocking config const constraint context continue cover  covergroup coverpoint cross design dist do end endcase endclass endclocking endconfig endgenerate endgroup endinterface endpackage endprogram endproperty endsequence enum expect export extends extern final first_match foreach fork forkjoin generate genvar iff ifnone ignore_bins illegal_bins import incdir inside instance int interface intersect join_any join_none liblist library local localparam logic longint matches modport new noshowcancelled null or package packed priority program property protected pulsestyle_onevent pulsestyle_ondetect pure rand randc randcase randsequence ref return rpmos sequence shortint shortreal showcancelled signed solve static string struct super tagged this throughout timeprecision timeunit tranif1 type typedef union unique unsigned use var virtual void wait_order wildcard with within xorA';
	var sysTasks = '$display $monitor $dumpall $dumpfile $dumpflush ' +
			  '$dumplimit $dumpoff $dumpon $dumpvars $fclose ' +
			  '$fdisplay $fopen $finish $fmonitor $fstrobe ' +
			  '$fwrite $fgetc $ungetc $fgets $fscanf $fread ' +
			  '$ftell $fseek $frewind $ferror $fflush $feof ' +
			  '$random $readmemb $readmemh $readmemx $signed ' +
			  '$stime $stop $strobe $time $unsigned $write';
	var macros = 'default-net define celldefine default_nettype ' +
			  'else elsif endcelldefine endif ifdef ifndef ' +
			  'include line nounconnected_drive resetall ' +
			  'timescale unconnected_drive undef';

	this.regexList = [
		{ regex: SyntaxHighlighter.regexLib.singleLineCComments,	css: 'comments' },
		{ regex: /\/\*([^\*][\s\S]*)?\*\//gm,						css: 'comments' },
		{ regex: /\/\*(?!\*\/)\*[\s\S]*?\*\//gm,					css: 'preprocessor' },
		{ regex: SyntaxHighlighter.regexLib.doubleQuotedString,		css: 'string' },
		{ regex: SyntaxHighlighter.regexLib.singleQuotedString,		css: 'string' },
		{ regex: /\b([\d]+(\.[\d]+)?|0x[a-f0-9]+)\b/gi,			css: 'value' },
		{ regex: /(?!\@interface\b)\@[\$\w]+\b/g,			css: 'color1' },
		{ regex: /\@interface\b/g,					css: 'color2' },
		{ regex: new RegExp(this.getKeywords(keywords), 'gm'),		css: 'keyword' },
		{ regex: new RegExp(this.getKeywords(macros), 'gm'),		css: 'keyword' },
		{ regex: new RegExp(this.getKeywords(sysTasks), 'gm'),		css: 'keyword' }
];

};

SyntaxHighlighter.brushes.Verilog.prototype = new SyntaxHighlighter.Highlighter();
SyntaxHighlighter.brushes.Verilog.aliases = ['verilog', 'v'];



