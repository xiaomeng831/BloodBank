package logic;

import common.ValidationException;
import dal.DonationRecordDAL;
import entity.DonationRecord;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ObjIntConsumer;

/**
 *
 * @author Hongxin Yin
 */
public class DonationRecordLogic extends GenericLogic<DonationRecord, DonationRecordDAL> {

    public static final String PERSON_ID = "person_id";
    public static final String DONATION_ID = "donation_id";
    public static final String TESTED = "tested";
    public static final String ADMINISTRATOR = "administrator";
    public static final String HOSPITAL = "hospital";
    public static final String CREATED = "created";
    public static final String ID = "record_id";

    public DonationRecordLogic() {
        super(new DonationRecordDAL());
    }

    @Override
    public List<DonationRecord> getAll() {
        return get(() -> dal().findAll());
    }

    @Override
    public DonationRecord getWithId(int id) {
        return get(() -> dal().findById(id));
    }

    public List<DonationRecord> getDonationRecordWithTested(boolean tested) {
        return get(() -> dal().findByTested(tested));
    }

    public List<DonationRecord> getDonationRecordWithAdministrator(String administrator) {
        return get(() -> dal().findByAdministrator(administrator));
    }

    public List<DonationRecord> getDonationRecordWithHospital(String username) {
        return get(() -> dal().findByHospital(username));
    }

    public List<DonationRecord> getDonationRecordWithCreated(Date created) {
        return get(() -> dal().findByCreated(created));
    }

    public List<DonationRecord> getDonationRecordWithPerson(int personId) {
        return get(() -> dal().findByPerson(personId));
    }

    public List<DonationRecord> getDonationRecordWithDonation(int donationId) {
        return get(() -> dal().findByDonation(donationId));
    }

    @Override
    public DonationRecord createEntity(Map<String, String[]> parameterMap) {

        Objects.requireNonNull(parameterMap, "parameterMap cannot be null");

        //create a new Entity object
        DonationRecord entity = new DonationRecord();

        //before using the values in the map, make sure to do error checking.
        //simple lambda to validate a string, this can also be place in another
        //method to be shared amoung all logic classes.
        // this is where all the input is checked and then we check to see if it has the same values in the table already
        ObjIntConsumer< String> validator = (value, length) -> {
            if (value == null || value.trim().isEmpty() || value.length() > length) {
                // GIVING AN ERROR WHEN I LEAVE THE STRING EMPTY
                String error = "";
                if (value == null || value.trim().isEmpty()) {
                    error = "value cannot be null or empty: " + value;
                }
                if (value.length() > length) {
                    error = "string length is " + value.length() + " > " + length;
                }
                throw new ValidationException(error);
            }
        };

        if (parameterMap.containsKey(ID)) {
            try {
                entity.setId(Integer.parseInt(parameterMap.get(ID)[0]));
            } catch (java.lang.NumberFormatException ex) {
                throw new ValidationException(ex);
            }
        }

        //extract the date from map first.
        //everything in the parameterMap is string so it must first be
        //converted to appropriate type. have in mind that values are
        //stored in an array of String; almost always the value is at
        //index zero unless you have used duplicated key/name somewhere.
        String tested = parameterMap.get(TESTED)[0];
        String administrator = parameterMap.get(ADMINISTRATOR)[0];
        String hospital = parameterMap.get(HOSPITAL)[0];

        //validate the data
        validator.accept(tested, 5);
        validator.accept(administrator, 100);
        validator.accept(hospital, 100);
        Date created = convertStringToDate(parameterMap.get(CREATED)[0].replace("T", " "));

        entity.setCreated(created);
        entity.setAdministrator(administrator);

        entity.setTested(Boolean.parseBoolean(tested));
        entity.setHospital(hospital);

        return entity;
    }

    /**
     * this method is used to send a list of all names to be used form table
     * column headers. by having all names in one location there is less chance
     * of mistakes.
     *
     * this list must be in the same order as getColumnCodes and
     * extractDataAsList
     *
     * @return list of all column names to be displayed.
     */
    @Override
    public List<String> getColumnNames() {
        return Arrays.asList("record_id", "person_id", "donation_id", "tested", "administrator", "hospital", "created");
    }

    /**
     * this method returns a list of column names that match the official column
     * names in the db. by having all names in one location there is less chance
     * of mistakes.
     *
     * this list must be in the same order as getColumnNames and
     * extractDataAsList
     *
     * @return list of all column names in DB.
     */
    @Override
    public List<String> getColumnCodes() {
        return Arrays.asList(ID, PERSON_ID, DONATION_ID, TESTED, ADMINISTRATOR, HOSPITAL, CREATED);
    }

    /**
     * return the list of values of all columns (variables) in given entity.
     *
     * this list must be in the same order as getColumnNames and getColumnCodes
     *
     * @param e - given Entity to extract data from.
     *
     * @return list of extracted values
     */
    @Override
    public List<?> extractDataAsList(DonationRecord e) {
        Integer persion_id = e.getPerson() == null ? null : e.getPerson().getId();
        Integer donation_id = e.getBloodDonation() == null ? null : e.getBloodDonation().getId();
        return Arrays.asList(e.getId(), persion_id, donation_id,
                e.getTested(), e.getAdministrator(), e.getHospital(), e.getCreated());
    }
}
