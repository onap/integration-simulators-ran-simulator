CREATE USER 'uroot'@'%' IDENTIFIED BY 'psecret';
CREATE SCHEMA IF NOT EXISTS `ransim_db` DEFAULT CHARACTER SET utf8;
USE `ransim_db`;
grant all privileges on ransim_db.* TO 'uroot'@'%' identified by 'psecret';
flush privileges;

drop table if exists nearrtric;
drop table if exists gnbcucpfunction;
drop table if exists gnbcuupfunction;
drop table if exists gnbdufunction;
drop table if exists nrcellcu;
drop table if exists nrcellrelation;
drop table if exists nrcelldu;
drop table if exists raninventory;
drop table if exists rrmpolicyratio;
drop table if exists rrmpolicymember;
drop table if exists tacells;
drop table if exists sliceprofile;
drop table if exists plmninfo;
drop table if exists trackingarea;
drop table if exists rannfnssi;
drop table if exists nssai;
drop table if exists rannssi;

-- -----------------------------------------------------
-- Table `ransim_db`.`cell`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ransim_db`.`cell`(
  `cell_id` varchar(45) NOT NULL,
  `last_modifiedts` datetime(6) NOT NULL,
  `location` varchar(200) DEFAULT NULL,
  `network_id` varchar(45) NOT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `pci_value` bigint(20) NOT NULL,
  `pnf_id` varchar(255) NOT NULL,
  PRIMARY KEY (`cell_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;
-- -----------------------------------------------------
-- Table `ransim_db`.`cell_nbr_info`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ransim_db`.`cell_nbr_info`(
  `cell_id` varchar(45) NOT NULL,
  `target_cell_id` varchar(45) NOT NULL,
  `ho` bit(1) NOT NULL,
  PRIMARY KEY (`cell_id`, `target_cell_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;
-- Guilin - Slicing Usecase
-- -----------------------------------------------------
-- Table `ransim_db`.`nearrtric`
-- -----------------------------------------------------
create table nearrtric (
  nearrtricid integer not null,
  gnbid integer,
  resourcetype varchar(255),
  primary key (nearrtricid)
) engine = InnoDB;
-- -----------------------------------------------------
-- Table `ransim_db`.`trackingarea`
-- -----------------------------------------------------
create table trackingarea (
  nearrtricid integer not null,
  tracking_area integer
) engine = InnoDB;
-- -----------------------------------------------------
-- Table `ransim_db`.`rannfnssi`
-- -----------------------------------------------------
create table rannfnssi (
  nearrtricid integer not null,
  rannfnssilist varchar(255)
) engine = InnoDB;
-- -----------------------------------------------------
-- Table `ransim_db`.`nssai`
-- -----------------------------------------------------
create table nssai (
  rannfnssiid varchar(255) not null,
  nssailist varchar(255)
) engine = InnoDB;
-- -----------------------------------------------------
-- Table `ransim_db`.`gnbcucpfunction`
-- -----------------------------------------------------
create table gnbcucpfunction (
  gnbcuname varchar(255) not null,
  gnbid integer,
  gnbidlength integer,
  nftype varchar(255),
  plmnid varchar(255),
  nearrtricid integer,
  primary key (gnbcuname)
) engine = InnoDB;
-- -----------------------------------------------------
-- Table `ransim_db`.`gnbcuupfunction`
-- -----------------------------------------------------
create table gnbcuupfunction (
  gnbcuupid varchar(255) not null,
  gnbid integer,
  gnbidlength integer,
  resourcetype varchar(255),
  nearrtricid integer,
  primary key (gnbcuupid)
) engine = InnoDB;
-- -----------------------------------------------------
-- Table `ransim_db`.`gnbdufunction`
-- -----------------------------------------------------
create table gnbdufunction (
  gnbduid integer not null,
  gnbduname varchar(255),
  gnbid integer,
  gnbidlength integer,
  nftype varchar(255),
  plmnid varchar(255),
  nearrtricid integer,
  primary key (gnbduid)
) engine = InnoDB;
-- -----------------------------------------------------
-- Table `ransim_db`.`nrcellcu`
-- -----------------------------------------------------
create table nrcellcu (
  celllocalid integer not null,
  gnbcuname varchar(255),
  resourcetype varchar(255),
  primary key (celllocalid)
) engine = InnoDB;
-- -----------------------------------------------------
-- Table `ransim_db`.`nrcelldu`
-- -----------------------------------------------------
create table nrcelldu (
  celllocalid integer not null,
  administrativestate varchar(255),
  resourcetype varchar(255),
  cellstate varchar(255),
  nrpci integer,
  nrtac integer,
  operationalstate varchar(255),
  gnbduid integer,
  prb integer,
  latitude numeric(17,15),
  longitude numeric(17,15),
  network_id varchar(255),
  primary key (celllocalid)
) engine = InnoDB;

-- -----------------------------------------------------
-- Table `ransim_db`.`nrcellrelation`
-- -----------------------------------------------------
create table nrcellrelation (
  sno integer not null,
  idnrcellrelation integer not null,
  nrtci integer,
  ishoallowed BOOLEAN,
  celllocalid integer not null,
  primary key (sno)
) engine = InnoDB;

-- -----------------------------------------------------
-- Table `ransim_db`.`raninventory`
-- -----------------------------------------------------
create table raninventory (
  rannfnssiid varchar(255) not null,
  isshareable varchar(255),
  nsstid varchar(255),
  slicetype varchar(255),
  subnetstatus varchar(255),
  talist varchar(500),
  primary key (rannfnssiid)
) engine = InnoDB;
-- -----------------------------------------------------
-- Table `ransim_db`.`rannssi`
-- -----------------------------------------------------
create table rannssi (
  rannfnssiid varchar(255) not null,
  rannssilist varchar(255)
) engine = InnoDB;
-- -----------------------------------------------------
-- Table `ransim_db`.`rrmpolicyratio`
-- -----------------------------------------------------
create table rrmpolicyratio (
  rrmpolicyid varchar(255) not null,
  quotatype varchar(255),
  rrmpolicydedicatedratio integer,
  rrmpolicymaxratio integer,
  rrmpolicyminratio integer,
  resourceid varchar(255),
  resourcetype varchar(255),
  slicetype varchar(255),
  primary key (rrmpolicyid)
) engine = InnoDB;
-- -----------------------------------------------------
-- Table `ransim_db`.`rrmpolicymember`
-- -----------------------------------------------------
create table rrmpolicymember (
  plmnid varchar(255) not null,
  snssai varchar(255) not null,
  rrm_policy_rrmpolicyid integer,
  primary key (plmnid, snssai)
) engine = InnoDB;
-- -----------------------------------------------------
-- Table `ransim_db`.`tacells`
-- -----------------------------------------------------
create table tacells (
  trackingarea integer not null,
  cells varchar(255),
  primary key (trackingarea)
) engine = InnoDB;
-- -----------------------------------------------------
-- Table `ransim_db`.`plmninfo`
-- -----------------------------------------------------
create table plmninfo (
  plmnid varchar(255) not null,
  globalSubscriberId varchar(255),
  snssai varchar(255),
  status varchar(255),
  subscriptionServiceType varchar(255),
  gnbcuupid varchar(255),
  nrcellcu_celllocalid integer,
  nrcelldu_celllocalid integer,
  nearrtricid integer,
  uLThptPerSlice integer,
  dLThptPerSlice integer,
  maxNumberOfConns integer,
  lastUpdatedTS TIMESTAMP DEFAULT CURRENT_TIMESTAMP()
) engine = InnoDB;
-- -----------------------------------------------------
-- Table `ransim_db`.`sliceprofile`
-- -----------------------------------------------------
create table sliceprofile (
  sliceprofileid varchar(255) not null,
  coveragearealist varchar(255),
  ulthptperslice integer,
  dlthptperslice integer,
  latency integer,
  maxnumberofconns integer,
  maxnoofues integer,
  plmnidlist varchar(255),
  resourcesharinglevel varchar(255),
  snssai varchar(255),
  uemobilitylevel varchar(255),
  rannfnssiid varchar(255),
  primary key (sliceprofileid)
) engine = InnoDB;

-- -----------------------------------------------------
--Pre-loaded data
-- -----------------------------------------------------
-- tacells

INSERT INTO tacells(trackingarea,cells) VALUES(101, '15289,15290,15296,15687,15689,15155,15174,15175');
INSERT INTO tacells(trackingarea,cells) VALUES(202, '15176,15825,15826,15425,15426,13999,14000');
INSERT INTO tacells(trackingarea,cells) VALUES(303, '11561,11562,11568,11569,10896,10897,14655,14656');
INSERT INTO tacells(trackingarea,cells) VALUES(404, '13905,13910,15360,15361,15548,15549,14427');

INSERT INTO tacells(trackingarea,cells) VALUES(505, '10896,10897,13905,13910');
INSERT INTO tacells(trackingarea,cells) VALUES(606, '15360,15361,15548,15549');
INSERT INTO tacells(trackingarea,cells) VALUES(707, '15425,15426,15427');
INSERT INTO tacells(trackingarea,cells) VALUES(808, '15687,15689');
INSERT INTO tacells(trackingarea,cells) VALUES(909, '14655,14656');

--nearrtric
INSERT INTO nearrtric (nearrtricid, gnbid, resourcetype) VALUES(11, 25, 'NearRTRIC');
INSERT INTO nearrtric (nearrtricid, gnbid, resourcetype) VALUES(22, 25, 'NearRTRIC');
--gnbcucpfunction
INSERT INTO gnbcucpfunction(gnbcuname, gnbid, gnbidlength, nftype,plmnid,nearrtricid) VALUES('cucpserver1', 25, 25, 'CUCP', '310-410', 11);
INSERT INTO gnbcucpfunction(gnbcuname, gnbid, gnbidlength, nftype,plmnid,nearrtricid) VALUES('cucpserver2', 25, 25, 'CUCP', '310-410',22);

--gnbcuupfunction
INSERT INTO gnbcuupfunction(gnbcuupid, gnbid, gnbidlength, resourcetype, nearrtricid) VALUES(1111, 25, 25, 'DRB', 11);
INSERT INTO gnbcuupfunction(gnbcuupid, gnbid, gnbidlength, resourcetype, nearrtricid) VALUES(2222, 25, 25, 'DRB', 22);

--gnbdufunction
INSERT INTO gnbdufunction(gnbduid,gnbduname, gnbid, gnbidlength, nftype, plmnid, nearrtricid) VALUES(110,'gnduserver1', 25, 25, 'DU', '310-410', 11);

INSERT INTO gnbdufunction(gnbduid,gnbduname, gnbid, gnbidlength, nftype, plmnid, nearrtricid) VALUES(220,'gnduserver2', 25, 25, 'DU', '310-410', 11);

INSERT INTO gnbdufunction(gnbduid,gnbduname, gnbid, gnbidlength, nftype, plmnid, nearrtricid) VALUES(330,'gnduserver3', 25, 25, 'DU', '310-410', 11);

INSERT INTO gnbdufunction(gnbduid,gnbduname, gnbid, gnbidlength, nftype, plmnid, nearrtricid) VALUES(440,'gnduserver4', 25, 25, 'DU', '310-410', 22);

INSERT INTO gnbdufunction(gnbduid,gnbduname, gnbid, gnbidlength, nftype, plmnid, nearrtricid) VALUES(550,'gnduserver5', 25, 25, 'DU', '310-410', 22);

INSERT INTO gnbdufunction(gnbduid,gnbduname, gnbid, gnbidlength, nftype, plmnid, nearrtricid) VALUES(660,'gnduserver6', 25, 25, 'DU', '310-410', 22);

--nrcellcu
INSERT INTO nrcellcu(celllocalid,resourcetype,gnbcuname) VALUES(15289,'RRC Connected Users','cucpserver1');

INSERT INTO nrcellcu(celllocalid,resourcetype,gnbcuname) VALUES(15290,'RRC Connected Users','cucpserver1');

INSERT INTO nrcellcu(celllocalid,resourcetype,gnbcuname) VALUES(15296,'RRC Connected Users','cucpserver1');

INSERT INTO nrcellcu(celllocalid,resourcetype,gnbcuname) VALUES(15687,'RRC Connected Users','cucpserver1');

INSERT INTO nrcellcu(celllocalid,resourcetype,gnbcuname) VALUES(15689,'RRC Connected Users','cucpserver1');

INSERT INTO nrcellcu(celllocalid,resourcetype,gnbcuname) VALUES(15155,'RRC Connected Users','cucpserver1');

INSERT INTO nrcellcu(celllocalid,resourcetype,gnbcuname) VALUES(15174,'RRC Connected Users','cucpserver1');

INSERT INTO nrcellcu(celllocalid,resourcetype,gnbcuname) VALUES(15175,'RRC Connected Users','cucpserver1');

INSERT INTO nrcellcu(celllocalid,resourcetype,gnbcuname) VALUES(15176,'RRC Connected Users','cucpserver1');

INSERT INTO nrcellcu(celllocalid,resourcetype,gnbcuname) VALUES(15825,'RRC Connected Users','cucpserver1');

INSERT INTO nrcellcu(celllocalid,resourcetype,gnbcuname) VALUES(15826,'RRC Connected Users','cucpserver1');

INSERT INTO nrcellcu(celllocalid,resourcetype,gnbcuname) VALUES(15425,'RRC Connected Users','cucpserver1');

INSERT INTO nrcellcu(celllocalid,resourcetype,gnbcuname) VALUES(15426,'RRC Connected Users','cucpserver1');

INSERT INTO nrcellcu(celllocalid,resourcetype,gnbcuname) VALUES(13999,'RRC Connected Users','cucpserver1');

INSERT INTO nrcellcu(celllocalid,resourcetype,gnbcuname) VALUES(14000,'RRC Connected Users','cucpserver1');

INSERT INTO nrcellcu(celllocalid,resourcetype,gnbcuname) VALUES(11561,'RRC Connected Users','cucpserver2');

INSERT INTO nrcellcu(celllocalid,resourcetype,gnbcuname) VALUES(11562,'RRC Connected Users','cucpserver2');

INSERT INTO nrcellcu(celllocalid,resourcetype,gnbcuname) VALUES(11568,'RRC Connected Users','cucpserver2');

INSERT INTO nrcellcu(celllocalid,resourcetype,gnbcuname) VALUES(11569,'RRC Connected Users','cucpserver2');

INSERT INTO nrcellcu(celllocalid,resourcetype,gnbcuname) VALUES(10896,'RRC Connected Users','cucpserver2');

INSERT INTO nrcellcu(celllocalid,resourcetype,gnbcuname) VALUES(10897,'RRC Connected Users','cucpserver2');

INSERT INTO nrcellcu(celllocalid,resourcetype,gnbcuname) VALUES(14655,'RRC Connected Users','cucpserver2');

INSERT INTO nrcellcu(celllocalid,resourcetype,gnbcuname) VALUES(14656,'RRC Connected Users','cucpserver2');

INSERT INTO nrcellcu(celllocalid,resourcetype,gnbcuname) VALUES(13905,'RRC Connected Users','cucpserver2');

INSERT INTO nrcellcu(celllocalid,resourcetype,gnbcuname) VALUES(13910,'RRC Connected Users','cucpserver2');

INSERT INTO nrcellcu(celllocalid,resourcetype,gnbcuname) VALUES(15360,'RRC Connected Users','cucpserver2');

INSERT INTO nrcellcu(celllocalid,resourcetype,gnbcuname) VALUES(15361,'RRC Connected Users','cucpserver2');

INSERT INTO nrcellcu(celllocalid,resourcetype,gnbcuname) VALUES(15548,'RRC Connected Users','cucpserver2');

INSERT INTO nrcellcu(celllocalid,resourcetype,gnbcuname) VALUES(15549,'RRC Connected Users','cucpserver2');

INSERT INTO nrcellcu(celllocalid,resourcetype,gnbcuname) VALUES(14427,'RRC Connected Users','cucpserver2');

--nrcellrelation

INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(0,15290,15290,true,15289);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(1,15296,15296,true,15289);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(2,15687,15687,true,15289);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(3,15687,15687,true,15290);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(4,15289,15289,true,15290);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(5,15296,15296,true,15290);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(6,15687,15687,true,15296);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(7,15289,15289,true,15296);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(8,15290,15290,true,15296);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(9,15289,15289,true,15687);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(10,15296,15296,true,15687);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(11,15290,15290,true,15687);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(12,15175,15175,true,15174);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(13,15176,15176,true,15174);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(14,15155,15155,true,15174);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(15,15174,15174,true,15175);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(16,15176,15176,true,15175);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(17,15155,15155,true,15175);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(18,15174,15174,true,15176);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(19,15175,15175,true,15176);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(20,15155,15155,true,15176);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(21,15174,15174,true,15155);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(22,15175,15175,true,15155);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(23,15176,15176,true,15155);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(24,15826,15826,true,15825);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(25,15689,15689,true,15825);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(26,13999,13999,true,15825);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(27,15825,15825,true,15826);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(28,13999,13999,true,15826);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(29,15689,15689,true,15826);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(30,15825,15825,true,15689);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(31,15826,15826,true,15689);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(32,13999,13999,true,15689);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(33,15825,15825,true,13999);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(34,15826,15826,true,13999);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(35,15689,15689,true,13999);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(36,15425,15425,true,14000);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(37,15426,15426,true,14000);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(38,14000,14000,true,15425);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(39,15426,15426,true,15425);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(40,15425,15425,true,15426);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(41,14000,14000,true,15426);

INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(42,11562,11562,true,11561);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(43,11568,11568,true,11561);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(44,11569,11569,true,11561);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(45,11561,11561,true,11562);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(46,11568,11568,true,11562);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(47,11569,11569,true,11562);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(48,11562,11562,true,11568);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(49,11561,11561,true,11568);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(50,11569,11569,true,11568);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(51,11561,11561,true,11569);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(52,11562,11562,true,11569);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(53,11568,11568,true,11569);

INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(54,10897,10897,true,10896);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(55,14655,14655,true,10896);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(56,14656,14656,true,10896);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(57,10896,10896,true,10897);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(58,14655,14655,true,10897);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(59,14656,14656,true,10897);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(60,10896,10896,true,14655);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(61,10897,10897,true,14655);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(62,14656,14656,true,14655);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(63,10896,10896,true,14656);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(64,10897,10897,true,14656);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(65,14655,14655,true,14656);

INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(66,13910,13910,true,13905);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(67,15360,15360,true,13905);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(68,15361,15361,true,13905);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(69,13905,13905,true,13910);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(70,15360,15360,true,13910);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(71,15361,15361,true,13910);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(72,13905,13905,true,15360);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(73,13910,13910,true,15360);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(74,15361,15361,true,15360);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(75,13905,13905,true,15361);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(76,13910,13910,true,15361);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(77,15360,15360,true,15361);

INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(78,15549,15549,true,15548);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(79,14427,14427,true,15548);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(80,15548,15548,true,15549);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(81,14427,14427,true,15549);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(82,15548,15548,true,14427);
INSERT INTO nrcellrelation(sno,idnrcellrelation,nrtci,ishoallowed,celllocalid) VALUES(83,15549,15549,true,14427);
	
--nrcelldu

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, latitude, longitude, network_id, operationalstate,gnbduid) VALUES(15289,'PRB','UNLOCKED','ACTIVE',0,310,21.478915984298443,-51.40392800806856,'RAN001','ENABLED',110);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, latitude, longitude, network_id, operationalstate,gnbduid) VALUES(15290,'PRB','UNLOCKED','ACTIVE',1,310,21.478915984298443,-51.40392800806856,'RAN001','ENABLED',110);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, latitude, longitude, network_id, operationalstate,gnbduid) VALUES(15296,'PRB','UNLOCKED','ACTIVE',2,310,21.478915984298443,-51.40392800806856,'RAN001','ENABLED',110);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, latitude, longitude, network_id, operationalstate,gnbduid) VALUES(15687,'PRB','UNLOCKED','ACTIVE',3,310,21.478915984298443,-51.40392800806856,'RAN001','ENABLED',110);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, latitude, longitude, network_id, operationalstate,gnbduid) VALUES(15689,'PRB','UNLOCKED','ACTIVE',4,310,21.478915984298443,-51.40392800806856,'RAN001','ENABLED',110);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, latitude, longitude, network_id, operationalstate,gnbduid) VALUES(15155,'PRB','UNLOCKED','ACTIVE',0,310,21.478915984298443,-51.40392800806856,'RAN001','ENABLED',220);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, latitude, longitude, network_id, operationalstate,gnbduid) VALUES(15174,'PRB','UNLOCKED','ACTIVE',1,310,21.478915984298443,-51.40392800806856,'RAN001','ENABLED',220);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, latitude, longitude, network_id, operationalstate,gnbduid) VALUES(15175,'PRB','UNLOCKED','ACTIVE',2,310,21.478915984298443,-51.40392800806856,'RAN001','ENABLED',220);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, latitude, longitude, network_id, operationalstate,gnbduid) VALUES(15176,'PRB','UNLOCKED','ACTIVE',3,310,21.478915984298443,-51.40392800806856,'RAN001','ENABLED',220);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, latitude, longitude, network_id, operationalstate,gnbduid) VALUES(15825,'PRB','UNLOCKED','ACTIVE',4,310,21.478915984298443,-51.40392800806856,'RAN001','ENABLED',220);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, latitude, longitude, network_id, operationalstate,gnbduid) VALUES(15826,'PRB','UNLOCKED','ACTIVE',0,310,21.478915984298443,-51.40392800806856,'RAN001','ENABLED',330);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, latitude, longitude, network_id, operationalstate,gnbduid) VALUES(15425,'PRB','UNLOCKED','ACTIVE',1,310,21.478915984298443,-51.40392800806856,'RAN001','ENABLED',330);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, latitude, longitude, network_id, operationalstate,gnbduid) VALUES(15426,'PRB','UNLOCKED','ACTIVE',2,310,21.478915984298443,-51.40392800806856,'RAN001','ENABLED',330);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, latitude, longitude, network_id, operationalstate,gnbduid) VALUES(13999,'PRB','UNLOCKED','ACTIVE',3,310,21.478915984298443,-51.40392800806856,'RAN001','ENABLED',330);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, latitude, longitude, network_id, operationalstate,gnbduid) VALUES(14000,'PRB','UNLOCKED','ACTIVE',4,310,21.478915984298443,-51.40392800806856,'RAN001','ENABLED',330);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, latitude, longitude, network_id, operationalstate,gnbduid) VALUES(11561,'PRB','UNLOCKED','ACTIVE',0,310,21.478915984298443,-51.40392800806856,'RAN001','ENABLED',440);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, latitude, longitude, network_id, operationalstate,gnbduid) VALUES(11562,'PRB','UNLOCKED','ACTIVE',1,310,21.478915984298443,-51.40392800806856,'RAN001','ENABLED',440);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, latitude, longitude, network_id, operationalstate,gnbduid) VALUES(11568,'PRB','UNLOCKED','ACTIVE',2,310,21.478915984298443,-51.40392800806856,'RAN001','ENABLED',440);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, latitude, longitude, network_id, operationalstate,gnbduid) VALUES(11569,'PRB','UNLOCKED','ACTIVE',3,310,21.478915984298443,-51.40392800806856,'RAN001','ENABLED',440);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, latitude, longitude, network_id, operationalstate,gnbduid) VALUES(10896,'PRB','UNLOCKED','ACTIVE',4,310,21.478915984298443,-51.40392800806856,'RAN001','ENABLED',440);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, latitude, longitude, network_id, operationalstate,gnbduid) VALUES(10897,'PRB','UNLOCKED','ACTIVE',0,310,21.478915984298443,-51.40392800806856,'RAN001','ENABLED',550);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, latitude, longitude, network_id, operationalstate,gnbduid) VALUES(14655,'PRB','UNLOCKED','ACTIVE',1,310,21.478915984298443,-51.40392800806856,'RAN001','ENABLED',550);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, latitude, longitude, network_id, operationalstate,gnbduid) VALUES(14656,'PRB','UNLOCKED','ACTIVE',2,310,21.478915984298443,-51.40392800806856,'RAN001','ENABLED',550);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, latitude, longitude, network_id, operationalstate,gnbduid) VALUES(13905,'PRB','UNLOCKED','ACTIVE',3,310,21.478915984298443,-51.40392800806856,'RAN001','ENABLED',550);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, latitude, longitude, network_id, operationalstate,gnbduid) VALUES(13910,'PRB','UNLOCKED','ACTIVE',4,310,21.478915984298443,-51.40392800806856,'RAN001','ENABLED',550);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, latitude, longitude, network_id, operationalstate,gnbduid) VALUES(15360,'PRB','UNLOCKED','ACTIVE',0,310,21.478915984298443,-51.40392800806856,'RAN001','ENABLED',660);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, latitude, longitude, network_id, operationalstate,gnbduid) VALUES(15361,'PRB','UNLOCKED','ACTIVE',1,310,21.478915984298443,-51.40392800806856,'RAN001','ENABLED',660);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, latitude, longitude, network_id, operationalstate,gnbduid) VALUES(15548,'PRB','UNLOCKED','ACTIVE',2,310,21.478915984298443,-51.40392800806856,'RAN001','ENABLED',660);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, latitude, longitude, network_id, operationalstate,gnbduid) VALUES(15549,'PRB','UNLOCKED','ACTIVE',3,310,21.478915984298443,-51.40392800806856,'RAN001','ENABLED',660);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, latitude, longitude, network_id, operationalstate,gnbduid) VALUES(14427,'PRB','UNLOCKED','ACTIVE',4,310,21.478915984298443,-51.40392800806856,'RAN001','ENABLED',660);

UPDATE nrcelldu SET prb=500;
