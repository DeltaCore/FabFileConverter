drl_lines = {}

local xyMatch = "^X(.-)Y(.-)$"
local xMatch = "^X(.-)$"
local yMatch = "^Y(.-)$"

local toolMatch = "^T(.-)C(.-)$"
local toolSelectMatch = "^T([0-9][0-9])$"

local formatMatch = "^(.-),(.-)Z$"
local fmatMatch = "^FMAT,(.-)$"

local last = {}
last.x = 0
last.y = 0

local drl_points = {}
local drl_tools = {}
local currentTool = nil
local currentFormat = ""
local drl_fileName = ""

local multForMinor = 100

function Tool(format, size, index, sSize)
	if format and size then
		local tool = Class("Tool")
		tool.format = format
		tool.size = size
		tool.index = index
		tool.sSize = sSize
		return tool
	else
		error("No format and/or size defined")
		return nil
	end
end

function Point(x,y,t)
	if x and y and t then
		local point = Class("Point")
		point.x = x
		point.y = y
		point.tool = t
		return point
	else
		error("No x and/or y and/or t is defined.")
		return nil
	end
end

function drl_convert()
	print("Converting ...")
	local currentTool = nil
	drl_openFile()
	drl_writeLine("M72\nM48")
	for index, tool in pairs(drl_tools) do
		local i = ""
		if string.len(tostring(tool.index)) < 2 then
			i = i .. "0"
		end
		drl_writeLine("T" .. i .. tool.index .. "C" .. tool.sSize)
	end
	drl_writeLine("%")
	for index, point in pairs(drl_points) do
		if point.tool == currentTool then else
			local i = ""
			if string.len(tostring(point.tool.index)) < 2 then
				i = i .. "0"
			end
			drl_writeLine("T" .. i .. point.tool.index)
			currentTool = point.tool
		end
		drl_writeLine("X" .. point.x .. "Y" .. point.y)
	end
	drl_writeLine("M30")
	drl_closeFile()
	print("Done.")
end

function drl_parseLine(line)
	print("Current line: '" .. line .. "' [")
	io.write("    ")
	if(string.match(line, xyMatch)) then
		local x,y = string.match(line, xyMatch)
		last.x = tonumber(x)
		last.y = tonumber(y)
		drl_points[#drl_points + 1] = Point(last.x,last.y,drl_tools[currentTool])
		print("[X|Y]["..x.."|"..y.."]")
	elseif(string.match(line,xMatch)) then
		local x = string.match(line, xMatch)
		last.x = tonumber(x)
		drl_points[#drl_points + 1] = Point(x,last.y,drl_tools[currentTool])
		print("[X]["..x.."]")
		io.write("    ")
		print("[X|Y]["..last.x.."|"..last.y.."]")
	elseif(string.match(line,yMatch)) then
		local y = string.match(line, yMatch)
		last.y = tonumber(y)
		drl_points[#drl_points + 1] = Point(last.x,y,drl_tools[currentTool])
		print("[Y]["..y.."]")
		io.write("    ")
		print("[X|Y]["..last.x.."|"..last.y.."]")
	elseif(string.match(line, toolMatch)) then
		local tNum, tSize = string.match(line, toolMatch)
		print(tNum, " ", tSize)
		drl_tools[tonumber(tNum)] = Tool(currentFormat, tonumber(tSize), tNum, tSize)
		print("[TOOL] Size: " .. tSize .. currentFormat.Major.Ending .. " index: " .. tNum)
	elseif(string.match(line, formatMatch)) then
		local formatType, mode = string.match(line, formatMatch)
		if(formatType == "INCH") then --TODO: add meters as optoin
			if mode == "T" then else
				error("File is not correctly exported. Please export with ' Supress Leading Zero's '")
			end
			currentFormat = Formats["Inch"]
			print("Got new Format: ")
			print_r(currentFormat)
		end
	elseif(string.match(line, fmatMatch)) then
		multForMinor = tonumber(string.match(line,fmatMatch)) + 1
		print("New multiplier for minor format: " .. multForMinor)
	elseif(string.match(line, toolSelectMatch)) then
		currentTool = tonumber(string.match(line, toolSelectMatch))
		print("Selecting tool: ")
		print_r(drl_tools[currentTool])
	else
		print("Unkown line. Ignoring...")
	end
	print("]")
end

function drl_read(content, fileName)
	drl_fileName = fileName
	local tbl = split(content, "\n")
	for _,line in pairs(tbl) do
		drl_lines[#drl_lines + 1] = line
		drl_parseLine(line)
	end
end

function drl_preview()
	SYS_clearTextArea()
	SYS_textAreaAddLine("File: " .. drl_fileName)
	SYS_textAreaAddLine("")
	SYS_textAreaAddLine("Found tools: [" .. #drl_tools .. "]: ")
	SYS_textAreaAddLine("")
	for index,tool in pairs(drl_tools) do
		SYS_textAreaAddLine("	[" .. index .."] - Size: " .. tool.size .. tool.format.Major.Ending)
	end
	SYS_textAreaAddLine("")
	SYS_textAreaAddLine("Found drill points [" .. #drl_points .. "]: ")
	for index,point in pairs(drl_points) do
		SYS_textAreaAddLine("	X: " .. point.x .. " Y: " .. point.y)
	end
end

function drl_show()
	print("Show")
end

RegisterConverter("drl", "^[dL][rR][lL]$", "ock")