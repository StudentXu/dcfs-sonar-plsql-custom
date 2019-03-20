CREATE OR REPLACE PACKAGE ENSEMBLE.AD_SOD IS -- Noncompliant
   procedure print_sdept(psno char);
   procedure print_grade(psno char);
   end;


CREATE OR REPLACE PACKAGE BODY ENSEMBLE.AD_SOD  -- Noncompliant
IS
procedure print_sdept(psno char) as
    psdept student.sdept%type;
begin
     select sdept into psdept
    from student
    where sno=psno;
end;
end;
/

create or replace function ensemble.str_list2( key_name in varchar2,  -- Noncompliant
                    key  in varchar2,
                    coname in varchar2,
                    tname     in varchar2 )
 return varchar2
as
    type rc  is ref cursor;
    str    varchar2(4000);
    sep    varchar2(2);
    val    varchar2(4000);
    cur    rc;
begin
    open cur for 'select '||coname||'
                    from '|| tname || '
                    where ' || key_name || ' = :x '
                using key;
    loop
        fetch cur into val;
        exit when cur%notfound;
        str := str || sep || val;
        sep := ', ';
    end loop;
    close cur;
    return str;
end;


create or replace procedure ensemble.str_list2( key_name in varchar2, -- Noncompliant
                    key  in varchar2,
                    coname in varchar2,
                    tname     in varchar2 )
as
    type rc  is ref cursor;
    str    varchar2(4000);
    sep    varchar2(2);
    val    varchar2(4000);
    cur    rc;
begin
    open cur for 'select '||coname||'
                    from '|| tname || '
                    where ' || key_name || ' = :x '
                using key;
    loop
        fetch cur into val;
        exit when cur%notfound;
        str := str || sep || val;
        sep := ', ';
    end loop;
    close cur;
end;


CREATE TABLE ensemble.DEPT( -- Noncompliant
EPTNO NUMBER(2) CONSTRAINT PK_DEPT PRIMARY KEY,
DNAME VARCHAR2(14),
LOC VARCHAR2(13)) ;

CREATE INDEX ensemble.IDX_SAL ON EMP(SAL); -- Noncompliant


BEGIN
	SELECT * INTO v_ad_bill_def FROM ensemble.ad_bill_def; -- Noncompliant
	update ensemble.ad_bill_def set v_ad_bill_def=1; -- Noncompliant
	delete from ensemble.ad_bill_def; -- Noncompliant	
END;

-- compliant
BEGIN
	SELECT * INTO v_ad_bill_def FROM ad_bill_def;
	update ad_bill_def set v_ad_bill_def=1; 
	delete from ad_bill_def;
END;

CREATE OR REPLACE PACKAGE AD_SOD IS 
   procedure print_sdept(psno char);
   procedure print_grade(psno char);
   end;


CREATE OR REPLACE PACKAGE BODY AD_SOD 
IS
procedure print_sdept(psno char) as
    psdept student.sdept%type;
begin
     select sdept into psdept
    from student
    where sno=psno;
end;
end;

create or replace function str_list2( key_name in varchar2,
                    key  in varchar2,
                    coname in varchar2,
                    tname     in varchar2 )
 return varchar2
as
    type rc  is ref cursor;
    str    varchar2(4000);
    sep    varchar2(2);
    val    varchar2(4000);
    cur    rc;
begin
    open cur for 'select '||coname||'
                    from '|| tname || '
                    where ' || key_name || ' = :x '
                using key;
    loop
        fetch cur into val;
        exit when cur%notfound;
        str := str || sep || val;
        sep := ', ';
    end loop;
    close cur;
    return str;
end;


create or replace procedure str_list2( key_name in varchar2,
                    key  in varchar2,
                    coname in varchar2,
                    tname     in varchar2 )
as
    type rc  is ref cursor;
    str    varchar2(4000);
    sep    varchar2(2);
    val    varchar2(4000);
    cur    rc;
begin
    open cur for 'select '||coname||'
                    from '|| tname || '
                    where ' || key_name || ' = :x '
                using key;
    loop
        fetch cur into val;
        exit when cur%notfound;
        str := str || sep || val;
        sep := ', ';
    end loop;
    close cur;
end;

CREATE TABLE DEPT(
EPTNO NUMBER(2) CONSTRAINT PK_DEPT PRIMARY KEY,
DNAME VARCHAR2(14),
LOC VARCHAR2(13)) ;

CREATE INDEX IDX_SAL ON EMP(SAL); 
