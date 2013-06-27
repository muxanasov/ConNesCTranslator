// Copyright (c) 2013 Mikhail Afanasov and DeepSe group. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package core;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import parsers.module.ParseException;
import parsers.module.Parser;

public class Context extends Module{
	
	private boolean _isParsed = false;
	private ContextConfiguration _parent = null;
	private List<Function> _layeredFunctions = new ArrayList<Function>();
	private HashMap<String, List<Function>> _defaultFunctions = new HashMap<String, List<Function>>();
	private HashMap<String, ArrayList<String>> _deafultDeclaration = new HashMap<String, ArrayList<String>>();
	private Function _check = new Function();
	private List<Function> _defaultEvents = new ArrayList<Function>();
	
	private HashMap<String, String> _transitionConditions = new HashMap<>();
	private ArrayList<String> _triggers = new ArrayList<>();

	public Context(FileManager fm, String name, Component parent) {
		super(fm, name);
		if (!(parent instanceof ContextConfiguration)) {
			Print.error(name + ".cnc","Configuration " + parent.getName() + " is not a context configuration!");
			_isValid = false;
			return;
		}
		_parent = (ContextConfiguration)parent;
	}
	
	public HashMap<String, String> getTransitionConditions() {
		return _transitionConditions;
	}
	
	public ArrayList<String> getTriggers() {
		return _triggers;
	}
	
	@Override
	public void parse() {
		Parser parser = new Parser(new StringReader(_file_cnc));
		try {
			parser.parse();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		_file  = parser.getParsedFile();
		
		for (String group : _file.usedGroups) {
			Function function = new Function();
			function.name = group + ".contextChanged";
			function.returnType = "void";
			Variable var = new Variable();
		    var.name = "con";
		    var.type = "context_t";
		    function.variables.add(var);
		    if (!_file.functions.get("event").contains(function)) {
		    	_defaultEvents .add(function);
		    }
		}
		
		_sourceFileArray  = _file_cnc.split("\n");
		
		_layeredFunctions = new ArrayList<Function>(_parent.getLayeredFunctions());
		
		Function activated = new Function();
		activated.name = "activated";
		activated.returnType = "void";
		
		Function deactivated = new Function();
		deactivated.name = "deactivated";
		deactivated.returnType = "void";
		
		_check = new Function();
		_check.name = "check";
		_check.returnType = "bool";
		
		_defaultFunctions.put("event", new ArrayList<Function>());
		_defaultFunctions.put("command", new ArrayList<Function>());
		_defaultFunctions.put("layered", _layeredFunctions);
		
		_defaultFunctions.get("event").add(activated);
		_defaultFunctions.get("event").add(deactivated);
		_defaultFunctions.get("command").add(_check);
		
		_deafultDeclaration.put("provides", new ArrayList<String>());
		_deafultDeclaration.put("uses", new ArrayList<String>());
		
		_deafultDeclaration.get("provides").add("ContextCommands as Command");
		if (!_layeredFunctions.isEmpty())
			_deafultDeclaration.get("provides").add(_parent.getName() + "Layer as Layered");
		_deafultDeclaration.get("uses").add("ContextEvents as Event");
		
		_isParsed  = true;
		
		ArrayList<String> transitionsToRemove = new ArrayList<>();
		for(String transition : _file.interfaces.get("transitions"))
			if (transition.contains(" if ")) {
				_transitionConditions.put(transition.split(" if ")[0], transition.split(" if ")[1]);
				transitionsToRemove.add(transition);
			}
		_file.interfaces.get("transitions").removeAll(transitionsToRemove);
		_file.interfaces.get("transitions").addAll(_transitionConditions.keySet());
		for (String trigger : _file.interfaces.get("triggers"))
			_triggers.add(trigger);
	}
	
	@Override
	public void build() {
		if (!_isParsed) parse();
		buildModule();
	}
	
	@Override
	protected void buildModule() {
		if (_parent == null) return;
		if (_file == null) return;
		if (_file.type != Component.Type.CONTEXT) return;
		
		String builtContext = "";
		// building includes
		for (String include : _file.includes)
			builtContext += "#include " + include + "\n";
				
		// building the name
		builtContext += "module " + _file.name + _parent.getName() +"Context {\n";
				
		// building declaration section
		for (String key : _file.interfaces.keySet())
			if (!key.equals("transitions")&&!key.equals("triggers"))
				for (String elem : _file.interfaces.get(key))
					builtContext += "  " + key + " interface " + elem + ";\n";
				for (String key : _deafultDeclaration.keySet())
					for (String elem : _deafultDeclaration.get(key))
						builtContext += "  " + key + " interface " + elem + ";\n";
				
				for (String group : _file.usedGroups)
					builtContext += "  uses context group " + group + ";\n";
				
				// building implementation section
				builtContext += "}\nimplementation {\n";
				
				// building implementation body
				
				// building user's functions
				for (String key : _file.functions.keySet())
					for (Function function : _file.functions.get(key)) {
						String firstName = "";
						// if default or layered function has been overridden
						// then we don't have to build it later
						// so delete it from _defaultFunctions
						if (_defaultFunctions.containsKey(key) &&
							_defaultFunctions.get(key).remove(function))
							firstName = Character.toUpperCase(key.charAt(0)) +
								key.substring(1) + ".";
						
						// building signature
						String fType = key;
						if (fType.equals("layered")) fType = "command";
						builtContext += "  " + fType + " " + function.returnType + 
								" " + firstName + function.name + "(";
						for (Variable var : function.variables) {
							builtContext += var.type + var.lexeme + " " + var.name;
							int last = function.variables.size() - 1;
							if (function.variables.lastIndexOf(var) != last)
								builtContext += ", ";
						}
						builtContext += ") {\n";
						
						// building body
						for (int i = function.bodyCoords.beginLine; 
								 i < function.bodyCoords.endLine - 1; i++)
							builtContext += _sourceFileArray[i] + "\n";
						builtContext += "  }\n";
					}
				
		// if not all of the layered function are implemented
		// then throw an exception
		if (_defaultFunctions.get("layered").size() > 0) {
			String msg = "";
			for (Function function : _defaultFunctions.get("layered"))
				msg += "\n" + function + " is not implemented!";
			Print.error(_file.name + ".cnc", msg);
			_defaultFunctions.remove("layered");
		}
				
		// building missing standard functions
		for (String key : _defaultFunctions.keySet())
			for (Function function : _defaultFunctions.get(key)) {
				// building signature
				String firstName = Character.toUpperCase(key.charAt(0)) +
						key.substring(1);
				builtContext += "  " + key + " " + function.returnType + 
						" " + firstName + "." + function.name + "(";
				for (Variable var : function.variables) {
					builtContext += var.type + var.lexeme + " " + var.name;
					int last = function.variables.size() - 1;
					if (function.variables.lastIndexOf(var) != last)
						builtContext += ", ";
				}
				builtContext += "){\n";
						
				// building body
				if (function.equals(_check))
					builtContext += "    return TRUE;\n";
				builtContext += "  }\n";
			}
		for (Function function : _defaultEvents) {
			// building signature
					
			builtContext += "  event " + function.returnType + 
							" " + function.name + "(";
			for (Variable var : function.variables) {
				builtContext += var.type + var.lexeme + " " + var.name;
				int last = function.variables.size() - 1;
				if (function.variables.lastIndexOf(var) != last)
					builtContext += ", ";
			}
			builtContext += "){\n  }\n";
		}
				
		builtContext += "  command void Command.activate() {\n" +
					"    signal Event.activated();\n" +
					"  }\n" +
					"  command void Command.deactivate() {\n" +
					"    signal Event.deactivated();\n" +
					"  }\n";
		builtContext += "  command bool Command.transitionIsPossible(context_t con) {\n";
		
		List<String> transitions = new ArrayList<String>();
		
		for (String transition : _file.interfaces.get("transitions"))
			if (!_parent.getComponents().containsKey(transition) ||
				 _parent.getComponents().get(transition).getType() != Component.Type.CONTEXT) {
				int strNum = getNumberOf("(,\\s+|\\s+)" + transition + "(\\s*,|\\s*;|\\s*)");
				Print.error(_file.name + ".cnc " + strNum, 
					"Component " + transition + " is not a Context or " +
					"does not belog to the group " + _parent.getName() + "!");
				continue;
			} else transitions.add(transition);
		
		if (transitions.isEmpty())
			builtContext += "    return TRUE;\n  }\n";
		else {
			builtContext += "    if (";
			for (int i = 0; i < transitions.size(); i++) {
				String tab = "";
				if (i > 0) tab = "        ";
				builtContext += tab + "con == " + transitions.get(i).toUpperCase()
					+_parent.getName().toUpperCase() + " ||\n";
			}
			builtContext = builtContext.substring(0, builtContext.length()-4);
			builtContext += ") return TRUE;\n    return FALSE;\n  }\n";
		}
		
		builtContext += "  command bool Command.conditionsAreSatisfied(context_t to, context_t cond) {\n";
		
		List<String> conditions = new ArrayList<String>();
		
		for (String key : _transitionConditions.keySet())
			if (!_parent.getComponents().containsKey(key) ||
				 _parent.getComponents().get(key).getType() != Component.Type.CONTEXT) {
				int strNum = getNumberOf("(,\\s+|\\s+)" + key + "(\\s*,|\\s*;|\\s*)");
				Print.error(_file.name + ".cnc " + strNum, 
					"Component " + key + " is not a Context or " +
					"does not belog to the group " + _parent.getName() + "!");
				continue;
			} else conditions.add(key);
		
		if (conditions.isEmpty())
			builtContext += "    return TRUE;\n  }\n";
		else {
			builtContext += "    switch (to) {\n";
			for (int i = 0; i < conditions.size(); i++) {
				String key = conditions.get(i);
				String[] cond = _transitionConditions.get(key).split("\\.");
				builtContext += "      case " + key.toUpperCase() + _parent.getName().toUpperCase() + ":\n" +
						        "        return cond == " + cond[1].toUpperCase() + cond[0].toUpperCase() + ";\n";
			}
			builtContext += "      default:\n" +
							"        return TRUE;\n" +
							"    }\n" +
							"  }\n";
		}
			
		// end of building
		builtContext += "}";
		
		String oldName = _file.name;
		int oldType = _file.type;
		// after this function _file.name will be changed to _file.name+_parent.getName()+"Context"
		// we are trying to save it
		// the same with type
		super.parse(builtContext);
		super.buildModule();
		
		_file.name = oldName;
		_file.type = oldType;
	}

}
