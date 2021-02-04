CREATE USER 'uroot'@'%' IDENTIFIED BY 'psecret';
CREATE SCHEMA IF NOT EXISTS `ransim_db` DEFAULT CHARACTER SET utf8 ;
USE `ransim_db` ;
grant all privileges on ransim_db.* TO 'uroot'@'%' identified by 'psecret';
flush privileges;
drop table if exists nearrtric;
drop table if exists gnbcucpfunction;
drop table if exists gnbcuupfunction;
drop table if exists gnbdufunction;
drop table if exists nrcellcu;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- -----------------------------------------------------
-- Table `ransim_db`.`cell_nbr_info`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ransim_db`.`cell_nbr_info`(
  `cell_id` varchar(45) NOT NULL,
  `target_cell_id` varchar(45) NOT NULL,
  `ho` bit(1) NOT NULL,
  PRIMARY KEY (`cell_id`,`target_cell_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- Guilin - Slicing Usecase
-- -----------------------------------------------------
-- Table `ransim_db`.`nearrtric`
-- -----------------------------------------------------
create table nearrtric (
       nearrtricid integer not null,
        gnbid integer,
        resourcetype varchar(255),
        primary key (nearrtricid)
    ) engine=InnoDB;
-- -----------------------------------------------------
-- Table `ransim_db`.`trackingarea`
-- -----------------------------------------------------
create table trackingarea (
       nearrtricid integer not null,
        tracking_area varchar(255)
    ) engine=InnoDB;
-- -----------------------------------------------------
-- Table `ransim_db`.`rannfnssi`
-- -----------------------------------------------------
 create table rannfnssi (
       nearrtricid integer not null,
        rannfnssilist varchar(255)
    ) engine=InnoDB;
-- -----------------------------------------------------
-- Table `ransim_db`.`nssai`
-- -----------------------------------------------------
create table nssai (
       rannfnssiid varchar(255) not null,
        nssailist varchar(255)
    ) engine=InnoDB;
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
    ) engine=InnoDB;
-- -----------------------------------------------------
-- Table `ransim_db`.`gnbcuupfunction`
-- -----------------------------------------------------
create table gnbcuupfunction (
	   gnbcuupid integer not null,
        gnbid integer,
        gnbidlength integer,
        resourcetype varchar(255),
        nearrtricid integer,
        primary key (gnbcuupid)
    ) engine=InnoDB;
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
    ) engine=InnoDB;
-- -----------------------------------------------------
-- Table `ransim_db`.`nrcellcu`
-- -----------------------------------------------------
create table nrcellcu (
       celllocalid integer not null,
        gnbcuname varchar(255),
	   resourcetype varchar(255),
        primary key (celllocalid)
    ) engine=InnoDB;
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
        primary key (celllocalid)
    ) engine=InnoDB;
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
    ) engine=InnoDB;
-- -----------------------------------------------------
-- Table `ransim_db`.`rannssi`
-- -----------------------------------------------------
create table rannssi (
       rannfnssiid varchar(255) not null,
       rannssilist varchar(255)
    ) engine=InnoDB;
-- -----------------------------------------------------
-- Table `ransim_db`.`rrmpolicyratio`
-- -----------------------------------------------------
create table rrmpolicyratio (
       rrmpolicyid integer not null,
        quotatype varchar(255),
        rrmpolicydedicatedratio integer,
        rrmpolicymaxratio integer,
        rrmpolicyminratio integer,
        resourceid varchar(255),
        resourcetype varchar(255),
        slicetype varchar(255),
        primary key (rrmpolicyid)
    ) engine=InnoDB;
-- -----------------------------------------------------
-- Table `ransim_db`.`rrmpolicymember`
-- -----------------------------------------------------
create table rrmpolicymember (
       plmnid varchar(255) not null,
        snssai varchar(255) not null,
        rrm_policy_rrmpolicyid integer,
        primary key (plmnid, snssai)
    ) engine=InnoDB;
-- -----------------------------------------------------
-- Table `ransim_db`.`tacells`
-- -----------------------------------------------------
create table tacells (
       trackingarea varchar(255) not null,
        cells varchar(255),
        primary key (trackingarea)
    ) engine=InnoDB;
-- -----------------------------------------------------
-- Table `ransim_db`.`plmninfo`
-- -----------------------------------------------------
create table plmninfo (
       plmnid varchar(255) not null,
        globalSubscriberId varchar(255),
        snssai varchar(255),
        status varchar(255),
        subscriptionServiceType varchar(255),
        gnbcuupid integer,
        nrcellcu_celllocalid integer,
        nrcelldu_celllocalid integer,
        nearrtricid integer,
           uLThptPerSlice integer,
           dLThptPerSlice integer,
           maxNumberOfConns integer,
        lastUpdatedTS TIMESTAMP DEFAULT CURRENT_TIMESTAMP()
    ) engine=InnoDB;
-- -----------------------------------------------------
-- Table `ransim_db`.`sliceprofile`
-- -----------------------------------------------------
create table sliceprofile (
       sliceprofileid varchar(255) not null,
        coveragearealist varchar(255),
        ulthptperslice  integer,
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
    ) engine=InnoDB;
-- -----------------------------------------------------
--Pre-loaded data
-- -----------------------------------------------------
-- tacells

INSERT INTO tacells(trackingarea,cells) VALUES('TA1', '15289,15290,15296,15687,15689,15155,15174,15175');
INSERT INTO tacells(trackingarea,cells) VALUES('TA2', '15176,15825,15826,15425,15426,13999,14000');
INSERT INTO tacells(trackingarea,cells) VALUES('TA3', '11561,11562,11568,11569,10896,10897,14655,14656');
INSERT INTO tacells(trackingarea,cells) VALUES('TA4', '13905,13910,15360,15361,15548,15549,14427');

--nearrtric
INSERT INTO nearrtric (nearrtricid, gnbid, resourcetype) VALUES(11, 98763, 'NearRTRIC');
INSERT INTO nearrtric (nearrtricid, gnbid, resourcetype) VALUES(22, 98763, 'NearRTRIC');
--gnbcucpfunction
INSERT INTO gnbcucpfunction(gnbcuname, gnbid, gnbidlength, nftype,plmnid,nearrtricid) VALUES('cucpserver1', 98763, 32, 'CUCP', '310-410', 11);
INSERT INTO gnbcucpfunction(gnbcuname, gnbid, gnbidlength, nftype,plmnid,nearrtricid) VALUES('cucpserver2', 98763, 32, 'CUCP', '310-410',22);
--gnbcuupfunction
INSERT INTO gnbcuupfunction(gnbcuupid, gnbid, gnbidlength, resourcetype, nearrtricid) VALUES(1111, 98763, 32, 'DRB', 11);
INSERT INTO gnbcuupfunction(gnbcuupid, gnbid, gnbidlength, resourcetype, nearrtricid) VALUES(2222, 98763, 32, 'DRB', 22);
--gnbdufunction
INSERT INTO gnbdufunction(gnbduid,gnbduname, gnbid, gnbidlength, nftype, plmnid, nearrtricid) VALUES(1,'gnduserver1', 98763, 32, 'DU', '310-410', 11);
INSERT INTO gnbdufunction(gnbduid,gnbduname, gnbid, gnbidlength, nftype, plmnid, nearrtricid) VALUES(2,'gnduserver2', 98763, 32, 'DU', '310-410', 11);
INSERT INTO gnbdufunction(gnbduid,gnbduname, gnbid, gnbidlength, nftype, plmnid, nearrtricid) VALUES(3,'gnduserver3', 98763, 32, 'DU', '310-410', 11);
INSERT INTO gnbdufunction(gnbduid,gnbduname, gnbid, gnbidlength, nftype, plmnid, nearrtricid) VALUES(4,'gnduserver4', 98763, 32, 'DU', '310-410', 22);
INSERT INTO gnbdufunction(gnbduid,gnbduname, gnbid, gnbidlength, nftype, plmnid, nearrtricid) VALUES(5,'gnduserver5', 98763, 32, 'DU', '310-410', 22);
INSERT INTO gnbdufunction(gnbduid,gnbduname, gnbid, gnbidlength, nftype, plmnid, nearrtricid) VALUES(6,'gnduserver6', 98763, 32, 'DU', '310-410', 22);
--nrcellcu
INSERT INTO nrcellcu(celllocalId,resourcetype,gnbcuname) VALUES(15289,'RRC Connected Users','cucpserver1');

INSERT INTO nrcellcu(celllocalId,resourcetype,gnbcuname) VALUES(15290,'RRC Connected Users','cucpserver1');

INSERT INTO nrcellcu(celllocalId,resourcetype,gnbcuname) VALUES(15296,'RRC Connected Users','cucpserver1');

INSERT INTO nrcellcu(celllocalId,resourcetype,gnbcuname) VALUES(15687,'RRC Connected Users','cucpserver1');

INSERT INTO nrcellcu(celllocalId,resourcetype,gnbcuname) VALUES(15689,'RRC Connected Users','cucpserver1');

INSERT INTO nrcellcu(celllocalId,resourcetype,gnbcuname) VALUES(15155,'RRC Connected Users','cucpserver1');

INSERT INTO nrcellcu(celllocalId,resourcetype,gnbcuname) VALUES(15174,'RRC Connected Users','cucpserver1');

INSERT INTO nrcellcu(celllocalId,resourcetype,gnbcuname) VALUES(15175,'RRC Connected Users','cucpserver1');

INSERT INTO nrcellcu(celllocalId,resourcetype,gnbcuname) VALUES(15176,'RRC Connected Users','cucpserver1');

INSERT INTO nrcellcu(celllocalId,resourcetype,gnbcuname) VALUES(15825,'RRC Connected Users','cucpserver1');

INSERT INTO nrcellcu(celllocalId,resourcetype,gnbcuname) VALUES(15826,'RRC Connected Users','cucpserver1');

INSERT INTO nrcellcu(celllocalId,resourcetype,gnbcuname) VALUES(15425,'RRC Connected Users','cucpserver1');

INSERT INTO nrcellcu(celllocalId,resourcetype,gnbcuname) VALUES(15426,'RRC Connected Users','cucpserver1');

INSERT INTO nrcellcu(celllocalId,resourcetype,gnbcuname) VALUES(13999,'RRC Connected Users','cucpserver1');

INSERT INTO nrcellcu(celllocalId,resourcetype,gnbcuname) VALUES(14000,'RRC Connected Users','cucpserver1'); 

INSERT INTO nrcellcu(celllocalId,resourcetype,gnbcuname) VALUES(11561,'RRC Connected Users','cucpserver2');

INSERT INTO nrcellcu(celllocalId,resourcetype,gnbcuname) VALUES(11562,'RRC Connected Users','cucpserver2');

INSERT INTO nrcellcu(celllocalId,resourcetype,gnbcuname) VALUES(11568,'RRC Connected Users','cucpserver2');

INSERT INTO nrcellcu(celllocalId,resourcetype,gnbcuname) VALUES(11569,'RRC Connected Users','cucpserver2');

INSERT INTO nrcellcu(celllocalId,resourcetype,gnbcuname) VALUES(10896,'RRC Connected Users','cucpserver2');

INSERT INTO nrcellcu(celllocalId,resourcetype,gnbcuname) VALUES(10897,'RRC Connected Users','cucpserver2');

INSERT INTO nrcellcu(celllocalId,resourcetype,gnbcuname) VALUES(14655,'RRC Connected Users','cucpserver2');

INSERT INTO nrcellcu(celllocalId,resourcetype,gnbcuname) VALUES(14656,'RRC Connected Users','cucpserver2');

INSERT INTO nrcellcu(celllocalId,resourcetype,gnbcuname) VALUES(13905,'RRC Connected Users','cucpserver2');

INSERT INTO nrcellcu(celllocalId,resourcetype,gnbcuname) VALUES(13910,'RRC Connected Users','cucpserver2');

INSERT INTO nrcellcu(celllocalId,resourcetype,gnbcuname) VALUES(15360,'RRC Connected Users','cucpserver2');

INSERT INTO nrcellcu(celllocalId,resourcetype,gnbcuname) VALUES(15361,'RRC Connected Users','cucpserver2');

INSERT INTO nrcellcu(celllocalId,resourcetype,gnbcuname) VALUES(15548,'RRC Connected Users','cucpserver2');

INSERT INTO nrcellcu(celllocalId,resourcetype,gnbcuname) VALUES(15549,'RRC Connected Users','cucpserver2');

INSERT INTO nrcellcu(celllocalId,resourcetype,gnbcuname) VALUES(14427,'RRC Connected Users','cucpserver2');

--nrcelldu

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, operationalstate,gnbduid) VALUES(15289,'PRB','UNLOCKED','ACTIVE',12,310,'ENABLED',1);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, operationalstate,gnbduid) VALUES(15290,'PRB','UNLOCKED','ACTIVE',12,310,'ENABLED',1);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, operationalstate,gnbduid) VALUES(15296,'PRB','UNLOCKED','ACTIVE',12,310,'ENABLED',1);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, operationalstate,gnbduid) VALUES(15687,'PRB','UNLOCKED','ACTIVE',12,310,'ENABLED',1);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, operationalstate,gnbduid) VALUES(15689,'PRB','UNLOCKED','ACTIVE',12,310,'ENABLED',1);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, operationalstate,gnbduid) VALUES(15155,'PRB','UNLOCKED','ACTIVE',12,310,'ENABLED',2);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, operationalstate,gnbduid) VALUES(15174,'PRB','UNLOCKED','ACTIVE',12,310,'ENABLED',2);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, operationalstate,gnbduid) VALUES(15175,'PRB','UNLOCKED','ACTIVE',12,310,'ENABLED',2);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, operationalstate,gnbduid) VALUES(15176,'PRB','UNLOCKED','ACTIVE',12,310,'ENABLED',2);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, operationalstate,gnbduid) VALUES(15825,'PRB','UNLOCKED','ACTIVE',12,310,'ENABLED',2);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, operationalstate,gnbduid) VALUES(15826,'PRB','UNLOCKED','ACTIVE',12,310,'ENABLED',3);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, operationalstate,gnbduid) VALUES(15425,'PRB','UNLOCKED','ACTIVE',12,310,'ENABLED',3);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, operationalstate,gnbduid) VALUES(15426,'PRB','UNLOCKED','ACTIVE',12,310,'ENABLED',3);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, operationalstate,gnbduid) VALUES(13999,'PRB','UNLOCKED','ACTIVE',12,310,'ENABLED',3);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, operationalstate,gnbduid) VALUES(14000,'PRB','UNLOCKED','ACTIVE',12,310,'ENABLED',3);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, operationalstate,gnbduid) VALUES(11561,'PRB','UNLOCKED','ACTIVE',12,310,'ENABLED',4);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, operationalstate,gnbduid) VALUES(11562,'PRB','UNLOCKED','ACTIVE',12,310,'ENABLED',4);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, operationalstate,gnbduid) VALUES(11568,'PRB','UNLOCKED','ACTIVE',12,310,'ENABLED',4);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, operationalstate,gnbduid) VALUES(11569,'PRB','UNLOCKED','ACTIVE',12,310,'ENABLED',4);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, operationalstate,gnbduid) VALUES(10896,'PRB','UNLOCKED','ACTIVE',12,310,'ENABLED',4);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, operationalstate,gnbduid) VALUES(10897,'PRB','UNLOCKED','ACTIVE',12,310,'ENABLED',5);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, operationalstate,gnbduid) VALUES(14655,'PRB','UNLOCKED','ACTIVE',12,310,'ENABLED',5);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, operationalstate,gnbduid) VALUES(14656,'PRB','UNLOCKED','ACTIVE',12,310,'ENABLED',5);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, operationalstate,gnbduid) VALUES(13905,'PRB','UNLOCKED','ACTIVE',12,310,'ENABLED',5);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, operationalstate,gnbduid) VALUES(13910,'PRB','UNLOCKED','ACTIVE',12,310,'ENABLED',5);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, operationalstate,gnbduid) VALUES(15360,'PRB','UNLOCKED','ACTIVE',12,310,'ENABLED',6);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, operationalstate,gnbduid) VALUES(15361,'PRB','UNLOCKED','ACTIVE',12,310,'ENABLED',6);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, operationalstate,gnbduid) VALUES(15548,'PRB','UNLOCKED','ACTIVE',12,310,'ENABLED',6);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, operationalstate,gnbduid) VALUES(15549,'PRB','UNLOCKED','ACTIVE',12,310,'ENABLED',6);

INSERT INTO nrcelldu(celllocalId,resourcetype,administrativestate,cellstate, nrpci, nrtac, operationalstate,gnbduid) VALUES(14427,'PRB','UNLOCKED','ACTIVE',12,310,'ENABLED',6);

