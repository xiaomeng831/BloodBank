package logic;

import common.EMFactory;
import common.TomcatStartUp;
import common.ValidationException;
import java.util.Arrays;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import entity.BloodBank;
import entity.BloodDonation;
import entity.Person;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.Hibernate;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BloodBankTest {
    private BloodBankLogic logic;
    private BloodBank expectedEntity;
    private Set<BloodDonation> donations;
    private Person testPerson;
    private int personID = 2;
    
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

        logic = LogicFactory.getFor( "BloodBank" );             

        EntityManager em = EMFactory.getEMF().createEntityManager();
        em.getTransaction().begin();         
        
        testPerson = em.find( Person.class, personID );
        if( testPerson == null ){
            testPerson = new Person();
            testPerson.setFirstName("Jinyu");
            testPerson.setLastName("Li");
            testPerson.setPhone("6139088878");
            testPerson.setAddress("Ottawa"); 
            testPerson.setBirth(logic.convertStringToDate("2000-03-06 8:8:8"));
            em.persist( testPerson );
        }       

      
        personID = testPerson.getId();
        donations = new HashSet<BloodDonation>();
        donations.add(new BloodDonation());        
        
        BloodBank entity = new BloodBank();
        entity.setName( "test name" );
        entity.setPrivatelyOwned(true);
        entity.setEstablished(logic.convertStringToDate("1111-11-11 11:11:11"));
        entity.setEmplyeeCount(5);
        entity.setOwner(testPerson);        
        entity.setBloodDonationSet(donations);

        expectedEntity = em.merge( entity );
        em.getTransaction().commit();
        em.close();
    }

    @AfterEach
    final void tearDown() throws Exception {
        if( expectedEntity != null ){
            logic.delete( expectedEntity );
        }
        
        EntityManager em = EMFactory.getEMF().createEntityManager();
        em.getTransaction().begin();
        
        Person testPerson = em.find( Person.class, personID );
        if (testPerson!= null) {
            em.remove(testPerson);            
        }
        em.getTransaction().commit();
        em.close();
    }
    
    @Test
    final void testGetName() {
        assertNotNull( expectedEntity );
        expectedEntity.setName("test name");
        String name = expectedEntity.getName();
        assertEquals(name, "test name");
    }
    
    @Test
    final void testGetPrivatelyOwned() {
        assertNotNull( expectedEntity );
        assertTrue(expectedEntity.getPrivatelyOwned());
    }
    
    @Test
    final void testGetEstablished() {
        assertNotNull( expectedEntity );
        assertEquals(expectedEntity.getEstablished(), logic.convertStringToDate("1111-11-11 11:11:11"));
    }
    
    @Test
    final void testGetEmployeeCount() {
        assertNotNull( expectedEntity );
        assertEquals(expectedEntity.getEmplyeeCount(), 5);
    }
    
    @Test
    final void testGetOwner() {
        assertNotNull( expectedEntity );
        assertEquals(expectedEntity.getOwner(), testPerson);
    }    
    
    @Test
    final void testGetBloodDonationSet() {
        assertNotNull( expectedEntity );
        assertEquals(expectedEntity.getBloodDonationSet().size(), 1);
    }
    
    @Test
    final void testEquals() {
        assertNotNull( expectedEntity );
        assertTrue(expectedEntity.equals(expectedEntity));
    }
    
    @Test
    final void testGetAll() {
        
        List<BloodBank> list = logic.getAll();       
        int originalSize = list.size();
        
        assertNotNull( expectedEntity );        
        logic.delete( expectedEntity );
       
        list = logic.getAll();        
        assertEquals( originalSize - 1, list.size() );
    }
    
    private void assertBloodBanksEqual( BloodBank expected, BloodBank actual ) {
        assertEquals(expected.getId(), actual.getId() ); 
        assertEquals(expected.getName(), actual.getName() );
        assertEquals(expected.getPrivatelyOwned(), actual.getPrivatelyOwned() );
        assertEquals(expected.getOwner(), actual.getOwner() );
        assertEquals(expected.getEstablished().compareTo(actual.getEstablished()), 0);
        assertEquals(expected.getEmplyeeCount(), actual.getEmplyeeCount() );        
    }
    
    @Test
    final void testGetWithId() {
        BloodBank returnedBank = logic.getWithId( expectedEntity.getId() );

        returnedBank.setOwner((Person)Hibernate.unproxy(returnedBank.getOwner()));
        
        assertBloodBanksEqual( expectedEntity, returnedBank );
    }
    
    @Test 
    final void testGetBloodBankWithName() {
        BloodBank returnedBank = logic.getBloodBankWithName( expectedEntity.getName() );
        
        returnedBank.setOwner((Person)Hibernate.unproxy(returnedBank.getOwner()));        
        
        assertBloodBanksEqual( expectedEntity, returnedBank );
    }
    
    @Test
    final void testGetBloodBankWithPrivatelyOwned() {
        List<BloodBank> returnedBanks = logic.getBloodBankWithPrivatelyOwned( expectedEntity.getPrivatelyOwned() );
        int found = 0;
        for(BloodBank bank : returnedBanks) {
            assertEquals(bank.getPrivatelyOwned(), expectedEntity.getPrivatelyOwned());
            if (bank.getId() == expectedEntity.getId()) {
                assertBloodBanksEqual(bank, expectedEntity);
                ++found;
            }
        } 
        assertEquals(found, 1);
    }   
    
    @Test
    final void testGetBloodBanksWithEstablished() {
        List<BloodBank> returnedBanks = logic.getBloodBankWithEstablished( expectedEntity.getEstablished() );
        int found = 0;
        for(BloodBank bank : returnedBanks) {
            assertEquals(expectedEntity.getEstablished().compareTo(bank.getEstablished()), 0);
            if (bank.getId() == expectedEntity.getId()) {
                assertBloodBanksEqual(bank, expectedEntity);
                ++found;
            }
        }
        assertEquals(found, 1);
    }
    
    @Test
    final void testGetBloodBanksWithEmployeeCount() {
        List<BloodBank> returnedBanks = logic.getBloodBankWithEstablished( expectedEntity.getEstablished() );
        int found = 0;
        for(BloodBank bank : returnedBanks) {
            assertEquals(expectedEntity.getEmplyeeCount(), bank.getEmplyeeCount());
            if (bank.getId() == expectedEntity.getId()) {
                assertBloodBanksEqual(bank, expectedEntity);
                ++found;
            }
        }
        assertEquals(found, 1);
    }
    
    @Test
    final void testGetBloodBanksWithOwner() {
        BloodBank returnedBank = logic.getBloodBankWithOwner( expectedEntity.getOwner().getId() );      
        returnedBank.setOwner((Person)Hibernate.unproxy(returnedBank.getOwner()));
        assertBloodBanksEqual(expectedEntity, returnedBank);
    }    
    
    @Test
    final void testCreateEntityAndAdd() {
        Map<String, String[]> sampleMap = new HashMap<>();
                            
        sampleMap.put( BloodBankLogic.PRIVATELY_OWNED, new String[]{ "false" } );
        sampleMap.put( BloodBankLogic.ESTABLISHED, new String[]{ "2021-08-13 20:12:23" } );       
        sampleMap.put( BloodBankLogic.NAME, new String[]{ "test names" } );
        sampleMap.put( BloodBankLogic.EMPLOYEE_COUNT, new String[]{ "5" } );        
        
        BloodBank returnedBloodbank = logic.createEntity( sampleMap );
        logic.add(returnedBloodbank );

        returnedBloodbank = logic.getBloodBankWithName(returnedBloodbank.getName() );  
        
        assertEquals( false, returnedBloodbank.getPrivatelyOwned() );
        assertEquals( 5, returnedBloodbank.getEmplyeeCount() );
        assertEquals(returnedBloodbank.getEstablished().compareTo(logic.convertStringToDate(sampleMap.get(BloodBankLogic.ESTABLISHED)[0])), 0);
      
        assertEquals( null, returnedBloodbank.getOwner() ); 
        assertEquals( sampleMap.get( BloodBankLogic.NAME )[ 0 ], returnedBloodbank.getName() );

        logic.delete(returnedBloodbank );
    }
    
    @Test
    final void testCreateEntity() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put( BloodBankLogic.ID, new String[]{ Integer.toString(expectedEntity.getId()) } );
        sampleMap.put( BloodBankLogic.PRIVATELY_OWNED, new String[]{ Boolean.toString(expectedEntity.getPrivatelyOwned()) } );
        sampleMap.put( BloodBankLogic.ESTABLISHED, new String[]{ logic.convertDateToString(expectedEntity.getEstablished()) } );
        if (expectedEntity.getOwner() != null) 
            sampleMap.put( BloodBankLogic.OWNER_ID, new String[]{ expectedEntity.getOwner().getId().toString() } );
        sampleMap.put( BloodBankLogic.NAME, new String[]{ expectedEntity.getName() } );
        sampleMap.put( BloodBankLogic.EMPLOYEE_COUNT, new String[]{ Integer.toString(expectedEntity.getEmplyeeCount()) } );

        BloodBank returnedBank = logic.createEntity( sampleMap );
        if (expectedEntity.getOwner() != null) {            
            returnedBank.setOwner(EMFactory.getEMF().createEntityManager().find( Person.class, personID));
        }
        
        assertBloodBanksEqual( expectedEntity, returnedBank );
    }

    @Test
    final void testCreateEntityNullAndEmptyValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            map.put( BloodBankLogic.ID, new String[]{ Integer.toString(expectedEntity.getId()) } );
            map.put( BloodBankLogic.PRIVATELY_OWNED, new String[]{ Boolean.toString(expectedEntity.getPrivatelyOwned()) } );
            map.put( BloodBankLogic.ESTABLISHED, new String[]{ expectedEntity.getEstablished().toString() } );
            if (expectedEntity.getOwner() != null) 
                map.put( BloodBankLogic.OWNER_ID, new String[]{ expectedEntity.getOwner().getId().toString() } );
            map.put( BloodBankLogic.NAME, new String[]{ expectedEntity.getName() } );
            map.put( BloodBankLogic.EMPLOYEE_COUNT, new String[]{ Integer.toString(expectedEntity.getEmplyeeCount()) } );

        };
        
        fillMap.accept( sampleMap );
        sampleMap.replace( BloodBankLogic.ID, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( BloodBankLogic.ID, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
        
        fillMap.accept( sampleMap );
        sampleMap.replace( BloodBankLogic.NAME, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( BloodBankLogic.NAME, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
        
        fillMap.accept( sampleMap );
        sampleMap.replace( BloodBankLogic.PRIVATELY_OWNED, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( BloodBankLogic.PRIVATELY_OWNED, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
                
        fillMap.accept( sampleMap );
        sampleMap.replace( BloodBankLogic.ESTABLISHED, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( BloodBankLogic.ESTABLISHED, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
        
        fillMap.accept( sampleMap );
        sampleMap.replace( BloodBankLogic.EMPLOYEE_COUNT, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( BloodBankLogic.EMPLOYEE_COUNT, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
                
    }
    
    @Test
    final void testCreateEntityBadLengthValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            map.put( BloodBankLogic.ID, new String[]{ Integer.toString(expectedEntity.getId()) } );
            map.put( BloodBankLogic.PRIVATELY_OWNED, new String[]{ Boolean.toString(expectedEntity.getPrivatelyOwned()) } );
            map.put( BloodBankLogic.ESTABLISHED, new String[]{ expectedEntity.getEstablished().toString() } );
            if (expectedEntity.getOwner() != null) 
                map.put( BloodBankLogic.OWNER_ID, new String[]{ expectedEntity.getOwner().getId().toString() } );
            map.put( BloodBankLogic.NAME, new String[]{ expectedEntity.getName() } );
            map.put( BloodBankLogic.EMPLOYEE_COUNT, new String[]{ Integer.toString(expectedEntity.getEmplyeeCount()) } );
        };
        
        IntFunction<String> generateString = ( int length ) -> {
            return new Random().ints( 'a', 'z' + 1 ).limit( length )
                    .collect( StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append )
                    .toString();
        };        
               
       
        fillMap.accept( sampleMap );
        sampleMap.replace( BloodBankLogic.ID, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( BloodBankLogic.ID, new String[]{ "12b" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        
       
        
        fillMap.accept( sampleMap );
        sampleMap.replace( BloodBankLogic.PRIVATELY_OWNED, new String[]{ "" } );
        assertThrows( common.ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( BloodBankLogic.PRIVATELY_OWNED, new String[]{ generateString.apply( 46 ) } );
        assertThrows( common.ValidationException.class, () -> logic.createEntity( sampleMap ) );
       
        
        fillMap.accept( sampleMap );
        sampleMap.replace( BloodBankLogic.ESTABLISHED, new String[]{ "" } );
        assertThrows( common.ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( BloodBankLogic.ESTABLISHED, new String[]{ generateString.apply( 46 ) } );
        assertThrows( common.ValidationException.class, () -> logic.createEntity( sampleMap ) );
        
        
        fillMap.accept( sampleMap );
        sampleMap.replace( BloodBankLogic.NAME, new String[]{ "" } );
        assertThrows( common.ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( BloodBankLogic.NAME, new String[]{ generateString.apply( 46 ) } );
        assertThrows( common.ValidationException.class, () -> logic.createEntity( sampleMap ) );
        
        
        fillMap.accept( sampleMap );
        sampleMap.replace( BloodBankLogic.EMPLOYEE_COUNT, new String[]{ "" } );
        assertThrows( common.ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( BloodBankLogic.EMPLOYEE_COUNT, new String[]{ generateString.apply( 46 ) } );
        assertThrows( common.ValidationException.class, () -> logic.createEntity( sampleMap ) );        
    }    
    
    @Test
    final void testExtractDataAsList() {
        List<?> list = logic.extractDataAsList( expectedEntity );
       assertEquals( expectedEntity.getId(), list.get( 0 ) );
       assertEquals( expectedEntity.getEmplyeeCount(), list.get( 1 ) );  
       assertEquals( expectedEntity.getName(), list.get( 2 ) );
       assertEquals( expectedEntity.getEstablished(), list.get( 3 ) );
       assertEquals( expectedEntity.getPrivatelyOwned(), list.get( 4 ) );        
       if (expectedEntity.getOwner() != null)
           assertEquals( expectedEntity.getOwner().getId(), list.get( 5 ) );        
    }
    
    @Test
    final void testGetColumnNames() {
        List<String> list = logic.getColumnNames();
        assertEquals( Arrays.asList("ID", "EmployeeCount", "Name", "Established", 
                "PrivatelyOwned", "owner_id" ), list );
    }

    @Test
    final void testGetColumnCodes() {
        List<String> list = logic.getColumnCodes();
        assertEquals( Arrays.asList( BloodBankLogic.ID, BloodBankLogic.EMPLOYEE_COUNT, BloodBankLogic.NAME, BloodBankLogic.ESTABLISHED,
                BloodBankLogic.PRIVATELY_OWNED, BloodBankLogic.OWNER_ID ), list );
    }   
}
