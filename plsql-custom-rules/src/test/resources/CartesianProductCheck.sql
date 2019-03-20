SELECT NVL (ctrl_branch, p_branch) branch, ksy.base_ccy -- Noncompliant
           FROM fm_client, fm_system ksy, rb_system rsy
          WHERE client_no = p_client_no;


SELECT *   -- Noncompliant
  FROM rb_acct a, rb_base_acct b
  WHERE internal_key = 111;

SELECT *  -- Noncompliant
   INTO v_acct_type
   FROM rb_acct ra,fm_client fc
   WHERE internal_key = 111; 
 
SELECT NVL (SUM (c.load_capacity), 0)
              INTO v_lck_sum
              FROM (SELECT id1
                      FROM gv$lock
                     WHERE TYPE = 'UL') a,
                   batch_split_process b,
                   batch_std_process c
             WHERE     b.split_key = a.id1
                   AND c.process_seq_no = b.process_seq_no
                   AND b.run_date = v_run_date; 
 SELECT acct_type -- Noncompliant
     INTO v_acct_type
     FROM rb_acct ra,fm_client fc,rb_acct_attach raa
    WHERE internal_key = 111 and ra.internal_key=raa.internal_key;
    


SELECT *   -- Noncompliant
  FROM rb_acct a, rb_base_acct b, rb_aio_acct c;
 
 SELECT *   -- Noncompliant
  FROM rb_acct a, rb_base_acct b, rb_aio_acct c
 WHERE a.base_acct_no = c.aio_acct_no;
 
 SELECT *  -- Noncompliant
  FROM rb_acct, rb_base_acct, rb_aio_acct
 WHERE rb_acct.base_acct_no = rb_aio_acct.aio_acct_no;

SELECT *
  FROM rb_acct a, rb_base_acct b, rb_aio_acct c
 WHERE a.base_acct_no = c.aio_acct_no AND a.base_acct_no = b.base_acct_no;

SELECT *
  FROM rb_acct, rb_base_acct, rb_aio_acct
 WHERE rb_acct.base_acct_no = rb_aio_acct.aio_acct_no AND rb_acct.base_acct_no = rb_base_acct.base_acct_no;
 
 