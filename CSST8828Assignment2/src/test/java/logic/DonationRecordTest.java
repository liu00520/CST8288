package logic;

import common.EMFactory;
import common.TomcatStartUp;
import common.ValidationException;
import entity.Account;
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

        logic = LogicFactory.getFor( "DonationRecord" );
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
      Person  person= em.find(Person.class,1);
         if (person==null){
             person=new Person();
             person.setFirstName("Lily");
             person.setLastName("Johns");
             person.setAddress("JUNIT 5 Test");
             person.setPhone("123456789");
             person.setBirth(logic.convertStringToDate("1990-10-10 10:10:10"));
               em.persist(person);
         }
       
         //create BloodDonation for dependancy 
         BloodDonation  blDon= em.find(BloodDonation.class,1);
         if (blDon==null){
             blDon=new BloodDonation();
             blDon.setMilliliters(5);
             blDon.setBloodGroup(BloodGroup.B);
             blDon.setRhd(RhesusFactor.Positive);
             blDon.setCreated(logic.convertStringToDate("200-12-15 09:05:20"));
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
        if( donationRecordExpected != null ){
            logic.delete(donationRecordExpected );
        }
    }

    @Test
    final void testGetAll() {
        //get all the accounts from the DB
        List<DonationRecord> list = logic.getAll();
        //store the size of list, this way we know how many accounts exits in DB
        int originalSize = list.size();

        //make sure account was created successfully
        assertNotNull(donationRecordExpected );
        //delete the new account
        logic.delete(donationRecordExpected );

        //get all accounts again
        list = logic.getAll();
        //the new size of accounts must be one less
        assertEquals( originalSize - 1, list.size() );
    }

   
    /**
     * helper method for testing all account fields
     *
     * @param expected
     * @param actual
     */
    private void assertDonRecordEquals( DonationRecord expected, DonationRecord actual ) {
        //assert all field to guarantee they are the same
        assertDonRecordEquals(expected, actual,true);
    }
    private void assertDonRecordEquals( DonationRecord expected, DonationRecord actual, boolean testDepend ) {
        //assert all field to guarantee they are the same
        assertEquals( expected.getId(),actual.getId());
        if(testDepend){
        assertEquals( expected.getPerson().getId(), actual.getPerson().getId());
        assertEquals( expected.getBloodDonation().getId(), actual.getBloodDonation().getId());
        }
        assertEquals( expected.getTested(), actual.getTested() );
        assertEquals( expected.getAdministrator(),actual.getAdministrator());
        assertEquals( expected.getHospital(),actual.getHospital() );
        assertEquals( expected.getCreated(), actual.getCreated() );
    }

// failing nullpointer excption
    @Test
    final void testGetWithId() {
       
        
       DonationRecord returnedRecord = logic.getWithId(donationRecordExpected.getId());
       assertDonRecordEquals(donationRecordExpected, returnedRecord,true);
    }
    @Test
    final void testGetDonationRecordWithHospital() {
       List<DonationRecord> returnedRecords = logic.getDonationRecordWithHospital(donationRecordExpected.getHospital());

        //the two accounts (testAcounts and returnedAccounts) must be the same
      for(DonationRecord record: returnedRecords ) {
            //all accounts must have the same password
            assertEquals( donationRecordExpected.getHospital(), record.getHospital() );
    }
    }
    @Test
    final void testGetDonationRecordWithAdminstrator() {
       List<DonationRecord> returnedRecords = logic.getDonationRecordWithAdminstrator(donationRecordExpected.getAdministrator());

        //the two accounts (testAcounts and returnedAccounts) must be the same
      for(DonationRecord record: returnedRecords ) {
            //all accounts must have the same password
            assertEquals( donationRecordExpected.getAdministrator(), record.getAdministrator() );
    }
    }
    @Test
    final void testGetDonationRecordWithTested() {
       List<DonationRecord> returnedRecords = logic.getDonationRecordWithTested(donationRecordExpected.getTested());

        //the two accounts (testAcounts and returnedAccounts) must be the same
      for(DonationRecord record: returnedRecords ) {
            //all accounts must have the same password
            assertEquals( donationRecordExpected.getTested(), record.getTested() );
    }
    }
    @Test
    final void testFindByCreated() {
       List<DonationRecord> returnedRecords = logic.findByCreated(donationRecordExpected.getCreated());

        //the two accounts (testAcounts and returnedAccounts) must be the same
      for(DonationRecord record: returnedRecords ) {
            //all accounts must have the same password
            assertEquals( donationRecordExpected.getCreated(), record.getCreated() );
    }
    }

//    @Test
//    final void testCreateEntityAndAdd() {
//        Map<String, String[]> sampleMap = new HashMap<>();
//        sampleMap.put( DonationRecordLogic.HOSPITAL, new String[]{ "Test Create Entity" } );
//        sampleMap.put( DonationRecordLogic.ADMINSTRATOR, new String[]{ "testCreateAccount" } );
//        sampleMap.put( DonationRecordLogic.CREATED, new String[]{ "create" } );
//        sampleMap.put( DonationRecordLogic.TESTED, new String[]{ "create" } );
//         sampleMap.put( DonationRecordLogic.PERSON_ID, new String[]{ "create" } );
//          sampleMap.put( DonationRecordLogic.DONATION_ID, new String[]{ "create" } );
//
//        DonationRecord returnedRecord = logic.createEntity( sampleMap );
//        logic.add( returnedRecord );
//
//       // returnedRecord = logic.getDonationRecordWithHospital(returnedRecord.getHospital() );
//
//        assertEquals( sampleMap.get( DonationRecordLogic.HOSPITAL )[ 0 ], returnedRecord.getHospital() );
//        assertEquals( sampleMap.get( DonationRecordLogic.ADMINSTRATOR )[ 0 ], returnedRecord.getAdministrator() );
//        assertEquals( sampleMap.get( DonationRecordLogic.CREATED )[ 0 ], returnedRecord.getCreated() );
//        assertEquals( sampleMap.get( DonationRecordLogic.TESTED )[ 0 ], returnedRecord.getTested() );
//        assertEquals( sampleMap.get( DonationRecordLogic.PERSON_ID )[ 0 ], returnedRecord.getPerson() );
//        assertEquals( sampleMap.get( DonationRecordLogic.DONATION_ID )[ 0 ], returnedRecord.getBloodDonation() );
//
//        logic.delete( returnedRecord );
//    }
//
    @Test
    final void testCreateEntity() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(DonationRecordLogic.ID, new String[]{ Integer.toString( donationRecordExpected.getId() ) } );
        sampleMap.put( DonationRecordLogic.HOSPITAL, new String[]{ donationRecordExpected.getHospital() } );
        sampleMap.put( DonationRecordLogic.ADMINSTRATOR, new String[]{ donationRecordExpected.getAdministrator() } );
        sampleMap.put(DonationRecordLogic.CREATED, new String[]{logic.convertDateToString( donationRecordExpected.getCreated())});
        sampleMap.put( DonationRecordLogic.TESTED, new String[]{ String.valueOf(donationRecordExpected.getTested()) } );
        
        
        DonationRecord returnedRecord= logic.createEntity( sampleMap );
        assertDonRecordEquals(donationRecordExpected,returnedRecord,false);
    }
//
    @Test
    final void testCreateEntityNullAndEmptyValuesID() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            map.put( DonationRecordLogic.ID, new String[]{ Integer.toString( donationRecordExpected.getId() ) } );
         
        };

//        //idealy every test should be in its own method
        fillMap.accept( sampleMap );
        sampleMap.replace( DonationRecordLogic.ID, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
     
        
    }
    
    
    @Test
    final void testCreateEntityEmptyValuesID() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            map.put( DonationRecordLogic.ID, new String[]{ Integer.toString( donationRecordExpected.getId() ) } );
         
        };

//        //idealy every test should be in its own method
        fillMap.accept( sampleMap );
        sampleMap.replace( DonationRecordLogic.ID, new String[]{} );
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
        
    }
    
    
       @Test
    final void testCreateEntityNullAndEmptyValuesTested() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            map.put( DonationRecordLogic.TESTED, new String[]{String.valueOf(donationRecordExpected.getTested()) } );
   
        };
//        //idealy every test should be in its own method
        fillMap.accept( sampleMap );
        sampleMap.replace( DonationRecordLogic.TESTED, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
       
        
    }
      @Test
    final void testCreateEntityEmptyValuesTested() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            map.put( DonationRecordLogic.TESTED, new String[]{String.valueOf(donationRecordExpected.getTested()) } );
   
        };
//        //idealy every test should be in its own method
        fillMap.accept( sampleMap );
        sampleMap.replace(DonationRecordLogic.TESTED, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
        
    }
    
     @Test
    final void testCreateEntityNullValuesHospital() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            map.put( DonationRecordLogic.HOSPITAL, new String[]{donationRecordExpected.getHospital()} );
           
        };

//        //idealy every test should be in its own method
        fillMap.accept( sampleMap );
        sampleMap.replace( DonationRecordLogic.HOSPITAL, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
       
        
    }
    
    
     @Test
    final void testCreateEntityNullAndEmptyValuesHospital() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            map.put( DonationRecordLogic.HOSPITAL, new String[]{donationRecordExpected.getHospital()} );
           
        };

//        //idealy every test should be in its own method
        fillMap.accept( sampleMap );
        sampleMap.replace( DonationRecordLogic.HOSPITAL, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
       
        
    }
    
    
    @Test
    final void testCreateEntityEmptyValuesAdmin() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            map.put( DonationRecordLogic.ADMINSTRATOR, new String[]{donationRecordExpected.getAdministrator()} );
          
        };
//
//        //idealy every test should be in its own method
        fillMap.accept( sampleMap );
        sampleMap.replace(DonationRecordLogic.ADMINSTRATOR, new String[]{} );
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
        
    }
    
    
    @Test
    final void testCreateEntityNullValuesDate() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            map.put( DonationRecordLogic.CREATED, new String[]{String.valueOf(donationRecordExpected.getCreated())} );
        };

//        //idealy every test should be in its own method
        fillMap.accept( sampleMap );
        sampleMap.replace( DonationRecordLogic.CREATED, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
       
    }
    
    
     @Test
    final void testCreateEntityEmotyValuesDate() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            map.put( DonationRecordLogic.CREATED, new String[]{String.valueOf(donationRecordExpected.getCreated())} );
        };

//        //idealy every test should be in its own method
        fillMap.accept( sampleMap );
        sampleMap.replace(DonationRecordLogic.CREATED, new String[]{} );
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
        
    }
    
    
     @Test
    final void testCreateEntityEmptyValuesPerson() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            map.put(DonationRecordLogic.PERSON_ID, new String[]{String.valueOf(donationRecordExpected.getPerson().getId())} );
        };

//        //idealy every test should be in its own method
        fillMap.accept( sampleMap );
        sampleMap.replace(DonationRecordLogic.PERSON_ID, new String[]{} );
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
        
    }
    

    @Test
    final void testCreateEntityBadLengthValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
             map.clear();
            map.put( DonationRecordLogic.ID, new String[]{ Integer.toString( donationRecordExpected.getId() ) } );
          //  map.put( DonationRecordLogic.PERSON_ID, new String[]{ Integer.toString(donationRecordExpected.getPerson().getId())} );
        //    map.put( DonationRecordLogic.DONATION_ID, new String[]{ Integer.toString(donationRecordExpected.getBloodDonation().getId())} );
            map.put( DonationRecordLogic.TESTED, new String[]{String.valueOf(donationRecordExpected.getTested()) } );
            map.put( DonationRecordLogic.HOSPITAL, new String[]{ donationRecordExpected.getHospital()} );
            map.put( DonationRecordLogic.ADMINSTRATOR, new String[]{ donationRecordExpected.getAdministrator()} );
            map.put( DonationRecordLogic.CREATED, new String[]{String.valueOf(donationRecordExpected.getCreated())} );
        };

        IntFunction<String> generateString = ( int length ) -> {
            //https://www.baeldung.com/java-random-string#java8-alphabetic
            //from 97 inclusive to 123 exclusive
            return new Random().ints( 'a', 'z' + 1 ).limit( length )
                    .collect( StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append )
                    .toString();
        };
//
//        //idealy every test should be in its own method
        fillMap.accept( sampleMap );
        sampleMap.replace( DonationRecordLogic.ID, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( DonationRecordLogic.ID, new String[]{ "12b" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
    }
//        fillMap.accept( sampleMap );
//        sampleMap.replace( AccountLogic.NICKNAME, new String[]{ "" } );
//        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
//        sampleMap.replace( AccountLogic.NICKNAME, new String[]{ generateString.apply( 46 ) } );
//        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
//
//        fillMap.accept( sampleMap );
//        sampleMap.replace( AccountLogic.NAME, new String[]{ "" } );
//        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
//        sampleMap.replace( AccountLogic.NAME, new String[]{ generateString.apply( 46 ) } );
//        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
//
//        fillMap.accept( sampleMap );
//        sampleMap.replace( AccountLogic.USERNAME, new String[]{ "" } );
//        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
//        sampleMap.replace( AccountLogic.USERNAME, new String[]{ generateString.apply( 46 ) } );
//        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
//
//        fillMap.accept( sampleMap );
//        sampleMap.replace( AccountLogic.PASSWORD, new String[]{ "" } );
//        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
//        sampleMap.replace( AccountLogic.PASSWORD, new String[]{ generateString.apply( 46 ) } );
//        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
//    }
//
//    @Test
//    final void testCreateEntityEdgeValues() {
//        IntFunction<String> generateString = ( int length ) -> {
//            //https://www.baeldung.com/java-random-string#java8-alphabetic
//            return new Random().ints( 'a', 'z' + 1 ).limit( length )
//                    .collect( StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append )
//                    .toString();
//        };
//
//        Map<String, String[]> sampleMap = new HashMap<>();
//        sampleMap.put( AccountLogic.ID, new String[]{ Integer.toString( 1 ) } );
//        sampleMap.put( AccountLogic.NICKNAME, new String[]{ generateString.apply( 1 ) } );
//        sampleMap.put( AccountLogic.USERNAME, new String[]{ generateString.apply( 1 ) } );
//        sampleMap.put( AccountLogic.PASSWORD, new String[]{ generateString.apply( 1 ) } );
//        sampleMap.put( AccountLogic.NAME, new String[]{ generateString.apply( 1 ) } );
//
//        //idealy every test should be in its own method
////        //Account returnedAccount = logic.createEntity( sampleMap );
////        assertEquals( Integer.parseInt( sampleMap.get( AccountLogic.ID )[ 0 ] ), returnedAccount.getId() );
////        assertEquals( sampleMap.get( AccountLogic.NICKNAME )[ 0 ], returnedAccount.getNickname() );
////        assertEquals( sampleMap.get( AccountLogic.USERNAME )[ 0 ], returnedAccount.getUsername() );
////        assertEquals( sampleMap.get( AccountLogic.PASSWORD )[ 0 ], returnedAccount.getPassword() );
////        assertEquals( sampleMap.get( AccountLogic.NAME )[ 0 ], returnedAccount.getName() );
//
//        sampleMap = new HashMap<>();
//        sampleMap.put( AccountLogic.ID, new String[]{ Integer.toString( 1 ) } );
//        sampleMap.put( AccountLogic.NICKNAME, new String[]{ generateString.apply( 45 ) } );
//        sampleMap.put( AccountLogic.USERNAME, new String[]{ generateString.apply( 45 ) } );
//        sampleMap.put( AccountLogic.PASSWORD, new String[]{ generateString.apply( 45 ) } );
//        sampleMap.put( AccountLogic.NAME, new String[]{ generateString.apply( 45 ) } );
//
//        //idealy every test should be in its own method
////        returnedAccount = logic.createEntity( sampleMap );
////        assertEquals( Integer.parseInt( sampleMap.get( AccountLogic.ID )[ 0 ] ), returnedAccount.getId() );
////        assertEquals( sampleMap.get( AccountLogic.NICKNAME )[ 0 ], returnedAccount.getNickname() );
////        assertEquals( sampleMap.get( AccountLogic.USERNAME )[ 0 ], returnedAccount.getUsername() );
////        assertEquals( sampleMap.get( AccountLogic.PASSWORD )[ 0 ], returnedAccount.getPassword() );
////        assertEquals( sampleMap.get( AccountLogic.NAME )[ 0 ], returnedAccount.getName() );
//    }
//
    
    
    @Test
    final void testGetColumnNames() {
        List<String> list = logic.getColumnNames();
        assertEquals( Arrays.asList( "RecordId", "Person_id", "Donation_id", "Tested", "Adminstrator", "Hospital", "Created" ), list );
    }

    @Test
    final void testGetColumnCodes() {
        List<String> list = logic.getColumnCodes();
        assertEquals( Arrays.asList( DonationRecordLogic.ID, DonationRecordLogic.PERSON_ID, DonationRecordLogic.DONATION_ID,
                DonationRecordLogic.TESTED, DonationRecordLogic.ADMINSTRATOR, 
                DonationRecordLogic.HOSPITAL, DonationRecordLogic.CREATED ), list );
    }

    @Test
    final void testExtractDataAsList() {
        List<?> list = logic.extractDataAsList( donationRecordExpected );
        assertEquals( donationRecordExpected.getId(), list.get( 0 ) );
        assertEquals( donationRecordExpected.getPerson(), list.get( 1 ) );
        assertEquals( donationRecordExpected.getBloodDonation(), list.get( 2 ) );
        assertEquals( donationRecordExpected.getTested(), list.get( 3 ) );
        assertEquals( donationRecordExpected.getAdministrator(), list.get( 4 ) );
        assertEquals( donationRecordExpected.getHospital(), list.get( 5 ) );
        assertEquals( donationRecordExpected.getCreated(), list.get( 6 ) );
    }
}

    
