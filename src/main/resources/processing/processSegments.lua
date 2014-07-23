--
-- Created by IntelliJ IDEA.
-- User: denys.kovalenko
-- Date: 7/3/2014
-- Time: 11:02 AM
-- To change this template use File | Settings | File Templates.
--

local argumentsMessages = cmsgpack.unpack(ARGV[1]);

local processSegment = function(argumentsMessage)
    local vin = argumentsMessage[1];
    local latitude = argumentsMessage[2];
    local longitude = argumentsMessage[3];
    local timestamp = argumentsMessage[4];

    local TIME_DELIMITER = 120000; -- 2 min

    -- Packed route segment data structure:
    local PACKED_ROUT_SEGMENTS_KEY = "packedRouteSegments:" .. vin;

    -- Unpacked route segment data structure:
    local LAST_POINT_TIMESTAMP = "lastPointTimestamp:" .. vin;
    local ROUTE_SEGMENT_START_TIMESTAMP_KEY = "routeSegmentStartTimestamp:" .. vin;
    local ROUTE_SEGMENT_END_TIMESTAMP_KEY = "routeSegmentEndTimestamp:" .. vin;
    local ROUT_SEGMENT_POINTS_LAT_KEY = "routeSegmentPointsLat:" .. vin;
    local ROUT_SEGMENT_POINTS_LON_KEY = "routeSegmentPointsLon:" .. vin;

    -- Function definitions
    local isSameSegment = function(prevTimestamp, timestamp)
        if prevTimestamp == false then
            return false;
        end

        return (timestamp - prevTimestamp) < TIME_DELIMITER;
    end

    local updateExistingSegment = function()
    -- Add new point to existing segment. End timestamp is already updated.
        redis.call("rpush", ROUT_SEGMENT_POINTS_LAT_KEY, latitude); -- RPUSH O(1)
        redis.call("rpush", ROUT_SEGMENT_POINTS_LON_KEY, longitude); -- RPUSH O(1)
        redis.call("set", ROUTE_SEGMENT_END_TIMESTAMP_KEY, timestamp); -- SET O(1)
    end

    local createNewSegment = function()
    -- Set start timestamp for current segment (end timestamp is already set before)
        redis.call("set", ROUTE_SEGMENT_START_TIMESTAMP_KEY, timestamp); -- SET O(1)
        redis.call("set", ROUTE_SEGMENT_END_TIMESTAMP_KEY, timestamp); -- SET O(1)
        redis.call("rpush", ROUT_SEGMENT_POINTS_LAT_KEY, latitude); -- RPUSH O(1)
        redis.call("rpush", ROUT_SEGMENT_POINTS_LON_KEY, longitude); -- RPUSH O(1)
    end

    local packPreviousSegment = function()
        local startTimestamp = tonumber(redis.call("get", ROUTE_SEGMENT_START_TIMESTAMP_KEY));
        local endTimestamp = tonumber(redis.call("get", ROUTE_SEGMENT_END_TIMESTAMP_KEY));
        local latitudes = redis.call("lrange", ROUT_SEGMENT_POINTS_LAT_KEY, 0, -1); -- LRANGE 0(N) in this case
        local longitudes = redis.call("lrange", ROUT_SEGMENT_POINTS_LON_KEY, 0, -1) -- LRANGE 0(N) in this case

        local points = {}
        for i = 1, #latitudes do
            points[i] = { tonumber(latitudes[i]), tonumber(longitudes[i])};
        end

        local unpackedSegment = {
            startTimestamp, endTimestamp, points
        };

        redis.call("lpush", PACKED_ROUT_SEGMENTS_KEY, cmsgpack.pack(unpackedSegment)); -- LPUSH O(1)
        redis.call("ltrim", PACKED_ROUT_SEGMENTS_KEY, 0, 19); -- LTRIM 0(N)
    end

    local clearDBForNewSegment = function()
        redis.call("del", ROUT_SEGMENT_POINTS_LAT_KEY); -- DEL O(N) - because of List deleting
        redis.call("del", ROUT_SEGMENT_POINTS_LON_KEY); -- DEL O(N) - because of List deleting
        redis.call("del", ROUTE_SEGMENT_START_TIMESTAMP_KEY); -- DEL O(1)
        redis.call("del", ROUTE_SEGMENT_END_TIMESTAMP_KEY); -- DEL O(1)
    end

    -- Read timestamp of previous point
    local prevTimestamp = redis.call("get", LAST_POINT_TIMESTAMP); -- GET 0(1)

    -- Save current point timestamp for VIN (override previous)
    redis.call("set", LAST_POINT_TIMESTAMP, timestamp); -- SET 0(1)



    if isSameSegment(prevTimestamp, timestamp) then
        updateExistingSegment();
    else
        -- If first point for VIN - we don't need to pack anything previous
        if not prevTimestamp == false then
            packPreviousSegment();
            clearDBForNewSegment();
        end
        createNewSegment();
    end
end


for i = 1, #argumentsMessages do
    processSegment(argumentsMessages[i]);
end