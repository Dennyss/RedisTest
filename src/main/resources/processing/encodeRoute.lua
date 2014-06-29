--
-- Created by IntelliJ IDEA.
-- User: denys.kovalenko
-- Date: 6/20/2014
-- Time: 5:30 PM
-- To change this template use File | Settings | File Templates.
--

local latitudeKey = KEYS[1];
local longitudeKey = KEYS[2];
local routeLength = ARGV[1];

local latitudeCoordinates = redis.call('lrange', latitudeKey, 0, routeLength - 1);
local longitudeCoordinates = redis.call('lrange', longitudeKey, 0, routeLength - 1);

local latitudePrevious = 0;
local longitudePrevious = 0;
local encodedRoute = "";

local function lshift(x, by)
    return x * 2 ^ by
end

local function rshift(x, by)
    return math.floor(x / 2 ^ by)
end

local function bitOR(x, y)
    local p = 1
    while p < x do
        p = p + p
    end
    while p < y do
        p = p + p
    end
    local z = 0
    repeat
        if p <= x or p <= y then
            z = z + p
            if p <= x then
                x = x - p
            end
            if p <= y then
                y = y - p
            end
        end
        p = p * 0.5
    until p < 1

    return z
end

local function odd(x)
    return x == math.floor(x / 2) * 2
end

local function bitAND(x, y)
    local c, pow = 0, 1
    while x > 0 and y > 0 do
        --print("Debug info: x= " .. x .. " y= " .. y)
        if odd(x) and odd(y) then
            c = c + pow
        end
        x = math.floor(x / 2)
        y = math.floor(y / 2)
        pow = pow * 2
    end
    return c
end

local encodeCoordinate = function(coordinate)
    local sgn_num = lshift(coordinate, 1);
    if coordinate < 0 then
        sgn_num = (-sgn_num - 1);   -- bit not, qeuals '~' in java.
    end

    local encodeString = "";
    while sgn_num >= 0x20 do
        local nextValue = bitOR(0x20, bitAND(sgn_num, 0x1f)) + 63;
        encodeString = encodeString .. nextValue;
        sgn_num = sgn_num + rshift(sgn_num, 5);
    end

    sgn_num = sgn_num + 63;
    encodeString = encodeString .. sgn_num;

    return encodeString;
end

for coordinateIndex = #latitudeCoordinates, 1, -1 do
    -- Get coordinates and multiply them by 1e5
    local latitudeCurrent = latitudeCoordinates[coordinateIndex] * 1e5;
    local longitudeCurrent = longitudeCoordinates[coordinateIndex] * 1e5;

    local latitudeDiff = latitudeCurrent - latitudePrevious;
    local longitudeDiff = longitudeCurrent - longitudePrevious;

    --Save current position for the next processing
    latitudePrevious = latitudeCurrent;
    longitudePrevious = longitudeCurrent;

    encodedRoute = encodedRoute .. encodeCoordinate(latitudeDiff) .. encodeCoordinate(longitudeDiff);
end

print(encodedRoute);
return encodedRoute;
