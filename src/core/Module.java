// Copyright (c) 2013 Mikhail Afanasov and DeepSe group. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package core;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import parsers.module.ModuleFile;
import parsers.module.ParseException;
import parsers.module.Parser;

public class Module extends Component{
	
	private ModuleFile _file = null;
	private String _builtModule = "";
	private String[] _sourceFileArray = null;
	ArrayList<Function> _defaultEvents = new ArrayList<>();
	

	public Module(String file_cnc) {
		super(file_cnc);
		
		_sourceFileArray  = file_cnc.split("\n");
		
		Parser parser = new Parser(new StringReader(file_cnc));
		try {
			parser.parse();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		_file  = parser.getParsedFile();
			
	}
	
	public String build() {
		
		// adding default include
		_sourceFileArray[0] = "#include \"Contexts.h\"\n" + _sourceFileArray[0];
		
		for (int i = 0; i < _file.declarationCoords.beginLine; i++)
			_builtModule += _sourceFileArray[i] + "\n";
		
		// building declarations
		for (String elem : _file.interfaces.get("provides"))
			_builtModule += "  provides interface " + elem + ";\n";
		for (String elem : _file.interfaces.get("uses"))
			_builtModule += "  uses interface " + elem + ";\n";
		
		// adding context groups
		for (String group : _file.usedGroups)
			_builtModule += "  uses interface ContextGroup as " + group + "Group;\n" +
							"  uses interface " + group + "Layer;\n";
		
		_builtModule += "}\nimplementation {\n";
		
		// I just use a regex here because I'm too lazy...
		// Actually I should use parsed functions and coords from (ModuleFile)_file
		for (int i = _file.implementationCoords.beginLine; i < _sourceFileArray.length; i++) {
			String toAdd = _sourceFileArray[i];
			for (String group : _file.usedGroups) {
				// trying to find activation like
				// activate <group_name>.<context_name>;
				Pattern p = Pattern.compile("activate\\s+" + group + ".(.*?);");
		    	Matcher m = p.matcher(toAdd);
		    	if (m.find()) {
		    		// if found, change it to
		    		// call <group_name>Group.actiavte(<CONTEXT_NAME><GROUP_NAME>);
		    		toAdd = toAdd.replaceAll("activate\\s+" + group + ".\\w+;",
		    				"call " + group + "Group.activate(" + 
		    				m.group(1).toUpperCase() + group.toUpperCase() + ");");
		    		break;
		    	}
		    	// trying to find layered function like
		    	// call <group_name>.
		    	p = Pattern.compile("call\\s+" + group + ".");
		    	m = p.matcher(toAdd);
		    	if (m.find()) {
		    		// if found, change it to
		    		// call <group_name>Layer.
		    		toAdd = toAdd.replaceAll("call\\s+" + group + ".",
		    				"call " + group + "Layer.");
		    		break;
		    	}
		    	// trying to find default functions like
		    	// event void <group_name>.contextChanged(context_t <var_name>)
		    	p = Pattern.compile("event\\s+void\\s+" + group + 
		    			".contextChanged\\s*\\(\\s*context_t\\s+(.*?)\\s*\\)");
		    	m = p.matcher(toAdd);
		    	if (m.find()) {
		    		// if found, change it to
		    		// event void <group_name>Group.contextChanged(context_t <var_name>)
		    		toAdd = toAdd.replaceAll("event\\s+void\\s+" + group + 
			    			".contextChanged\\s*\\(\\s*context_t\\s+\\w+\\s*\\)",
		    				"event void " + group + "Group.contextChanged(context_t " +
			    			m.group(1) + ")");
		    		break;
		    	}
			}
			_builtModule += toAdd + "\n";
		}
		
		return _builtModule;
	}

}
