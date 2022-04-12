package logic;

import common.EMFactory;
import common.TomcatStartUp;
import common.ValidationException;
import entity.Person;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Jinyu Li
 */
class PersonTest {

    private PersonLogic logic;
    private Person expectedEntity;

    @BeforeAll
    final static void setUpBeforeClass() throws Exception {
        TomcatStartUp.createTomcat( "/SimpleBloodBank", "common.ServletListener", "simplebloodbank-PU-test" );
    }

    @AfterAll
    final static void tearDownAfterClass() throws Exception {
        TomcatStartUp.stopAndDestroyTomcat();
    }

    @BeforeEach
    final void setUp() throws Exception {

        logic = LogicFactory.getFor( "Person" );
        /* **********************************
         * ***********IMPORTANT**************
         * **********************************/
        //we only do this for the test.
        //always create Entity using logic.
        //we manually make the account to not rely on any logic functionality , just for testing

        //get an instance of EntityManager
        EntityManager em = EMFactory.getEMF().createEntityManager();
        //start a Transaction
        em.getTransaction().begin();

        Person entity = new Person();
        entity.setFirstName( "Jinyu" );
        entity.setLastName( "Li" );
        entity.setPhone( "6139088878" );
        entity.setAddress( "Ottawa" );
        entity.setBirth(logic.convertStringToDate( "2021-08-06 11:11:11" ));

        //add a person to hibernate, person is now managed.
        //we use merge instead of add so we can get the updated generated ID.
        expectedEntity = em.merge( entity );
        //commit the changes
        em.getTransaction().commit();
        //close EntityManager
        em.close();
    }

    @AfterEach
    final void tearDown() throws Exception {
        if( expectedEntity != null ){
            logic.delete( expectedEntity );
        }
    }

    @Test
    final void testGetAll() {
        //get all the persons from the DB
        List<Person> list = logic.getAll();
        //store the size of list, this way we know how many people exist in the DB
        int originalSize = list.size();

        //make sure person was created successfully
        assertNotNull( expectedEntity );
        //delete the new person
        logic.delete( expectedEntity );

        //get all persons again
        list = logic.getAll();
        //the new size of persons must be one less
        assertEquals( originalSize - 1, list.size() );
    }

    /**
     * helper method for testing all person fields
     *
     * @param expected
     * @param actual
     */
    private void assertPersonEquals( Person expected, Person actual ) {
        //assert all field to guarantee they are the same
        assertEquals( expected.getId(), actual.getId() );
        assertEquals( expected.getFirstName(), actual.getFirstName() );
        assertEquals( expected.getLastName(), actual.getLastName() );
        assertEquals( expected.getPhone(), actual.getPhone() );
        assertEquals( expected.getAddress(), actual.getAddress() );
        assertEquals( expected.getBirth(), actual.getBirth() );
    }

    @Test
    final void testGetWithId() {
        //using the id of test person get another person from logic
        Person returnedPerson = logic.getWithId( expectedEntity.getId() );

        //the two persons (testPerson and returnedPerson) must be the same
        assertPersonEquals( expectedEntity, returnedPerson );
    }

    @Test
    final void testCreateEntityNullAndEmptyValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
        map.put( PersonLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) }  );
        map.put( PersonLogic.FIRSTNAME, new String[]{ expectedEntity.getFirstName() } );
        map.put( PersonLogic.LASTNAME , new String[]{ expectedEntity.getLastName() } );
        map.put( PersonLogic.PHONE, new String[]{ expectedEntity.getPhone() } );
        map.put( PersonLogic.ADDRESS, new String[]{ expectedEntity.getAddress() } );
        map.put( PersonLogic.BIRTH, new String[]{ logic.convertDateToString(expectedEntity.getBirth()) } );
        };

        //idealy every test should be in its own method
        //ID
        fillMap.accept( sampleMap );
        sampleMap.replace( PersonLogic.ID, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( PersonLogic.ID, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

        //First_Name
        fillMap.accept( sampleMap );
        sampleMap.replace( PersonLogic.FIRSTNAME, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( PersonLogic.FIRSTNAME, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

        //Last_Name
        fillMap.accept( sampleMap );
        sampleMap.replace( PersonLogic.LASTNAME, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( PersonLogic.LASTNAME, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

        //Phone
        fillMap.accept( sampleMap );
        sampleMap.replace( PersonLogic.PHONE, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( PersonLogic.PHONE, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

        //Address
        fillMap.accept( sampleMap );
        sampleMap.replace( PersonLogic.ADDRESS, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( PersonLogic.ADDRESS, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
        
        //Birth
        fillMap.accept( sampleMap );
        sampleMap.replace( PersonLogic.BIRTH, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( PersonLogic.BIRTH, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
    }

    @Test
    final void testCreateEntityBadLengthValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            map.put( PersonLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
            map.put( PersonLogic.FIRSTNAME, new String[]{ expectedEntity.getFirstName() } );
            map.put( PersonLogic.LASTNAME, new String[]{ expectedEntity.getLastName() } );
            map.put( PersonLogic.PHONE, new String[]{ expectedEntity.getPhone() } );
            map.put( PersonLogic.ADDRESS, new String[]{ expectedEntity.getAddress() } );
            map.put( PersonLogic.BIRTH, new String[]{ logic.convertDateToString(expectedEntity.getBirth()) } );
        };

        IntFunction<String> generateString = ( int length ) -> {
        
            return new Random().ints( 'a', 'z' + 1 ).limit( length )
                    .collect( StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append )
                    .toString();
        };

      
        fillMap.accept( sampleMap );
        sampleMap.replace( PersonLogic.ID, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( PersonLogic.ID, new String[]{ "12b" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( PersonLogic.FIRSTNAME, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( PersonLogic.FIRSTNAME, new String[]{ generateString.apply( 46 ) } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( PersonLogic.LASTNAME, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( PersonLogic.LASTNAME, new String[]{ generateString.apply( 46 ) } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( PersonLogic.PHONE, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( PersonLogic.PHONE, new String[]{ generateString.apply( 46 ) } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( PersonLogic.ADDRESS, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( PersonLogic.ADDRESS, new String[]{ generateString.apply( 46 ) } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        

    }

    @Test
    final void testGetColumnNames() {
        List<String> list = logic.getColumnNames();
        assertEquals( Arrays.asList( "id", "first_name", "last_name", "phone", "address", "birth" ), list );
    }

    @Test
    final void testGetColumnCodes() {
        List<String> list = logic.getColumnCodes();
        assertEquals( Arrays.asList( PersonLogic.ID, PersonLogic.FIRSTNAME, PersonLogic.LASTNAME, PersonLogic.PHONE, PersonLogic.ADDRESS , PersonLogic.BIRTH) , list );
    }

    @Test
    final void testExtractDataAsList() {
        List<?> list = logic.extractDataAsList( expectedEntity );
        assertEquals( expectedEntity.getId(), list.get( 0 ) );
        assertEquals( expectedEntity.getFirstName(), list.get( 1 ) );
        assertEquals( expectedEntity.getLastName(), list.get( 2 ) );
        assertEquals( expectedEntity.getPhone(), list.get( 3 ) );
        assertEquals( expectedEntity.getAddress(), list.get( 4 ) );
        assertEquals( expectedEntity.getBirth(), list.get( 5 ) );
    }
}
