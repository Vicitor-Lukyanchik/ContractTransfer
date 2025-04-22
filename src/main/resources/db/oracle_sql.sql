create table DOGOVOR
(
  DOG_ID       NUMBER(7) not null,
  VDOC_ID      NUMBER(2),
  DOG_PRI      NUMBER(1),
  DOG_ND       VARCHAR2(30),
  DOG_DATH     DATE,
  DOG_REG      DATE,
  DOG_DBEG     DATE,
  DOG_DEND     DATE,
  FIRM_ID      NUMBER(5),
  CUR_ID       NUMBER(4),
  DOG_SUMD     NUMBER(16,2),
  DOG_THEM     VARCHAR2(35),
  DOG_SLUZBA   CHAR(4),
  DOG_OTV      VARCHAR2(20),
  DOG_SOGL     NUMBER(1),
  DOG_SPECIF   NUMBER(1),
  RASCH_ID     NUMBER(2),
  DOG_CO_DAY   NUMBER(5),
  DOG_DAY      CHAR(2),
  DOG_PR_ADV   NUMBER(2),
  DOG_SHTR     NUMBER(6,2),
  DOG_DOTGR    DATE,
  DOG_DOPL     DATE,
  DOG_ISP_ID   NUMBER(5),
  DOG_DAT_ED   DATE,
  DOG_OPERABLE NUMBER(1),
  DEP_ID       NUMBER(4),
  TPACK_ID     NUMBER(3),
  DELIV_ID     NUMBER(3),
  DOG_CLAIM    NUMBER(1),
  PROD_SPOK    DATE
)

create table DOGO_SOGL
(
  DOG_ID      NUMBER(7) not null,
  SOGL_ID     NUMBER(7) not null,
  SOGL_ND     VARCHAR2(20) not null,
  SOGL_DATH   DATE not null,
  SOGL_REG    DATE,
  SOGL_DBEG   DATE,
  SOGL_DEND   DATE,
  FIRM_ID     NUMBER(5) not null,
  CUR_ID      NUMBER(4),
  SOGL_SUMD   NUMBER(16,2),
  SOGL_THEM   VARCHAR2(35),
  SOGL_SLUZBA CHAR(4),
  SOGL_OTV    VARCHAR2(20),
  RASCH_ID    NUMBER(2),
  SOGL_CO_DAY NUMBER(5),
  SOGL_DAY    CHAR(2),
  SOGL_PR_ADV NUMBER(2),
  SOGL_SHTR   NUMBER(6,2),
  SOGL_DOTGR  DATE,
  SOGL_DOPL   DATE,
  SOGL_ISP_ID NUMBER(5),
  SOGL_DAT_ED DATE
)

create table DOGO_SPECIF
(
  DOG_ID          NUMBER(7) not null,
  SPECIF_ID       NUMBER(7) not null,
  SPECIF_NS       VARCHAR2(50),
  SPECIF_DATH     DATE,
  SPECIF_REG      DATE,
  CUR_ID          NUMBER(4),
  SPECIF_SUMS     NUMBER(16,2),
  SPECIF_NDS      NUMBER(16,2),
  SPECIF_KURS     NUMBER(14,2),
  RASCH_ID        NUMBER(2),
  SPECIF_CO_DAY   NUMBER(5),
  SPECIF_PR_ADV   NUMBER(2),
  SPECIF_DAY      CHAR(2),
  SPECIF_SHTR     NUMBER(6,2),
  SPECIF_DOPL     DATE,
  SPECIF_DOTGR    DATE,
  SPECIF_ISP      NUMBER(5),
  SPECIF_DAT_ED   DATE,
  TPACK_ID        NUMBER(3),
  SPECIF_OPERABLE NUMBER(1),
  DELIV_ID        NUMBER(3)
)

create table PROVISO
(
  PROV_ID     NUMBER(7) not null,
  DOG_ID      NUMBER(7) not null,
  RASCH_ID    NUMBER(2),
  PROV_CO_DAY NUMBER(5),
  PROV_DAY    CHAR(2),
  PROV_PR_ADV NUMBER(2),
  PROV_DOTGR  DATE,
  PROV_DOPL   DATE,
  PROV_ISP    NUMBER(5),
  PROV_DREG   DATE,
  PROV_DEDIT  DATE
)

create table P_RASCH
(
  RASCH_ID   NUMBER(2) not null,
  RASCH_NAIM VARCHAR2(40) not null,
  RASCH_IMP  NUMBER(1) default 0,
  RASCH_EXP  NUMBER(1) default 0
)

create table VID_DOC
(
  VDOC_ID   NUMBER(2) not null,
  VDOC_NAIM VARCHAR2(25) not null
)

create table POLZ_02015
(
  POLZ_ID   NUMBER(5) not null,
  POLZ_NAME VARCHAR2(50) not null,
  POLZ_FUN  NUMBER(2) default 0 not null,
  DEP_ID    NUMBER(4) not null,
  POLZ_ADM  NUMBER(1) default 0 not null,
  ID_USER   NUMBER(5),
  POLZ_EXIT NUMBER(1) default 0,
  POLZ_EXP  NUMBER(1) default 0,
  POLZ_URB  NUMBER(1) default 0
)

create table PACKING
(
  PACK_ID       NUMBER(7) not null,
  DOG_ID        NUMBER(7) not null,
  SPECIF_ID     NUMBER(7) not null,
  KOD_GROUP     CHAR(3) not null,
  KOD_SUBGR     CHAR(6) not null,
  KODM          CHAR(15) not null,
  VES_UNIT      NUMBER(10,6) not null,
  KOL_UNIT      NUMBER(10) not null,
  PACK_ISP      NUMBER(5) not null,
  PACK_DAT_REG  DATE not null,
  PACK_DAT_ED   DATE,
  PACK_OPERABLE NUMBER(1),
  NAIM_ID       NUMBER(8)
)

create table PACK_TYPE
(
  TPACK_ID   NUMBER(3) not null,
  TPACK_NAME VARCHAR2(45) not null
)
