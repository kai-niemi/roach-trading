begin;
set transaction isolation level read committed;
update account
set balance=balance+41,
    currency='EUR',
    last_modified_at='2024-02-27 15:34:21.2912951',
    name='alice',
    parent_id='fc01a4e1-3e11-46a7-bd28-9d0f8e485f83'
where id ='00ae584b-b4cc-4a45-9aab-4e4122015ad5';