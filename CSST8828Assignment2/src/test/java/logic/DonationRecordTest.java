package logic;

import common.EMFactory;
import common.TomcatStartUp;
import common.ValidationException;
import entity.Account;
import entity.DonationRecord;
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

        DonationRecord entity = new DonationRecord();
        entity.setHospital( "Ottawa Hospital" );
        entity.setAdministrator( "Smith" );
        entity.setTested(false);
        entity.setCreated(logic.convertStringToDate("200-12-15 09:05:20"));
      
        //add an account to hibernate, account is now managed.
        //we use merge instead of add so we can get the updated generated ID.
        donationRecordExpected = em.merge( entity );
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
        assertEquals( expected.getId(),actual.getId() );
        assertEquals( expected.getHospital(),actual.getHospital() );
        assertEquals( expected.getAdministrator(), actual.getAdministrator() );
        assertEquals( expected.getTested(), actual.getTested() );
        assertEquals( expected.getCreated(), actual.getCreated() );
        assertEquals( expected.getPerson(), actual.getPerson() );
        assertEquals( expected.getBloodDonation(), actual.getBloodDonation() );
    }


    @Test
    final void testGetWithId() {
        //using the id of test account get another account from logic
        DonationRecord returnedRecord = logic.getWithId( donationRecordExpected.getId() );

        //the two accounts (testAcounts and returnedAccounts) must be the same
       assertDonRecordEquals(donationRecordExpected, returnedRecord );
    }
//
//    @Test
//    final void testGetDonationRecordWithHospital() {
//       List<DonationRecord> returnedRecords = logic.getDonationRecordWithHospital(expectedEntity.getHospital());
//
//        //the two accounts (testAcounts and returnedAccounts) must be the same
//      for(DonationRecord record: returnedRecords ) {
//            //all accounts must have the same password
//            assertEquals( expectedEntity.getHospital(), record.getHospital() );
//    }
//    }
//    @Test
//    final void testGetDonationRecordWithAdminstrator() {
//       List<DonationRecord> returnedRecords = logic.getDonationRecordWithAdminstrator(expectedEntity.getAdministrator());
//
//        //the two accounts (testAcounts and returnedAccounts) must be the same
//      for(DonationRecord record: returnedRecords ) {
//            //all accounts must have the same password
//            assertEquals( expectedEntity.getAdministrator(), record.getAdministrator() );
//    }
//    }
//    @Test
//    final void testGetDonationRecordWithTested() {
//       List<DonationRecord> returnedRecords = logic.getDonationRecordWithTested(expectedEntity.getTested());
//
//        //the two accounts (testAcounts and returnedAccounts) must be the same
//      for(DonationRecord record: returnedRecords ) {
//            //all accounts must have the same password
//            assertEquals( expectedEntity.getTested(), record.getTested() );
//    }
//    }
//    @Test
//    final void testFindByCreated() {
//       List<DonationRecord> returnedRecords = logic. findByCreated(expectedEntity.getCreated());
//
//        //the two accounts (testAcounts and returnedAccounts) must be the same
//      for(DonationRecord record: returnedRecords ) {
//            //all accounts must have the same password
//            assertEquals( expectedEntity.getCreated(), record.getCreated() );
//    }
//    }
//    // need to test person and BlookdDonation after merge
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
//    @Test
//    final void testCreateEntity() {
//        Map<String, String[]> sampleMap = new HashMap<>();
//        sampleMap.put(DonationRecordLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
//        sampleMap.put( DonationRecordLogic.HOSPITAL, new String[]{ expectedEntity.getHospital() } );
//        sampleMap.put( DonationRecordLogic.ADMINSTRATOR, new String[]{ expectedEntity.getAdministrator() } );
//       // sampleMap.put( DonationRecordLogic.CREATED, new String[]{ expectedEntity.getCreated() } );
//       // sampleMap.put( DonationRecordLogic.TESTED, new String[]{ expectedEntity.getTested() } );
//
//        DonationRecord returnedRecord= logic.createEntity( sampleMap );
//
//        assertDonRecordEquals( expectedEntity, returnedRecord );
//    }
//
//    @Test
//    final void testCreateEntityNullAndEmptyValues() {
//        Map<String, String[]> sampleMap = new HashMap<>();
//        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
//            map.clear();
//            map.put( AccountLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
////            map.put( AccountLogic.NICKNAME, new String[]{ expectedEntity.getNickname() } );
////            map.put( AccountLogic.USERNAME, new String[]{ expectedEntity.getUsername() } );
////            map.put( AccountLogic.PASSWORD, new String[]{ expectedEntity.getPassword() } );
////            map.put( AccountLogic.NAME, new String[]{ expectedEntity.getName() } );
//        };
//
//        //idealy every test should be in its own method
//        fillMap.accept( sampleMap );
//        sampleMap.replace( AccountLogic.ID, null );
//        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
//        sampleMap.replace( AccountLogic.ID, new String[]{} );
//        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
//
//        fillMap.accept( sampleMap );
//        sampleMap.replace( AccountLogic.NAME, null );
//        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
//        sampleMap.replace( AccountLogic.NAME, new String[]{} );
//        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
//
//        fillMap.accept( sampleMap );
//        //can be null
//        sampleMap.replace( AccountLogic.NICKNAME, new String[]{} );
//        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
//
//        fillMap.accept( sampleMap );
//        sampleMap.replace( AccountLogic.USERNAME, null );
//        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
//        sampleMap.replace( AccountLogic.USERNAME, new String[]{} );
//        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
//
//        fillMap.accept( sampleMap );
//        sampleMap.replace( AccountLogic.PASSWORD, null );
//        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
//        sampleMap.replace( AccountLogic.PASSWORD, new String[]{} );
//        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
//    }
//
//    @Test
//    final void testCreateEntityBadLengthValues() {
//        Map<String, String[]> sampleMap = new HashMap<>();
//        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
//            map.clear();
//            map.put( AccountLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
////            map.put( AccountLogic.NICKNAME, new String[]{ expectedEntity.getNickname() } );
////            map.put( AccountLogic.USERNAME, new String[]{ expectedEntity.getUsername() } );
////            map.put( AccountLogic.PASSWORD, new String[]{ expectedEntity.getPassword() } );
////            map.put( AccountLogic.NAME, new String[]{ expectedEntity.getName() } );
//        };
//
//        IntFunction<String> generateString = ( int length ) -> {
//            //https://www.baeldung.com/java-random-string#java8-alphabetic
//            //from 97 inclusive to 123 exclusive
//            return new Random().ints( 'a', 'z' + 1 ).limit( length )
//                    .collect( StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append )
//                    .toString();
//        };
//
//        //idealy every test should be in its own method
//        fillMap.accept( sampleMap );
//        sampleMap.replace( AccountLogic.ID, new String[]{ "" } );
//        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
//        sampleMap.replace( AccountLogic.ID, new String[]{ "12b" } );
//        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
//
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
//    @Test
//    final void testGetColumnNames() {
//        List<String> list = logic.getColumnNames();
//        assertEquals( Arrays.asList( "ID", "Name", "Nickname", "Username", "Password" ), list );
//    }
//
//    @Test
//    final void testGetColumnCodes() {
//        List<String> list = logic.getColumnCodes();
//        assertEquals( Arrays.asList( AccountLogic.ID, AccountLogic.NAME, AccountLogic.NICKNAME, AccountLogic.USERNAME, AccountLogic.PASSWORD ), list );
//    }
//
//    @Test
//    final void testExtractDataAsList() {
//        List<?> list = logic.extractDataAsList( expectedEntity );
//        assertEquals( expectedEntity.getId(), list.get( 0 ) );
////        assertEquals( expectedEntity.getName(), list.get( 1 ) );
////        assertEquals( expectedEntity.getNickname(), list.get( 2 ) );
////        assertEquals( expectedEntity.getUsername(), list.get( 3 ) );
////        assertEquals( expectedEntity.getPassword(), list.get( 4 ) );
//    }
}

    
