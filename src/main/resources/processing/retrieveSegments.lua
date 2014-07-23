--
-- Created by IntelliJ IDEA.
-- User: denys.kovalenko
-- Date: 7/22/2014
-- Time: 2:17 PM
-- To change this template use File | Settings | File Templates.
--

local vin = KEYS[1];

-- Packed route segment data structure:
local PACKED_ROUT_SEGMENTS_KEY = "packedRouteSegments:" .. vin;

-- Unpacked route segment data structure:
local ROUTE_SEGMENT_START_TIMESTAMP_KEY = "routeSegmentStartTimestamp:" .. vin;
local ROUTE_SEGMENT_END_TIMESTAMP_KEY = "routeSegmentEndTimestamp:" .. vin;
local ROUT_SEGMENT_POINTS_LAT_KEY = "routeSegmentPointsLat:" .. vin;
local ROUT_SEGMENT_POINTS_LON_KEY = "routeSegmentPointsLon:" .. vin;


local packPreviousSegment = function(latitudes, longitudes)
    local startTimestamp = tonumber(redis.call("get", ROUTE_SEGMENT_START_TIMESTAMP_KEY));
    local endTimestamp = tonumber(redis.call("get", ROUTE_SEGMENT_END_TIMESTAMP_KEY));

    local points = {}
    for i = 1, #latitudes do
        points[i] = { tonumber(latitudes[i]), tonumber(longitudes[i]) };
    end

    local unpackedSegment = {
        startTimestamp, endTimestamp, points
    };

    redis.call("lpush", PACKED_ROUT_SEGMENTS_KEY, cmsgpack.pack(unpackedSegment)); -- LPUSH O(1)
    redis.call("ltrim", PACKED_ROUT_SEGMENTS_KEY, 0, 19) -- LTRIM 0(N)
end

local deleteUnpackedData = function()
    redis.call("del", ROUT_SEGMENT_POINTS_LAT_KEY); -- DEL O(N) - because of List deleting
    redis.call("del", ROUT_SEGMENT_POINTS_LON_KEY); -- DEL O(N) - because of List deleting
    redis.call("del", ROUTE_SEGMENT_START_TIMESTAMP_KEY); -- DEL O(1)
    redis.call("del", ROUTE_SEGMENT_END_TIMESTAMP_KEY); -- DEL O(1)
end

-- Check if there some unpacked points in unpacked datastructure, if exist - pack it
local latitudes = redis.call("lrange", ROUT_SEGMENT_POINTS_LAT_KEY, 0, -1) -- LRANGE 0(N)
local longitudes = redis.call("lrange", ROUT_SEGMENT_POINTS_LON_KEY, 0, -1) -- LRANGE 0(N)
if not latitudes == false then
    packPreviousSegment(latitudes, longitudes);
    deleteUnpackedData();
end
