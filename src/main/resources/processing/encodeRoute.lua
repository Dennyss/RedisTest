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

local encodeCoordinate = function(coordinate)
    -- todo: implement coordinate encoding
    return coordinate .. " ";
end

for coordinateIndex = #latitudeCoordinates, 1, -1 do
    local latitudeCurrent = latitudeCoordinates[coordinateIndex];
    local longitudeCurrent = longitudeCoordinates[coordinateIndex];

    local latitudeDiff = latitudeCurrent - latitudePrevious;
    local longitudeDiff = longitudeCurrent - longitudePrevious;

    --Save current position for the next processing
    latitudePrevious = latitudeCurrent;
    longitudePrevious = longitudeCurrent;

    encodedRoute = encodedRoute .. encodeCoordinate(latitudeDiff) .. encodeCoordinate(longitudeDiff);
end

return encodedRoute;
