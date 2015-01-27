package net.ccmob.alpha.fabfileconverter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.ccmob.alpha.fabfileconverter.types.LuaFabricationType;

import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

public class Lua {

	public static void loadModule(final CoreGui c){
		System.out.println("Loading lua converters ...");
		c.globals.set("SYS_createConverter", new ThreeArgFunction() {
			@Override
			public LuaValue call(LuaValue name, LuaValue regex,
					LuaValue newEnding) {
				c.addConverter(new LuaFabricationType(name.toString(), regex
						.toString(), newEnding.toString(), c.globals));
				return null;
			}
		});
		c.globals.set("SYS_clearTextArea", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				c.txtrPreviewTextfield.setText("");
				return null;
			}
		});
		c.globals.set("SYS_textAreaAddLine", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue line) {
				c.txtrPreviewTextfield.append(line.toString()
						+ String.format("%n"));
				return null;
			}

		});
		c.globals.set("SYS_textAreaScrollTop", new ZeroArgFunction() {

			@Override
			public LuaValue call() {
				c.txtrPreviewTextfield.select(0, 0);
				return null;
			}
		});
		c.globals.set("SYS_regex_match", new TwoArgFunction() {
			Pattern p = Pattern.compile("");
			Matcher m;
			@Override
			public LuaValue call(LuaValue pattern, LuaValue value) {
				StringBuilder b = new StringBuilder();
				p = Pattern.compile(pattern.checkjstring());
				m = p.matcher(value.checkjstring());
				if(m.matches()){
					for(int i = 1;i<m.groupCount()+1;i++)
						b.append(m.group(i) + "\n");
					return LuaString.valueOf(b.toString());
				}else{
					return LuaValue.NIL;
				}
			}
		});
		c.globals.loadfile("res/main.lua").call();
	}

}
