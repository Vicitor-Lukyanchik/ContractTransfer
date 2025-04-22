	 // Для dbf-таблицы reg_dog
	 li_dog_id     = lds_imp.Object.D_KD[k]
		 ls_them = lds_imp.Object.D_KPO[k]
		 ls_otv  = lds_imp.Object.D_OTV[k]
		 li_rasch_id = lds_imp.Object.D_OR_AC[k]
		 li_vdoc_id = lds_imp.Object.OT_HR[k]
		 li_cur_id = lds_imp.Object.D_CUR[k]
		 ls_isp = lds_imp.Object.D_ISP[k]
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
		 li_isp_id = integer(ls_isp)
  		 li_dep = get_dep_id(ls_sluzba)
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


// Для dbf-таблицы reg_dcop
         li_dog_id     = lds_imp.Object.D_KD[k]
		 ll_sogl_id     = lds_imp.Object.D_KSO[k]
		 ls_otv  = lds_imp.Object.D_OTV[k]
		 li_rasch_id = lds_imp.Object.D_OR_AC[k]
		 li_cur_id = lds_imp.Object.D_CUR[k]
		 ls_isp = lds_imp.Object.D_ISP[k]
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
		 li_isp_id = integer(ls_isp)
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


// Для dbf-таблицы specif
     	 ll_spec_id     = lds_imp.Object.S_ID[k]
   		 li_dog_id     = lds_imp.Object.S_KD[k]
   		 li_rasch_id = lds_imp.Object.S_OR_AC[k]
   		 li_cur_id = lds_imp.Object.S_CUR[k]
   		 ls_isp = lds_imp.Object.S_ISP[k]
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
   		 li_isp_id = integer(ls_isp)
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


// Для dbf-таблицы alt_opl
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



// Для dbf-таблицы p_rasch
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
		UPDATE "P_RASCH"
	      SET "RASCH_NAIM" = :ls_naim,
   		        "RASCH_IMP" = :li_imp,
	             "RASCH_EXP" = :li_exp


// Для dbf-таблицы vid_doc
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


// Для dbf-таблицы PPP
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


// Для dbf-таблицы packing
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
						 ("PACKING"."VES_UNIT" = :ldec_curs);
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
								1         ) ;