package logic;

import common.ValidationException;
import dal.BloodDonationDAL;
import entity.BloodDonation;
import entity.BloodGroup;
import entity.RhesusFactor;

import java.time.Instant;
import java.util.*;
import java.util.function.ObjIntConsumer;

/**
 * This class represents the Logic of DAO design pattern.
 * @author Wenwen Ji
 */
public class BloodDonationLogic extends GenericLogic<BloodDonation, BloodDonationDAL> {

    public static final String DOMAIN = "BloodDonation";
    public static final String BANK_ID = "bank_id";
    public static final String MILLILITERS = "milliliters";
    public static final String BLOOD_GROUP = "blood_group";
    public static final String RHESUS_FACTOR = "rhesus_factor";
    public static final String CREATED = "created";
    public static final String ID = "donation_id";

    public BloodDonationLogic() {
        super( new BloodDonationDAL());
    }

    /**
     * Get the table column names of database.
     * @return List of column names.
     */
    @Override
    public List<String> getColumnCodes() {
        return Arrays.asList(ID, BANK_ID, MILLILITERS, BLOOD_GROUP, RHESUS_FACTOR, CREATED);
    }

    /**
     * Get the table column label names of browser shown.
     * @return List of column label names.
     */
    @Override
    public List<String> getColumnNames() {
        return Arrays.asList("ID", "Bank ID", "Milliliters", "Blood Group", "Rhesus Factor", "Created");
    }

    /**
     * Extract query results of blood donation entity to a list of data.
     * @param b Blood donation entity to convert.
     * @return List of data.
     */
    @Override
    public List<?> extractDataAsList(BloodDonation b) {
        return Arrays.asList(b.getId(), b.getBloodBank(), b.getMilliliters(), b.getBloodGroup(), b.getRhd(), b.getCreated().toString());
    }

    /**
     * Create a blood donation entity by form data of Servlet request.
     * Also validate form data input.
     * @param parameterMap Form data of Servlet request.
     * @return Blood donation entity to create.
     */
    @Override
    public BloodDonation createEntity(Map<String, String[]> parameterMap) {
        Objects.requireNonNull( parameterMap, "parameterMap cannot be null" );

        //create a new Entity object
        BloodDonation donation = new BloodDonation();

        if (parameterMap.containsKey(ID)) {
            try {
                donation.setId(Integer.parseInt(parameterMap.get(ID)[0]));
            } catch (java.lang.NumberFormatException ex) {
                throw new ValidationException(ex);
            }
        }

        //before using the values in the map, make sure to do error checking.
        //simple lambda to validate a string, this can also be place in another
        //method to be shared amoung all logic classes.
        ObjIntConsumer< String> validator = (value, length ) -> {
            if( value == null || value.trim().isEmpty() || value.length() > length ){
                String error = "";
                if( value == null || value.trim().isEmpty() ){
                    error = "value cannot be null or empty: " + value;
                }
                if( value.length() > length ){
                    error = "string length is " + value.length() + " > " + length;
                }
                throw new ValidationException( error );
            }
        };

        String milliliters = parameterMap.get(MILLILITERS)[0];
        String bloodGroup = parameterMap.get(BLOOD_GROUP)[0];
        String rhd = parameterMap.get(RHESUS_FACTOR)[0];
        String date = parameterMap.get(CREATED)[0].replace("T", " ");
        Date created = convertStringToDate(date);

        validator.accept(milliliters, 1000);
        validator.accept(bloodGroup, 2);
        validator.accept(rhd, 10);

        donation.setBloodGroup(BloodGroup.valueOf(bloodGroup));
        donation.setMilliliters(Integer.parseInt(milliliters));
        donation.setRhd(RhesusFactor.valueOf(rhd));
        donation.setCreated(created);

        return donation;
    }

    /**
     * Get all blood donation.
     * @return All the results of blood donation.
     */
    @Override
    public List<BloodDonation> getAll() {
        return get(() -> dal().findAll());
    }

    /**
     * Get the blood donation with donation id.
     * @param id The id of blood donation.
     * @return Blood donation entity.
     */
    @Override
    public BloodDonation getWithId(int id) {
        return get(() -> dal().findById(id));
    }

    /**
     * Get the blood donation with milliliters of blood donated.
     * @param milliliters The milliliters of blood donated.
     * @return Blood donation entity.
     */
    public BloodDonation getBloodDonationWithMilliliters(int milliliters) {
        return dal().findByMilliliters(milliliters);
    }

    /**
     * Get the blood donation with the blood group.
     * @param bloodGroup The blood group of the blood donated.
     * @return List of blood donation entity.
     */
    public List<BloodDonation> getBloodDonationWithBloodGroup(BloodGroup bloodGroup) {
        return dal().findByBloodGroup(bloodGroup);
    }

    /**
     * Get the blood donation with the date which the donation created.
     * @param created The data when the blood donation created.
     * @return List of blood donation entity.
     */
    public List<BloodDonation> getBloodDonationWithCreated(Date created) {
        return dal().findByCreated(created);
    }

    /**
     * Get the blood donation with the Rhesus factor.
     * @param rhd The rhesus factor of blood.
     * @return List of blood donation entity.
     */
    public List<BloodDonation> getBloodDonationWithRhd(RhesusFactor rhd) {
        return dal().findByRhd(rhd);
    }

    /**
     * Get the blood donation with blood bank.
     * @param bankId The id of blood bank.
     * @return List of blood donation entity.
     */
    public List<BloodDonation> getBloodDonationWithBloodBank(int bankId) {
        return dal().findByBloodBank(bankId);
    }

    /**
     * Update blood donation.
     * @param parameterMap Form data of Servlet request.
     * @return Blood donation entity.
     */
    public BloodDonation updateDonation(Map<String, String[]> parameterMap) {
        BloodDonation existingDonation = getWithId(Integer.parseInt(parameterMap.get(ID)[0]));
        if (existingDonation == null) {
            return null;
        }
        BloodDonation donationToUpdate = createEntity(parameterMap);
        donationToUpdate.setId(existingDonation.getId());
        return update(donationToUpdate);
    }
}
