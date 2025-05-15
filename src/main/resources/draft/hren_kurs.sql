integer  li_sql, li_imp,nFile,l_rkod
long ll_zap, ll_kol_str, k,ll_all_zap,ll_ora_id, ll_kol_tek, ll_zap_tek,ll_kod,li_ora_count
string ls_im,l_rdata
decimal ldc_sh,ldc_kurs,ldc_kurs1,l_rkurs
date ld_dkurs,li_ora_dat
//
tr_cl = CREATE TRANSACTION
tr_cl.DBMS = "ODBC"
tr_cl.Database = ""
tr_cl.LogID = ""
tr_cl.LogPass = ""
tr_cl.ServerName = ""
tr_cl.DBParm = "ConnectString='DSN=firm'"
CONNECT USING tr_cl;
If tr_cl.SQLCode <> 0 Then
	error.uf_sqlerror()
End If
//
ll_kol_tek = 0
ll_zap_tek = 0
lds_imp = CREATE datastore
lds_imp.dataobject = "ds_rate"
lds_imp.settransobject (tr_cl)
lds_imp.Retrieve ()
ll_zap = 1
ll_all_zap = lds_imp.Rowcount ()
ll_kol_str  = ll_all_zap
w_app_frame.iuoProgressBar.maxPosition = ll_kol_str
w_app_frame.iuoProgressBar.setStep = 1
ls_im = 'set transaction use rollback segment RB5'
execute immediate :ls_im;
//
If ll_all_zap > 0 Then
   w_app_frame.iuoProgressBar.Position = 0

   st_4.Text = lds_imp.dataobject

   FOR k = ll_zap to ll_all_zap
          Yield()
		 ll_kod         = lds_imp.Object.KOD[k]
          ld_dkurs      = lds_imp.Object.DATA1[k]
		 ldc_sh = lds_imp.Object.SHKALA[k]
		 ldc_kurs=lds_imp.Object.KURS[k]
		 ldc_kurs1=lds_imp.Object.KURS_BO[k]
		 if isnull(ldc_kurs1) then
			ldc_kurs1 = 0
		 end if
    	     ll_kol_tek++
          st_5.Text = string(ll_kol_tek)

		 SELECT "S03000"."RATE"."CUR_KOD", "S03000"."RATE"."RAT_DATA"
		 INTO :li_ora_count, :li_ora_dat
		 FROM   "S03000"."RATE"
		 WHERE "S03000"."RATE"."CUR_KOD" = :ll_kod and "S03000"."RATE"."RAT_DATA"=:ld_dkurs;
		  li_sql = SQLCA.sqlcode
	      If li_sql =0 then
//        Обнавление данных из DBF
             UPDATE "S03000"."RATE"
		    SET       "S03000"."RATE"."CUR_KOD"  = :ll_kod,
			            "S03000"."RATE"."RAT_DATA"=:ld_dkurs,
					   "S03000"."RATE"."RAT_KURS"=:ldc_kurs,
					   "S03000"."RATE"."RAT_SHKALA"=:ldc_sh,
					   "S03000"."RATE"."RAT_KURS_BO"=:ldc_kurs1
		    WHERE  "S03000"."RATE"."CUR_KOD" = :ll_kod and "S03000"."RATE"."RAT_DATA"=:ld_dkurs;
   		 If SQLCA.sqlcode = -1 then
		       error.uf_sqlerror ()
             rollback;
				 w_app_frame.iuoProgressBar.Position = 0
				 Return -1
        	 End If
    	 ElseIf li_sql =100 then
//        Дозапись данных из DBF
              INSERT INTO "S03000"."RATE"
                                ( "S03000"."RATE"."CUR_KOD" ,
                                  "S03000"."RATE"."RAT_DATA",
  					      	  "S03000"."RATE"."RAT_KURS",
							  "S03000"."RATE"."RAT_SHKALA" ,
							  "S03000"."RATE"."RAT_KURS_BO")
              VALUES ( :ll_kod,
 	                        :ld_dkurs,
   	 		               :ldc_kurs,
					      :ldc_sh,
					      :ldc_kurs1);

			   If SQLCA.sqlcode = -1 then
		         error.uf_sqlerror ()
               rollback;
				   w_app_frame.iuoProgressBar.Position = 0
				   Return -1
        	   End If
		 ElseIf li_sql =-1 then
		        error.uf_sqlerror ()
              rollback;
				  w_app_frame.iuoProgressBar.Position = 0
				  Return -1
  	    End If



//          INSERT INTO "S03000"."RATE"
//                    ( "S03000"."RATE"."CUR_KOD" ,
//                      "S03000"."RATE"."RAT_DATA",
//  							 "S03000"."RATE"."RAT_KURS",
//							 "S03000"."RATE"."RAT_SHKALA" ,
//							 "S03000"."RATE"."RAT_KURS_BO")
//              VALUES ( :ll_kod,
// 	                    :ld_dkurs,
//   	 		           :ldc_kurs,
//					        :ldc_sh,
//					        :ldc_kurs1)
//						 ;
//	         If sqlca.sqlcode =-1 then
//		      error.uf_sqlerror ()
//                rollback;
//			Return -1
//		   End If


        ll_zap_tek++
           st_6.Text = string(ll_zap_tek)
           w_app_frame.event trigger ue_updateprogressbar ( )
       NEXT
       COMMIT;
// Запись информации в xml
       k=0
       nFile= FileOpen("L:\W_BUH\МНС\Плательщик V1\description\currency_rate.xml",StreamMode!,Write!,Shared!,Replace!)
	  FileWrite(nFile,'<?xml version="1.0" encoding="windows-1251" ?>')
      FileWrite(nFile, '<root>')
	  DECLARE cur_rate CURSOR FOR
	  SELECT  TO_CHAR("S03000"."RATE"."RAT_DATA",'dd.mm.yyyy'),
				  "S03000"."RATE"."RAT_KURS","S03000"."RATE"."CUR_KOD"
       FROM "S03000"."RATE"
       WHERE ("S03000"."RATE"."CUR_KOD"     = 1 OR "S03000"."RATE"."CUR_KOD"     = 978 OR "S03000"."RATE"."CUR_KOD"     = 858) AND "S03000"."RATE"."RAT_DATA">= TO_DATE('01.01.2010')
	 ORDER BY "S03000"."RATE"."RAT_DATA"
       USING sqlca ;
       OPEN cur_rate;
       If sqlca.sqlcode = 0 Then
       Do While true
		   k++
	       Fetch cur_rate into :l_rdata, :l_rkurs, :l_rkod ;
            If sqlca.sqlcode<>100  Then
		      FileWrite(nFile, '<item id="')
               FileWrite(nFile, Trim(String(k)))
			  If l_rkod= 858 Then
                   FileWrite(nFile, '" code="643" date="')
			 End If
 			  If l_rkod= 1 Then
                   FileWrite(nFile, '" code="840" date="')
			 End If
			  If l_rkod= 978 Then
                   FileWrite(nFile, '" code="978" date="')
			 End If
		      FileWrite(nFile, l_rdata)
		      FileWrite(nFile, '" value="')
               FileWrite(nFile, Trim(String(l_rkurs)))
		      FileWrite(nFile, '"/>')
			Else
			    EXIT
		End If
            LOOP
     End If
     Close cur_rate;
      FileWrite(nFile, '</root>')
	 FileClose(nFile)

       w_app_frame.iuoProgressBar.Position = 0
       MessageBox ( 'Импорт', 'Импорт данных завершен!', Information!, OK!)
	  cbx_10.Checked = false
Else
	MessageBox("Импорт","Нет информации в формате .dbf",StopSign!)
	Return -1
End If
Return 1