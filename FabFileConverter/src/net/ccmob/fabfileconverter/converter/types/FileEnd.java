package net.ccmob.fabfileconverter.converter.types;

import java.util.ArrayList;

import net.ccmob.fabfileconverter.PluginUtils.FabricationTypeConverter;

public class FileEnd { // XXX FileEnd : fully Working (Tested)

	public String	                  regex	          = "";
	public FabricationTypeConverter	converter;
	public String	                  name	          = "";
	public String	                  endName	        = "";

	public ArrayList<String>	      disabledButtons	= new ArrayList<String>();

	public FileEnd(String name_, String endName, String regex,
	    FabricationTypeConverter converter_) {
		this.name = name_;
		this.endName = endName;
		this.regex = regex;
		this.converter = converter_;
	}

	public boolean checkFileEnd(String fileend) {
		System.out.println("FILE : " + fileend + " ; REGEX : " + regex);
		String arg;
		int index = fileend.lastIndexOf(".");
		arg = fileend.substring(index + 1);
		if (arg.matches(regex)) {
			return true;
		} else {
			return false;
		}
	}

	public void init(){
		converter.clearSettings();
		converter = converter.create();
	}
	
	public void disableButtons(String name) {
		disabledButtons.add(name);
	}

	public ArrayList<String> getDisabledButtons() {
		return disabledButtons;
	}

}
