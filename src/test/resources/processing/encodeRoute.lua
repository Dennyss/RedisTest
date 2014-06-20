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

local latitudeCoordinates = redis.call('lrange', latitudeKey, 0, routeLength);
local longitudeCoordinates = redis.call('lrange', longitudeKey, 0, routeLength);

-- todo: add encoding logic here and return result
