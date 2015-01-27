Sys = {}
term = {}

function Sys.RegisterConverter(converterName, converterRegex, newEnding)
	print("Registering " .. converterName .. " -> " .. converterRegex)
	SYS_createConverter(converterName, converterRegex, newEnding)
end

function Sys.CreatePreviewWindow(converterName,renderFunction)
	SYS_createConverterWindow(converterName, renderFunction)
end

function string.split(pString, pPattern) --Nano:True
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

function string.matchRegex(pattern, value, o)
	ret = SYS_regex_match(pattern, value)
	if ret ~= nil then
		if type(ret) == "string" then
			tbl = string.split(ret, "\n");
			if o then
				print("Got regex match in form of a " .. type(tbl) .. ": ") 
				term.print_r(tbl)
			end
			return tbl
		end
	end
	return nil
end

function string.repeatChar(str, int)
	local nstr = ""
	for i = 0, int do
		nstr = nstr .. str
	end
	return nstr
end

function string.isCharInt(c)
	return c == "0" or c == "1" or c == "2" or c == "3" or c == "4" or c == "5" or c == "6" or c == "7" or c == "8" or c == "9"
end

function table_sort_comp(w1,w2)
	if type(w1) == "table" and type(w2) == "string" then
		return false
	elseif type(w1) == "string" and type(w2) == "table" then
		return true
	elseif type(w1) == "table" and type(w2) == "integer" then
		return true
	elseif type(w1) == "integer" and type(w2) == "table" then
		return false
	elseif type(w1) == "integer" and type(w2) == "string" then
		return true	
	elseif type(w1) == "string" and type(w2) == "integer" then
		return false
	end
	return false
end

function term.print_r(tbl, r, tabIndex)
	if r then
		local str = '"' .. tostring(r) .. '" ['
		print(str)
		local tIndex = 0
		if tabIndex then
			tIndex = tabIndex
		end
		term.print_r(tbl, nil, tIndex + string.len(str))
		print("]")
		return
	end
	local tab = ""
	local tIndex = 0
	if tabIndex then
		tIndex = tabIndex
	else
		tIndex = 0
	end
	tab = string.repeatChar(" ", tIndex)
	if tbl then
		if type(tbl) == "table" then
			for index, value in pairs(tbl) do
				if type(value) == "table" then
					local str = '"' .. tostring(index) .. '" ['
					print(tab .. str)
					term.print_r(value, nil, tIndex + string.len(str))
					print(tab .. "]")
				else
					local str = '"' .. tostring(index) .. '" ['
					if value ~= nil then
						print(tab .. '"' .. index .. '" => "' .. value .. '"')
					else
						print(tab .. '"' .. index .. '" => nil')
					end
				end
			end
		else
			if tbl ~= nil then
				print(tab .. '"' .. index .. '" => "' .. value .. '"')
			end
		end
	end
end

function Sys.Class(name)
	local self = {}
	self.className = name
	return self
end

Colors = {}

function Colors.RGB(r,g,b)
	local color = Sys.Class("color")
	color.r = r
	color.g = g
	color.b = b
	return color
end

fs = {}

function fs.exists(name)
   local f=io.open(name,"r")
   if f~=nil then io.close(f) return true else return false end
end

