--
-- Created by IntelliJ IDEA.
-- User: denys.kovalenko
-- Date: 7/3/2014
-- Time: 11:02 AM
-- To change this template use File | Settings | File Templates.
--

local argumentsMessage = cmsgpack.unpack(ARGV[1]);

local vin = argumentsMessage[1];
local point = argumentsMessage[2];
local timestamp = argumentsMessage[3];

local TIME_DELIMITER = 120000; -- 2 min
local LAST_POINT_TIMESTAMP_KEY = "lastPointTimestamp:" .. vin;
local ROUT_SEGMENTS_KEY = "routeSegments:" .. vin;

-- Function definitions
local isSameSegment = function(prevTimestamp, timestamp)
    if prevTimestamp == false then
        return false;
    end

    return (timestamp - prevTimestamp) < TIME_DELIMITER;
end


local updateExistingSegmentPackMessage = function(point, timestamp, existingSegment)
    if existingSegment == false then
        createNewSegmentPackMessage(point, timestamp);
    end

    -- The rout segment is not empty (this is not first point here), update entTimestamp, add new point, return message pack with this point
    local unpackedExistingSegment = cmsgpack.unpack(existingSegment[#existingSegment]);
    unpackedExistingSegment[2] = timestamp;   -- update endTimestamp
    table.insert(unpackedExistingSegment[3], point);

    return cmsgpack.pack(unpackedExistingSegment);
end


local createNewSegmentPackMessage = function(point, timestamp)
    -- Create new pack message with new point
    local newSegment = {timestamp, timestamp, {point}};

    return cmsgpack.pack(newSegment);
end


-- Read timestamp of previous point
local prevTimestamp = redis.call("get", LAST_POINT_TIMESTAMP_KEY);

-- Save current point timestamp for VIN (override previous)
redis.call("set", LAST_POINT_TIMESTAMP_KEY, timestamp);


if isSameSegment(prevTimestamp, timestamp) then
    --  Save segment to DB, change last existing segment (add new point and update last timestamp).
    local existingSegment = redis.call("lrange", ROUT_SEGMENTS_KEY, 0, 0);
    redis.call("lset", ROUT_SEGMENTS_KEY, 0, updateExistingSegmentPackMessage(point, timestamp, existingSegment));
else
    -- Create new segment and add it to first position of the list
    redis.call("lpush", ROUT_SEGMENTS_KEY, createNewSegmentPackMessage(point, timestamp));
    redis.call("ltrim", ROUT_SEGMENTS_KEY, 0, 19);
end