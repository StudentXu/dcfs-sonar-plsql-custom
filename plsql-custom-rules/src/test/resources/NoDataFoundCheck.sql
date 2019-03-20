Prompt Package Body CDB_SECURITY;
CREATE OR REPLACE PACKAGE BODY           "CDB_SECURITY" AS
PROCEDURE checkAuth(pCUserName VARCHAR2 ,
                    pAUserName VARCHAR2 ,
                    pPassWord  VARCHAR2 ,
         pResult   OUT  BOOLEAN,
        pErrmsg   OUT VARCHAR2)
IS
   v_CLever NUMBER;
   v_ALever NUMBER;
   v_text VARCHAR2(1024);
 
BEGIN
    SELECT user_level -- Noncompliant {{The SELECT statement must have NO_DATA_FOUND exception capture.}}
        INTO v_text
 FROM FM_USER
 WHERE user_id = pCUserName ;
 
 v_CLever :=  TO_NUMBER(trim(v_text));
 
    SELECT user_level -- Noncompliant {{The SELECT statement must have NO_DATA_FOUND exception capture.}}
        INTO v_text
 FROM FM_USER
 WHERE user_id = pAUserName ;
 
 v_ALever :=  TO_NUMBER(trim(v_text));
 

 EXCEPTION
    WHEN others THEN
        pResult := FALSE ;
     pErrmsg := 'Oracle Exception!'||SQLERRM ;
 
END checkAuth;
 
END Cdb_Security;
/


BEGIN
         SELECT branch  -- Noncompliant {{The SELECT statement must have NO_DATA_FOUND exception capture.}}
           INTO v_branch
           FROM gl_saving_base_acct
          WHERE acct_no = v_input.acct_no;

EXCEPTION
         WHEN othersw    
         THEN
            v_qeq :=1;
END;

BEGIN  
  V_INT_VAL := 123456;  
  INSERT INTO MY_TEST VALUES ('TEST_SUCCESS', V_INT_VAL);  
  SELECT i INTO V_COUNT FROM MY_TEST;     -- Noncompliant {{The SELECT statement must have NO_DATA_FOUND exception capture.}}
  INSERT INTO MY_TEST VALUES ('TEST_FAIL', 'ABC');    
  COMMIT;  
EXCEPTION  
  WHEN OTHERSw THEN  
    ROLLBACK;
    SELECT COUNT(*) INTO V_COUNT FROM MY_TEST;  
END; 



BEGIN
         SELECT branch 
           INTO v_branch
           FROM gl_saving_base_acct
          WHERE acct_no = v_input.acct_no;

EXCEPTION
         WHEN NO_DATA_FOUND    
         THEN
            v_qeq :=1;
END;


BEGIN
         SELECT branch 
           FROM gl_saving_base_acct
          WHERE acct_no = v_input.acct_no;

EXCEPTION
         WHEN others    
         THEN
            v_qeq :=1;
END;

BEGIN
         SELECT AVG(branch)
           INTO v_branch
           FROM gl_saving_base_acct
          WHERE acct_no = v_input.acct_no;

EXCEPTION
         WHEN others    
         THEN
            v_qeq :=1;
END;

BEGIN
         SELECT AVG(branch)
           INTO v_branch
           FROM gl_saving_base_acct
          WHERE acct_no = v_input.acct_no;

EXCEPTION
         WHEN others    
         THEN
            v_qeq :=1;
END;

BEGIN
         SELECT AVG(branch)
           INTO v_branch
           FROM gl_saving_base_acct
          WHERE acct_no = v_input.acct_no;

EXCEPTION
         WHEN others    
         THEN
            v_qeq :=1;
END;

BEGIN
         SELECT MAX(branch)
           INTO v_branch
           FROM gl_saving_base_acct
          WHERE acct_no = v_input.acct_no;

EXCEPTION
         WHEN others    
         THEN
            v_qeq :=1;
END;


BEGIN
         SELECT MIN(branch)
           INTO v_branch
           FROM gl_saving_base_acct
          WHERE acct_no = v_input.acct_no;

EXCEPTION
         WHEN others    
         THEN
            v_qeq :=1;
END;


BEGIN
         SELECT STDDEV(branch)
           INTO v_branch
           FROM gl_saving_base_acct
          WHERE acct_no = v_input.acct_no;

EXCEPTION
         WHEN others    
         THEN
            v_qeq :=1;
END;

BEGIN
         SELECT SUM(branch)
           INTO v_branch
           FROM gl_saving_base_acct
          WHERE acct_no = v_input.acct_no;

EXCEPTION
         WHEN others    
         THEN
            v_qeq :=1;
END;

BEGIN
         SELECT COUNT(branch)
           INTO v_branch
           FROM gl_saving_base_acct
          WHERE acct_no = v_input.acct_no;

EXCEPTION
         WHEN others    
         THEN
            v_qeq :=1;
END;

BEGIN
         SELECT MEDIAN(branch)
           INTO v_branch
           FROM gl_saving_base_acct
          WHERE acct_no = v_input.acct_no;

EXCEPTION
         WHEN others    
         THEN
            v_qeq :=1;
END;

DECLARE
   v_acct_type   VARCHAR2 (5 CHAR);
BEGIN
   SELECT acct_type
     INTO v_acct_type
     FROM rb_acct ra,fm_client fc
    WHERE internal_key = 111;
EXCEPTION
   WHEN TOO_MANY_ROWS
   THEN
     NULL;
   WHEN NO_DATA_FOUND 
   THEN
     NULL;
END;