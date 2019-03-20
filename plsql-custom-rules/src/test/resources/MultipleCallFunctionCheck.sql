CREATE OR REPLACE PACKAGE BODY ENSEMBLE.AD_SOD 
IS
procedure print_sdept(psno char) as
    psdept student.sdept%type;
begin
	RBSTOR.get_acct_detail_aio (v_acct_no, 
                                 p_acct_stats.aio_internal_key,
                                 v_acct_det
                                );
    select sdept into psdept
    from student
    where sno=psno;
    RBSTOR.get_acct_detail_aio (v_acct_no,
                                 p_acct_stats.aio_internal_key,
                                 v_acct_det
                                );
    RBSTOR.get_acct_detail_aiot (v_acct_no,    
                                 p_acct_stats.aio_internal_key,
                                 v_acct_det
                                ); 
    RBSTOR.get_acct_detail_aio (v_acct_no,     -- Noncompliant
                                 p_acct_stats.aio_internal_key,
                                 v_acct_det
                                ); 
end;
end;
/
CREATE OR REPLACE PACKAGE BODY ENSEMBLE.AD_SOD 
IS
procedure print_sdept(psno char) as
    psdept student.sdept%type;
begin
fm_util.log_error(SQLCODE);
fm_util.log_error(SQLCODE);
fm_util.log_error(SQLCODE); 
sys.dbms_output.put_line(SQLCODE);
sys.dbms_output.put_line(SQLCODE);
sys.dbms_output.put_line(SQLCODE); 
cbsd_log.DEBUG(SQLCODE);
cbsd_log.DEBUG(SQLCODE);
cbsd_log.DEBUG(SQLCODE);
raise_application_error(SQLCODE);
raise_application_error(SQLCODE);
raise_application_error(SQLCODE);
end;
end;
CREATE OR REPLACE PACKAGE BODY ENSEMBLE.AD_SOD 
IS
procedure print_sdept(psno char) as
    psdept student.sdept%type;
begin
	get_acct_detail_aio (v_acct_no, 
                                 p_acct_stats.aio_internal_key,
                                 v_acct_det
                                );
    select sdept into psdept
    from student
    where sno=psno;
    get_acct_detail_aio (v_acct_no,
                                 p_acct_stats.aio_internal_key,
                                 v_acct_det
                                );
    get_acct_detail_aiot (v_acct_no,    
                                 p_acct_stats.aio_internal_key,
                                 v_acct_det
                                ); 
    get_acct_detail_aio (v_acct_no,    -- Noncompliant
                                 p_acct_stats.aio_internal_key,
                                 v_acct_det
                                ); 
end;
end;
/

CREATE OR REPLACE PACKAGE BODY ENSEMBLE.AD_SOD 
IS
procedure print_sdept(psno char) as
    psdept student.sdept%type;
begin
	RBSTOR.get_acct_detail_aio (v_acct_no, 
                                 p_acct_stats.aio_internal_key,
                                 v_acct_det
                                );
    select max(sdept),max(id),max(score) into psdept,pid,psocre
    from student
    where sno=psno;
    RBSTOR.get_acct_detail_aio (v_acct_no,
                                 p_acct_stats.aio_internal_key,
                                 v_acct_det
                                );                     
end;
end;
/