package logic;

import common.ValidationException;
import dal.BloodBankDAL;
import entity.BloodBank;
import entity.Person;
import java.util.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ObjIntConsumer;

/**
 * @author Xiaomeng Xu
 * extends GenericLogic Class
 */
public class BloodBankLogic extends GenericLogic <BloodBank, BloodBankDAL> {
    // column names in the database
    public static final String OWNER_ID = "owner_id";
    public static final String PRIVATELY_OWNED = "privately_owned";
    public static final String ESTABLISHED = "established";
    public static final String NAME = "name";
    public static final String EMPLOYEE_COUNT = "employee_count";
    public static final String ID = "id";
    
    // non-argument construct
    public BloodBankLogic() {
        super (new BloodBankDAL());
    }    
    
    /**
     * retrieve all the records about BloodBanks from the database
     * @return the list of all records about BloodBank in the database
     */
    @Override
    public List getAll() {
        return get( () -> dal().findAll() );
    }
    
    /**
     * retrieve a record about BloodBank from the database based on id
     * @param id  id of the BloodBank
     * @return a record about BloodBank or null
     */
    @Override
    public BloodBank getWithId(int id) {
        return get( () -> dal().findById( id ) );
    } 
    
    /**
     * retrieve a BloodBank from the database by name
     * @param name  name of the BloodBank
     * @return a record about BloodBank or null
     */
    public BloodBank getBloodBankWithName(String name) {
        return get( () -> dal().findByName( name ) );
    }
    
    /**
     * retrieve BloodBanks from the database by private or not
     * @param privatelyOwned  true if private
     * @return the list of records about BloodBank
     */
    public List<BloodBank> getBloodBankWithPrivatelyOwned(boolean privatelyOwned) {
        return get( () -> dal().findByPrivatelyOwned( privatelyOwned ) );
    }
    
    /**
     * retrieve BloodBanks from the database by the established date
     * @param established the established date
     * @return the list of records about BloodBank
     */    
    public List<BloodBank> getBloodBankWithEstablished(Date established) {
        return get( () -> dal().findByEstablished( established ) );
    } 
    
    /**
     * retrieve a BloodBank from the database by the id of the owner
     * @param ownerId the id of the owner
     * @return records about BloodBank
     */
    public BloodBank getBloodBankWithOwner(int ownerId) {
        return get( () -> dal().findByOwner( ownerId ) );
    }
    
    /**
     * retrieve BloodBanks from the database by the number of employee
     * @param count the number of employees
     * @return the list of records about BloodBank
     */
    public List<BloodBank> getBloodBanksWithEmployeeCount(int count) {
        return get( () -> dal().findByEmployeeCount( count ) );
    }    
    
    /**
     * method is used to create a BloodBank
     * @param parameterMap
     * @return 
     */
    @Override
    public BloodBank createEntity(Map<String, String[]> parameterMap) {
        
        Objects.requireNonNull( parameterMap, "parameterMap cannot be null" );      
       
        BloodBank entity = new BloodBank();

        if( parameterMap.containsKey( ID ) ){
            try {
                String idString = parameterMap.get( ID )[ 0 ];                
                entity.setId( Integer.parseInt( idString ) );
            } catch( java.lang.NumberFormatException ex ) {
                throw new ValidationException( ex );
            }
        }

        ObjIntConsumer< String> validator = ( value, length ) -> {
            if( value == null || value.trim().isEmpty() || value.length() > length ){
                String error = "";
                if( value == null || value.trim().isEmpty() ){
                    error = "value cannot be null or empty: " + value;
                }
                if(value != null && value.length() > length ){
                    error = "string length is " + value.length() + " > " + length;
                }
                throw new ValidationException( error );
            }
        };      
        
        String employeeCount = parameterMap.get(EMPLOYEE_COUNT)[0];
        
        String privatelyOwned = parameterMap.get(PRIVATELY_OWNED)[0];
        String name = parameterMap.get(NAME)[0];  
        String establishedStr = parameterMap.get(ESTABLISHED)[0];
            
        Date established = convertStringToDate(establishedStr);//        
     
        validator.accept( employeeCount, 45 );        
        validator.accept( privatelyOwned, 45 );
        validator.accept( name, 45 );
        validator.accept( establishedStr, 45);       
        
        entity.setEmplyeeCount( Integer.parseInt(employeeCount) );
        
        entity.setEstablished( established );
        entity.setPrivatelyOwned( Boolean.parseBoolean(privatelyOwned) );
        entity.setName( name );           

        return entity;
    }       

    /**
     * get the names of the columns
     * @return Database column names
     */
   @Override
    public List<String> getColumnNames() {
        return Arrays.asList( "ID", "EmployeeCount", "Name", "Established", 
                "PrivatelyOwned", "owner_id" );
    }
    
    /**
     * get the codes of the columns
     * @return Database column names
     */
    @Override
    public List<String> getColumnCodes() {
        return Arrays.asList( ID, EMPLOYEE_COUNT, NAME, ESTABLISHED,
                PRIVATELY_OWNED, OWNER_ID );
    }

    /**
     * methods is used to pack BloodBank data into a list
     * @param e BloodBank object
     * @return a list of all a BloodBank objects values
     */
    @Override
    public List<?> extractDataAsList( BloodBank e ) {
        int ownerId = 0;
        if (e.getOwner() != null) {
            ownerId = e.getOwner().getId();
        }        
        return Arrays.asList( e.getId(), e.getEmplyeeCount(), e.getName(), 
                        e.getEstablished(), e.getPrivatelyOwned(), ownerId ); 
    }
}