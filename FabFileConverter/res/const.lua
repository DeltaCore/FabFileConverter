Formats = {}

function Format(major_name,major_ending,minor_name,minor_ending)
	Formats[major_name] = {}
	Formats[major_name].Major = {}
	Formats[major_name].Major.Name = major_name
	Formats[major_name].Major.Ending = major_ending
	Formats[major_name].Minor = {}
	Formats[major_name].Minor.Name = minor_name
	Formats[major_name].Minor.Ending = minor_ending
end

Format("Inch", "\"", "Mil", "mil")
Format("Meter", "m", "Millimeter", "mm")