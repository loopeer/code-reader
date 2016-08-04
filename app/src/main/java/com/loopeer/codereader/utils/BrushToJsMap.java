package com.loopeer.codereader.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BrushToJsMap {
    public static HashMap<String, List<String>> mapping;

    public BrushToJsMap() {
        if (mapping == null) {
            mapping = new HashMap();
            init();
        }
    }

    public static void init() {
        List list = new ArrayList();
        list.add("actionscript3");
        list.add("as3");
        mapping.put("AS3", list);
        list = new ArrayList();
        list.add("applescript");
        list.add("scpt");
        mapping.put("AppleScript", list);
        list = new ArrayList();
        list.add("bash");
        list.add("shell");
        list.add("sh");
        list.add("rc");
        list.add("conf");
        mapping.put("Bash", list);
        list = new ArrayList();
        list.add("coldfusion");
        list.add("cfm");
        list.add("cf");
        mapping.put("ColdFusion", list);
        list = new ArrayList();
        list.add("cpp");
        list.add("c");
        list.add("cc");
        list.add("h");
        list.add("hpp");
        mapping.put("Cpp", list);
        list = new ArrayList();
        list.add("c#");
        list.add("c-sharp");
        list.add("csharp");
        list.add("cs");
        mapping.put("CSharp", list);
        list = new ArrayList();
        list.add("css");
        mapping.put("Css", list);
        list = new ArrayList();
        list.add("delphi");
        list.add("pascal");
        list.add("pas");
        list.add("simba");
        mapping.put("Delphi", list);
        list = new ArrayList();
        list.add("diff");
        list.add("patch");
        mapping.put("Diff", list);
        list = new ArrayList();
        list.add("erl");
        list.add("hrl");
        mapping.put("Erlang", list);
        list = new ArrayList();
        list.add("groovy");
        mapping.put("Groovy", list);
        list = new ArrayList();
        list.add("java");
        mapping.put("Java", list);
        list = new ArrayList();
        list.add("jfx");
        list.add("javafx");
        mapping.put("JavaFX", list);
        list = new ArrayList();
        list.add("js");
        list.add("jscript");
        list.add("javascript");
        mapping.put("JScript", list);
        list = new ArrayList();
        list.add("perl");
        list.add("pl");
        mapping.put("Perl", list);
        list = new ArrayList();
        list.add("php");
        mapping.put("Php", list);
        list = new ArrayList();
        list.add("text");
        list.add("plain");
        list.add("rst");
        list.add("txt");
        mapping.put("Plain", list);
        list = new ArrayList();
        list.add("powershell");
        list.add("ps");
        list.add("ps1");
        mapping.put("PowerShell", list);
        list = new ArrayList();
        list.add("py");
        list.add("python");
        mapping.put("Python", list);
        list = new ArrayList();
        list.add("ruby");
        list.add("rails");
        list.add("ror");
        mapping.put("Ruby", list);
        list = new ArrayList();
        list.add("sass");
        list.add("scss");
        mapping.put("Sass", list);
        list = new ArrayList();
        list.add("scala");
        mapping.put("Scala", list);
        list = new ArrayList();
        list.add("sql");
        mapping.put("Sql", list);
        list = new ArrayList();
        list.add("v");
        list.add("sv");
        mapping.put("Verilog", list);
        list = new ArrayList();
        list.add("vb");
        list.add("vbnet");
        mapping.put("Vb", list);
        list = new ArrayList();
        list.add("xml");
        list.add("xslt");
        list.add("htm");
        list.add("html");
        list.add("xhtml");
        list.add("xaml");
        mapping.put("Xml", list);
        list = new ArrayList();
        list.add("gradle");
        mapping.put("Gradle", list);
        list = new ArrayList();
        list.add("txt");
        list.add("bat");
        mapping.put("NoF", list);
        /*Iterator iterator = G.settings.customFileToJSMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry localEntry = (Map.Entry) ((Iterator) list).next();
            List localList = Arrays.asList(((String) localEntry.getValue()).split(","));
            ((List) mapping.get(localEntry.getKey())).addAll(localList);
        }*/
    }

    public String getJsFileForExtension(String paramString) {
        paramString = paramString.toLowerCase();
        Iterator localIterator = mapping.entrySet().iterator();
        while (localIterator.hasNext()) {
            Map.Entry localEntry = (Map.Entry) localIterator.next();
            if (((List) localEntry.getValue()).indexOf(paramString) != -1) {
                return (String) localEntry.getKey();
            }
        }
        return null;
    }

    public void refreshMaping() {
    }
}
