import java.util.GregorianCalendar;


public class Don {

    private static final String SEP = ", ";

    public String lastName;
    public String firstName;
    public double amount;
    public int numDonations;
    public String politicalParty;
    public String municipality;
    public String postalCode;
    public GregorianCalendar donationDate;
    public String province;

    @Override
    public String toString() {
        StringBuffer repr = new StringBuffer();

        repr.append("Donation date : ");
        repr.append(this.donationDate.get(GregorianCalendar.YEAR));
        repr.append(SEP);

        repr.append("Last name : ");
        repr.append(this.lastName);
        repr.append(SEP);

        repr.append("First name : ");
        repr.append(this.firstName);
        repr.append(SEP);

        repr.append("Amount : ");
        repr.append(this.amount);
        repr.append(SEP);

        repr.append("Number of donations :");
        repr.append(this.numDonations);
        repr.append(SEP);

        repr.append("Political party : ");
        repr.append(this.politicalParty);
        repr.append(SEP);

        repr.append("Municipality : ");
        repr.append(this.municipality);
        repr.append(SEP);

        repr.append("Postal code : ");
        repr.append(this.postalCode);
        repr.append(SEP);

        repr.append("Province : ");
        repr.append(this.province);

        return repr.toString();
    }
}
