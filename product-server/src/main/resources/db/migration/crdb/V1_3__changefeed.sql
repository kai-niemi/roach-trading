SET CLUSTER SETTING kv.rangefeed.enabled = true;

-- CANCEL JOBS (SELECT job_id FROM [SHOW JOBS] where job_type = 'CHANGEFEED' and status = 'running');

CREATE CHANGEFEED FOR TABLE product
    INTO 'webhook-https://localhost:8443/api/cdc/product?insecure_tls_skip_verify=true'
    WITH updated, resolved='15s',
    webhook_sink_config='{"Flush": {"Messages": 5, "Frequency": "1s"}, "Retry": {"Max": "inf"}}';

-- CREATE CHANGEFEED FOR TABLE product
--     INTO 'webhook-https://192.168.1.1:8443/api/cdc/product?insecure_tls_skip_verify=true'
--     WITH updated, resolved='15s',
--     webhook_sink_config='{"Flush": {"Messages": 5, "Frequency": "1s"}, "Retry": {"Max": "inf"}}';

SELECT job_id FROM [SHOW JOBS] where job_type = 'CHANGEFEED' and status = 'running';

-- SHOW CHANGEFEED JOBS;