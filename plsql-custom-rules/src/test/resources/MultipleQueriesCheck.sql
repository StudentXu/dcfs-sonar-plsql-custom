
BEGIN
   SELECT acct_Type
     INTO v_acct_Type
     FROM rb_acct;
    
   SELECT ccy -- Noncompliant
     INTO v_ccy
     FROM rb_acct;
    
END;

BEGIN
   SELECT acct_Type
     INTO v_acct_Type
     FROM rb_acct
    WHERE internal_key = :p_internal_key;
    
   SELECT ccy   -- Noncompliant
     INTO v_ccy
     FROM rb_acct
    WHERE internal_key = :p_internal_key;
   
    SELECT ccya   -- Noncompliant
     INTO v_ccya
     FROM rb_acct
    WHERE internal_key = :p_internal_key;

END;



-- compliant
BEGIN
   SELECT acct_Type
     INTO v_acct_Type
     FROM dual;
    
   SELECT ccy 
     INTO v_ccy
     FROM dual;
    
END;
BEGIN
   SELECT acct_Type,ccy
     INTO v_acct_Type,v_ccy
     FROM rb_acct
    WHERE internal_key = :p_internal_key;
END;

BEGIN
   SELECT acct_Type
     INTO v_acct_Type
     FROM rb_acct
    WHERE internal_key = :p_internal_key;
    
   SELECT ccy   
     INTO v_ccy
     FROM rb_acct
    WHERE internal_key = :lp_internal_key;
    
END;


BEGIN
	SELECT acct_Type,ccy
     INTO v_acct_Type,v_ccy
     FROM rb_acct
    WHERE internal_key = :p_internal_key;
    
   SELECT a.acct_Type,a.ccy
     INTO v_acct_Type,v_ccy
     FROM rb_acct a,rb_info b
    WHERE internal_key = :p_internal_key and a.id=b.id;
END;