package dal;

import entity.Account;
import entity.Person;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *CST8288
 * @author Jinyu Li
 */
public class PersonDAL extends GenericDAL<Person> {

    public PersonDAL() {
        super( Person.class );
    }

    @Override
    public List<Person> findAll() {
        //first argument is a name given to a named query defined in appropriate entity
        //second argument is map used for parameter substitution.
        //parameters are names starting with : in named queries, :[name]
        return findResults( "Person.findAll", null );
    }

    @Override
    public Person findById( int id ) {
        Map<String, Object> map = new HashMap<>();
        map.put( "id", id );
        //first argument is a name given to a named query defined in appropriate entity
        //second argument is map used for parameter substitution.
        //parameters are names starting with : in named queries, :[name]
        //in this case the parameter is named "id" and value for it is put in map
        return findResult( "Person.findById", map );
    }

    public List<Person> findByFirstName( String firstName ) {
        Map<String, Object> map = new HashMap<>();
        map.put( "firstName", firstName );
        return findResults( "Person.findByFirstName", map );
    }

    public List<Person> findByLastName( String lastName ) {
        Map<String, Object> map = new HashMap<>();
        map.put( "lastName", lastName );
        return findResults( "Person.findByLastName", map );
    }
    
    public List<Person> findByBirth( String birth ) {
        Map<String, Object> map = new HashMap<>();
        map.put( "birth", birth );
        return findResults( "Person.findByBirth", map );
    }
    
    public Person findByPhone( String phone ) {
        Map<String, Object> map = new HashMap<>();
        map.put( "phone", phone );
        return findResult( "Person.findByPhone", map );
    }

    public List<Person> findByAddress( String address ) {
        Map<String, Object> map = new HashMap<>();
        map.put( "address", address );
        return findResults( "Person.findByAddress", map );
    }

public List<Person> findContaining(String search) {
        Map<String, Object> map = new HashMap<>();
        map.put("search", search);
        return findResults("Person.findContaining", map);
    }
}
