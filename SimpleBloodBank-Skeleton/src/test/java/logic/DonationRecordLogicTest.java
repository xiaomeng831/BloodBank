package logic;

import common.EMFactory;
import common.TomcatStartUp;
import common.ValidationException;
import entity.BloodDonation;
import entity.BloodGroup;
import entity.DonationRecord;
import entity.Person;
import entity.RhesusFactor;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Hongxin Yin
 */
public class DonationRecordLogicTest {

    private DonationRecordLogic logic;
    private DonationRecord expectedEntity;
    private String createdDate = "2020-08-12 10:02:30";

    @BeforeAll
    final static void setUpBeforeClass() throws Exception {
        TomcatStartUp.createTomcat("/SimpleBloodBank", "common.ServletListener", "simplebloodbank-PU-test");
    }

    @AfterAll
    final static void tearDownAfterClass() throws Exception {
        TomcatStartUp.stopAndDestroyTomcat();
    }

    @BeforeEach
    final void setUp() throws Exception {

        logic = LogicFactory.getFor("DonationRecord");
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

        Date dateTest = logic.convertStringToDate(createdDate);
        
        // get blood donation entity
        BloodDonation bloodDonation = em.find(BloodDonation.class, 1);
        if (bloodDonation == null) {
            bloodDonation = new BloodDonation();
            bloodDonation.setCreated(dateTest);
            bloodDonation.setMilliliters(100);
            bloodDonation.setRhd(RhesusFactor.Negative);
            bloodDonation.setBloodGroup(BloodGroup.B);
            em.persist(bloodDonation);
        }

        // get person entity
        Person person = em.find(Person.class, 1);
        if (person == null) {
            person = new Person();
            person.setFirstName("Jinyu");
            person.setLastName("Li");
            person.setBirth(dateTest);
            person.setAddress("255A Wilbrod");
            person.setPhone("819-235-5263");
            em.persist(person);
        }

        // create entity 
        DonationRecord entity = new DonationRecord();

        entity.setHospital("Ottawa Hospital");
        entity.setId(1);
        entity.setCreated(dateTest);
        entity.setAdministrator("Hongxin");
        entity.setPerson(person);
        entity.setBloodDonation(bloodDonation);

        //add an DonationRecord to hibernate, DonationRecord is now managed.
        //we use merge instead of add so we can get the updated generated ID.
        expectedEntity = em.merge(entity);
        //commit the changes
        em.getTransaction().commit();
        //close EntityManager
        em.close();
    }

    @AfterEach
    final void tearDown() throws Exception {
        if (expectedEntity != null) {
            logic.delete(expectedEntity);
        }
    }

    @Test
    final void testGetAll() {
        //get all the DonationRecords from the DB
        List<DonationRecord> list = logic.getAll();

        //store the size of list, this way we know how many donation records exits in DB
        int originalSize = list.size();
        
        //make sure donation record was created successfully
        assertNotNull(expectedEntity);
        // delete new donation record
        logic.delete(expectedEntity);

        //get all donation records again
        list = logic.getAll();

        //the new size of accounts must be one less
        assertEquals(originalSize - 1, list.size());
    }

    /**
     * helper method for testing all DonationRecord fields
     *
     * @param expected
     * @param actual
     */
    private void assertDonationRecordEquals(DonationRecord expected, DonationRecord actual) {
        //assert all field to guarantee they are the same
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getPerson().getId(), actual.getPerson().getId());
        assertEquals(expected.getBloodDonation().getId(), actual.getBloodDonation().getId());
        assertEquals(expected.getAdministrator(), actual.getAdministrator());
        assertEquals(expected.getHospital(), actual.getHospital());
        assertEquals(expected.getCreated(), actual.getCreated());
    }

    @Test
    final void testGetWithId() {
        // using the id of test donation record get another donation record from logic
        DonationRecord returnedDonationRecord = logic.getWithId(expectedEntity.getId());
        assertDonationRecordEquals(expectedEntity, returnedDonationRecord);
    }

    @Test
    final void testGetDonationRecordWithPerson() {
        List<DonationRecord> returnedDonationRecords = logic.getDonationRecordWithPerson(expectedEntity.getPerson().getId());
        returnedDonationRecords.forEach(returnedDonationRecord -> {
            assertEquals(expectedEntity.getPerson().getId(), returnedDonationRecord.getPerson().getId());
        });
    }

    @Test
    final void testGetDonationRecordWithDonation() {
        List<DonationRecord> returnedDonationRecords = logic.getDonationRecordWithDonation(expectedEntity.getBloodDonation().getId());
        returnedDonationRecords.forEach(returnedDonationRecord -> {
            assertEquals(expectedEntity.getBloodDonation().getId(), returnedDonationRecord.getBloodDonation().getId());
        });
    }

    @Test
    final void testGetDonationRecordWithTested() {
        List<DonationRecord> returnedDonationRecords = logic.getDonationRecordWithTested(expectedEntity.getTested());
        returnedDonationRecords.forEach(returnedDonationRecord -> {
            assertEquals(expectedEntity.getTested(), returnedDonationRecord.getTested());
        });
    }

    @Test
    final void testGetDonationRecordWithAdministrator() {
        List<DonationRecord> returnedDonationRecords = logic.getDonationRecordWithHospital(expectedEntity.getAdministrator());
        returnedDonationRecords.forEach(returnedDonationRecord -> assertEquals(expectedEntity.getAdministrator(), returnedDonationRecord.getAdministrator()));
    }

    @Test
    final void testGetDonationRecordWithCreated() {
        List<DonationRecord> returnedDonationRecords = logic.getDonationRecordWithCreated(expectedEntity.getCreated());
        returnedDonationRecords.forEach(returnedDonationRecord -> {
            assertEquals(expectedEntity.getCreated(), returnedDonationRecord.getCreated());
        });
    }

    @Test
    final void testCreateEntity() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(DonationRecordLogic.ID, new String[]{Integer.toString(expectedEntity.getId())});
        sampleMap.put(DonationRecordLogic.ADMINISTRATOR, new String[]{expectedEntity.getAdministrator()});
        sampleMap.put(DonationRecordLogic.HOSPITAL, new String[]{expectedEntity.getHospital()});
        sampleMap.put(DonationRecordLogic.CREATED, new String[]{createdDate});
        sampleMap.put(DonationRecordLogic.TESTED, new String[]{Boolean.toString(expectedEntity.getTested())});

        DonationRecord returnedDonationRecord = logic.createEntity(sampleMap);
        int personId = expectedEntity.getPerson().getId();
        if (expectedEntity.getPerson() != null) {
            returnedDonationRecord.setPerson(EMFactory.getEMF().createEntityManager().find(Person.class, personId));
        }

        int donationId = expectedEntity.getBloodDonation().getId();
        if (expectedEntity.getBloodDonation() != null) {
            returnedDonationRecord.setBloodDonation(EMFactory.getEMF().createEntityManager().find(BloodDonation.class, donationId));
        }
        assertDonationRecordEquals(expectedEntity, returnedDonationRecord);
    }

    @Test
    final void testCreateEntityNullAndEmptyValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = (Map<String, String[]> map) -> {
            map.clear();
            map.put(DonationRecordLogic.ID, new String[]{Integer.toString(expectedEntity.getId())});
            map.put(DonationRecordLogic.PERSON_ID, new String[]{expectedEntity.getPerson().getId().toString()});
            map.put(DonationRecordLogic.DONATION_ID, new String[]{expectedEntity.getBloodDonation().getId().toString()});
            map.put(DonationRecordLogic.ADMINISTRATOR, new String[]{expectedEntity.getAdministrator()});
            map.put(DonationRecordLogic.TESTED, new String[]{Boolean.toString(expectedEntity.getTested())});
            map.put(DonationRecordLogic.HOSPITAL, new String[]{expectedEntity.getHospital()});
            map.put(DonationRecordLogic.CREATED, new String[]{logic.convertDateToString(expectedEntity.getCreated())});
        };

        //idealy every test should be in its own method
        fillMap.accept(sampleMap);
        sampleMap.replace(DonationRecordLogic.ID, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(DonationRecordLogic.ID, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(DonationRecordLogic.ADMINISTRATOR, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(DonationRecordLogic.TESTED, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(DonationRecordLogic.TESTED, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(DonationRecordLogic.HOSPITAL, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(DonationRecordLogic.HOSPITAL, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(DonationRecordLogic.CREATED, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(DonationRecordLogic.CREATED, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));
    }

    @Test
    final void testCreateEntityBadHospitalValue() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            map.put( DonationRecordLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
            map.put( DonationRecordLogic.ADMINISTRATOR, new String[]{ expectedEntity.getAdministrator() } );
            map.put( DonationRecordLogic.TESTED, new String[]{ Boolean.toString(expectedEntity.getTested()) } );
            map.put( DonationRecordLogic.HOSPITAL, new String[]{ expectedEntity.getHospital() } );
            map.put( DonationRecordLogic.CREATED, new String[]{ logic.convertDateToString(expectedEntity.getCreated() )} );
        };

        //idealy every test should be in its own method
        fillMap.accept( sampleMap );
        sampleMap.replace( DonationRecordLogic.HOSPITAL, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
    }
    
    @Test
    final void testGetColumnNames() {
        List<String> list = logic.getColumnNames();
        assertEquals(Arrays.asList("record_id", "person_id", "donation_id", "tested", "administrator", "hospital", "created"), list);
    }

    @Test
    final void testGetColumnCodes() {
        List<String> list = logic.getColumnCodes();
        assertEquals(Arrays.asList(DonationRecordLogic.ID, DonationRecordLogic.PERSON_ID,
                DonationRecordLogic.DONATION_ID, DonationRecordLogic.TESTED,
                DonationRecordLogic.ADMINISTRATOR, DonationRecordLogic.HOSPITAL, DonationRecordLogic.CREATED), list);
    }

    @Test
    final void testExtractDataAsList() {
        List<?> list = logic.extractDataAsList(expectedEntity);
        assertEquals(expectedEntity.getId(), list.get(0));
        assertEquals(expectedEntity.getPerson().getId(), list.get(1));
        assertEquals(expectedEntity.getBloodDonation().getId(), list.get(2));
        assertEquals(expectedEntity.getTested(), list.get(3));
        assertEquals(expectedEntity.getAdministrator(), list.get(4));
        assertEquals(expectedEntity.getHospital(), list.get(5));
        assertEquals(expectedEntity.getCreated(), list.get(6));
    }
}
