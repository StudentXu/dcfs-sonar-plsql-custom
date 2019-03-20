BEGIN
   FOR i IN c_sub_acct (v_internal_key) 
   LOOP -- Noncompliant
      --循环计算单个子账户的v_actual_int,统计单账户利息，未初始化变量导致后续账户利息统计错误    
      v_return :=
         fdstor.get_int_rate (p_int_type          => v_cr_int_type,
                              p_ccy               => v_ccy,
                              p_effect_date       => v_fr,
                              p_balance           => v_bal,
                              p_int_rate          => v_actual_rate);

      IF v_cr_acct_level_int_rate IS NOT NULL
      THEN
         v_actual_rate := v_cr_acct_level_int_rate;
      END IF;

      v_diff := (v_to - v_fr);
      v_actual_int := (v_bal * v_actual_rate * v_diff) / v_day_basis / 100;
   END LOOP;
END;

BEGIN 
   LOOP  -- Noncompliant
      v_return :=
         fdstor.get_int_rate (p_int_type          => v_cr_int_type,
                              p_ccy               => v_ccy,
                              p_effect_date       => v_fr,
                              p_balance           => v_bal,
                              p_int_rate          => v_actual_rate);

      IF v_cr_acct_level_int_rate IS NOT NULL
      THEN
         v_actual_rate := v_cr_acct_level_int_rate;
      END IF;

      v_diff := (v_to - v_fr);
      v_actual_int := (v_bal * v_actual_rate * v_diff) / v_day_basis / 100;
   END LOOP;
END;

begin
    while pnum<=10 loop -- Noncompliant
          dbms_output.put_line(pnum);
          pnum := pnum+1;
          v_actual_int := pnum / 100; 
      end loop;
end;


-- compliant
BEGIN
   FOR i IN c_sub_acct (v_internal_key)
   LOOP
      v_actual_int := 0;    --每次初始化，循环计算单个子账户的利息      
      v_return :=
         fdstor.get_int_rate (p_int_type          => v_cr_int_type,
                              p_ccy               => v_ccy,
                              p_effect_date       => v_fr,
                              p_balance           => v_bal,
                              p_int_rate          => v_actual_rate);

      IF v_cr_acct_level_int_rate IS NOT NULL
      THEN
         v_actual_rate := v_cr_acct_level_int_rate;
      END IF;

      v_diff := (v_to - v_fr);
      v_actual_int := (v_bal * v_actual_rate * v_diff) / v_day_basis / 100;
   END LOOP;
END;


BEGIN
   LOOP
      v_actual_int := 0;    --每次初始化，循环计算单个子账户的利息      
      v_return :=
         fdstor.get_int_rate (p_int_type          => v_cr_int_type,
                              p_ccy               => v_ccy,
                              p_effect_date       => v_fr,
                              p_balance           => v_bal,
                              p_int_rate          => v_actual_rate);

      IF v_cr_acct_level_int_rate IS NOT NULL
      THEN
         v_actual_rate := v_cr_acct_level_int_rate;
      END IF;

      v_diff := (v_to - v_fr);
      v_actual_int := (v_bal * v_actual_rate * v_diff) / v_day_basis / 100;
   END LOOP;
END;


begin
    while pnum<=10 loop
    	  v_actual_int := 0; 
          dbms_output.put_line(pnum);
          pnum := pnum+1;
          v_actual_int := pnum / 100; 
      end loop;
end;