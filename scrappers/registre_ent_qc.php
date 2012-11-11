<?php
/*
  Permet d'aller chercher toutes les entreprises du Québec et les dirigeants de chaque entreprise
*/

//Connection au serveur de données
$db = new PDO(
    'mysql:host=66.116.150.171;dbname=enewe10_hk',
    'enewe10_hack',
    'Hack0ns',
    array(PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES utf8"));



$sth = $db->prepare('SELECT date FROM scrapper_update WHERE url = ?');
$scrapper_info = $sth->execute(array($data_url));

//Dernier update
$last_update = $scrapper_info ? $scrapper_info[0] : null;

if ($last_update === null) {
	$sth = $db->prepare('INSERT INTO scrapper_update (url) VALUES (?)');
	$sth->execute(array($data_url));
}

if (($handle = fopen("registre_ent_qc.csv", "r")) !== FALSE) {
    	while (($data = fgetcsv($handle, 1000, ",")) !== FALSE) {
    		
		//Ajouter l'entreprise à notre registre
		$sth = $db->prepare('INSERT INTO entreprise (nom, adresse, data_source) VALUES (?, ?, ?)');		
		$sth->execute(array($nom, $adresse, 'registre_ent_qc'));
		$compagnie = $db->lastInsertId(); 
		
		
	}
    	
	fclose($handle);

}
$where_clause = $last_update ? 'where date_scraped > '.$last_update : '';

$query = str_replace(array('{select}', '{where}', '{limit}'), array('COUNT(*) as cnt', $where_clause, ''), $data_url);

//Nombre de nouvelle données
$count = json_decode(file_get_contents($query), true);

$count = reset($count);
$count = $count['cnt'];

//Nombre de query à faire pour alléger le traitement des données
$nombre_iterations = ceil($count / 10000);


for ($k = 0; $k < $nombre_iterations; $k++) {
	$begin = $k * 10000;
	$query = str_replace(array('{select}', '{where}', '{limit}'), array('CompanyName, RegistryUrl', $where_clause, 'LIMIT 10000 OFFSET '.$begin), $data_url);
	
	//Récuperer les entreprises
	$entreprises = json_decode(file_get_contents($query), true);
	
	foreach ($entreprises as $i => $entreprise) {
		if (isset($entreprise['RegistryUrl']) == false) {
			var_dump($entreprises);
			continue 2;
		}

		var_dump(($begin + $i). ' '.$entreprise['RegistryUrl']);

		$adresse = trim($doc->find('.search h2:contains("Registered Office Address")')->next()->html());		
		
		//Trouver le node des dirigeants
		foreach ($doc->find('.search strong:contains("Directors") ~ ul')->find('li') as $dirigeant) {
			$dirigeant = trim($dirigeant->nodeValue);

			//Enregistrer chaque dirigeant comme citoyen
			$sth = $db->prepare('INSERT INTO citoyen (nom, data_source) VALUES (?, ?)');	
			$sth->execute(array($dirigeant, 'registre_ent_ca'));
			$citoyen = $db->lastInsertId();

			//Ajouter le lien entre l'entreprise et le dirigeant
			$sth = $db->prepare('INSERT INTO dirigeant_entreprise (entreprise_id, citoyen_id) VALUES (?, ?)');
			$sth->execute(array($compagnie, $citoyen));
		}
	}
}

//mettre à jour la date de mise à jour du scrapper
$sth = $db->prepare('UPDATE scrapper_update SET date=NOW() WHERE url = ?');
$sth->execute(array($data_url));
