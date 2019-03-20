
BEGIN
   Open c2;
   Fetch c2 Into c2rt;
   If c2%Found Then
      Close c2;
      v_msg.internal_key := c2rt.internal_key;
      If ft_swf.init (v_msg, v_err) Then
         If c2rt.message_type In ('MT100', 'MT103') Then
            If ft_swf.get_tag ('57', v_opt, v_rid, v_grp, v_f57.content, v_temp, v_err) Then
               If ft_swf.set_f5x (v_f57, ft_swfd.with_message, v_err) Then
                  v_bic := v_f57.bnk_id;
               End If;
            End If;
           --
            If ft_swf.get_tag ('59', v_opt, v_rid, v_grp, v_f59.content, v_temp, v_err) Then
               If ft_swf.set_f5x (v_f59, ft_swfd.with_message, v_err) Then
                  v_dcd.acct_no := substr (v_f59.acct_line.acct_no, 1, 20);
               End If;
            END IF;
           --
            Open c3_bank (v_bic);
            Fetch c3_bank Into v_dcd.bank_code;
            Close c3_bank;
         Elsif c2rt.message_type In ('MT202', 'MT203') Then
            v_dcd.acct_no := Null;
         Elsif c2rt.message_type In ('MT200', 'MT201') Then
            v_dcd.acct_no := Null;
         Else
            Null;
         End If;
      End If;
   End If;
   If c2%IsOpen Then
      Close c2;
   End If;
End;
   

BEGIN
      oltp2.v_step := 1;

      OPEN rb_oltp.c_nfn_oltp_temp (p_nfnhd_rec.tran_num, p_hdr_rec.run_date);

      FETCH rb_oltp.c_nfn_oltp_temp
       INTO v_nfnhd, v_seq_no;

      IF rb_oltp.c_nfn_oltp_temp%NOTFOUND
      THEN
         p_action_code := oltp_error.err_no_oltp_temp_rec;

         CLOSE rb_oltp.c_nfn_oltp_temp;

         RETURN;
      END IF;

      CLOSE rb_oltp.c_nfn_oltp_temp;

      -- set the data for td pretermination
      v_error := set_tdpr (v_nfnhd, v_tdpr_rec);

      IF v_error <> oltp_error.success_code
      THEN
         RAISE tdpr_error;
      END IF;

      oltp2.v_step := 2;
      rb_time_dep.td_pretermination (v_nfnhd.tran_num,
                                     p_hdr_rec.acct_no,
                                     v_tdpr_rec.cert_no,
                                     p_hdr_rec.branch,
                                     p_hdr_rec.run_date,
                                     v_tdpr_rec.penalty_rate,
                                     v_tdpr_rec.penalty_amt,
                                     v_tdpr_rec.preterm_amt,
                                     v_tdpr_rec.int_adj,
                                     v_tdpr_rec.int_rate,
                                     v_tdpr_rec.wdrawn_amt_ind,
                                     v_error
                                    );
      p_action_code := v_error;
   EXCEPTION
      WHEN tdpr_error
      THEN
         oltp2.v_proc := 'TDPR';
         p_action_code := v_error;
         oltp2.debug_oltp (v_error);
         RETURN;
   END;


BEGIN
select * from test;
      OPEN c_atm_settle;
         FETCH c_atm_settle
          INTO v_int;
 INSERT INTO MY_TEST VALUES ('TEST_FAIL', 'ABC');    
         CLOSE c_atm_settle;   
END;

create or replace procedure cursor_test is
begin
   declare
   cursor c_s1 is select a from system.test1;
   cc int;
   begin
       open c_s1; 
       while c_s1%found loop
           fetch c_s1 into cc;
            dbms_output.put_line(cc);
       end loop;
       close c_s1;  
   end;
end cursor_test;

BEGIN
OPEN c_atm_settle (select a from system.test1);
         FETCH c_atm_settle
          INTO v_count;
CLOSE c_atm_settle;       
END;

create or replace procedure cursor_test is
begin
   declare
   cursor c_s1 is select a from system.test1;
   cc int;
   begin
       open c_s1; -- Noncompliant {{The cursor must be closed.}}
       while c_s1%found loop
           fetch c_s1 into cc;
            dbms_output.put_line(cc);
       end loop;
       close c_s2;  
   end;
end cursor_test;


BEGIN
      OPEN c_atm_settle;  -- Noncompliant {{The cursor must be closed.}}
         FETCH c_atm_settle
          INTO v_int;        
END;


create or replace procedure cursor_test is
begin
   declare
   cursor c_s1 is select a from system.test1;
   cc int;
   begin
       open c_s1; -- Noncompliant {{The cursor must be closed.}}
       while c_s1%found loop
           fetch c_s1 into cc;
            dbms_output.put_line(cc);
       end loop;
   end;
end cursor_test;


BEGIN
select * from test;
OPEN c_atm_settle (select a from system.test1); -- Noncompliant {{The cursor must be closed.}}
         FETCH c_atm_settle
          INTO v_count;
 INSERT INTO MY_TEST VALUES ('TEST_FAIL', 'ABC');    
END;