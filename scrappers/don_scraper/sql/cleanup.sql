SELECT COUNT(*) FROM parti;
DELETE FROM parti WHERE data_source='dons_quebec';
SELECT COUNT(*) FROM parti;

SELECT COUNT(*) FROM citoyen;
DELETE FROM citoyen WHERE data_source='dons_quebec';
SELECT COUNT(*) FROM citoyen;

SELECT COUNT(*) FROM dons_quebec;
TRUNCATE TABLE dons_quebec;
SELECT COUNT(*) FROM dons_quebec;