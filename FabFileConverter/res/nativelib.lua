function RegisterConverter(converterName, converterRegex, newEnding)
	print("Registering " .. converterName .. " -> " .. converterRegex)
	SYS_createConverter(converterName, converterRegex, newEnding)
end

function CreatePreviewWindow(converterName,renderFunction)
	SYS_createConverterWindow(converterName, renderFunction)
end

function split(pString, pPattern) --Nano:True
   local Table = {}  -- NOTE: use {n = 0} in Lua-5.0
   local fpat = "(.-)" .. pPattern
   local last_end = 1
   local s, e, cap = pString:find(fpat, 1)
   while s do
      if s ~= 1 or cap ~= "" then
     table.insert(Table,cap)
      end
      last_end = e+1
      s, e, cap = pString:find(fpat, last_end)
   end
   if last_end <= #pString then
      cap = pString:sub(last_end)
      table.insert(Table, cap)
   end
   return Table
end

function print_r(tbl, tabIndex)
	local tback = tabIndex
	if tabIndex == nil then
		tback = 0
	end
	local tab = ""
	for i = 0, tback do
		tab = tab .. "  "
	end
	if type(tbl) == "table" then
		for index, value in pairs(tbl) do
			if type(value) == "table" then
				local str = '"' .. index .. '" => '
				print(tab .. str .. "[")
				tabIndex = tonumber(tback)
				local b = ""
				for i = 1, string.len(str)  do
					b = b .. " "
				end
				print_r(value, tabIndex+(string.len(str) / 2))
				tback = tback - 2
				print(tab .. b .. "[")
			else
				print(tab .. '"' .. index .. '" => "', value ,'"')
			end
		end
	else
		print(tab .. "[" .. tbl .. "]")
	end	
end

function Class(name)
	local self = {}
	self.className = name
	return self
end

function RGB(r,g,b)
	local color = Class("color")
	color.r = r
	color.g = g
	color.b = b
	return color
end