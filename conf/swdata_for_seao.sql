CREATE  TABLE `enewe10_hk`.`swdata` (
  `categorie` VARCHAR(255) NOT NULL ,
  `publication` DATETIME NULL ,
  `no` VARCHAR(45) NULL ,
  `organisation` VARCHAR(255) NULL ,
  `statut` VARCHAR(45) NULL ,
  `uri` VARCHAR(255) NULL ,
  `titre` VARCHAR(255) NULL ,
  `type` VARCHAR(255) NULL ,
  `id` INT NULL ,
  `fermeture` DATETIME NULL ,
  `updated_at` DATETIME NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `status_org` (`organisation` ASC, `statut` ASC) )