package logic;

import common.ValidationException;
import dal.PersonDAL;
import entity.Person;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ObjIntConsumer;


/**
 * CST8288
 * @author Jinyu Li
 */
public class PersonLogic extends GenericLogic<Person, PersonDAL> {
    /**
     * create static final variables with proper name of each column. this way you will never manually type it again,
     * instead always refer to these variables.
     */    
    public static final String FIRSTNAME = "first_name";
    public static final String LASTNAME = "last_name";
    public static final String PHONE = "phone";
    public static final String ADDRESS = "address";
    public static final String BIRTH = "birth";
    public static final String ID = "id";

	PersonLogic() {
		super(new PersonDAL());
	}

	@Override
	public List<String> getColumnNames() {
		return Arrays.asList("id","first_name","last_name","phone","address","birth");
	}

	@Override
	public List<String> getColumnCodes() {
		return  Arrays.asList(ID,FIRSTNAME,LASTNAME,PHONE,ADDRESS,BIRTH);
	}
        
@Override
	public List<Person> getAll() {
		return get(()->dal().findAll());
	}

	@Override
	public Person getWithId(int id) {
		return get(()->dal().findById(id));
	}
        
        @Override
        public List<Person> search( String search ) {
                return get( () -> dal().findContaining( search ) );
    }
	@Override
	public List<?> extractDataAsList(Person e) {
		return Arrays.asList( e.getId(), e.getFirstName(), e.getLastName(), e.getPhone(), e.getAddress(), e.getBirth() );
	}

	@Override
	public Person createEntity(Map<String, String[]> parameterMap) {

      Objects.requireNonNull( parameterMap, "parameterMap cannot be null" );

      Person entity = new Person();

      if( parameterMap.containsKey( ID ) ){
          try {
              entity.setId( Integer.parseInt( parameterMap.get( ID )[ 0 ] ) );
          } catch( NumberFormatException ex ) {
              throw new ValidationException( ex );
          }
      }


      ObjIntConsumer< String> validator = ( value, length ) -> {
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


      String firstName = parameterMap.get(FIRSTNAME)[0];
      String lastName = parameterMap.get(LASTNAME)[0];
      String phone = parameterMap.get(PHONE)[0];
      String address = parameterMap.get(ADDRESS)[0];
      SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd" ); 
      Date birthD =null;

      try {
          birthD = sdf.parse(parameterMap.get(BIRTH)[0]);
      } catch (ParseException e) {
          e.printStackTrace();
      }


      //validate the data
      validator.accept( firstName, 45 );
      validator.accept( lastName, 45 );
      validator.accept( phone, 45 );
      validator.accept( address, 45 );

      //set values on entity
      entity.setFirstName(firstName);
      entity.setLastName(lastName);
      entity.setPhone(phone);
      entity.setAddress(address);
      entity.setBirth(birthD);

      return entity;
	}

	
}