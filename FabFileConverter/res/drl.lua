drl_lines = {}

drl_tool_colors = {}

function addColor(rgb)
	drl_tool_colors[#drl_tool_colors + 1] = rgb
end

addColor(Colors.RGB(255,0,0))
addColor(Colors.RGB(0,255,0))
addColor(Colors.RGB(0,0,255))
addColor(Colors.RGB(255,255,0))
addColor(Colors.RGB(0,255,255))
addColor(Colors.RGB(255,0,255))
addColor(Colors.RGB(255,255,255))

function drl_g_drawArc(x,y,diameter,rgb)
	drl_setXY(x ,y)
	drl_setRGB(rgb.r,rgb.g,rgb.b)
	drl_drawArc(diameter)
end

function drl_g_drawLine(x1,y1,x2,y2,rgb)
	drl_setXY(x1,y1)
	drl_setRGB(rgb.r,rgb.g,rgb.b)
	drl_drawLine(x2,y2)
end

drl_d_font = "Arial"

function drl_g_placeText(x,y,text,size,fontName,rgb)
	if b then
		drl_setRGB(rgb.r,rgb.g,rgb.b)
		drl_setXY(x,y)
		drl_placeText(text,size,fontName)
	else
		drl_setXY(x,y)
		drl_setRGB(fontName.r,fontName.r,fontName.g)
		drl_placeText(text,size,drl_d_font)
	end
end

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

function drl_frame_render()
	local minX = 0
	local minY = 0
	local maxX = 0
	local maxY = 0
	local xoy = 0

	for index, point in pairs(drl_points) do
		if(point.x > maxX) then
			maxX = point.x
		elseif (point.x < minX) then
			minX = point.x
		end
		if(point.y > maxY) then
			maxY = point.y
		elseif (point.y < minY) then
			minY = point.Y
		end
	end

	local xScale = drl_frame_width / (maxX + minX);
	local yScale = drl_frame_height / (maxY + minY);
	
	local scale = 0;
	
	if (xScale > yScale) then
		scale = yScale;
	elseif (yScale > xScale) then
		scale = xScale;
	elseif (xScale == yScale) then
		scale = xScale;
	end
	drl_setXY(0,0)
	drl_setRGB(0,0,0)
	drl_drawRect(drl_frame_width,drl_frame_height)
	local lastX = 0
	local lastY = 0
	local toolSize = 0
	for index, point in pairs(drl_points) do
		toolSize = (point.tool.size * (math.pow(10,multForMinor))) * scale
		local x = (point.x * scale) - (toolSize)
		local y = (point.y * scale) - (toolSize)
		if #drl_tool_colors < tonumber(point.tool.index) then
			drl_g_drawArc(x, y, toolSize, Colors.RGB(255,255,255))
		else
			drl_g_drawArc(x, y, toolSize, drl_tool_colors[point.tool.index])
		end
		drl_g_drawLine(lastX, lastY, x, y, Colors.RGB(255,255,255))
		lastX = x
		lastY = y
	end
end

function drl_frame_resize(width,height)

end

function Tool(format, size, index, sSize) --sSize is the size written in the file so for example: "0.0300"
	if format and size and index and sSize then
		local tool = Sys.Class("Tool")
		tool.format = format
		tool.size = size
		tool.index = tonumber(index)
		tool.sSize = sSize
		return tool
	else
		error("No format and/or size and/or index and/or sSize defined")
		return nil
	end
end

function Point(x,y,t)
	if x and y and t then
		local point = Sys.Class("Point")
		point.x = tonumber(x)
		point.y = tonumber(y)
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
		local x = tonumber(point.x)
		local y = tonumber(point.y)
		if x < 100 then
			x = "0" .. x
		end
		if y < 100 then
			y = "0" .. y
		end
		x = "00" .. x
		y = "00" .. y
		drl_writeLine("X" .. x .. "Y" .. y)
	end
	drl_writeLine("M30")
	drl_closeFile()
	print("Done.")
end

function drl_parseLine(line)
	if(string.match(line, xyMatch)) then
		local x,y = string.match(line, xyMatch)
		last.x = tonumber(x)
		last.y = tonumber(y)
		drl_points[#drl_points + 1] = Point(last.x,last.y,drl_tools[currentTool])
	elseif(string.match(line,xMatch)) then
		local x = string.match(line, xMatch)
		last.x = tonumber(x)
		drl_points[#drl_points + 1] = Point(x,last.y,drl_tools[currentTool])
	elseif(string.match(line,yMatch)) then
		local y = string.match(line, yMatch)
		last.y = tonumber(y)
		drl_points[#drl_points + 1] = Point(last.x,y,drl_tools[currentTool])
	elseif(string.match(line, toolMatch)) then
		local tNum, tSize = string.match(line, toolMatch)
		drl_tools[tonumber(tNum)] = Tool(currentFormat, tonumber(tSize), tNum, tSize)
	elseif(string.match(line, formatMatch)) then
		local formatType, mode = string.match(line, formatMatch)
		if(formatType == "INCH") then --TODO: add meters as optoin
			if mode == "T" then else
				error("File is not correctly exported. Please export with ' Supress Leading Zero's '")
			end
			currentFormat = Formats["Inch"]
		end
	elseif(string.match(line, fmatMatch)) then
		multForMinor = tonumber(string.match(line,fmatMatch)) + 1
	elseif(string.match(line, toolSelectMatch)) then
		currentTool = tonumber(string.match(line, toolSelectMatch))
	end
end

function drl_read(content, rawFileName, fileEnding)
	fileName = rawFileName .. "." .. fileEnding
	drl_lines = {}
	drl_tools = {}
	drl_points = {}
	last = {}
	last.x = 0
	last.y = 0
	currentTool = nil
	multForMinor = 2
	drl_fileName = fileName
	local tbl = string.split(content, "\n")
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
	SYS_textAreaScrollTop()
end

local request = true

function drl_show()
	if request then
		request = false
		drl_requestFrame("drl_frame_render", "drl_frame_resize", "Drill preview")
		drl_frame_setVisible(true)
		drl_frame_update()
	else
		drl_frame_setVisible(true)
		drl_frame_update()
	end
end

Sys.RegisterConverter("drl", "^[dD][rR][lL]$", "txt")
