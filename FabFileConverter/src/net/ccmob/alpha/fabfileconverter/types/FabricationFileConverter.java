package net.ccmob.alpha.fabfileconverter.types;

public abstract class FabricationFileConverter {

	private String name;
	private String regex;
	private String convertedFileEnding;

	public FabricationFileConverter(String name, String regex,
			String convertedFileEnding) {
		this.setName(name);
		this.setRegex(regex);
		this.setConvertedFileEnding(convertedFileEnding);
	}

	public abstract void convert(String nFileName);

	public abstract void read(String fileName);

	public abstract void preview();

	public abstract void show();

	public void init() {
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	private void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the regex
	 */
	public String getRegex() {
		return regex;
	}

	/**
	 * @param regex
	 *            the regex to set
	 */
	private void setRegex(String regex) {
		this.regex = regex;
	};

	/**
	 * @return the convertedFileEnding
	 */
	public String getConvertedFileEnding() {
		return convertedFileEnding;
	}

	/**
	 * @param convertedFileEnding the convertedFileEnding to set
	 */
	private void setConvertedFileEnding(String convertedFileEnding) {
		this.convertedFileEnding = convertedFileEnding;
	}

	public boolean checkFileEnd(String fileend) {
		return ((fileend.substring(fileend.lastIndexOf(".") + 1))
				.matches(regex));
	}

}
