gm_lines = {}

gm_tool_colors = {}

function addColor(rgb)
	gm_tool_colors[#gm_tool_colors + 1] = rgb
end

function addTool(t)
	gm_tools[#gm_tools + 1] = t
end

addColor(Colors.RGB(255,0,0))
addColor(Colors.RGB(0,255,0))
addColor(Colors.RGB(0,0,255))
addColor(Colors.RGB(255,255,0))
addColor(Colors.RGB(0,255,255))
addColor(Colors.RGB(255,0,255))
addColor(Colors.RGB(255,255,255))

function gm_g_drawArc(x,y,diameter,rgb)
	gm_setXY(x,y)
	gm_setRGB(rgb.r,rgb.g,rgb.b)
	gm_drawArc(diameter)
end

function gm_g_drawLine(x1,y1,x2,y2,rgb)
	gm_setXY(x1,y1)
	gm_setRGB(rgb.r,rgb.g,rgb.b)
	gm_drawLine(x2,y2)
end

gm_d_font = "Arial"

function gm_g_placeText(x,y,text,size,fontName,rgb)
	if b then
		gm_setRGB(rgb.r,rgb.g,rgb.b)
		gm_setXY(x,y)
		gm_placeText(text,size,fontName)
	else
		gm_setXY(x,y)
		gm_setRGB(fontName.r,fontName.r,fontName.g)
		gm_placeText(text,size,gm_d_font)
	end
end

function gm_frame_render()
	local minX = 0
	local minY = 0
	local maxX = 0
	local maxY = 0
	local xoy = 0

	for index, point in pairs(gm_points) do
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

	local xScale = gm_frame_width / (maxX + minX);
	local yScale = gm_frame_height / (maxY + minY);
	
	local scale = 0;
	
	if (xScale > yScale) then
		scale = yScale;
	elseif (yScale > xScale) then
		scale = xScale;
	elseif (xScale == yScale) then
		scale = xScale;
	end
	gm_setXY(0,0)
	gm_setRGB(0,0,0)
	gm_drawRect(gm_frame_width,gm_frame_height)
	local lastX = 0
	local lastY = 0
	local toolSize = 0
	local pColors = {
		Colors.RGB(255,0,0),
		Colors.RGB(0,0,255)
	}
	--term.print_r(gm_tool_colors)
	for index, point in pairs(gm_points) do
		local penState = tonumber(point.penState)
		
		toolSize = (point.tool.size * (math.pow(10,multForMinor))) * scale
		local x = (point.x * scale) - (toolSize)
		local y = (point.y * scale) - (toolSize)
		--print("Tool index " .. point.tool.index)
		if gm_tool_colors[point.tool.index] == nil then
			gm_g_drawArc(x, y, toolSize, Colors.RGB(255,255,255))
		else
			gm_g_drawArc(x, y, toolSize, gm_tool_colors[point.tool.index])
		end
		if point.penState then
			if #pColors >= tonumber(point.penState) then
				local penState = tonumber(point.penState)
				if penState == PenPos.PD then
					gm_setLineThickness(toolSize)
				elseif penState == PenPos.PU then
					gm_setLineThickness(1)
				end
				gm_g_drawLine(lastX, lastY, x, y, pColors[tonumber(point.penState)])
			end
		end
		lastX = x
		lastY = y
	end
end

function gm_frame_resize(width,height)

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

PenPos = {}
PenPos.PD = 1
PenPos.PU = 2

function GMPoint(x,y,t, penState)
	if x and y and t and penState then
		local point = Sys.Class("GMPoint")
		point.x = tonumber(x)
		point.y = tonumber(y)
		point.tool = t
		point.penState = penState
		return point
	else
		error("No x and/or y and/or t is defined.")
		return nil
	end
end

function gm_is_point_last_in_tool(tIndex, pIndex)
	local flagP = false
	for index, point in ipairs(gm_points) do
		if index == pIndex then
			flagP = true
		else
			if flagP then
				if point.tool.index == gm_tools[tIndex].index then
					return false
				end
			end
		end
	end
	return true
end

function gm_convert()
	print("Tools: ")
	gm_openFile()
	gm_writeLine("PA;")
	local lastState = -1
	for tIndex = 1, #gm_tools do
		gm_writeLine("PU;SP" .. tostring(tIndex) .. ";")
		for index, point in ipairs(gm_points) do
			if point.tool.index == gm_tools[tIndex].index then
				-- correct tool
				local line = ""
				print(point.penState .. " - " .. PenPos.PD .. " - " .. PenPos.PD)
				if tonumber(point.penState) == PenPos.PD then
					print("PD")
					line = line .. "PD"
				else
					print("PU")
					line = line .. "PU"
				end
				line = line .. point.x .. "," .. point.y .. ";"
				if gm_points[index + 1] then
					if gm_points[index + 1].tool.index == gm_tools[tIndex].index then
						if tonumber(gm_points[index + 1].penState) ~= tonumber(point.penState) then
							if tonumber(gm_points[index + 1].penState) == PenPos.PD then
								print("PD;")
								line = line .. "PD;"
							else
								print("PU;")
								line = line .. "PU;"
							end
						end
					end
				end
				gm_writeLine(line)
			end
		end
	end
	gm_writeLine("PU;")
	gm_closeFile()
end

local rXYd					= "[xX]([-0-9]{1,})[yY]([-0-9]{1,})[dD][0]([1-2])[*]";
local rXd					= "[xX]([-0-9]{1,})[dD][0]([1-2])[*]";
local rYd					= "[yY]([-0-9]{1,})[dD][0]([1-2])[*]";

local rXY					= "[xX]([-0-9]{1,})[yY]([-0-9]{1,})[*]";
local rX					= "[xX]([-0-9]{1,})[*]";

local rY					= "[yY]([-0-9]{1,})[*]";

local rTool					= "%ADD([0-9]{1,})C,([0-9.]{1,})[*][%]";
local rD					= "[Dd][0]([1-2])[*]";
local rToolSelect			= "[G][5][4][D]([0-9]{1,})[*]";

function gm_addPoint(p)
	gm_points[#gm_points + 1] = p
end

local currentTool = nil

local last = {}
last.x = 0
last.y = 0
last.ps = 0

function gm_parseLine(line)
	if string.matchRegex(rXYd, line) ~= nil then
		local groups = string.matchRegex(rXYd, line)
		term.print_r(groups)
		if #groups == 3 then
			local x = groups[1]
			last.x = x
			local y = groups[2]
			last.y = y
			local ps = groups[3]
			last.ps = ps
			gm_addPoint(GMPoint(x,y,currentTool,ps))
		else
			print("Error in args for rXYd - needed length 3: ")
			term.print_r(groups, "groups")
		end
	elseif string.matchRegex(rXd, line) ~= nil then
		local groups = string.matchRegex(rXd, line)
		term.print_r(groups)
		if #groups == 2 then
			local x = groups[1]
			last.x = x
			local y = last.y
			local ps = groups[2]
			last.ps = ps
			gm_addPoint(GMPoint(x,y,currentTool,ps))
		else
			print("Error in args for rXd - needed length 2: ")
			term.print_r(groups, "groups")
		end
	elseif string.matchRegex(rYd, line) ~= nil then
		local groups = string.matchRegex(rYd, line)
		term.print_r(groups)
		if #groups == 2 then
			local x = last.x
			local y = groups[1]
			last.y = y
			local ps = groups[2]
			last.ps = ps
			gm_addPoint(GMPoint(x,y,currentTool,ps))
		else
			print("Error in args for rYd - needed length 2: ")
			term.print_r(groups, "groups")
		end
	elseif string.matchRegex(rXY, line) ~= nil then
		local groups = string.matchRegex(rXY, line)
		if #groups == 2 then
			local x = groups[1]
			last.x = x
			local y = groups[2]
			last.y = y
			local ps = last.ps
			gm_addPoint(GMPoint(x,y,currentTool,ps))
		else
			print("Error in args for rXY - needed length 2: ")
			term.print_r(groups, "groups")
		end
	elseif string.matchRegex(rX, line) ~= nil then
		local groups = string.matchRegex(rX, line)
		if #groups == 1 then
			local x = groups[1]
			last.x = x
			local y = last.y
			local ps = last.ps
			gm_addPoint(GMPoint(x,y,currentTool,ps))
		else
			print("Error in args for rX - needed length 2: ")
			term.print_r(groups, "groups")
		end
	elseif string.matchRegex(rY, line) ~= nil then
		local groups = string.matchRegex(rY, line)
		if #groups == 1 then
			local x = last.x
			local y = groups[1]
			last.y = y
			local ps = last.ps
			gm_addPoint(GMPoint(x,y,currentTool,ps))
		else
			print("Error in args for rY - needed length 2: ")
			term.print_r(groups, "groups")
		end
	elseif string.matchRegex(rTool, line) ~= nil then
		local groups = string.matchRegex(rTool, line)
		if #groups == 2 then
			local index = tonumber(groups[1]) - 10
			local size = groups[2]
			addTool(Tool(Formats["Inch"], tonumber(size), index, size))
		else
			print("Error in args for rTool - needed length 2: ")
			term.print_r(groups, "groups")
		end
	elseif string.matchRegex(rD, line) ~= nil then
		local groups = string.matchRegex(rD, line)
		if #groups == 1 then
			last.ps = groups[1]
		else
			print("Error in args for rD - needed length 1: ")
			term.print_r(groups, "groups")
		end
	elseif string.matchRegex(rToolSelect, line) ~= nil then
		print("rToolSelect")
		local groups = string.matchRegex(rToolSelect, line)
		currentTool = gm_tools[tonumber(groups[1]) - 10]
	end
end

function gm_read(content, rawFileName, fileEnding)
	fileName = rawFileName .. "." .. fileEnding
	gm_lines = {}
	gm_tools = {}
	gm_points = {}
	last = {}
	last.x = 0
	last.y = 0
	currentTool = nil
	multForMinor = 3
	gm_fileName = fileName
	gm_readFile(content)

	local tIndex = 2
	local eStart = ""
	local flag = false
	for i = 1, string.len(fileEnding) do
		if not flag then
			local cC = string.sub(fileEnding, i, i)
			if string.isCharInt(cC) then
				local t = string.sub(fileEnding, i, string.len(fileEnding))
				eStart = string.sub(fileEnding, 0, i-1)
				tIndex = tonumber(t) + 1
				flag = true		
			end
		end
	end
	for i = tIndex, 10 do
		local fName = rawFileName .. "." .. eStart .. i
		--print("Checking " .. fName)
		if fs.exists(fName) then
			print("Found file: " .. fName)
			local fileHandle = io.open(fName, "r")
			if fileHandle then
				local contentOfFile = ""
				for line in fileHandle:lines() do
					contentOfFile = contentOfFile .. line .. "\n"
				end
				if contentOfFile ~= "" then
					print("Parsing file: " .. fName)
					gm_readFile(contentOfFile)
				end
			else
				print("No access to file: " .. fName)
			end
		end
	end
	
	term.print_r(gm_tools)
	--term.print_r(gm_points)
end

function gm_readFile(content, rawFileName, fileEnding)
	local tbl = string.split(content, "\n")
	for _,line in pairs(tbl) do
		gm_lines[#gm_lines + 1] = line
		gm_parseLine(line)
	end
end


function gm_preview()
	SYS_clearTextArea()
	SYS_textAreaAddLine("File: " .. gm_fileName)
	SYS_textAreaAddLine("")
	SYS_textAreaAddLine("Found tools: [" .. #gm_tools .. "]: ")
	SYS_textAreaAddLine("")
	for index,tool in pairs(gm_tools) do
		SYS_textAreaAddLine("	[" .. index .."] - Size: " .. tool.size .. tool.format.Major.Ending)
	end
	SYS_textAreaAddLine("")
	SYS_textAreaAddLine("Found mill points [" .. #gm_points .. "]: ")
	for index,point in pairs(gm_points) do
		SYS_textAreaAddLine("	X: " .. point.x .. " Y: " .. point.y)
	end
	SYS_textAreaScrollTop()
end

local request = true

function gm_show()
	if request then
		request = false
		gm_requestFrame("gm_frame_render", "gm_frame_resize", "Mill preview")
		gm_frame_setVisible(true)
		gm_frame_update()
	else
		gm_frame_setVisible(true)
		gm_frame_update()
	end
end

Sys.RegisterConverter("gm", "^[gG]{1}[mM]{0,1}([1-9]{1})$", "ock")
