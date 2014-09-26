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
    local PACKED_SEGMENTS = "packedSegments:" .. vin;
    -- Unpacked route segment data structure:
    local LAST_POINT_TIMESTAMP = "lastPointTimestamp:" .. vin;
    local SEGMENT_TIMESTAMPS = "segmentTimestamps:" .. vin;
    local START_TIMESTAMP = "start";
    local END_TIMESTAMP = "end";
    local SEGMENT_POINTS_LAT = "segmentPointsLat:" .. vin;
    local SEGMENT_POINTS_LON = "segmentPointsLon:" .. vin;

    local isSameSegment = function(prevTimestamp, timestamp)
        if prevTimestamp == false then
            return false;
        end

        return (timestamp - prevTimestamp) < TIME_DELIMITER;
    end

    local updateExistingSegment = function()
    -- Add new point to existing segment.
        redis.call("rpush", SEGMENT_POINTS_LAT, latitude); -- RPUSH O(1)
        redis.call("rpush", SEGMENT_POINTS_LON, longitude); -- RPUSH O(1)
        redis.call("hmset", SEGMENT_TIMESTAMPS, END_TIMESTAMP, timestamp); -- HMSET O(N)
    end

    local createNewSegment = function()
    -- Set start and end timestamps for current segment
        redis.call("hmset", SEGMENT_TIMESTAMPS, START_TIMESTAMP, timestamp); -- HMSET O(N)
        redis.call("hmset", SEGMENT_TIMESTAMPS, END_TIMESTAMP, timestamp); -- HMSET O(N)
        redis.call("rpush", SEGMENT_POINTS_LAT, latitude); -- RPUSH O(1)
        redis.call("rpush", SEGMENT_POINTS_LON, longitude); -- RPUSH O(1)
    end

    local packPreviousSegment = function()
        local startTimestamp = tonumber(redis.call("hmget", SEGMENT_TIMESTAMPS, START_TIMESTAMP)[1]);
        local endTimestamp = tonumber(redis.call("hmget", SEGMENT_TIMESTAMPS, END_TIMESTAMP)[1]);
        local latitudes = redis.call("lrange", SEGMENT_POINTS_LAT, 0, -1); -- LRANGE O(N) in this case
        local longitudes = redis.call("lrange", SEGMENT_POINTS_LON, 0, -1); -- LRANGE O(N) in this case

        local points = {}
        for i = 1, #latitudes do
            points[i] = { tonumber(latitudes[i]), tonumber(longitudes[i])};
        end

        local unpackedSegment = {
            startTimestamp, endTimestamp, points
        };

        redis.call("lpush", PACKED_SEGMENTS, cmsgpack.pack(unpackedSegment)); -- LPUSH O(1)
        redis.call("ltrim", PACKED_SEGMENTS, 0, 19); -- LTRIM O(N)
    end

    local clearDBForNewSegment = function()
        redis.call("del", SEGMENT_POINTS_LAT); -- DEL O(N) - because of List deleting
        redis.call("del", SEGMENT_POINTS_LON); -- DEL O(N) - because of List deleting
        redis.call("del", SEGMENT_TIMESTAMPS); -- DEL O(N)
    end

    -- Read timestamp of previous point
    local prevTimestamp = redis.call("get", LAST_POINT_TIMESTAMP); -- GET O(1)

    -- Save current point timestamp for VIN (override previous)
    redis.call("set", LAST_POINT_TIMESTAMP, timestamp); -- SET O(1)

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