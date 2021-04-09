package logic;

import common.EMFactory;
import common.TomcatStartUp;
import common.ValidationException;
import entity.Account;
import entity.BloodBank;
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
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import static logic.DonationRecordLogic.ADMINSTRATOR;
import static logic.DonationRecordLogic.CREATED;
import static logic.DonationRecordLogic.DONATION_ID;
import static logic.DonationRecordLogic.HOSPITAL;
import static logic.DonationRecordLogic.PERSON_ID;
import static logic.DonationRecordLogic.TESTED;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 *
 * @author sarah
 */
//@Disabled
class DonationRecordTest {

    private DonationRecordLogic logic;
    private DonationRecord donationRecordExpected;
    private Person person;
    private BloodDonation blDon;

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

        //create person for dependancy 
//        person= em.find(Person.class,1);
        TypedQuery<Person> tq = em.createQuery("Select p from Person p", Person.class);
        List<Person> list = tq.getResultList();
        if (list.isEmpty()) {
            person = new Person();
            person.setFirstName("Lily");
            person.setLastName("Johns");
            person.setAddress("JUNIT 5 Test");
            person.setPhone("123456789");
            person.setBirth(logic.convertStringToDate("1990-10-10 10:10:10"));
            em.persist(person);
        } else {
            person = list.get(0);
        }

        //create BloodDonation for dependancy 
        blDon = em.find(BloodDonation.class, 1);
        if (blDon == null) {
            blDon = new BloodDonation();
            blDon.setMilliliters(5);
            blDon.setBloodGroup(BloodGroup.B);
            blDon.setRhd(RhesusFactor.Positive);
            blDon.setCreated(logic.convertStringToDate("2001-12-15 09:05:20"));

            em.persist(blDon);
        }

        DonationRecord entity = new DonationRecord();
        entity.setHospital("Ottawa Hospital");
        entity.setAdministrator("Smith");
        entity.setTested(false);
        entity.setCreated(logic.convertStringToDate("200-12-15 09:05:20"));
        entity.setPerson(person);
        entity.setBloodDonation(blDon);

        //add an account to hibernate, account is now managed.
        //we use merge instead of add so we can get the updated generated ID.
        donationRecordExpected = em.merge(entity);
        //commit the changes
        em.getTransaction().commit();
        //close EntityManager
        em.close();
    }

    @AfterEach
    final void tearDown() throws Exception {
        if (donationRecordExpected != null) {
            logic.delete(donationRecordExpected);

        }
    }

    @Test
    final void testGetAll() {

        List<DonationRecord> list = logic.getAll();
        int originalSize = list.size();
        assertNotNull(donationRecordExpected);
        logic.delete(donationRecordExpected);
        list = logic.getAll();
        assertEquals(originalSize - 1, list.size());
    }

    /**
     * helper method for testing all account fields
     *
     * @param expected
     * @param actual
     */
    private void assertDonRecordEquals(DonationRecord expected, DonationRecord actual) {
        //assert all field to guarantee they are the same
        assertDonRecordEquals(expected, actual, true);
    }

    private void assertDonRecordEquals(DonationRecord expected, DonationRecord actual, boolean testDepend) {
        //assert all field to guarantee they are the same
        assertEquals(expected.getId(), actual.getId());
        if (testDepend) {
            assertEquals(expected.getPerson().getId(), actual.getPerson().getId());
            assertEquals(expected.getBloodDonation().getId(), actual.getBloodDonation().getId());
        }
        assertEquals(expected.getTested(), actual.getTested());
        assertEquals(expected.getAdministrator(), actual.getAdministrator());
        assertEquals(expected.getHospital(), actual.getHospital());
        assertEquals(expected.getCreated(), actual.getCreated());
    }

    //failing nullpointer excption
    @Test
    final void testGetWithId() {

        DonationRecord record = logic.getWithId(donationRecordExpected.getId());

        assertDonRecordEquals(donationRecordExpected, record, false);

    }

    @Test
    final void testGetDonationRecordWithHospital() {
        List<DonationRecord> returnedRecords = logic.getDonationRecordWithHospital(donationRecordExpected.getHospital());
        for (DonationRecord record : returnedRecords) {
            assertEquals(donationRecordExpected.getHospital(), record.getHospital());
        }
    }

    @Test
    final void testGetDonationRecordWithAdminstrator() {
        List<DonationRecord> returnedRecords = logic.getDonationRecordWithAdminstrator(donationRecordExpected.getAdministrator());

        for (DonationRecord record : returnedRecords) {
            assertEquals(donationRecordExpected.getAdministrator(), record.getAdministrator());
        }
    }

    @Test
    final void testGetDonationRecordWithTested() {
        List<DonationRecord> returnedRecords = logic.getDonationRecordWithTested(donationRecordExpected.getTested());
        for (DonationRecord record : returnedRecords) {
            assertEquals(donationRecordExpected.getTested(), record.getTested());
        }
    }

    @Test
    final void testGetDonationRecordWithPerson() {
        List<DonationRecord> returnedRecords = logic.findByPerson(donationRecordExpected.getPerson().getId());
        for (DonationRecord record : returnedRecords) {
            assertEquals(donationRecordExpected.getPerson().getId(), record.getPerson().getId());
        }
    }

    @Test
    final void testGetDonationRecordWithBloodDonation() {
        List<DonationRecord> returnedRecords = logic.findByDonation(donationRecordExpected.getBloodDonation().getId());
        for (DonationRecord record : returnedRecords) {
            assertEquals(donationRecordExpected.getBloodDonation().getId(), record.getBloodDonation().getId());
        }
    }

    @Test
    final void testFindByCreated() {
        List<DonationRecord> returnedRecords = logic.findByCreated(donationRecordExpected.getCreated());
        for (DonationRecord record : returnedRecords) {
            assertEquals(donationRecordExpected.getCreated(), record.getCreated());
        }
    }

    @Test
    final void testCreateEntityAndAdd() {
        Map<String, String[]> sampleMap = new HashMap<>();

        sampleMap.put(DonationRecordLogic.ADMINSTRATOR, new String[]{"Test Create Entity"});
        sampleMap.put(DonationRecordLogic.HOSPITAL, new String[]{"testCreateDonationRecord"});
        sampleMap.put(DonationRecordLogic.TESTED, new String[]{"false"});
        sampleMap.put(DonationRecordLogic.CREATED, new String[]{"1990-10-10 10:10:10.0"});
        sampleMap.put(DonationRecordLogic.PERSON_ID, new String[]{"2"});
        sampleMap.put(DonationRecordLogic.DONATION_ID, new String[]{"6"});

     
      PersonLogic personLogic = LogicFactory.getFor("Person");

        
        DonationRecord returnedRecord = logic.createEntity(sampleMap);
        //call the person logic, get the person with specific id then add it to record
   //    int PERSON_ID1 = Integer.valueOf(DonationRecordLogic.PERSON_ID);
      //  logic.findByPerson(PERSON_ID1);
      // Person c = personLogic.getWithId(PERSON_ID1);
        //add the depedencies
      //  returnedRecord.setPerson(c);
        logic.add(returnedRecord);

        returnedRecord = logic.getWithId(returnedRecord.getId());

        assertEquals(sampleMap.get(DonationRecordLogic.ADMINSTRATOR)[0], returnedRecord.getAdministrator());
        assertEquals(sampleMap.get(DonationRecordLogic.HOSPITAL)[0], returnedRecord.getHospital());
        assertEquals(sampleMap.get(DonationRecordLogic.CREATED)[0], returnedRecord.getCreated().toString());
        assertEquals(sampleMap.get(DonationRecordLogic.TESTED)[0], String.valueOf(returnedRecord.getTested()));
     //   assertEquals(sampleMap.get(DonationRecordLogic.PERSON_ID)[0],returnedRecord.getPerson().getId());
        //assertEquals(sampleMap.get( DonationRecordLogic.DONATION_ID)[ 0 ], returnedRecord.getBloodDonation().getId().toString() );

        logic.delete(returnedRecord);
    }

    @Test
    final void testCreateEntity() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(DonationRecordLogic.ID, new String[]{Integer.toString(donationRecordExpected.getId())});
        sampleMap.put(DonationRecordLogic.HOSPITAL, new String[]{donationRecordExpected.getHospital()});
        sampleMap.put(DonationRecordLogic.ADMINSTRATOR, new String[]{donationRecordExpected.getAdministrator()});
        sampleMap.put(DonationRecordLogic.CREATED, new String[]{logic.convertDateToString(donationRecordExpected.getCreated())});
        sampleMap.put(DonationRecordLogic.TESTED, new String[]{String.valueOf(donationRecordExpected.getTested())});

        DonationRecord returnedRecord = logic.createEntity(sampleMap);
        assertDonRecordEquals(donationRecordExpected, returnedRecord, false);
    }

    @Test
    final void testCreateEntityNullAndEmpty() {
        Map<String, String[]> testMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = (Map<String, String[]> map) -> {

            map.clear();
            testMap.put(DonationRecordLogic.ID, new String[]{Integer.toString(donationRecordExpected.getId())});
            testMap.put(DonationRecordLogic.TESTED, new String[]{String.valueOf(donationRecordExpected.getTested())});
            testMap.put(DonationRecordLogic.HOSPITAL, new String[]{donationRecordExpected.getHospital()});
            testMap.put(DonationRecordLogic.ADMINSTRATOR, new String[]{donationRecordExpected.getAdministrator()});
            testMap.put(DonationRecordLogic.CREATED, new String[]{String.valueOf(donationRecordExpected.getCreated())});
        };
        fillMap.accept(testMap);
        testMap.replace(DonationRecordLogic.ID, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(testMap));
        testMap.replace(DonationRecordLogic.ID, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(testMap));

        testMap.replace(DonationRecordLogic.TESTED, null);
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(testMap));

        testMap.replace(DonationRecordLogic.HOSPITAL, null);
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(testMap));

        testMap.replace(DonationRecordLogic.ADMINSTRATOR, null);
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(testMap));

        testMap.replace(DonationRecordLogic.CREATED, null);
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(testMap));

    }

    @Test
    final void testCreateEntityBadLengthValues() {
        Map<String, String[]> testMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = (Map<String, String[]> map) -> {
            map.clear();
            map.put(DonationRecordLogic.ID, new String[]{Integer.toString(donationRecordExpected.getId())});
            map.put(DonationRecordLogic.TESTED, new String[]{String.valueOf(donationRecordExpected.getTested())});
            map.put(DonationRecordLogic.HOSPITAL, new String[]{donationRecordExpected.getHospital()});
            map.put(DonationRecordLogic.ADMINSTRATOR, new String[]{donationRecordExpected.getAdministrator()});
            map.put(DonationRecordLogic.CREATED, new String[]{String.valueOf(donationRecordExpected.getCreated())});
        };

        IntFunction<String> generateString = (int length) -> {
            return new Random().ints('a', 'z' + 1).limit(length)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
        };

//        //idealy every test should be in its own method
        fillMap.accept(testMap);

        testMap.replace(DonationRecordLogic.ID, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(testMap));
        testMap.replace(DonationRecordLogic.ID, new String[]{"12b"});
        assertThrows(ValidationException.class, () -> logic.createEntity(testMap));

        testMap.replace(DonationRecordLogic.HOSPITAL, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(testMap));
        fillMap.accept(testMap);
        testMap.replace(DonationRecordLogic.HOSPITAL, new String[]{generateString.apply(101)});
        assertThrows(ValidationException.class, () -> logic.createEntity(testMap));

        testMap.replace(DonationRecordLogic.ADMINSTRATOR, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(testMap));
        fillMap.accept(testMap);
        testMap.replace(DonationRecordLogic.ADMINSTRATOR, new String[]{generateString.apply(101)});
        assertThrows(ValidationException.class, () -> logic.createEntity(testMap));

        testMap.replace(DonationRecordLogic.TESTED, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(testMap));
        fillMap.accept(testMap);

        testMap.replace(DonationRecordLogic.CREATED, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(testMap));
        fillMap.accept(testMap);
        testMap.replace(DonationRecordLogic.CREATED, new String[]{"2020-10-10T10:10"});
        assertThrows(ValidationException.class, () -> logic.createEntity(testMap));
    }

    @Test
    final void testCreateEntityEdgeCase() {
        IntFunction<String> generateString = (int e) -> {
            return new Random().ints('a', 'z' + 1).limit(e).collect(
                    StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
        };

        Map<String, String[]> testMap = new HashMap<>();
        testMap.put(DonationRecordLogic.ID, new String[]{Integer.toString(1)});
        testMap.put(DonationRecordLogic.ADMINSTRATOR, new String[]{generateString.apply(1)});
        testMap.put(DonationRecordLogic.HOSPITAL, new String[]{generateString.apply(1)});
        testMap.put(DonationRecordLogic.TESTED, new String[]{"false"});
        testMap.put(DonationRecordLogic.CREATED, new String[]{"0001-01-01 01:00:00"});

        DonationRecord returned = logic.createEntity(testMap);
        assertEquals(Integer.parseInt(testMap.get(DonationRecordLogic.ID)[0]), returned.getId());
        assertEquals(testMap.get(DonationRecordLogic.HOSPITAL)[0], returned.getHospital());
        assertEquals(testMap.get(DonationRecordLogic.ADMINSTRATOR)[0], returned.getAdministrator());
        assertEquals(testMap.get(DonationRecordLogic.TESTED)[0], String.valueOf(returned.getTested()));
        assertEquals(testMap.get(DonationRecordLogic.CREATED)[0], logic.convertDateToString(returned.getCreated()));

        testMap = new HashMap<>();
        testMap.put(DonationRecordLogic.ID, new String[]{Integer.toString(1)});
        testMap.put(DonationRecordLogic.ADMINSTRATOR, new String[]{generateString.apply(100)});
        testMap.put(DonationRecordLogic.HOSPITAL, new String[]{generateString.apply(100)});
        testMap.put(DonationRecordLogic.TESTED, new String[]{"true"});
        testMap.put(DonationRecordLogic.CREATED, new String[]{"9999-12-30 24:59:59"});

        returned = logic.createEntity(testMap);
        assertEquals(Integer.parseInt(testMap.get(DonationRecordLogic.ID)[0]), returned.getId());
        assertEquals(testMap.get(DonationRecordLogic.HOSPITAL)[0], returned.getHospital());
        assertEquals(testMap.get(DonationRecordLogic.ADMINSTRATOR)[0], returned.getAdministrator());
        assertEquals(testMap.get(DonationRecordLogic.TESTED)[0], String.valueOf(returned.getTested()));
        assertEquals(testMap.get(DonationRecordLogic.CREATED)[0], logic.convertDateToString(returned.getCreated()));
    }

    @Test
    final void testGetColumnNames() {
        List<String> list = logic.getColumnNames();
        assertEquals(Arrays.asList("RecordId", "Person_id", "Donation_id", "Tested", "Adminstrator", "Hospital", "Created"), list);
    }

    @Test
    final void testGetColumnCodes() {
        List<String> list = logic.getColumnCodes();
        assertEquals(Arrays.asList(DonationRecordLogic.ID, DonationRecordLogic.PERSON_ID, DonationRecordLogic.DONATION_ID,
                DonationRecordLogic.TESTED, DonationRecordLogic.ADMINSTRATOR,
                DonationRecordLogic.HOSPITAL, DonationRecordLogic.CREATED), list);
    }

    @Test
    final void testExtractDataAsList() {
        List<?> list = logic.extractDataAsList(donationRecordExpected);
        assertEquals(donationRecordExpected.getId(), list.get(0));
        assertEquals(donationRecordExpected.getPerson(), list.get(1));
        assertEquals(donationRecordExpected.getBloodDonation(), list.get(2));
        assertEquals(donationRecordExpected.getTested(), list.get(3));
        assertEquals(donationRecordExpected.getAdministrator(), list.get(4));
        assertEquals(donationRecordExpected.getHospital(), list.get(5));
        assertEquals(donationRecordExpected.getCreated(), list.get(6));
    }

    @Test
    final void testExtractDataAsListInvalid() {
        List<?> list = logic.extractDataAsList(donationRecordExpected);
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(7));
    }

    @Test
    final void testSearch() {
        int foundFull = 0;
        //search for a substring of one of the fields in the expectedAccount
        String searchString = donationRecordExpected.getHospital().substring(3);
        //in account we only search for display name and user, this is completely based on your design for other entities.
        List<DonationRecord> returnedRecords = logic.search(searchString);
        for (DonationRecord record : returnedRecords) {
            //all accounts must contain the substring
            assertTrue(record.getHospital().contains(searchString) || record.getAdministrator().contains(searchString));
            //exactly one account must be the same
            if (record.getId().equals(donationRecordExpected.getId())) {
                assertDonRecordEquals(donationRecordExpected, record, false);
                foundFull++;
            }
        }
        assertEquals(1, foundFull, "if zero means not found, if more than one means duplicate");
    }

}
