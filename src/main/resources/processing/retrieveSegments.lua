--
-- Created by IntelliJ IDEA.
-- User: denys.kovalenko
-- Date: 7/22/2014
-- Time: 2:17 PM
-- To change this template use File | Settings | File Templates.
--

local vin = KEYS[1];

-- Packed route segment data structure:
local PACKED_SEGMENTS = "packedSegments:" .. vin;

-- Unpacked route segment data structure:
local SEGMENT_TIMESTAMPS = "segmentTimestamps:" .. vin;
local START_TIMESTAMP = "start";
local END_TIMESTAMP = "end";
local SEGMENT_POINTS_LAT = "segmentPointsLat:" .. vin;
local SEGMENT_POINTS_LON = "segmentPointsLon:" .. vin;

local findPreviousSegment = function()
    local startTimestamp = tonumber(redis.call("hmget", SEGMENT_TIMESTAMPS, START_TIMESTAMP)[1]);
    local endTimestamp = tonumber(redis.call("hmget", SEGMENT_TIMESTAMPS, END_TIMESTAMP)[1]);

    local latitudes = redis.call("lrange", SEGMENT_POINTS_LAT, 0, -1); -- LRANGE O(N)
    local longitudes = redis.call("lrange", SEGMENT_POINTS_LON, 0, -1); -- LRANGE O(N)

    local points = {}
    for i = 1, #latitudes do
        points[i] = { tonumber(latitudes[i]), tonumber(longitudes[i]) };
    end

    local unpackedSegment = {
        startTimestamp, endTimestamp, points
    };

    return cmsgpack.pack(unpackedSegment);
end

local segments = redis.call("lrange", PACKED_SEGMENTS, 0, 18);

-- Add to first element unppacked segment
table.insert(segments, 1, findPreviousSegment());

return segments;
