package dal;

import entity.BloodDonation;
import entity.BloodGroup;
import entity.RhesusFactor;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents the Data Access Layer of DAO design pattern.
 * @author Wenwen Ji
 */
public class BloodDonationDAL extends GenericDAL<BloodDonation> {

    public BloodDonationDAL() {
        super(BloodDonation.class);
    }

    /**
     * Find all blood donation.
     * @return All the results of blood donation.
     */
    @Override
    public List<BloodDonation> findAll() {
        return findResults("BloodDonation.findAll", null);
    }

    /**
     * Find the blood donation by donation id.
     * @param id The id of blood donation.
     * @return Blood donation entity.
     */
    @Override
    public BloodDonation findById(int id) {
        Map<String, Object> params = new HashMap<>();
        params.put( "donationId", id);

        return findResult("BloodDonation.findByDonationId", params);
    }

    /**
     * Find the blood donation by milliliters of blood donated.
     * @param milliliters The milliliters of blood donated.
     * @return Blood donation entity.
     */
    public BloodDonation findByMilliliters(int milliliters) {
        Map<String, Object> params = new HashMap<>();
        params.put( "milliliters", milliliters);

        return findResult("BloodDonation.findByMilliliters", params);
    }

    /**
     * Find the blood donation by the blood group.
     * @param bloodGroup The blood group of the blood donated.
     * @return List of blood donation entity.
     */
    public List<BloodDonation> findByBloodGroup(BloodGroup bloodGroup) {
        Map<String, Object> params = new HashMap<>();
        params.put( "bloodGroup", bloodGroup);

        return findResults("BloodDonation.findByBloodGroup", params);
    }

    /**
     * Find the blood donation by the Rhesus factor.
     * @param rhd The rhesus factor of blood.
     * @return List of blood donation entity.
     */
    public List<BloodDonation> findByRhd(RhesusFactor rhd) {
        Map<String, Object> params = new HashMap<>();
        params.put( "rhd", rhd);

        return findResults("BloodDonation.findByRhd", params);
    }

    /**
     * Find the blood donation by the date which the donation created.
     * @param created The data when the blood donation created.
     * @return List of blood donation entity.
     */
    public List<BloodDonation> findByCreated(Date created) {
        Map<String, Object> params = new HashMap<>();
        params.put( "created", created);

        return findResults("BloodDonation.findByCreated", params);
    }

    /**
     * Find the blood donation by blood bank.
     * @param bloodBankId The id of blood bank.
     * @return List of blood donation entity.
     */
    public List<BloodDonation> findByBloodBank(int bloodBankId) {
        Map<String, Object> params = new HashMap<>();
        params.put( "bloodBankId", bloodBankId);

        return findResults("BloodDonation.findByBloodBank", params);
    }
}
