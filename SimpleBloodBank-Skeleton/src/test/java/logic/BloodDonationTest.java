package logic;

import common.EMFactory;
import common.TomcatStartUp;
import entity.BloodBank;
import entity.BloodDonation;
import entity.BloodGroup;
import entity.RhesusFactor;
import java.util.Collections;
import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class is has the example of how to add dependency when working with junit. it is commented because some
 * components need to be made first. You will have to import everything you need.
 *
 * @author Shariar (Shawn) Emami
 * @author Wenwen Ji
 */
class BloodDonationTest {

    private BloodDonationLogic logic;
    private BloodDonation expectedEntity;
    private BloodBank bb;

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

        logic = LogicFactory.getFor( "BloodDonation" );
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
        //check if the dependency exists on DB already
        //em.find takes two arguments, the class type of return result and the primary key.
        BloodBankLogic bbLogic = LogicFactory.getFor("BloodBank");
        bb = bbLogic.getBloodBankWithName("JUNIT");
        
        //if result is null create the entity and persist it
        if( bb == null ){
            //create object
            bb = new BloodBank();
            bb.setName( "JUNIT" );
            bb.setPrivatelyOwned( true );
            bb.setEstablished( logic.convertStringToDate( "1111-11-11 11:11:11" ) );
            bb.setEmplyeeCount( 111 );
            //persist the dependency first
            bbLogic.add(bb );
        }

        //create the desired entity
        BloodDonation entity = new BloodDonation();
        entity.setMilliliters( 100 );
        entity.setBloodGroup( BloodGroup.AB );
        entity.setRhd( RhesusFactor.Negative );
        entity.setCreated( logic.convertStringToDate( "1111-11-11 11:11:11" ) );
        //add dependency to the desired entity
        entity.setBloodBank( bb );
        entity.setDonationRecordSet(Collections.emptySet());

        //add desired entity to hibernate, entity is now managed.
        //we use merge instead of add so we can get the managed entity.
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
        //get all the accounts from the DB
        List<BloodDonation> list = logic.getAll();
        //store the size of list, this way we know how many accounts exits in DB
        int originalSize = list.size();

        //make sure account was created successfully
        assertNotNull( expectedEntity );
        //delete the new account
        logic.delete( expectedEntity );

        //get all accounts again
        list = logic.getAll();
        //the new size of accounts must be one less
        assertEquals( originalSize - 1, list.size() );
    }

    @Test
    final void testGetColumnCodes() {
        List<String> codes = logic.getColumnCodes();

        assertEquals(6, codes.size());
        assertEquals("donation_id", codes.get(0));
        assertEquals("created", codes.get(5));
    }

    @Test
    final void testGetColumnNames() {
        List<String> colNames = logic.getColumnNames();

        assertEquals(6, colNames.size());
        assertEquals("ID", colNames.get(0));
        assertEquals("Created", colNames.get(5));
    }

    @Test
    final void testExtractDataAsList() {
        List<?> dataList = logic.extractDataAsList(expectedEntity);

        assertEquals(6, dataList.size());
        assertEquals(100, dataList.get(2));
        assertEquals(BloodGroup.AB, dataList.get(3));
    }

    @Test
    final void testGetWithId() {
        BloodDonation result = logic.getWithId(expectedEntity.getId());

        assertEquals(expectedEntity, result);
    }

    @Test
    final void testCreateEntity() {
        Map<String, String[]> parameterMap = new HashMap<>();
        parameterMap.put(BloodDonationLogic.ID, new String[]{"2"});
        parameterMap.put(BloodDonationLogic.BANK_ID, new String[]{"1"});
        parameterMap.put(BloodDonationLogic.BLOOD_GROUP, new String[]{"AB"});
        parameterMap.put(BloodDonationLogic.MILLILITERS, new String[]{"500"});
        parameterMap.put(BloodDonationLogic.RHESUS_FACTOR, new String[]{"Positive"});
        parameterMap.put(BloodDonationLogic.CREATED, new String[]{"2021-08-13T20:12:23"});

        BloodDonation result = logic.createEntity(parameterMap);
        // new created donation has id 2, as "expectedEntity" created with id 1
        assertEquals(BloodGroup.AB, result.getBloodGroup());
        assertEquals(500, result.getMilliliters());
        assertEquals(RhesusFactor.Positive, result.getRhd());
        assertNotNull(result.getCreated());
    }

    @Test
    final void testGetBloodDonationWithMilliliters() {
        BloodDonation result = logic.getBloodDonationWithMilliliters(100);
        assertEquals(expectedEntity, result);
    }

    @Test
    final void testGetBloodDonationWithBloodGroup() {
        List<BloodDonation> results = logic.getBloodDonationWithBloodGroup(BloodGroup.AB);

        assertEquals(1, results.size());
        assertEquals(expectedEntity, results.get(0));
    }

    @Test
    final void testGetBloodDonationWithCreated() {
        List<BloodDonation> results = logic.getBloodDonationWithCreated(logic.convertStringToDate("1111-11-11 11:11:11"));

        assertEquals(1, results.size());
        assertEquals(expectedEntity, results.get(0));
    }

    @Test
    final void testGetBloodDonationWithRHD() {
        List<BloodDonation> results = logic.getBloodDonationWithRhd(RhesusFactor.Negative);

        assertEquals(1, results.size());
        assertEquals(expectedEntity, results.get(0));
    }

    @Test
    final void testGetBloodDonationWithBloodBankId() {
        List<BloodDonation> results = logic.getBloodDonationWithBloodBank(bb.getId());

        assertEquals(1, results.size());
        assertEquals(expectedEntity, results.get(0));
    }
}
