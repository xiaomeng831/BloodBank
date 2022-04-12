package dal;

import entity.BloodBank;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  @author Xiaomeng Xu
 *  implements BloodBank DAL and extends GenericDAL Class
 */
public class BloodBankDAL extends GenericDAL <BloodBank> {
   
    /**
     *  non-argument constructor
     *  in constructor call GenericDAL super()
     */
    public BloodBankDAL() {
        super(BloodBank.class);
    }
    
    /**
     * find all records in the database
     * @return the list of all records about BloodBank in the database
     */
    @Override
    public List<BloodBank> findAll() {
        return findResults( "BloodBank.findAll", null );
    }

    /**
     * find records by Id
     * @param bankId - id of BloodBank
     * @return records about BloodBank in the database matching bankId
     */
    @Override
    public BloodBank findById(int bankId) {
        Map<String, Object> map = new HashMap<>();
        map.put( "bankId", bankId );
      
        return findResult( "BloodBank.findByBankId", map );
    }    
    
    /**
     * find records by name
     * @param name - name of BloodBank
     * @return records about BloodBank in the database matching name
     */
    public BloodBank findByName(String name) {
        Map<String, Object> map = new HashMap<>();
        map.put( "name", name );
        return findResult( "BloodBank.findByName", map );
    }
   
    /**
     * find records by PrivatelyOwned
     * @param privatelyOwned - status about private or not of BloodBank
     * @return the list of all records about BloodBank in the database which are private or not
     */
    public List<BloodBank> findByPrivatelyOwned(boolean privatelyOwned) {
        Map<String, Object> map = new HashMap<>();
        map.put( "privatelyOwned", privatelyOwned );
        return findResults( "BloodBank.findByPrivatelyOwned", map );
    }
    
    /**
     * find records by Established
     * @param established - Established Date of BloodBank
     * @return the list of all records about BloodBank in the database matching the Established date searched
     */
    public List<BloodBank> findByEstablished(Date established) {
        Map<String, Object> map = new HashMap<>();
        map.put( "established", established );
        return findResults( "BloodBank.findByEstablished", map );
    }
    
     /**
     * find records by owner
     * @param ownerId- id of owner of BloodBank
     * @return records about BloodBank in the database matching the ownerId searched
     */
    public BloodBank findByOwner(int ownerId) {
        Map<String, Object> map = new HashMap<>();
        map.put( "ownerId", ownerId );
        return findResult( "BloodBank.findByOwner", map );
    }
    
    /**
     * find records by EmployeeCount
     * @param employeeCount - number of employees
     * @return the list of all records about BloodBank in the database matching the number of employees searched
     */
    public List<BloodBank> findByEmployeeCount(int employeeCount) {
        Map<String, Object> map = new HashMap<>();
        map.put( "employeeCount", employeeCount );
        return findResults( "BloodBank.findByEmployeeCount", map );
    }
        
    /**
     * find records by search term
     * @param search- search term
     * @return the list of the records about BloodBank in the database matching the search term
     */
    public List<BloodBank> findContaining(String search) {
        Map<String, Object> map = new HashMap<>();
        map.put( "search", search );
        return findResults( "BloodBank.findContaining", map );
    }        
}
