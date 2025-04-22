integer li_kol, li_type, li_firm,li_zap,li_status,li_sql,li_oper,li_tpack_id,Net,li_exp,li_imp
integer li_rasch_id,li_vdoc_id,li_cur_id,li_isp_id,li_sogl,li_specif,li_pri,li_co_day,li_pr_adv,li_dep,li_polz_exit,li_polz_exp,li_polz_urb
long ll_zap, ll_kol_str, k,j,ll_id,ll_all_zap,ll_firm_ind,ll_zap_bar,ll_ins,ll_upd
long li_dog_id,ll_firm_id,ll_spec_id,ll_sogl_id,ll_opl_id,ll_temp,ll_naim_id,ll_tpack_id,li_ora_count
decimal ldec_kol,ldec_sumd,ldec_shtr,ldec_nds,ldec_curs,ldec_ves
string ls_im,ls_source,ls_target,ls_temp,ls_naim
string ls_them,ls_otv,ls_day,ls_nd,ls_sluzba,ls_isp
date ld_dath,ld_dbeg,ld_dend,ld_dopl,ld_dotgr,ld_dreg,ld_ded,ld_temp,ld_prod
datetime ldt_temp
if gi_user_dost = 2  then
	MessageBox("Импорт данных","Нет прав!  -  Доступ по чтению!")
else
// проверить последний импорт
  SELECT  max("IMP_DBF"."IMP_ID")
    INTO :ll_id
    FROM "IMP_DBF"
      ;
   SELECT "IMP_DBF"."IMP_DNEW",
         "IMP_DBF"."IMP_POLZ"
    INTO :ldt_temp,  :ls_otv
    FROM "IMP_DBF"
   WHERE "IMP_DBF"."IMP_ID" = :ll_id   ;

ls_temp = "Последний импорт был сделан:  " + string(date(ldt_temp))+" в "+string(time(ldt_temp))+" ~r~n  Пользователем "+ls_otv+"! "+"~r~n        Хотите повторить импорт ?"

Net = MessageBox("Импорт данных", ls_temp, &
    Exclamation!, OKCancel!, 2)

IF Net = 1 THEN
datastore lds_imp
transaction tr_cl
pointer oldpointer // Declares a pointer-указатель. для мыши
oldpointer = SetPointer(HourGlass!)     // будут песоч.часы на экране
cb_2.enabled = false

tr_cl = create transaction
tr_cl.DBMS			= ProfileString("02015.ini", "Database2", "DBMS"," ")
tr_cl.autocommit	= true
tr_cl.DBParm		= ProfileString("02015.ini", "Database2", "DbParm",	" ")
ls_source = ProfileString("02015.ini", "Database2", "source",	" ")
ls_target = ProfileString("02015.ini", "Database2", "target",	" ")
connect using tr_cl;
IF tr_cl.SqlCode <> 0 THEN
	error.uf_sqlerror()
	destroy tr_cl
	 SetPointer(oldpointer)
	 MessageBox("Импорт данных"," Нет связи с *.dbf!")
	return 0
END IF
// Импорт таблицы договоров из .dbf в Oracle
ld_temp = today()
li_kol = 0
li_zap = 0
lds_imp = CREATE datastore
lds_imp.dataobject = "dw_dogov"
lds_imp.settransobject (tr_cl)
lds_imp.Retrieve ()
ll_zap_bar = 100
ll_zap = 1
li_oper = 1
ll_ins = 0
ll_upd = 0
ll_all_zap = lds_imp.Rowcount ()
ll_kol_str  = ll_all_zap
if gi_user_dost = 1 then
  w_app_frame.iuoProgressBar.maxPosition = ll_kol_str
  w_app_frame.iuoProgressBar.setStep = 1
 elseif gi_user_dost = 3 then
   w_app_frm_isp.iuoProgressBar.maxPosition = ll_kol_str
   w_app_frm_isp.iuoProgressBar.setStep = 1
 end if
//vpb_1.Position = 0
//vpb_1.maxPosition = ll_kol_str
//vpb_1.setStep = 1
ll_zap_bar = ll_kol_str


// Присвоить всем договорам оперативность 0
  UPDATE "DOGOVOR"
     SET "DOG_OPERABLE" = 0  ;
If ll_all_zap > 0 then
   li_zap = 0
	if gi_user_dost = 1 then
		   w_app_frame.iuoProgressBar.Position = 0
	elseif gi_user_dost = 3 then
		   w_app_frm_isp.iuoProgressBar.Position = 0
	end if
	st_1.Text = "Договора"
  // st_2.Text = lds_imp.dataobject+" Запись .dbf ¦ "
   st_2.Text = "Запись в   .dbf ¦ "
   st_4.Text = "Запись в Oracle ¦ "
//	ls_count_name_e = " "
   FOR k = ll_zap to ll_all_zap
		yield() //  для других процессов во время импорт
     	 li_dog_id     = lds_imp.Object.D_KD[k]
		 ls_them = lds_imp.Object.D_KPO[k]
		 ls_otv  = lds_imp.Object.D_OTV[k]
		 li_rasch_id = lds_imp.Object.D_OR_AC[k]
		 li_vdoc_id = lds_imp.Object.OT_HR[k]
		 li_cur_id = lds_imp.Object.D_CUR[k]
		 ls_isp = lds_imp.Object.D_ISP[k]
		 li_isp_id = integer(ls_isp)
		 li_sogl = lds_imp.Object.D_SOGL[k]
		 li_specif = lds_imp.Object.D_SPECIF[k]
		 li_pri = lds_imp.Object.D_PR[k]
		 li_co_day = lds_imp.Object.D_CO_DAY[k]
		 li_pr_adv = lds_imp.Object.D_PR_ADV[k]
		 ldec_sumd = lds_imp.Object.D_SUMD[k]
		 ldec_shtr = lds_imp.Object.D_STR[k]
		 ls_day = lds_imp.Object.D_DAY[k]
		 ls_nd = lds_imp.Object.D_ND[k]
		 ls_sluzba = lds_imp.Object.D_OTD[k]
		 //li_dep = S11000.GetDepId(:ls_sluzba)
  		 li_dep = get_dep_id(ls_sluzba)
		 ld_dath = lds_imp.Object.D_DATH[k]
		 ld_dbeg = lds_imp.Object.D_DATHO[k]
		 ld_dend = lds_imp.Object.D_DATE[k]
		 ld_dopl = lds_imp.Object.D_DAT_OPL[k]
		 ld_dotgr = lds_imp.Object.D_OTGR[k]
		 ld_dreg = lds_imp.Object.D_REG[k]
		 ld_ded = lds_imp.Object.D_DAT_ED[k]
		 ll_firm_id = lds_imp.Object.D_KFIRM[k]
		 ll_tpack_id = lds_imp.Object.D_PACK[k]
		 ld_prod = lds_imp.Object.PORD_SROK[k]
		 li_kol++
       st_3.Text = string(li_kol)
	if not isnull(li_dog_id)	 then
		 SELECT "DOGOVOR"."DOG_ID"
		 INTO :li_ora_count
		 FROM "DOGOVOR"
		 WHERE "DOGOVOR"."DOG_ID" = :li_dog_id ;
		 li_sql = SQLCA.sqlcode
	    If li_sql =0 then
//        Обнoвление данных из DBF
          UPDATE "DOGOVOR"
           SET "VDOC_ID" = :li_vdoc_id,
               "DOG_PRI" = :li_pri,
               "DOG_ND" = :ls_nd,
               "DOG_DATH" = :ld_dath,
               "DOG_REG" = :ld_dreg,
               "DOG_DBEG" = :ld_dbeg,
               "DOG_DEND" = :ld_dend,
               "FIRM_ID" = :ll_firm_id,
               "CUR_ID" = :li_cur_id,
               "DOG_SUMD" = :ldec_sumd,
               "DOG_THEM" = :ls_them,
               "DOG_SLUZBA" = :ls_sluzba,
               "DOG_OTV" = :ls_otv,
               "DOG_SOGL" = :li_sogl,
               "DOG_SPECIF" = :li_specif,
               "RASCH_ID" = :li_rasch_id,
               "DOG_CO_DAY" = :li_co_day,
               "DOG_DAY" = :ls_day,
               "DOG_PR_ADV" = :li_pr_adv,
               "DOG_SHTR" = :ldec_shtr,
               "DOG_DOTGR" = :ld_dotgr,
               "DOG_DOPL" = :ld_dopl,
               "DOG_ISP_ID" = :li_isp_id,
               "DOG_DAT_ED" = :ld_ded,
               "DOG_OPERABLE" = :li_oper,
			  "TPACK_ID" = :ll_tpack_id,
					"DEP_ID" = :li_dep,
					"PROD_SPOK" = : ld_prod
         WHERE "DOGOVOR"."DOG_ID" = :li_dog_id ;
  			If SQLCA.sqlcode = -1 then
		       error.uf_sqlerror ()
             rollback;
				 ll_upd = 0
					if gi_user_dost = 1 then
					   w_app_frame.iuoProgressBar.Position = 0
					elseif gi_user_dost = 3 then
					   w_app_frm_isp.iuoProgressBar.Position = 0
					end if
        	End If
			ll_upd++
    	 ElseIf li_sql =100 then
//        Дозапись данных из DBF
  			  INSERT INTO "DOGOVOR"
	         ( "DOG_ID",
   	        "VDOC_ID",
      	     "DOG_PRI",
	           "DOG_ND",
	           "DOG_DATH",
   	        "DOG_REG",
      	     "DOG_DBEG",
         	  "DOG_DEND",
	           "FIRM_ID",
   	        "CUR_ID",
      	     "DOG_SUMD",
	        	  "DOG_THEM",
   	        "DOG_SLUZBA",
	           "DOG_OTV",
   	        "DOG_SOGL",
      	     "DOG_SPECIF",
         	  "RASCH_ID",
	           "DOG_CO_DAY",
   	        "DOG_DAY",
      	     "DOG_PR_ADV",
	           "DOG_SHTR",
   	        "DOG_DOTGR",
      	     "DOG_DOPL",
         	  "DOG_ISP_ID",
	           "DOG_DAT_ED",
				  "TPACK_ID" ,
	           "DOG_OPERABLE",
				  "PROD_SPOK")
	  		VALUES (:li_dog_id,
					  :li_vdoc_id,
		           :li_pri,
		           :ls_nd,
		           :ld_dath,
		           :ld_dreg,
      		     :ld_dbeg,
		           :ld_dend,
      		     :ll_firm_id,
		           :li_cur_id,
      		     :ldec_sumd,
		           :ls_them,
      		     :ls_sluzba,
		           :ls_otv,
      		     :li_sogl,
		           :li_specif,
		           :li_rasch_id,
      		     :li_co_day,
		           :ls_day,
      		     :li_pr_adv,
		           :ldec_shtr,
      		     :ld_dotgr,
		           :ld_dopl,
		           :li_isp_id,
      		     :ld_ded,
				  :ll_tpack_id,
		           :li_oper,
				: ld_prod )  ;
			   If SQLCA.sqlcode = -1 then
		         error.uf_sqlerror ()
               rollback;
					ll_ins = 0
					if gi_user_dost = 1 then
					   w_app_frame.iuoProgressBar.Position = 0
					elseif gi_user_dost = 3 then
					   w_app_frm_isp.iuoProgressBar.Position = 0
					end if
        	   End If
				ll_ins++
		 ElseIf li_sql =-1 then
		        error.uf_sqlerror ()
              rollback;
					if gi_user_dost = 1 then
					   w_app_frame.iuoProgressBar.Position = 0
					elseif gi_user_dost = 3 then
					   w_app_frm_isp.iuoProgressBar.Position = 0
					end if
  	    End If
		 li_zap++
       st_5.Text = string(li_zap)
	   if gi_user_dost = 1 then
		 w_app_frame.event trigger ue_updateprogressbar ( )
       elseif gi_user_dost = 3 then
		  w_app_frm_isp.event trigger ue_updateprogressbar ( )
       end if
	end if // конец пров на null
//		 vpb_1.StepIt()
	NEXT
	//добавить в imp_dbf
	li_pri = 1
	SELECT seq_s02015.nextval INTO :ll_id FROM dual;
	  INSERT INTO "IMP_DBF"
		         ( "IMP_ID",
      		     "SPIS_ID",
		           "IMP_DNEW",
		           "IMP_UPD_REC",
      		     "IMP_INS_REC",
		           "IMP_POLZ" )
		  VALUES ( :ll_id,
		           :li_pri,
      		     sysdate,
		           :ll_upd,
      		     :ll_ins,
		           :gs_user_name )  ;
           If SQLCA.sqlcode = -1 then
		         error.uf_sqlerror ()
               rollback;
					ll_ins = 0
					if gi_user_dost = 1 then
					   w_app_frame.iuoProgressBar.Position = 0
					elseif gi_user_dost = 3 then
					   w_app_frm_isp.iuoProgressBar.Position = 0
					end if
        	   End If
		commit;
		dw_1.retrieve()
End If
// Импорт таблицы доп.соглашений из .dbf в Oracle
li_kol = 0
li_zap = 0
lds_imp = CREATE datastore
lds_imp.dataobject = "dw_sogl"
lds_imp.settransobject (tr_cl)
lds_imp.Retrieve ()
ll_zap_bar = 100
ll_ins = 0
ll_upd = 0
ll_zap = 1
li_oper = 1
ll_all_zap = lds_imp.Rowcount ()
ll_kol_str  = ll_all_zap
if gi_user_dost = 1 then
  w_app_frame.iuoProgressBar.maxPosition = ll_kol_str
  w_app_frame.iuoProgressBar.setStep = 1
 elseif gi_user_dost = 3 then
   w_app_frm_isp.iuoProgressBar.maxPosition = ll_kol_str
   w_app_frm_isp.iuoProgressBar.setStep = 1
 end if
//vpb_1.Position = 0
//vpb_1.maxPosition = ll_kol_str
//vpb_1.setStep = 1
ll_zap_bar = ll_kol_str
ls_im = 'set transaction use rollback segment RB5'
execute immediate :ls_im;
If ll_all_zap > 0 then
   li_zap = 0
	if gi_user_dost = 1 then
		   w_app_frame.iuoProgressBar.Position = 0
	elseif gi_user_dost = 3 then
		   w_app_frm_isp.iuoProgressBar.Position = 0
	end if
   st_1.Text = "Доп.соглашения"
   //st_2.Text = lds_imp.dataobject+" Запись .dbf ¦ "
   st_2.Text = "Запись в   .dbf ¦ "
   st_4.Text = "Запись в Oracle ¦ "
//	ls_count_name_e = " "
   FOR k = ll_zap to ll_all_zap
		yield()
     	 li_dog_id     = lds_imp.Object.D_KD[k]
		 ll_sogl_id     = lds_imp.Object.D_KSO[k]
		 //ls_them = lds_imp.Object.D_KPO[k]
		 ls_otv  = lds_imp.Object.D_OTV[k]
		 li_rasch_id = lds_imp.Object.D_OR_AC[k]
		 li_cur_id = lds_imp.Object.D_CUR[k]
		 ls_isp = lds_imp.Object.D_ISP[k]
		 li_isp_id = integer(ls_isp)
		 li_co_day = lds_imp.Object.D_CO_DAY[k]
		 li_pr_adv = lds_imp.Object.D_PR_ADV[k]
		 ldec_sumd = lds_imp.Object.D_SUMD[k]
		 ldec_shtr = lds_imp.Object.D_STR[k]
		 ls_day = lds_imp.Object.D_DAY[k]
		 ls_nd = lds_imp.Object.D_ND[k]
		 ls_sluzba = lds_imp.Object.D_OTD[k]
		 ld_dath = lds_imp.Object.D_DATH[k]
		 ld_dbeg = lds_imp.Object.D_DATHO[k]
		 ld_dend = lds_imp.Object.D_DATE[k]
		 ld_dopl = lds_imp.Object.D_DAT_OPL[k]
		 ld_dotgr = lds_imp.Object.D_OTGR[k]
		 ld_dreg = lds_imp.Object.D_REG[k]
		 ld_ded = lds_imp.Object.D_DAT_ED[k]
		 ll_firm_id = lds_imp.Object.D_KFIRM[k]
		 li_kol++
       st_3.Text = string(li_kol)
		 SELECT "DOGO_SOGL"."SOGL_ID"
		 INTO :li_ora_count
		 FROM "DOGO_SOGL"
		 WHERE "DOGO_SOGL"."SOGL_ID" = :ll_sogl_id ;
		 li_sql = SQLCA.sqlcode
	    If li_sql =0 then
//        Обнoвление данных из DBF
		  UPDATE "DOGO_SOGL"
		     SET "DOG_ID" = :li_dog_id,
		         "SOGL_ND" = :ls_nd,
      		   "SOGL_DATH" = :ld_dath,
		         "SOGL_REG" = :ld_dreg,
      		   "SOGL_DBEG" = :ld_dbeg,
		         "SOGL_DEND" = :ld_dend,
      		   "FIRM_ID" = :ll_firm_id,
		         "CUR_ID" = :li_cur_id,
      		   "SOGL_SUMD" = :ldec_sumd,
		         "SOGL_SLUZBA" = :ls_sluzba,
      		   "SOGL_OTV" = :ls_otv,
		         "RASCH_ID" = :li_rasch_id,
      		   "SOGL_CO_DAY" = :li_co_day,
		         "SOGL_DAY" = :ls_day,
      		   "SOGL_PR_ADV" = :li_pr_adv,
		         "SOGL_SHTR" = :ldec_shtr,
      		   "SOGL_DOTGR" = :ld_dotgr,
		         "SOGL_DOPL" = :ld_dopl,
      		   "SOGL_ISP_ID" = :li_isp_id,
		         "SOGL_DAT_ED" = :ld_ded
		   WHERE "DOGO_SOGL"."SOGL_ID" = :ll_sogl_id;
  			If SQLCA.sqlcode = -1 then
		       error.uf_sqlerror ()
             rollback;
				 ll_upd = 0
					if gi_user_dost = 1 then
					   w_app_frame.iuoProgressBar.Position = 0
					elseif gi_user_dost = 3 then
					   w_app_frm_isp.iuoProgressBar.Position = 0
					end if
        	End If
			ll_upd++
    	 ElseIf li_sql =100 then
//        Дозапись данных из DBF
		  INSERT INTO "DOGO_SOGL"
      			   ( "DOG_ID",
			           "SOGL_ID",
         			  "SOGL_ND",
			           "SOGL_DATH",
         			  "SOGL_REG",
			           "SOGL_DBEG",
         			  "SOGL_DEND",
			           "FIRM_ID",
         			  "CUR_ID",
			           "SOGL_SUMD",
			           "SOGL_SLUZBA",
         			  "SOGL_OTV",
			           "RASCH_ID",
         			  "SOGL_CO_DAY",
			           "SOGL_DAY",
         			  "SOGL_PR_ADV",
			           "SOGL_SHTR",
         			  "SOGL_DOTGR",
			           "SOGL_DOPL",
         			  "SOGL_ISP_ID",
			           "SOGL_DAT_ED" )
			  		VALUES (:li_dog_id,
							  :ll_sogl_id,
				           :ls_nd,
				           :ld_dath,
				           :ld_dreg,
		      		     :ld_dbeg,
				           :ld_dend,
		      		     :ll_firm_id,
				           :li_cur_id,
		      		     :ldec_sumd,
				           :ls_sluzba,
				           :ls_otv,
				           :li_rasch_id,
		      		     :li_co_day,
				           :ls_day,
		      		     :li_pr_adv,
				           :ldec_shtr,
		      		     :ld_dotgr,
				           :ld_dopl,
				           :li_isp_id,
		      		     :ld_ded );
			   If SQLCA.sqlcode = -1 then
		         error.uf_sqlerror ()
               rollback;
					ll_ins = 0
					if gi_user_dost = 1 then
					   w_app_frame.iuoProgressBar.Position = 0
					elseif gi_user_dost = 3 then
					   w_app_frm_isp.iuoProgressBar.Position = 0
					end if
        	   End If
			   ll_ins++
		 ElseIf li_sql =-1 then
		        error.uf_sqlerror ()
              rollback;
					if gi_user_dost = 1 then
					   w_app_frame.iuoProgressBar.Position = 0
					elseif gi_user_dost = 3 then
					   w_app_frm_isp.iuoProgressBar.Position = 0
					end if
  	    End If
		 li_zap++
       st_5.Text = string(li_zap)
	   if gi_user_dost = 1 then
		 w_app_frame.event trigger ue_updateprogressbar ( )
       elseif gi_user_dost = 3 then
		  w_app_frm_isp.event trigger ue_updateprogressbar ( )
       end if
//		 vpb_1.StepIt()
	NEXT
	//добавить в imp_dbf!!!
	li_pri = 2
	SELECT seq_s02015.nextval INTO :ll_id FROM dual;
	  INSERT INTO "IMP_DBF"
		         ( "IMP_ID",
      		     "SPIS_ID",
		           "IMP_DNEW",
		           "IMP_UPD_REC",
      		     "IMP_INS_REC",
		           "IMP_POLZ" )
		  VALUES ( :ll_id,
		           :li_pri,
      		     sysdate,
		           :ll_upd,
      		     :ll_ins,
		           :gs_user_name )  ;
           If SQLCA.sqlcode = -1 then
		         error.uf_sqlerror ()
               rollback;
					ll_ins = 0
					if gi_user_dost = 1 then
					   w_app_frame.iuoProgressBar.Position = 0
					elseif gi_user_dost = 3 then
					   w_app_frm_isp.iuoProgressBar.Position = 0
					end if
        	   End If
		//dw_1.update()
		dw_1.retrieve()
End If
// Импорт таблицы Спецификаций из .dbf в Oracle
li_kol = 0
li_zap = 0
ll_zap_bar = 100
lds_imp = CREATE datastore
lds_imp.dataobject = "dw_specif"
lds_imp.settransobject (tr_cl)
lds_imp.Retrieve ()
ll_zap = 1
ll_ins = 0
ll_upd = 0
li_oper = 1
ll_all_zap = lds_imp.Rowcount ()
ll_kol_str  = ll_all_zap
if gi_user_dost = 1 then
  w_app_frame.iuoProgressBar.maxPosition = ll_kol_str
  w_app_frame.iuoProgressBar.setStep = 1
 elseif gi_user_dost = 3 then
   w_app_frm_isp.iuoProgressBar.maxPosition = ll_kol_str
   w_app_frm_isp.iuoProgressBar.setStep = 1
 end if
If ll_all_zap > 0 then
   li_zap = 0
// Присвоить всем спецификациям оперативность 0
  UPDATE "DOGO_SPECIF"
     SET "SPECIF_OPERABLE" = 0  ;
    if gi_user_dost = 1 then
	   w_app_frame.iuoProgressBar.Position = 0
	elseif gi_user_dost = 3 then
	   w_app_frm_isp.iuoProgressBar.Position = 0
	end if
   st_1.Text = "Спецификации"
   st_2.Text = "Запись в   .dbf ¦ "
   st_4.Text = "Запись в Oracle ¦ "
//  	ls_city_name_e = " "
   FOR k = ll_zap to ll_all_zap
		yield()
     	 ll_spec_id     = lds_imp.Object.S_ID[k]
		 li_dog_id     = lds_imp.Object.S_KD[k]
		 li_rasch_id = lds_imp.Object.S_OR_AC[k]
		 li_cur_id = lds_imp.Object.S_CUR[k]
		 ls_isp = lds_imp.Object.S_ISP[k]
		 li_isp_id = integer(ls_isp)
		 li_co_day = lds_imp.Object.S_CO_DAY[k]
		 li_pr_adv = lds_imp.Object.S_PR_ADV[k]
		 ldec_sumd = lds_imp.Object.S_SUMS[k]
		 ldec_shtr = lds_imp.Object.S_STR[k]
		 ls_day = lds_imp.Object.S_DAY[k]
		 ls_nd = lds_imp.Object.S_NS[k]
		 ldec_nds = lds_imp.Object.S_NDS[k]
		 ldec_curs = lds_imp.Object.S_KURS[k]
		 ld_dath = lds_imp.Object.S_DATH[k]
		 ld_dopl = lds_imp.Object.S_DAT_OPL[k]
		 ld_dotgr = lds_imp.Object.S_DAT_OTGR[k]
		 ld_dreg = lds_imp.Object.S_REG[k]
		 ld_ded = lds_imp.Object.S_DAT_ED[k]
		 ll_tpack_id = lds_imp.Object.S_PACK[k]
		 li_kol++
       st_3.Text = string(li_kol)
	  	 SELECT "DOGO_SPECIF"."SPECIF_ID"
       INTO   :li_ora_count
       FROM   "DOGO_SPECIF"
       WHERE  "DOGO_SPECIF"."SPECIF_ID" = :ll_spec_id ;
       li_sql = SQLCA.sqlcode
       If li_sql =0 then
//        Обнoвление данных из DBF
			  UPDATE "DOGO_SPECIF"
			     SET "DOG_ID" = :li_dog_id,
			         "SPECIF_ID" = :ll_spec_id,
         			"SPECIF_NS" = :ls_nd,
			         "SPECIF_DATH" = :ld_dath,
         			"SPECIF_REG" = :ld_dreg,
			         "CUR_ID" = :li_cur_id,
         			"SPECIF_SUMS" = :ldec_sumd,
			         "SPECIF_NDS" = :ldec_nds,
         			"SPECIF_KURS" = :ldec_curs,
			         "RASCH_ID" = :li_rasch_id,
         			"SPECIF_CO_DAY" = :li_co_day,
			         "SPECIF_PR_ADV" = :li_pr_adv,
         			"SPECIF_DAY" = :ls_day,
			         "SPECIF_SHTR" = :ldec_shtr,
         			"SPECIF_DOPL" = :ld_dopl,
			         "SPECIF_DOTGR" = :ld_dotgr,
         			"SPECIF_ISP" = :li_isp_id,
			         "SPECIF_DAT_ED" = :ld_ded,
					"TPACK_ID"	= :ll_tpack_id,
					"SPECIF_OPERABLE" = :li_oper
			   WHERE "DOGO_SPECIF"."SPECIF_ID" = :ll_spec_id;
          If SQLCA.sqlcode = -1 then
	          error.uf_sqlerror ()
             rollback;
				 ll_upd = 0
					if gi_user_dost = 1 then
					   w_app_frame.iuoProgressBar.Position = 0
					elseif gi_user_dost = 3 then
					   w_app_frm_isp.iuoProgressBar.Position = 0
					end if
		       Return
          End If
			 ll_upd++
       ElseIf li_sql =100 then
//            Дозапись данных из DBF
			  INSERT INTO "DOGO_SPECIF"
							( "DOG_ID",
							  "SPECIF_ID",
							  "SPECIF_NS",
							  "SPECIF_DATH",
							  "SPECIF_REG",
							  "CUR_ID",
							  "SPECIF_SUMS",
							  "SPECIF_NDS",
							  "SPECIF_KURS",
							  "RASCH_ID",
							  "SPECIF_CO_DAY",
							  "SPECIF_PR_ADV",
							  "SPECIF_DAY",
							  "SPECIF_SHTR",
							  "SPECIF_DOPL",
							  "SPECIF_DOTGR",
							  "SPECIF_ISP",
							  "SPECIF_DAT_ED",
							  "TPACK_ID",
							  "SPECIF_OPERABLE")
				  VALUES (  :li_dog_id,
								:ll_spec_id,
								:ls_nd,
								:ld_dath,
								:ld_dreg,
								:li_cur_id,
								:ldec_sumd,
								:ldec_nds,
								:ldec_curs,
								:li_rasch_id,
								:li_co_day,
								:li_pr_adv,
								:ls_day,
								:ldec_shtr,
								:ld_dopl,
								:ld_dotgr,
								:li_isp_id,
								:ld_ded,
								:ll_tpack_id,
								:li_oper	)  ;
			     If SQLCA.sqlcode = -1 then
		           error.uf_sqlerror ()
                 rollback;
					  ll_ins = 0
					if gi_user_dost = 1 then
					   w_app_frame.iuoProgressBar.Position = 0
					elseif gi_user_dost = 3 then
					   w_app_frm_isp.iuoProgressBar.Position = 0
					end if
        	     End If
				ll_ins++
    	 ElseIf li_sql =-1 then
		        error.uf_sqlerror ()
              rollback;
					if gi_user_dost = 1 then
					   w_app_frame.iuoProgressBar.Position = 0
					elseif gi_user_dost = 3 then
					   w_app_frm_isp.iuoProgressBar.Position = 0
					end if
  	    End If
	    li_zap++
       st_5.Text = string(li_zap)
	   if gi_user_dost = 1 then
		 w_app_frame.event trigger ue_updateprogressbar ( )
       elseif gi_user_dost = 3 then
		  w_app_frm_isp.event trigger ue_updateprogressbar ( )
       end if
//  		 vpb_2.StepIt()
	NEXT
	li_pri = 3 //specif.dbf
	SELECT seq_s02015.nextval INTO :ll_id FROM dual;
	  INSERT INTO "IMP_DBF"
		         ( "IMP_ID",
      		     "SPIS_ID",
		           "IMP_DNEW",
		           "IMP_UPD_REC",
      		     "IMP_INS_REC",
		           "IMP_POLZ" )
		  VALUES ( :ll_id,
		           :li_pri,
      		     sysdate,
		           :ll_upd,
      		     :ll_ins,
		           :gs_user_name )  ;
           If SQLCA.sqlcode = -1 then
		         error.uf_sqlerror ()
               rollback;
					ll_ins = 0
					if gi_user_dost = 1 then
					   w_app_frame.iuoProgressBar.Position = 0
					elseif gi_user_dost = 3 then
					   w_app_frm_isp.iuoProgressBar.Position = 0
					end if
        	   End If
		//dw_1.update()
		dw_1.retrieve()

End If
// Импорт таблицы альтернатив расчетов из .dbf в Oracle
li_kol = 0
li_zap = 0
lds_imp = CREATE datastore
lds_imp.dataobject = "dw_proviso"
lds_imp.settransobject (tr_cl)
lds_imp.Retrieve ()
ll_zap = 1
ll_ins = 0
ll_upd = 0
ll_all_zap = lds_imp.Rowcount ()
ll_kol_str  = ll_all_zap
if gi_user_dost = 1 then
  w_app_frame.iuoProgressBar.maxPosition = ll_kol_str
  w_app_frame.iuoProgressBar.setStep = 1
 elseif gi_user_dost = 3 then
   w_app_frm_isp.iuoProgressBar.maxPosition = ll_kol_str
   w_app_frm_isp.iuoProgressBar.setStep = 1
 end if
//vpb_3.Position = 0
//vpb_3.maxPosition = ll_kol_str
//vpb_3.setStep = ll_zap_bar
If ll_all_zap > 0 then
   li_zap = 0
	if gi_user_dost = 1 then
	   w_app_frame.iuoProgressBar.Position = 0
	elseif gi_user_dost = 3 then
	   w_app_frm_isp.iuoProgressBar.Position = 0
	end if
	st_1.Text = "Альтернативы расчетов"
   st_2.Text = "Запись в   .dbf ¦ "
   st_4.Text = "Запись в Oracle ¦ "
   FOR k = ll_zap to ll_all_zap
		yield()
     	 li_dog_id     = lds_imp.Object.D_KD[k]
		 ll_opl_id     = lds_imp.Object.ID_OPL[k]
		 li_rasch_id = lds_imp.Object.RASCH_ID[k]
		 ls_isp = lds_imp.Object.ISP[k]
		 li_isp_id = integer(ls_isp)
		 li_co_day = lds_imp.Object.CO_DAY[k]
		 li_pr_adv = lds_imp.Object.PR_ADV[k]
		 ls_day = lds_imp.Object.DAY[k]
		 ld_dopl = lds_imp.Object.DAT_OPL[k]
		 ld_dotgr = lds_imp.Object.DAT_OTGR[k]
		 ld_dreg = lds_imp.Object.DAT_PEG[k]
		 ld_ded = lds_imp.Object.DAT_ED[k]
		 li_kol++
       st_3.Text = string(li_kol)
	    SELECT "PROVISO"."PROV_ID"
    	 INTO :li_ora_count
	    FROM "PROVISO"
	    WHERE "PROVISO"."PROV_ID" = :ll_opl_id   ;
		 li_sql = SQLCA.sqlcode
	    If li_sql =0 then
//        Обнoвление данных из DBF
		     UPDATE "PROVISO"
				  SET "DOG_ID" = :li_dog_id,
						"RASCH_ID" = :li_rasch_id,
						"PROV_CO_DAY" = :li_co_day,
						"PROV_DAY" = :ls_day,
						"PROV_PR_ADV" = :li_pr_adv,
						"PROV_DOTGR" = :ld_dotgr,
						"PROV_DOPL" = :ld_dopl,
						"PROV_ISP" = :li_isp_id,
						"PROV_DREG" = :ld_dreg,
						"PROV_DEDIT" = :ld_ded
		  	   WHERE "PROVISO"."PROV_ID" = :ll_opl_id;
  			If SQLCA.sqlcode = -1 then
		       error.uf_sqlerror ()
             rollback;
				 ll_upd = 0
					if gi_user_dost = 1 then
					   w_app_frame.iuoProgressBar.Position = 0
					elseif gi_user_dost = 3 then
					   w_app_frm_isp.iuoProgressBar.Position = 0
					end if
			End If
			ll_upd++
    	 ElseIf li_sql =100 then
//        Дозапись данных из DBF
				  INSERT INTO "PROVISO"
								( "PROV_ID",
								  "DOG_ID",
								  "RASCH_ID",
								  "PROV_CO_DAY",
								  "PROV_DAY",
								  "PROV_PR_ADV",
								  "PROV_DOTGR",
								  "PROV_DOPL",
								  "PROV_ISP",
								  "PROV_DREG",
								  "PROV_DEDIT" )
					  VALUES ( :ll_opl_id,
								  :li_dog_id,
								  :li_rasch_id,
								  :li_co_day,
								  :ls_day,
								  :li_pr_adv,
								  :ld_dotgr,
								  :ld_dopl,
								  :li_isp_id,
								  :ld_dreg,
								  :ld_ded )  ;
			   If SQLCA.sqlcode = -1 then
		         error.uf_sqlerror ()
               rollback;
					ll_ins = 0
					if gi_user_dost = 1 then
					   w_app_frame.iuoProgressBar.Position = 0
					elseif gi_user_dost = 3 then
					   w_app_frm_isp.iuoProgressBar.Position = 0
					end if
        	   End If
				ll_ins++
		 ElseIf li_sql =-1 then
		        error.uf_sqlerror ()
              rollback;
					if gi_user_dost = 1 then
					   w_app_frame.iuoProgressBar.Position = 0
					elseif gi_user_dost = 3 then
					   w_app_frm_isp.iuoProgressBar.Position = 0
					end if
  	    End If
		 li_zap++
       st_5.Text = string(li_zap)
	   if gi_user_dost = 1 then
		 w_app_frame.event trigger ue_updateprogressbar ( )
       elseif gi_user_dost = 3 then
		  w_app_frm_isp.event trigger ue_updateprogressbar ( )
       end if
//		 vpb_1.StepIt()
	NEXT
	//добавить в imp_dbf!!!
	li_pri = 5  // alt_opl.dbf
	SELECT seq_s02015.nextval INTO :ll_id FROM dual;
	  INSERT INTO "IMP_DBF"
		         ( "IMP_ID",
      		     "SPIS_ID",
		           "IMP_DNEW",
		           "IMP_UPD_REC",
      		     "IMP_INS_REC",
		           "IMP_POLZ" )
		  VALUES ( :ll_id,
		           :li_pri,
      		     sysdate,
		           :ll_upd,
      		     :ll_ins,
		           :gs_user_name )  ;
           If SQLCA.sqlcode = -1 then
		         error.uf_sqlerror ()
               rollback;
					ll_ins = 0
					if gi_user_dost = 1 then
					   w_app_frame.iuoProgressBar.Position = 0
					elseif gi_user_dost = 3 then
					   w_app_frm_isp.iuoProgressBar.Position = 0
					end if
        	   End If
		//dw_1.update()
		dw_1.retrieve()
End If
// Импорт таблицы кодов расчетов из .dbf в Oracle
li_kol = 0
li_zap = 0
lds_imp = CREATE datastore
lds_imp.dataobject = "dw_rasch"
lds_imp.settransobject (tr_cl)
lds_imp.Retrieve ()
ll_ins = 0
ll_upd = 0
ll_zap = 1
ll_all_zap = lds_imp.Rowcount ()
ll_kol_str  = ll_all_zap
if gi_user_dost = 1 then
  w_app_frame.iuoProgressBar.maxPosition = ll_kol_str
  w_app_frame.iuoProgressBar.setStep = 1
 elseif gi_user_dost = 3 then
   w_app_frm_isp.iuoProgressBar.maxPosition = ll_kol_str
   w_app_frm_isp.iuoProgressBar.setStep = 1
 end if
//vpb_3.Position = 0
//vpb_3.maxPosition = ll_kol_str
//vpb_3.setStep = ll_zap_bar
If ll_all_zap > 0 then
   li_zap = 0
	if gi_user_dost = 1 then
	   w_app_frame.iuoProgressBar.Position = 0
	elseif gi_user_dost = 3 then
	   w_app_frm_isp.iuoProgressBar.Position = 0
	end if
	st_1.Text = "Коды расчетов"
   st_2.Text = "Запись в   .dbf ¦ "
   st_4.Text = "Запись в Oracle ¦ "
   FOR k = ll_zap to ll_all_zap
		 li_rasch_id = lds_imp.Object.RASCH_ID[k]
		 ls_naim = lds_imp.Object.rasch_naim[k]
		 li_exp =lds_imp.Object.rasch_exp[k]
		 li_imp =lds_imp.Object.rasch_imp[k]
		 li_kol++
       st_3.Text = string(li_kol)
		   SELECT "P_RASCH"."RASCH_ID"
		    INTO :li_ora_count
		    FROM "P_RASCH"
		   WHERE "P_RASCH"."RASCH_ID" = :li_rasch_id   ;
		 li_sql = SQLCA.sqlcode
	    If li_sql =0 then
//        Обнoвление данных из DBF
//			  UPDATE "P_RASCH"
//			     SET "RASCH_NAIM" = :ls_naim
//			   WHERE "P_RASCH"."RASCH_ID" = :li_rasch_id;

		UPDATE "P_RASCH"
	      SET "RASCH_NAIM" = :ls_naim,
   		        "RASCH_IMP" = :li_imp,
	             "RASCH_EXP" = :li_exp
           WHERE "P_RASCH"."RASCH_ID" = :li_rasch_id;

  			If SQLCA.sqlcode = -1 then
		       error.uf_sqlerror ()
             rollback;
				 ll_upd = 0
					if gi_user_dost = 1 then
					   w_app_frame.iuoProgressBar.Position = 0
					elseif gi_user_dost = 3 then
					   w_app_frm_isp.iuoProgressBar.Position = 0
					end if
			End If
			ll_upd++
    	 ElseIf li_sql =100 then
//        Дозапись данных из DBF
				   INSERT INTO "P_RASCH"
					                  ( "RASCH_ID",
				                         "RASCH_NAIM",
  								    "RASCH_IMP",
								    "RASCH_EXP"
													  )
					  VALUES ( :li_rasch_id,
					                  :ls_naim,
								  :li_imp,
								  :li_exp) ;
			   If SQLCA.sqlcode = -1 then
		         error.uf_sqlerror ()
               rollback;
					ll_ins = 0
					if gi_user_dost = 1 then
					   w_app_frame.iuoProgressBar.Position = 0
					elseif gi_user_dost = 3 then
					   w_app_frm_isp.iuoProgressBar.Position = 0
					end if
        	   End If
				ll_ins++
		 ElseIf li_sql =-1 then
		        error.uf_sqlerror ()
              rollback;
					if gi_user_dost = 1 then
					   w_app_frame.iuoProgressBar.Position = 0
					elseif gi_user_dost = 3 then
					   w_app_frm_isp.iuoProgressBar.Position = 0
					end if
  	    End If
		 li_zap++
       st_5.Text = string(li_zap)
	   if gi_user_dost = 1 then
		 w_app_frame.event trigger ue_updateprogressbar ( )
       elseif gi_user_dost = 3 then
		  w_app_frm_isp.event trigger ue_updateprogressbar ( )
       end if
//		 vpb_1.StepIt()
	NEXT
	//добавить в imp_dbf!!!
	li_pri = 6
	SELECT seq_s02015.nextval INTO :ll_id FROM dual;
	  INSERT INTO "IMP_DBF"
		         ( "IMP_ID",
      		      "SPIS_ID",
		           "IMP_DNEW",
		           "IMP_UPD_REC",
      		      "IMP_INS_REC",
		           "IMP_POLZ" )
		  VALUES ( :ll_id,
		                :li_pri,
             		        sysdate,
      		           :ll_upd,
         		      :ll_ins,
		               :gs_user_name )  ;
           If SQLCA.sqlcode = -1 then
		         error.uf_sqlerror ()
               rollback;
					ll_ins = 0
					if gi_user_dost = 1 then
					   w_app_frame.iuoProgressBar.Position = 0
					elseif gi_user_dost = 3 then
					   w_app_frm_isp.iuoProgressBar.Position = 0
					end if
        	   End If
		//dw_1.update()
		dw_1.retrieve()
End If
// Импорт таблицы видов документов из .dbf в Oracle
li_kol = 0
li_zap = 0
lds_imp = CREATE datastore
lds_imp.dataobject = "dw_doco"
lds_imp.settransobject (tr_cl)
lds_imp.Retrieve ()
ll_zap = 1
ll_ins = 0
ll_upd = 0
ll_all_zap = lds_imp.Rowcount ()
ll_kol_str  = ll_all_zap
if gi_user_dost = 1 then
  w_app_frame.iuoProgressBar.maxPosition = ll_kol_str
  w_app_frame.iuoProgressBar.setStep = 1
 elseif gi_user_dost = 3 then
   w_app_frm_isp.iuoProgressBar.maxPosition = ll_kol_str
   w_app_frm_isp.iuoProgressBar.setStep = 1
 end if
//vpb_3.Position = 0
//vpb_3.maxPosition = ll_kol_str
//vpb_3.setStep = ll_zap_bar
If ll_all_zap > 0 then
   li_zap = 0
    if gi_user_dost = 1 then
	   w_app_frame.iuoProgressBar.Position = 0
	elseif gi_user_dost = 3 then
	   w_app_frm_isp.iuoProgressBar.Position = 0
	end if
	st_1.Text = "Виды документов"
   st_2.Text = "Запись в   .dbf ¦ "
   st_4.Text = "Запись в Oracle ¦ "
   FOR k = ll_zap to ll_all_zap
		 li_vdoc_id = lds_imp.Object.id_doc[k]
		 ls_naim = lds_imp.Object.NAIM_DOC[k]
		 li_kol++
       st_3.Text = string(li_kol)
		 SELECT "VID_DOC"."VDOC_ID"
		   INTO :li_ora_count
		   FROM "VID_DOC"
		  WHERE "VID_DOC"."VDOC_ID" = :li_vdoc_id;
		 li_sql = SQLCA.sqlcode
	    If li_sql =0 then
//        Обнoвление данных из DBF
 		   UPDATE "VID_DOC"
		     SET "VDOC_NAIM" = :ls_naim
		   WHERE "VID_DOC"."VDOC_ID" = :li_vdoc_id ;
  			If SQLCA.sqlcode = -1 then
		       error.uf_sqlerror ()
             rollback;
				 ll_upd = 0
					if gi_user_dost = 1 then
					   w_app_frame.iuoProgressBar.Position = 0
					elseif gi_user_dost = 3 then
					   w_app_frm_isp.iuoProgressBar.Position = 0
					end if
			End If
			ll_upd++
    	 ElseIf li_sql =100 then
//        Дозапись данных из DBF
		  INSERT INTO "VID_DOC"
		         ( "VDOC_ID",
      		     "VDOC_NAIM" )
		  VALUES ( :li_vdoc_id,
					  :ls_naim  )  ;
			   If SQLCA.sqlcode = -1 then
		         error.uf_sqlerror ()
               rollback;
					ll_ins = 0
					if gi_user_dost = 1 then
					   w_app_frame.iuoProgressBar.Position = 0
					elseif gi_user_dost = 3 then
					   w_app_frm_isp.iuoProgressBar.Position = 0
					end if
        	   End If
				ll_ins++
		 ElseIf li_sql =-1 then
		        error.uf_sqlerror ()
              rollback;
					if gi_user_dost = 1 then
					   w_app_frame.iuoProgressBar.Position = 0
					elseif gi_user_dost = 3 then
					   w_app_frm_isp.iuoProgressBar.Position = 0
					end if
  	    End If
		 li_zap++
       st_5.Text = string(li_zap)
	   if gi_user_dost = 1 then
		 w_app_frame.event trigger ue_updateprogressbar ( )
       elseif gi_user_dost = 3 then
		  w_app_frm_isp.event trigger ue_updateprogressbar ( )
       end if
//		 vpb_1.StepIt()
	NEXT
	//добавить в imp_dbf!!!
	li_pri = 4
	SELECT seq_s02015.nextval INTO :ll_id FROM dual;
	  INSERT INTO "IMP_DBF"
		         ( "IMP_ID",
      		     "SPIS_ID",
		           "IMP_DNEW",
		           "IMP_UPD_REC",
      		     "IMP_INS_REC",
		           "IMP_POLZ" )
		  VALUES ( :ll_id,
		           :li_pri,
      		     sysdate,
		           :ll_upd,
      		     :ll_ins,
		           :gs_user_name )  ;
           If SQLCA.sqlcode = -1 then
		         error.uf_sqlerror ()
               rollback;
					ll_ins = 0
					if gi_user_dost = 1 then
					   w_app_frame.iuoProgressBar.Position = 0
					elseif gi_user_dost = 3 then
					   w_app_frm_isp.iuoProgressBar.Position = 0
					end if
        	   End If
		//dw_1.update()
		dw_1.retrieve()
End If
// Импорт таблицы пользователей 02015 из .dbf в Oracle
li_kol = 0
li_zap = 0
lds_imp = CREATE datastore
lds_imp.dataobject = "dw_polz"
lds_imp.settransobject (tr_cl)
lds_imp.Retrieve ()
ll_ins = 0
ll_upd = 0
ll_zap = 1
ll_all_zap = lds_imp.Rowcount ()
ll_kol_str  = ll_all_zap
if gi_user_dost = 1 then
  w_app_frame.iuoProgressBar.maxPosition = ll_kol_str
  w_app_frame.iuoProgressBar.setStep = 1
 elseif gi_user_dost = 3 then
   w_app_frm_isp.iuoProgressBar.maxPosition = ll_kol_str
   w_app_frm_isp.iuoProgressBar.setStep = 1
 end if
If ll_all_zap > 0 then
   li_zap = 0
   if gi_user_dost = 1 then
	   w_app_frame.iuoProgressBar.Position = 0
    elseif gi_user_dost = 3 then
	   w_app_frm_isp.iuoProgressBar.Position = 0
	end if
   st_1.Text = "Пользователи "
   st_2.Text = "Запись в   .dbf ¦ "
   st_4.Text = "Запись в Oracle ¦ "
   FOR k = ll_zap to ll_all_zap
		yield()
		 ls_temp = lds_imp.Object.P_KUSER[k]
		 li_isp_id = integer(ls_temp)
		 ls_naim = lds_imp.Object.P_FAM[k]
		 ls_sluzba = lds_imp.Object.P_PODR[k]
  		 li_dep = get_dep_id(ls_sluzba)
		 li_type = lds_imp.Object.P_FUN[k]
		 li_status = lds_imp.Object.P_ADM[k]
		 li_polz_exit = lds_imp.Object.P_EXIT[k]
		 li_polz_exp = lds_imp.Object.P_EXP[k]
		 li_polz_urb = lds_imp.Object.P_URB[k]
		 li_kol++
       st_3.Text = string(li_kol)
		   SELECT "POLZ_02015"."POLZ_ID"
		    INTO :li_ora_count
		    FROM "POLZ_02015"
		   WHERE "POLZ_02015"."POLZ_ID" = :li_isp_id   ;
		 li_sql = SQLCA.sqlcode
	    If li_sql =0 then
//        Обнoвление данных из DBF
		     UPDATE "POLZ_02015"
				  SET 	"POLZ_NAME" = :ls_naim,
						"POLZ_FUN" = :li_type,
						"POLZ_ADM" = :li_status,
						"POLZ_EXIT" = :li_polz_exit,
						"POLZ_EXP" = :li_polz_exp,
						"POLZ_URB" = :li_polz_urb,
						"DEP_ID" = :li_dep
 	  	      WHERE "POLZ_02015"."POLZ_ID" = :li_isp_id;
  			If SQLCA.sqlcode = -1 then
		       error.uf_sqlerror ()
             rollback;
				 ll_upd = 0
					if gi_user_dost = 1 then
					   w_app_frame.iuoProgressBar.Position = 0
					elseif gi_user_dost = 3 then
					   w_app_frm_isp.iuoProgressBar.Position = 0
					end if
			End If
			ll_upd++
    	 ElseIf li_sql =100 then
//        Дозапись данных из DBF
				   INSERT INTO "POLZ_02015"
					         ( "POLZ_ID",
				               "POLZ_NAME",
							 "POLZ_FUN",
   						      "POLZ_ADM",
						      "DEP_ID",
   						      "POLZ_EXIT",
   						      "POLZ_EXP" ,
   						      "POLZ_URB"
								)
					  VALUES ( :li_isp_id,
					                :ls_naim,
							       :li_type,
								   :li_status,
								   :li_dep,
								   :li_polz_exit,
								   :li_polz_exp,
								   :li_polz_urb);
			   If SQLCA.sqlcode = -1 then
		         error.uf_sqlerror ()
               rollback;
					ll_ins = 0
					if gi_user_dost = 1 then
					   w_app_frame.iuoProgressBar.Position = 0
					elseif gi_user_dost = 3 then
					   w_app_frm_isp.iuoProgressBar.Position = 0
					end if
        	   End If
				ll_ins++
		 ElseIf li_sql =-1 then
		        error.uf_sqlerror ()
              rollback;
					if gi_user_dost = 1 then
					   w_app_frame.iuoProgressBar.Position = 0
					elseif gi_user_dost = 3 then
					   w_app_frm_isp.iuoProgressBar.Position = 0
					end if
  	    End If
		 li_zap++
       st_5.Text = string(li_zap)
	   if gi_user_dost = 1 then
		 w_app_frame.event trigger ue_updateprogressbar ( )
       elseif gi_user_dost = 3 then
		  w_app_frm_isp.event trigger ue_updateprogressbar ( )
       end if
//		 vpb_1.StepIt()
	NEXT
	//добавить в imp_dbf!!!
	li_pri = 7
	SELECT seq_s02015.nextval INTO :ll_id FROM dual;
	  INSERT INTO "IMP_DBF"
		         ( "IMP_ID",
        		      "SPIS_ID",
		           "IMP_DNEW",
		           "IMP_UPD_REC",
      		      "IMP_INS_REC",
		           "IMP_POLZ" )
		  VALUES ( :ll_id,
		                :li_pri,
      		            sysdate,
		                :ll_upd,
           		       :ll_ins,
		                :gs_user_name )  ;
           If SQLCA.sqlcode = -1 then
		         error.uf_sqlerror ()
               rollback;
					ll_ins = 0
					if gi_user_dost = 1 then
					   w_app_frame.iuoProgressBar.Position = 0
					elseif gi_user_dost = 3 then
					   w_app_frm_isp.iuoProgressBar.Position = 0
					end if
        	   End If
		//dw_1.update()
		dw_1.retrieve()
End If
// Импорт таблицы по таре из .dbf в Oracle
li_kol = 0
li_zap = 0
ll_zap_bar = 100
lds_imp = CREATE datastore
lds_imp.dataobject = "dw_packing"
lds_imp.settransobject (tr_cl)
ll_all_zap =lds_imp.Retrieve ()
ll_zap = 1
ll_ins = 0
ll_upd = 0
ll_kol_str  = ll_all_zap
if gi_user_dost = 1 then
  w_app_frame.iuoProgressBar.maxPosition = ll_kol_str
  w_app_frame.iuoProgressBar.setStep = 1
 elseif gi_user_dost = 3 then
   w_app_frm_isp.iuoProgressBar.maxPosition = ll_kol_str
   w_app_frm_isp.iuoProgressBar.setStep = 1
 end if
  UPDATE "PACKING"
     SET "PACK_OPERABLE" = 0  ;
If ll_all_zap > 0 then
   li_zap = 0
   if gi_user_dost = 1 then
	   w_app_frame.iuoProgressBar.Position = 0
   elseif gi_user_dost = 3 then
	   w_app_frm_isp.iuoProgressBar.Position = 0
   end if
   st_1.Text = "Тара"
   st_2.Text = "Запись в   .dbf ¦ "
   st_4.Text = "Запись в Oracle ¦ "
   FOR k = ll_zap to ll_all_zap
		yield()
		 ll_spec_id     = lds_imp.Object.S_ID[k]
		 if  isnull(ll_spec_id) then
		     ll_spec_id = 0
	     end if
		 li_dog_id     = lds_imp.Object.D_KD[k]
		 ls_isp = lds_imp.Object.ISP[k]
		 li_isp_id = integer(ls_isp)
		 ls_day = lds_imp.Object.KODM[k] //код материала
		 ls_nd = lds_imp.Object.KOD1[k]    //код группы
		 ls_them = lds_imp.Object.KOD3[k]    //код подгруппы
		 ldec_kol = lds_imp.Object.KOL_UNIT[k]
		 ldec_curs = round( lds_imp.Object.VES_UNIT[k],8)		 //вес единицы
		 ldec_nds =  lds_imp.Object.VES_UNIT[k]		 //вес единицы
		 ld_dreg = lds_imp.Object.DAT_REG[k]
		 ld_ded = lds_imp.Object.DAT_ED[k]
		 li_kol++
          st_3.Text = string(li_kol)
          SELECT "S16071"."SM_NAME"."NAIM_ID"
   				 INTO :ll_naim_id
			     FROM "S16071"."SM_NAME"
				WHERE "S16071"."SM_NAME"."NAIM_KOD_OLD" = :ls_day   ;
          If isnull(ll_naim_id) then
				ll_naim_id = 0
		 end if
//        Обнoвление данных из DBF
          UPDATE "PACKING"
                 SET "DOG_ID" = :li_dog_id,
                        "SPECIF_ID" = :ll_spec_id,
                        "KOD_GROUP" = :ls_nd,
			          "KOD_SUBGR" = :ls_them,
			          "KODM" = :ls_day,
			          "VES_UNIT" = :ldec_curs,
			          "KOL_UNIT" = :ldec_kol,
			          "PACK_ISP" = :li_isp_id,
			          "PACK_DAT_ED" = :ld_ded,
					 "NAIM_ID"	= :ll_naim_id,
			          "PACK_OPERABLE" = 1
                  WHERE  ("PACKING"."SPECIF_ID" = :ll_spec_id) AND
		                      ("PACKING"."DOG_ID" = :li_dog_id) AND
			   		   	    ("PACKING"."KODM" = :ls_day) AND
							("PACKING"."VES_UNIT" = :ldec_curs) ;
          If SQLCA.sqlcode = -1 then
	          error.uf_sqlerror ()
             rollback;
				 ll_upd = 0
					if gi_user_dost = 1 then
					   w_app_frame.iuoProgressBar.Position = 0
					elseif gi_user_dost = 3 then
					   w_app_frm_isp.iuoProgressBar.Position = 0
					end if
		       Return
          End If

          li_sql = SQLCA.sqlcode
          If li_sql =0  and SQLCA.sqlnrows>0 then
			 ll_upd++
       ElseIf li_sql =0 and SQLCA.sqlnrows=0 then
//            Дозапись данных из DBF
			 SELECT seq_s02015_spr.nextval INTO :ll_id FROM dual;
			 INSERT INTO "PACKING"
					         ( "PACK_ID",
					           "DOG_ID",
					           "SPECIF_ID",
					           "KOD_GROUP",
					           "KOD_SUBGR",
					           "KODM",
					           "VES_UNIT",
					           "KOL_UNIT",
					           "PACK_ISP",
					           "PACK_DAT_REG",
					           "PACK_DAT_ED" ,
							  "NAIM_ID",
							  "PACK_OPERABLE")
				VALUES (  :ll_id,
					           :li_dog_id,
					           :ll_spec_id,
					           :ls_nd,
					           :ls_them,
					           :ls_day,
					           :ldec_curs,
					           :ldec_kol,
							  :li_isp_id,
					           :ld_dreg,
					           :ld_ded,
 							  :ll_naim_id,
								1         )  ;
			     If SQLCA.sqlcode = -1 then
		             error.uf_sqlerror ()
                       rollback;
					ll_ins = 0
					if gi_user_dost = 1 then
					   w_app_frame.iuoProgressBar.Position = 0
					elseif gi_user_dost = 3 then
					   w_app_frm_isp.iuoProgressBar.Position = 0
					end if
        	         End If
					  ll_ins++
    	 ElseIf li_sql =-1 then
		        error.uf_sqlerror ()
              rollback;
				    if gi_user_dost = 1 then
					   w_app_frame.iuoProgressBar.Position = 0
					elseif gi_user_dost = 3 then
					   w_app_frm_isp.iuoProgressBar.Position = 0
					end if
  	    End If
	    li_zap++
       st_5.Text = string(li_zap)
	   if gi_user_dost = 1 then
		 w_app_frame.event trigger ue_updateprogressbar ( )
       elseif gi_user_dost = 3 then
		  w_app_frm_isp.event trigger ue_updateprogressbar ( )
       end if
	NEXT
	li_pri = 9 //packing.dbf
	SELECT seq_s02015.nextval INTO :ll_id FROM dual;
	  INSERT INTO "IMP_DBF"
		         ( "IMP_ID",
      		     "SPIS_ID",
		           "IMP_DNEW",
		           "IMP_UPD_REC",
      		     "IMP_INS_REC",
		           "IMP_POLZ" )
		  VALUES ( :ll_id,
		           :li_pri,
      		     sysdate,
		           :ll_upd,
      		     :ll_ins,
		           :gs_user_name )  ;
           If SQLCA.sqlcode = -1 then
		         error.uf_sqlerror ()
               rollback;
					ll_ins = 0
					if gi_user_dost = 1 then
					   w_app_frame.iuoProgressBar.Position = 0
					elseif gi_user_dost = 3 then
					   w_app_frm_isp.iuoProgressBar.Position = 0
					end if
        	   End If
		//dw_1.update()
		dw_1.retrieve()
End If

// Импорт таблицы типов тары из .dbf в Oracle
li_kol = 0
li_zap = 0
lds_imp = CREATE datastore
lds_imp.dataobject = "dw_tpack"
lds_imp.settransobject (tr_cl)
lds_imp.Retrieve ()
ll_zap = 1
ll_ins = 0
ll_upd = 0
ll_all_zap = lds_imp.Rowcount ()
ll_kol_str  = ll_all_zap
if gi_user_dost = 1 then
  w_app_frame.iuoProgressBar.maxPosition = ll_kol_str
  w_app_frame.iuoProgressBar.setStep = 1
 elseif gi_user_dost = 3 then
   w_app_frm_isp.iuoProgressBar.maxPosition = ll_kol_str
   w_app_frm_isp.iuoProgressBar.setStep = 1
 end if
//vpb_3.Position = 0
//vpb_3.maxPosition = ll_kol_str
//vpb_3.setStep = ll_zap_bar
If ll_all_zap > 0 then
   li_zap = 0
   if gi_user_dost = 1 then
	   w_app_frame.iuoProgressBar.Position = 0
   elseif gi_user_dost = 3 then
	   w_app_frm_isp.iuoProgressBar.Position = 0
	end if
	st_1.Text = "Типы тары"
   st_2.Text = "Запись в   .dbf ¦ "
   st_4.Text = "Запись в Oracle ¦ "
   FOR k = ll_zap to ll_all_zap
	 yield()
	 li_tpack_id = lds_imp.Object.PACK_ID[k]
		 ls_naim = lds_imp.Object.PACK_NAIM[k]
		 li_kol++
       st_3.Text = string(li_kol)
		 SELECT "PACK_TYPE"."TPACK_ID"
		   INTO :li_ora_count
		   FROM "PACK_TYPE"
		  WHERE "PACK_TYPE"."TPACK_ID" = :li_tpack_id;
		 li_sql = SQLCA.sqlcode
	    If li_sql =0 then
//        Обнoвление данных из DBF
 		   UPDATE "PACK_TYPE"
		     SET "TPACK_NAME" = :ls_naim
		   WHERE "PACK_TYPE"."TPACK_ID" = :li_tpack_id ;
  			If SQLCA.sqlcode = -1 then
		       error.uf_sqlerror ()
             rollback;
				 ll_upd = 0
					if gi_user_dost = 1 then
					   w_app_frame.iuoProgressBar.Position = 0
					elseif gi_user_dost = 3 then
					   w_app_frm_isp.iuoProgressBar.Position = 0
					end if
			End If
			ll_upd++
    	 ElseIf li_sql =100 then
//        Дозапись данных из DBF
		  INSERT INTO "PACK_TYPE"
		         ( "TPACK_ID",
      		     "TPACK_NAME" )
		  VALUES ( :li_tpack_id,
					  :ls_naim  )  ;
			   If SQLCA.sqlcode = -1 then
		         error.uf_sqlerror ()
               rollback;
					ll_ins = 0
					if gi_user_dost = 1 then
					   w_app_frame.iuoProgressBar.Position = 0
					elseif gi_user_dost = 3 then
					   w_app_frm_isp.iuoProgressBar.Position = 0
					end if
        	   End If
				ll_ins++
		 ElseIf li_sql =-1 then
		        error.uf_sqlerror ()
              rollback;
					if gi_user_dost = 1 then
					   w_app_frame.iuoProgressBar.Position = 0
					elseif gi_user_dost = 3 then
					   w_app_frm_isp.iuoProgressBar.Position = 0
					end if
  	    End If
		 li_zap++
       st_5.Text = string(li_zap)
	   if gi_user_dost = 1 then
		 w_app_frame.event trigger ue_updateprogressbar ( )
       elseif gi_user_dost = 3 then
		  w_app_frm_isp.event trigger ue_updateprogressbar ( )
       end if
//		 vpb_1.StepIt()
	NEXT
	//добавить в imp_dbf!!!
	li_pri = 8
	SELECT seq_s02015.nextval INTO :ll_id FROM dual;
	  INSERT INTO "IMP_DBF"
		         ( "IMP_ID",
      		     "SPIS_ID",
		           "IMP_DNEW",
		           "IMP_UPD_REC",
      		     "IMP_INS_REC",
		           "IMP_POLZ" )
		  VALUES ( :ll_id,
		           :li_pri,
      		     sysdate,
		           :ll_upd,
      		     :ll_ins,
		           :gs_user_name )  ;
           If SQLCA.sqlcode = -1 then
		         error.uf_sqlerror ()
               rollback;
					ll_ins = 0
					if gi_user_dost = 1 then
					   w_app_frame.iuoProgressBar.Position = 0
					elseif gi_user_dost = 3 then
					   w_app_frm_isp.iuoProgressBar.Position = 0
					end if
        	   End If
		//dw_1.update()
		dw_1.retrieve()
End If

COMMIT ;
if gi_user_dost = 1 then
   w_app_frame.iuoProgressBar.Position = 0
elseif gi_user_dost = 3 then
   w_app_frm_isp.iuoProgressBar.Position = 0
end if
st_1.Text = ""
st_2.Text = ""
st_4.Text = ""
st_3.Text = ""
st_5.Text = ""
SetPointer(oldpointer)
cb_2.enabled = true

MessageBox("Импорт данных"," Завершено!")

END IF
end if // по доступу
//close(parent)
//