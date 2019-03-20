begin
  select empno
    into var
    from emp;
exception
  when NO_DATA_FOUND then -- Noncompliant {{Handle the variables used in the SELECT INTO statements here so their values do not become undefined.}}
    null;
end;

begin
  select empno
    into var
    from emp;
exception
  when too_many_rows or no_data_found then -- Noncompliant
    null;
end;

begin
  select empno
    into var
    from emp;
exception
  when no_data_found or too_many_rows then -- Noncompliant
    null;
end;

-- compliant
begin
  select empno
    into var
    from emp;
exception
  when NO_DATA_FOUND then
    var := null;
end;

begin
  select empno
    into var
    from emp;
exception
  when NO_DATA_FOUND then
    log(sqlerrm);
    raise myexception;
end;