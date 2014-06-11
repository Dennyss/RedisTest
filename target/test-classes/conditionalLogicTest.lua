-- Conditional logic, HEXISTS and HINCR excercise and testing example

if redis.call("HEXISTS", KEYS[1], ARGV[1]) == 1 then
    return redis.call("HINCRBY", KEYS[1], ARGV[1], 1)
else
    return nil
end


--if redis.call("EXISTS", KEYS[1], ARGV[1]) == 1 then
--    return redis.call("INCR", KEYS[1], ARGV[1])
--else
--    return nil
--end
