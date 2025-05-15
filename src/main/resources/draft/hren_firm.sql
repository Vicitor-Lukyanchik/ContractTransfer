integer li_kol, li_type, li_firm,li_zap,li_status
integer li_count_id,li_city_id,li_firm_id,li_bank_id,li_ora_count,li_sql
long ll_zap, ll_kol_str, k,j,ll_id,ll_all_zap,ll_firm_ind,ll_zap_bar,ll_kod_gp
decimal ldec_kol
string ls_im,ls_firm_name_all,ls_firm_adr_rp,ls_firm_adr_ep
string ls_count_name_r,ls_count_kb,ls_count_name_e,ls_city_name_r,ls_city_name_e
string ls_firm_name_r,ls_firm_name_e,ls_firm_unn_inn,ls_firm_adr_r,ls_firm_adr_e,ls_firm_okpo,ls_firm_okonh
string ls_firm_sp,ls_firm_np,ls_firm_lnp,ls_firm_kv,ls_firm_kpp
string ls_bank_rsch,ls_bank_mfo,ls_bank_name_r,ls_bank_name_e,ls_bank_adr_r,ls_bank_adr_e,ls_bank_rsch_b
string ls_bank_k_r,ls_bank_k_e,ls_bank_adrbk_r,ls_bank_adrbk_e,ls_bank_blz,ls_bank_swift


// Импорт таблицы стран из .dbf в Oracle

li_kol = 0
li_zap = 0
lds_imp = CREATE datastore
lds_imp.dataobject = "dw_country"
lds_imp.settransobject (tr_cl)
lds_imp.Retrieve ()
ll_zap_bar = 1000
ll_zap = 1
ll_all_zap = lds_imp.Rowcount ()
ll_kol_str  = ll_all_zap
w_app_frame.iuoProgressBar.maxPosition = ll_kol_str
w_app_frame.iuoProgressBar.setStep = 1
vpb_1.Position = 0
vpb_1.maxPosition = ll_kol_str
vpb_1.setStep = ll_kol_str
//ll_zap_bar = ll_kol_str
ls_im = 'set transaction use rollback segment RB5'
execute immediate :ls_im;
Yield()
If ll_all_zap > 0 then
   li_zap = 0
   w_app_frame.iuoProgressBar.Position = 0
	st_1.Text = "Страны"
//   st_2.Text = lds_imp.dataobject+" Запись .dbf № "
   st_2.Text = "Запись в   .dbf № "
   st_4.Text = "Запись в Oracle № "
	ls_count_name_e = " "
   FOR k = ll_zap to ll_all_zap
          Yield()
     	 li_count_id     = lds_imp.Object.COUNTR_KOD[k]
		 ls_count_name_r = lds_imp.Object.COUNTR_NAZ[k]
		 ls_count_kb     = lds_imp.Object.COUNTR_KB[k]
		 li_kol++
       st_3.Text = string(li_kol)
		 SELECT "COUNTRY"."COUNT_ID"
		 INTO :li_ora_count
		 FROM "COUNTRY"
		 WHERE "COUNTRY"."COUNT_ID" = :li_count_id ;
		 li_sql = SQLCA.sqlcode
	    If li_sql =0 then
//        Обнавление данных из DBF
          UPDATE "COUNTRY"
			 SET    "COUNTRY"."COUNT_NAME_R" = :ls_count_name_r,
			        "COUNTRY"."COUNT_KB"     = :ls_count_kb,
					  "COUNTRY"."COUNT_NAME_E" = :ls_count_name_e
			 WHERE  "COUNTRY"."COUNT_ID"     = :li_count_id;
   		 If SQLCA.sqlcode = -1 then
		       error.uf_sqlerror ()
             rollback;
				 w_app_frame.iuoProgressBar.Position = 0
				 Return
        	 End If
    	 ElseIf li_sql =100 then
//        Дозапись данных из DBF
            INSERT INTO "COUNTRY"
                      ( "COUNT_ID",
                        "COUNT_NAME_R",
                        "COUNT_KB",
                        "COUNT_NAME_E" )
            VALUES ( :li_count_id,
                     :ls_count_name_r,
				   	   :ls_count_kb,
      			      :ls_count_name_e) ;
			   If SQLCA.sqlcode = -1 then
		         error.uf_sqlerror ()
               rollback;
				   w_app_frame.iuoProgressBar.Position = 0
				   Return
        	   End If
		 ElseIf li_sql =-1 then
		        error.uf_sqlerror ()
              rollback;
				  w_app_frame.iuoProgressBar.Position = 0
				  Return
  	    End If
		 li_zap++
       st_5.Text = string(li_zap)
		 w_app_frame.event trigger ue_updateprogressbar ( )
		 vpb_1.StepIt()
	NEXT
End If

// Импорт таблицы городов из .dbf в Oracle

li_kol = 0
li_zap = 0
ll_zap_bar = 1000
lds_imp = CREATE datastore
lds_imp.dataobject = "dw_city"
lds_imp.settransobject (tr_cl)
lds_imp.Retrieve ()
ll_zap = 1
ll_all_zap = lds_imp.Rowcount ()
ll_kol_str  = ll_all_zap
w_app_frame.iuoProgressBar.maxPosition = ll_kol_str
w_app_frame.iuoProgressBar.setStep = 1
vpb_2.Position = 0
vpb_2.maxPosition = ll_kol_str
vpb_2.setStep = ll_zap_bar
If ll_all_zap > 0 then
   li_zap = 0
   w_app_frame.iuoProgressBar.Position = 0
	st_6.Text = "Города"
   st_2.Text = "Запись в   .dbf № "
   st_4.Text = "Запись в Oracle № "
  	ls_city_name_e = " "
   FOR k = ll_zap to ll_all_zap
		 Yield()
		 li_city_id      = lds_imp.Object.CITY_KOD[k]
 	    li_count_id     = lds_imp.Object.CITY_COUNT[k]
    	 ls_city_name_r  = lds_imp.Object.CITY_NAZ[k]
//		 OemToCharA(ls_city_name_r,ls_city_name_r)
		 li_kol++
       st_3.Text = string(li_kol)
	  	 SELECT "CITY"."CITY_ID"
       INTO   :li_ora_count
       FROM   "CITY"
       WHERE  "CITY"."CITY_ID" = :li_city_id ;
       li_sql = SQLCA.sqlcode
       If li_sql =0 then
//        Обнавление данных из DBF
          UPDATE "CITY"
	       SET    "CITY"."COUNT_ID"     = :li_count_id,
	              "CITY"."CITY_NAME_R"  = :ls_city_name_r,
			        "CITY"."CITY_NAME_E"  = :ls_city_name_e
		    WHERE  "CITY"."CITY_ID"      = :li_city_id ;
          If SQLCA.sqlcode = -1 then
	          error.uf_sqlerror ()
             rollback;
	          w_app_frame.iuoProgressBar.Position = 0
		       Return
          End If
       ElseIf li_sql =100 then
//            Дозапись данных из DBF
        	     INSERT INTO "CITY"
                        ( "CITY_ID",
			    	           "COUNT_ID",
                          "CITY_NAME_R",
                          "CITY_NAME_E" )
              VALUES ( :li_city_id,
					        :li_count_id,
                       :ls_city_name_r,
				  	        :ls_count_name_e) ;
			     If SQLCA.sqlcode = -1 then
		           error.uf_sqlerror ()
                 rollback;
				     w_app_frame.iuoProgressBar.Position = 0
				     Return
        	     End If
    	 ElseIf li_sql =-1 then
		        error.uf_sqlerror ()
              rollback;
			     w_app_frame.iuoProgressBar.Position = 0
			     Return
  	    End If
	    li_zap++
       st_5.Text = string(li_zap)
		 w_app_frame.event trigger ue_updateprogressbar ( )
  		 vpb_2.StepIt()
	NEXT
End If

// Импорт таблицы фирм из .dbf в Oracle

li_kol = 0
li_zap = 0
ll_zap_bar = 1000
lds_imp = CREATE datastore
lds_imp.dataobject = "dw_firm"
lds_imp.settransobject (tr_cl)
lds_imp.Retrieve ()
ll_zap = 1
ll_all_zap = lds_imp.Rowcount ()
ll_kol_str  = ll_all_zap
w_app_frame.iuoProgressBar.maxPosition = ll_kol_str
w_app_frame.iuoProgressBar.setStep = 1
vpb_3.Position = 0
vpb_3.maxPosition = ll_kol_str
vpb_3.setStep = ll_zap_bar
If ll_all_zap > 0 then
   li_zap = 0
   w_app_frame.iuoProgressBar.Position = 0
	st_7.Text = "Фирмы"
   st_2.Text = "Запись в   .dbf № "
   st_4.Text = "Запись в Oracle № "
     FOR k = ll_zap to ll_all_zap
            Yield()
     	 li_firm_id      = lds_imp.Object.FIRM_KOD[k]
         li_city_id      = lds_imp.Object.FIRM_CITY[k]
         ls_firm_name_r  = lds_imp.Object.FIRM_NAZ[k]
		 ls_firm_name_e  = lds_imp.Object.NFIRM_E[k]
		 ll_firm_ind     = lds_imp.Object.IND[k]
		 ls_firm_unn_inn = lds_imp.Object.UNN_INN[k]
		 ls_firm_adr_r   = lds_imp.Object.ADR_R[k]
		 ls_firm_adr_e   = lds_imp.Object.ADR_E[k]
		 ls_firm_okpo    = lds_imp.Object.OKPO[k]
		 ls_firm_okonh   = lds_imp.Object.OKONH[k]
		 ls_firm_kpp     = lds_imp.Object.KPP[k]
		 ls_firm_name_all = lds_imp.Object.FIRM_NAL[k]
		 ls_firm_adr_rp = lds_imp.Object.ADR_RP[k]
		 ls_firm_adr_ep = lds_imp.Object.ADR_EP[k]
		 li_status = lds_imp.Object.STATUS[k]
		 ll_kod_gp = lds_imp.Object.KOD_GP[k]
//		 OemToCharA(ls_firm_name_r,ls_firm_name_r)
//		 OemToCharA(ls_firm_name_e,ls_firm_name_e)
//		 OemToCharA(ls_firm_unn_inn,ls_firm_unn_inn)
//		 OemToCharA(ls_firm_adr_r,ls_firm_adr_r)
//		 OemToCharA(ls_firm_adr_e,ls_firm_adr_e)
//		 OemToCharA(ls_firm_okpo,ls_firm_okpo)
//		 OemToCharA(ls_firm_okonh,ls_firm_okonh)
//		 OemToCharA(ls_firm_kpp,ls_firm_kpp)
		 li_kol++
       st_3.Text = string(li_kol)
     	 SELECT "FIRM"."FIRM_ID"
       INTO   :li_ora_count
       FROM   "FIRM"
       WHERE  "FIRM"."FIRM_ID" = :li_firm_id ;
       li_sql = SQLCA.sqlcode
       If li_sql =0 then
//        Обнавление данных из DBF
          UPDATE "FIRM"
	       SET    "FIRM"."CITY_ID"      = :li_city_id,
	              "FIRM"."FIRM_NAME_R"  = :ls_firm_name_r,
			        "FIRM"."FIRM_NAME_E"  = :ls_firm_name_e,
					  "FIRM"."FIRM_IND"     = :ll_firm_ind,
					  "FIRM"."FIRM_UNN_INN" = :ls_firm_unn_inn,
					  "FIRM"."FIRM_ADR_R"   = :ls_firm_adr_r,
					  "FIRM"."FIRM_ADR_E"   = :ls_firm_adr_e,
					  "FIRM"."FIRM_OKPO"    = :ls_firm_okpo,
					  "FIRM"."FIRM_OKONH"   = :ls_firm_okonh,
				     "FIRM"."CA_ID"        = 1,
				     "FIRM"."FIRM_SP"      = Null,
					  "FIRM"."FIRM_NP"      = Null,
					  "FIRM"."FIRM_LNP"     = Null,
					  "FIRM"."FIRM_DVP"     = To_Date(Null,'dd.mm.yyyy'),
					  "FIRM"."FIRM_KV"      = Null,
					  "FIRM"."FIRM_KPP"    = :ls_firm_kpp,
					  "FIRM"."FIRM_NAME_ALL"  = :ls_firm_name_all,
					  "FIRM"."FIRM_ADR_RP"   = :ls_firm_adr_rp,
					  "FIRM"."FIRM_ADR_EP"   = :ls_firm_adr_ep,
					  "FIRM"."FIRM_STATUS"   = :li_status,
					  "FIRM"."FIRM_KOD_GP"   = :ll_kod_gp
		    WHERE  "FIRM"."FIRM_ID"      = :li_firm_id ;
          If SQLCA.sqlcode = -1 then
	          error.uf_sqlerror ()
             rollback;
	          w_app_frame.iuoProgressBar.Position = 0
		       Return
          End If
       ElseIf li_sql =100 then
//            Дозапись данных из DBF
				  INSERT INTO "FIRM"
                        ( "FIRM_ID",
	             		     "CITY_ID",
                          "FIRM_NAME_R",
							     "FIRM_NAME_E",
								  "FIRM_IND",
								  "FIRM_UNN_INN",
								  "FIRM_ADR_R",
								  "FIRM_ADR_E",
								  "FIRM_OKPO",
								  "FIRM_OKONH",
								  "FIRM"."CA_ID",
								  "FIRM_SP",
         					      "FIRM_NP",
								  "FIRM_LNP",
								  "FIRM_DVP",
								  "FIRM_KV",
								  "FIRM_KPP",
								  "FIRM_NAME_ALL",
								  "FIRM_ADR_RP",
								  "FIRM_ADR_EP",
								  "FIRM_STATUS",
								  "FIRM_KOD_GP")
              VALUES ( :li_firm_id,
		      	        :li_city_id,
					        :ls_firm_name_r,
							  :ls_firm_name_e,
							  :ll_firm_ind,
							  :ls_firm_unn_inn,
							  :ls_firm_adr_r,
							  :ls_firm_adr_e,
							  :ls_firm_okpo,
				           :ls_firm_okonh,
							  1,
							  Null,
							  Null,
							  Null,
							  To_Date(Null,'dd.mm.yyyy'),
							  Null,
							  :ls_firm_kpp,
							  :ls_firm_name_all,
							  :ls_firm_adr_rp,
							  :ls_firm_adr_ep,
							  :li_status,
							  :ll_kod_gp);
			     If SQLCA.sqlcode = -1 then
		           error.uf_sqlerror ()
                 rollback;
				     w_app_frame.iuoProgressBar.Position = 0
				     Return
        	     End If
    	 ElseIf li_sql =-1 then
		        error.uf_sqlerror ()
              rollback;
			     w_app_frame.iuoProgressBar.Position = 0
			     Return
  	    End If
       li_zap++
       st_5.Text = string(li_zap)
		 w_app_frame.event trigger ue_updateprogressbar ( )
		 vpb_3.StepIt()
   NEXT
End If

// Импорт таблицы фирм из .dbf в Oracle (по банкам)

li_kol = 0
li_zap = 0
ll_zap_bar = 1000
lds_imp = CREATE datastore
lds_imp.dataobject = "dw_bank"
lds_imp.settransobject (tr_cl)
lds_imp.Retrieve ()
ll_zap = 1
ll_all_zap = lds_imp.Rowcount ()
ll_kol_str  = ll_all_zap
w_app_frame.iuoProgressBar.maxPosition = ll_kol_str
w_app_frame.iuoProgressBar.setStep = 1
vpb_4.Position = 0
vpb_4.maxPosition = ll_kol_str
vpb_4.setStep = ll_zap_bar
If ll_all_zap > 0 then
   li_zap = 0
   w_app_frame.iuoProgressBar.Position = 0
	st_8.Text = "Банки"
   st_2.Text = "Запись в   .dbf № "
   st_4.Text = "Запись в Oracle № "
    FOR k = ll_zap to ll_all_zap
       li_bank_id      = lds_imp.Object.FIRM_KOD[k]
	    li_firm_id      = lds_imp.Object.FIRM_KOD[k]
 		 ls_bank_rsch    = lds_imp.Object.RSCH[k]
		 ls_bank_mfo     = lds_imp.Object.MFO[k]
       ls_bank_name_r  = lds_imp.Object.BANK_R[k]
		 ls_bank_name_e  = lds_imp.Object.BANK_E[k]
		 ls_bank_adr_r   = lds_imp.Object.ADRB_R[k]
  		 ls_bank_adr_e   = lds_imp.Object.ADRB_E[k]
       ls_bank_rsch_b  = lds_imp.Object.RSCHB[k]
       ls_bank_k_r     = lds_imp.Object.BANKK_R[k]
       ls_bank_k_e     = lds_imp.Object.BANKK_E[k]
		 ls_bank_adrbk_r = lds_imp.Object.ADRBK_R[k]
		 ls_bank_adrbk_e = lds_imp.Object.ADRBK_E[k]
	    ls_bank_blz     = lds_imp.Object.BLZ[k]
		 ls_bank_swift   = lds_imp.Object.SWIFT[k]
//		 OemToCharA(ls_bank_rsch,ls_bank_rsch)
//		 OemToCharA(ls_bank_mfo,ls_bank_mfo)
//		 OemToCharA(ls_bank_name_r,ls_bank_name_r)
//		 OemToCharA(ls_bank_name_e,ls_bank_name_e)
//		 OemToCharA(ls_bank_adr_r,ls_bank_adr_r)
//		 OemToCharA(ls_bank_adr_e,ls_bank_adr_e)
//		 OemToCharA(ls_bank_rsch_b,ls_bank_rsch_b)
//		 OemToCharA(ls_bank_k_r,ls_bank_k_r)
//		 OemToCharA(ls_bank_k_e,ls_bank_k_e)
//		 OemToCharA(ls_bank_adrbk_r,ls_bank_adrbk_r)
//		 OemToCharA(ls_bank_adrbk_e,ls_bank_adrbk_e)
//		 OemToCharA(ls_bank_blz,ls_bank_blz )
//		 OemToCharA(ls_bank_swift,ls_bank_swift)
	    li_kol++
       st_3.Text = string(li_kol)
     	 SELECT "BANK"."BANK_ID"
       INTO   :li_ora_count
       FROM   "BANK"
       WHERE  "BANK"."BANK_ID" = :li_bank_id ;
       li_sql = SQLCA.sqlcode
       If li_sql =0 then
//        Обнавление данных из DBF
          UPDATE "BANK"
	       SET    "BANK"."FIRM_ID"      = :li_firm_id,
			        "BANK"."BANK_RSCH"    = :ls_bank_rsch,
			        "BANK"."BANK_MFO"     = :ls_bank_mfo,
	              "BANK"."BANK_NAME_R"  = :ls_bank_name_r,
			        "BANK"."BANK_NAME_E"  = :ls_bank_name_e,
   			     "BANK"."BANK_ADR_R"   = :ls_bank_adr_r,
			        "BANK"."BANK_ADR_E"   = :ls_bank_adr_e,
					  "BANK"."BANK_RSCH_B"  = :ls_bank_rsch_b,
					  "BANK"."BANK_K_R"     = :ls_bank_k_r,
					  "BANK"."BANK_K_E"     = :ls_bank_k_e,
					  "BANK"."BANK_ADRBK_R" = :ls_bank_adrbk_r,
			        "BANK"."BANK_ADRBK_E" = :ls_bank_adrbk_e,
					  "BANK"."BANK_BLZ"     = :ls_bank_blz,
			        "BANK"."BANK_SWIFT"   = :ls_bank_swift,
					  "BANK"."CUR_KOD"      = 906
		    WHERE  "BANK"."BANK_ID"      = :li_bank_id ;
          If SQLCA.sqlcode = -1 then
	          error.uf_sqlerror ()
             rollback;
	          w_app_frame.iuoProgressBar.Position = 0
		       Return
          End If
       ElseIf li_sql =100 then
//            Дозапись данных из DBF
                         INSERT INTO "BANK"
                                   ( "BANK_ID",
					      		          "FIRM_ID",
												 "BANK_RSCH",
												 "BANK_MFO",
                                     "BANK_NAME_R",
		      								 "BANK_NAME_E",
												 "BANK_ADR_R",
		      								 "BANK_ADR_E",
				      						 "BANK_RSCH_B",
						       				 "BANK_K_R",
									      	 "BANK_K_E",
			      							 "BANK_ADRBK_R",
												 "BANK_ADRBK_E",
												 "BANK_BLZ",
							      			 "BANK_SWIFT",
												 "CUR_KOD")
                            VALUES ( :li_bank_id,
          									 :li_firm_id,
												 :ls_bank_rsch,
		      			                :ls_bank_mfo,
      										 :ls_bank_name_r,
		       								 :ls_bank_name_e,
					      					 :ls_bank_adr_r,
												 :ls_bank_adr_e,
												 :ls_bank_rsch_b,
												 :ls_bank_k_r,
												 :ls_bank_k_r,
					      					 :ls_bank_adrbk_r,
												 :ls_bank_adrbk_e,
												 :ls_bank_blz,
				                  	    :ls_bank_swift,
												 906) ;
			     If SQLCA.sqlcode = -1 then
		           error.uf_sqlerror ()
                 rollback;
				     w_app_frame.iuoProgressBar.Position = 0
				     Return
        	     End If
    	 ElseIf li_sql =-1 then
		        error.uf_sqlerror ()
              rollback;
			     w_app_frame.iuoProgressBar.Position = 0
			     Return
  	    End If
       li_zap++
       st_5.Text = string(li_zap)
		 w_app_frame.event trigger ue_updateprogressbar ( )
		 vpb_4.StepIt()
   NEXT
   w_app_frame.iuoProgressBar.Position = 0
End If
COMMIT ;
Return