--
-- Created by IntelliJ IDEA.
-- User: denys.kovalenko
-- Date: 7/3/2014
-- Time: 11:02 AM
-- To change this template use File | Settings | File Templates.
--
local vin = ARGV[1];
local latitude = ARGV[2];
local longitude = ARGV[3];
local timestamp = ARGV[4];

local TIME_DELIMITER = 120000; -- 2 min
local LAST_POINT_TIMESTAMP_KEY = "lastPointTimestamp:" .. vin;
local ROUT_SEGMENTS_KEY = "routeSegments:" .. vin;

-- Function definitions
local isSameSegment = function(prevTimestamp, timestamp)
    if prevTimestamp == "nil" then
        return false;
    end
    return (timestamp - prevTimestamp) < TIME_DELIMITER;
end


local changeExistingRoutePackMessage = function(vin, latitude, longitude, timestamp, existingSegment)
    if existingSegment == "nil" then
        -- todo: do we need this? The rout segment is empty (it will be the first point here), return message pack with this point

        --return  MessagePack();
    end

    -- The rout segment is not empty (this is not first point here), remove timestamp from last point, add new point return message pack with this point





    return "existingSegment";
end


local createNewSegmentPackMessage = function(vin, latitude, longitude, timestamp)
    -- Create new pack message with new point




    return "newSegment";
end


-- Read timestamp of previous point
local prevTimestamp = redis.call("get", LAST_POINT_TIMESTAMP_KEY);

-- Save current point timestamp for VIN (override previous)
redis.call("set", LAST_POINT_TIMESTAMP_KEY, timestamp);


if isSameSegment(prevTimestamp, timestamp) then
    --  Save point to DB, change existing segment (add new point and update last timestamp).
    local existingSegment = redis.call("lrange", ROUT_SEGMENTS_KEY, 0, 0);
    redis.call("lset", ROUT_SEGMENTS_KEY, 0, changeExistingRoutePackMessage(vin, latitude, longitude, timestamp, existingSegment));
else
    -- Create new segment and add it to first position of the list
    redis.call("lpush", ROUT_SEGMENTS_KEY, createNewSegmentPackMessage(vin, latitude, longitude, timestamp));
end