import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.StringUtils;

import au.com.bytecode.opencsv.CSVReader;


public class FederalScraper {

    public FederalScraper() {}

    private static final Logger LOG = Logger.getLogger(FederalScraper.class.getName());

    public void scrape(String file) throws IOException, SQLException {
        // Init DB
        Connection conn = DriverManager.getConnection("jdbc:mysql://66.116.150.171:3306/enewe10_hk?user=enewe10_hack&password=Hack0ns");

        CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(new File(file)), Charset.forName("ISO-8859-1")));
        String line[];

        PreparedStatement insertParty = conn.prepareStatement("INSERT INTO parti (nom, data_source) VALUES(?, 'dons_ontario')", Statement.RETURN_GENERATED_KEYS);
        PreparedStatement insertPerson = conn.prepareStatement("INSERT INTO citoyen (nom, data_source) VALUES(?, 'dons_ontario')", Statement.RETURN_GENERATED_KEYS);
        PreparedStatement insertDon = conn.prepareStatement("INSERT INTO dons_quebec (person_id, donation_date, amount, num_donations, party_id, postal_code, province, data_source) VALUES(?,?,?,?,?,?,?,'dons_ontario')", Statement.RETURN_GENERATED_KEYS);

        HashMap<String, Long> partys = new HashMap<String, Long>();

        // Print heading
        line = reader.readNext();
        for(int i = 0;i < line.length; i++) {
            System.out.println(i + " : " + line[i]);
        }

        long partyCount = 0;
        long personCount = 0;
        long donCount = 0;

        while((line = reader.readNext()) != null) {
            Don don = new Don();
            don.firstName = StringUtils.newStringUtf8(line[2].getBytes());
            don.politicalParty = StringUtils.newStringUtf8(line[3].getBytes());
            don.amount = Double.parseDouble(StringUtils.newStringUtf8(line[12].getBytes()));
            // We don't know how many times in a period the donation has been made, but there is at least 1
            don.amount = 1;

            don.donationDate = new GregorianCalendar(Integer.parseInt(StringUtils.newStringUtf8(line[17].getBytes())), 0, 1);

            // Postal code and province not already there
            if(StringUtils.newStringUtf8(line[14].getBytes()).length() > 0) {
                don.municipality = StringUtils.newStringUtf8(line[14].getBytes());
                don.province = StringUtils.newStringUtf8(line[15].getBytes());
                don.postalCode = StringUtils.newStringUtf8(line[16].getBytes());
            }

            // Do the inserts

            // Insert person
            insertPerson.setString(1, don.firstName);
            insertPerson.execute();

            // Get person generated ID
            ResultSet personIDSet = insertPerson.getGeneratedKeys();
            if(personIDSet.next()) {
                long personID = personIDSet.getLong(1);
                personCount++;

                // Find the party
                boolean hasParty = false;
                long partyID = 0;

                // Check in hashmap first
                if(partys.containsKey(don.politicalParty)) {
                    partyID = partys.get(don.politicalParty);
                    hasParty = true;
                } else {
                    // Try inserting the party
                    insertParty.setString(1, don.politicalParty);
                    insertParty.execute();
                    ResultSet partyIDSet = insertParty.getGeneratedKeys();
                    if(partyIDSet.next()) {
                        hasParty = true;
                        partyID = partyIDSet.getLong(1);
                        partys.put(don.politicalParty, partyID);
                        partyCount++;
                    }
                }


                // Get generated party ID
                if(hasParty) {

                    // Insert donation
                    insertDon.setLong(1, personID);
                    insertDon.setDate(2, new java.sql.Date(don.donationDate.getTimeInMillis()));
                    insertDon.setDouble(3, don.amount);
                    insertDon.setInt(4, don.numDonations);
                    insertDon.setLong(5, partyID);
                    insertDon.setString(6, don.postalCode);

                    insertDon.execute();
                    ResultSet donIDSet = insertDon.getGeneratedKeys();

                    if(donIDSet.next()) {
                        donCount++;
                        StringBuilder logLine = new StringBuilder();
                        logLine.append("Party count : ");
                        logLine.append(partyCount);
                        logLine.append(", person count : ");
                        logLine.append(personCount);
                        logLine.append(", donation count : ");
                        logLine.append(donCount);
                        LOG.info(logLine.toString());
                    } else {
                        LOG.warning("Insert don error - " + don.toString());
                    }
                } else {
                    LOG.warning("Insert party error - " + don.politicalParty);
                }
            } else {
                LOG.warning("Insert person error - " + don.firstName);
            }
        }

        reader.close();
        conn.close();
    }

    /**
     * Main
     */
    public static void main(String[] args) throws IOException, SQLException {
        FederalScraper scraper = new FederalScraper();
        scraper.scrape("dons-partis-politiques-canada.csv");
    }

}
