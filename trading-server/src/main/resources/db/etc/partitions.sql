--
-- Table partitioning by country code
--

ALTER RANGE default CONFIGURE ZONE USING num_replicas = 3, gc.ttlseconds = 600;
SHOW ZONE CONFIGURATION FOR RANGE default;