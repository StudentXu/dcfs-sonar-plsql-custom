begin
UPDATE rb_acct   -- Noncompliant{The where clause should be included in the delete/update statement.}
   SET acct_status = 'A'; 
end;

begin
 DELETE FROM Person; -- Noncompliant
 UPDATE rb_acct
   SET acct_status = 'A' where id=1;
end;

-- compliant
begin
UPDATE rb_acct
   SET acct_status = 'A' where id=1;
end;

begin
 DELETE FROM Person where id='smith';
end;

UPDATE rb_acct -- Noncompliant
   SET acct_status = 'A'; 