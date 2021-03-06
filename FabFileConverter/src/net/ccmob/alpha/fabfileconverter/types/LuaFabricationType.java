package net.ccmob.alpha.fabfileconverter.types;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import net.ccmob.alpha.fabfileconverter.LuaFrame;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

public class LuaFabricationType extends FabricationFileConverter {

	private LuaValue convert;
	private LuaValue read;
	private LuaValue preview;
	private LuaValue show;
	private String nFile;

	private FileWriter writer;

	private LuaFrame frame;

	public LuaFabricationType(String name, String regex, String newEnding,
			Globals lGlobals) {
		super(name, regex, newEnding);
		convert = lGlobals.get(name + "_convert");
		read = lGlobals.get(name + "_read");
		preview = lGlobals.get(name + "_preview");
		show = lGlobals.get(name + "_show");

		lGlobals.set(name + "_openFile", new LStartFile(this));
		lGlobals.set(name + "_writeLine", new LWriteLine(this));
		lGlobals.set(name + "_closeFile", new LEndFile(this));
		lGlobals.set(name + "_requestFrame", new LRequestFrame(this));
	}

	@Override
	public void convert(String nFileName) {
		this.nFile = nFileName;
		convert.call();
	}

	@Override
	public void read(String fName) {
		String lines = "";
		String rawName = fName.substring(0, fName.lastIndexOf('.'));
		String ending = fName.substring(fName.lastIndexOf('.') + 1);
		System.out.println(ending);
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(new File(fName)));
			String line;
			while ((line = reader.readLine()) != null) {
				lines += line + "\n";
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}	
		read.call(LuaValue.valueOf(lines),LuaValue.valueOf(rawName),LuaValue.valueOf(ending));
	}

	@Override
	public void preview() {
		preview.call();
	}

	@Override
	public void show() {
		show.call();
	}

	public LuaFrame getFrame() {
		return frame;
	}

	public void setFrame(LuaFrame frame) {
		this.frame = frame;
	}

	private class LStartFile extends ZeroArgFunction {

		private LuaFabricationType converter;
		private File f;

		public LStartFile(LuaFabricationType conv) {
			this.converter = conv;
		}

		@Override
		public LuaValue call() {
			f = new File(converter.nFile);
			if (f.exists()) {
				f.delete();
			}
			if (converter.writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (converter.nFile.isEmpty()) {
				System.err.println("No file selected");
				return LuaValue.valueOf("No File selected");
			}
			try {
				writer = new FileWriter(new File(converter.nFile));
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

	}

	private class LEndFile extends ZeroArgFunction {

		private LuaFabricationType converter;

		public LEndFile(LuaFabricationType conv) {
			this.converter = conv;
		}

		@Override
		public LuaValue call() {
			if (converter.writer != null) {
				try {
					converter.writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				return LuaValue.valueOf("Stream not open.");
			}
			return null;
		}

	}

	private class LWriteLine extends OneArgFunction {

		private LuaFabricationType converter;

		public LWriteLine(LuaFabricationType conv) {
			this.converter = conv;
		}

		@Override
		public LuaValue call(LuaValue line) {
			if (converter.writer != null) {
				try {
					converter.writer.write(line.toString()
							+ String.format("%n"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				return LuaValue.valueOf("Stream not open.");
			}
			return null;
		}

	}

	private class LRequestFrame extends ThreeArgFunction {

		private LuaFabricationType converter;

		public LRequestFrame(LuaFabricationType conv) {
			this.converter = conv;
		}

		@Override
		public LuaValue call(LuaValue renderFunc, LuaValue resizeFunction,
				LuaValue windowName) {			
			converter.setFrame(new LuaFrame(renderFunc, resizeFunction, converter.getName(),
					windowName.toString()));
			return null;
		}

	}

}
