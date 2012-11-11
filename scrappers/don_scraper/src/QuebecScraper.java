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


public class QuebecScraper {

    private static final Logger LOG = Logger.getLogger(QuebecScraper.class.getName());

    public QuebecScraper() {}

    public void scrape(String file) throws IOException, SQLException {
        // Init DB
        Connection conn = DriverManager.getConnection("jdbc:mysql://66.116.150.171:3306/enewe10_hk?user=enewe10_hack&password=Hack0ns");

        CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(new File(file)), Charset.forName("ISO-8859-1")));
        String line[];

        PreparedStatement insertParty = conn.prepareStatement("INSERT INTO parti (nom, data_source) VALUES(?, 'dons_quebec')", Statement.RETURN_GENERATED_KEYS);
        PreparedStatement insertPerson = conn.prepareStatement("INSERT INTO citoyen (nom, data_source) VALUES(?, 'dons_quebec')", Statement.RETURN_GENERATED_KEYS);
        PreparedStatement insertDon = conn.prepareStatement("INSERT INTO dons_quebec (person_id, donation_date, amount, num_donations, party_id, postal_code, province, data_source) VALUES(?,?,?,?,?,?,'QC','dons_quebec')", Statement.RETURN_GENERATED_KEYS);

        HashMap<String, Long> partys = new HashMap<String, Long>();

        reader.readNext();

        long partyCount = 0;
        long personCount = 0;
        long donCount = 0;

        int i = 0;

        while((line = reader.readNext()) != null) {
            // INFO: Party count : 22, person count : 34982, donation count : 34982
            // Count gave on last run 35690
            if(i > 35690) {
                Don don = new Don();
                don.donationDate = new GregorianCalendar(Integer.parseInt(StringUtils.newStringUtf8(line[2].getBytes())), 0, 1);
                don.lastName = StringUtils.newStringUtf8(line[4].getBytes());
                don.firstName = StringUtils.newStringUtf8(line[5].getBytes());
                don.amount = Double.parseDouble(StringUtils.newStringUtf8(line[6].getBytes()));
                don.numDonations = Integer.parseInt(StringUtils.newStringUtf8(line[7].getBytes()));
                don.politicalParty = StringUtils.newStringUtf8(line[8].getBytes());
                don.municipality = StringUtils.newStringUtf8(line[9].getBytes());
                don.postalCode = StringUtils.newStringUtf8(line[10].getBytes());


                StringBuilder fullName = new StringBuilder(don.firstName.length() + don.lastName.length() + 1);
                fullName.append(don.firstName);
                fullName.append(" ");
                fullName.append(don.lastName);

                insertPerson.setString(1, fullName.toString());

                // Insert person
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
                        // Try inserting
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
                    LOG.warning("Insert person error - " + fullName);
                }
            }

            i++;
        }
        reader.close();
        conn.close();
    }

    /**
     * Main
     */
    public static void main(String[] args) throws IOException, SQLException {
        QuebecScraper scraper = new QuebecScraper();
        scraper.scrape("dons_aux_partis_politiques_du_quebec.csv");
    }

}
