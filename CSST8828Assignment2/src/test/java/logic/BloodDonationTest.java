package logic;

import common.EMFactory;
import common.TomcatStartUp;
import entity.BloodBank;
import entity.BloodDonation;
import entity.BloodGroup;
import entity.RhesusFactor;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * This class is has the example of how to add dependency when working with junit. it is commented because some
 * components need to be made first. You will have to import everything you need.
 *
 * @author Shariar (Shawn) Emami
 */
class BloodDonationTest {

    private BloodDonationLogic logic;
    private BloodDonation expectedEntity;

    @BeforeAll
    final static void setUpBeforeClass() throws Exception {
        TomcatStartUp.createTomcat( "/SimpleBloodBank", "common.ServletListener", "simplebloodbank-PU-test" );
    }
//
    @AfterAll
    final static void tearDownAfterClass() throws Exception {
        TomcatStartUp.stopAndDestroyTomcat();
    }
//
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
        //check if the depdendecy exists on DB already
        //em.find takes two arguments, the class type of return result and the primery key.
        BloodBank bb = em.find( BloodBank.class, 1 );
        //if result is null create the entity and persist it
        if( bb == null ){
            //cearet object
            bb = new BloodBank();
            bb.setName( "JUNIT" );
            bb.setPrivatelyOwned( true );
            bb.setEstablished( logic.convertStringToDate( "1111-11-11 11:11:11" ) );
            bb.setEmplyeeCount( 111 );
            //persist the dependency first
            em.persist( bb );
        }

        //create the desired entity
        BloodDonation entity = new BloodDonation();
        entity.setMilliliters( 100 );
        entity.setBloodGroup( BloodGroup.AB );
        entity.setRhd( RhesusFactor.Negative );
        entity.setCreated( logic.convertStringToDate( "1111-11-11 11:11:11" ) );
        //add dependency to the desired entity
        entity.setBloodBank( bb );

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
    
    /**
     * helper method for testing all account fields
     *
     * @param expected
     * @param actual
     */
    private void assertBloodDonationEquals( BloodDonation expected, BloodDonation actual ) {
        //assert all field to guarantee they are the same
        assertEquals( expected.getId(), actual.getId() );
        assertEquals( expected.getMilliliters(), actual.getMilliliters() );
        assertEquals( expected.getBloodGroup(), actual.getBloodGroup() );
        assertEquals( expected.getRhd(), actual.getRhd() );
        assertEquals( expected.getCreated(), actual.getCreated() );
        assertEquals( expected.getBloodBank(), actual.getBloodBank() );
        assertEquals( expected.getDonationRecordSet(), actual.getDonationRecordSet() );
    }
    
    @Test
    final void testGetWithId() {
        //using the id of test account get another account from logic
        BloodDonation returnedDonation = logic.getWithId( expectedEntity.getId() );

        //the two accounts (testAcounts and returnedAccounts) must be the same
        assertBloodDonationEquals( expectedEntity, returnedDonation );
    }
    
    @Test
    final void testGetBloodDonationWithBloodGroup() {
        List<BloodDonation> returnedDonations = logic.getBloodDonationWithBloodGroup(expectedEntity.getBloodGroup());
        
        //the two accounts (testAcounts and returnedAccounts) must be the same
        for (BloodDonation donation: returnedDonations) {
            //all accounts must have the same password
            assertEquals( expectedEntity.getBloodBank(), donation.getBloodBank());
        }
    }
    
    @Test
    final void testGetBloodDonationWithMilliliters() {
        List<BloodDonation> returnedDonations = logic.getBloodDonationWithMilliliters(expectedEntity.getMilliliters());
        
        //the two accounts (testAcounts and returnedAccounts) must be the same
        for (BloodDonation donation: returnedDonations) {
            //all accounts must have the same password
            assertEquals( expectedEntity.getMilliliters(), donation.getMilliliters());
        }
    }
    
    @Test
    final void testGetBloodDonationWithRhd() {
        List<BloodDonation> returnedDonations = logic.getBloodDonationWithRhd(expectedEntity.getRhd());
        
        //the two accounts (testAcounts and returnedAccounts) must be the same
        for (BloodDonation donation: returnedDonations) {
            //all accounts must have the same password
            assertEquals( expectedEntity.getRhd(), donation.getRhd());
        }
    }
    
    @Test
    final void testGetBloodDonationWithCreated() {
        List<BloodDonation> returnedDonations = logic.getBloodDonationWithCreated(expectedEntity.getCreated());
        
        //the two accounts (testAcounts and returnedAccounts) must be the same
        for (BloodDonation donation: returnedDonations) {
            //all accounts must have the same password
            assertEquals( expectedEntity.getCreated(), donation.getCreated());
        }
    }
    
    @Test
    final void testCreateEntityAndAdd() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put( BloodDonationLogic.BLOOD_GROUP, new String[]{ "Test Create Entity" } );
        sampleMap.put( BloodDonationLogic.MILLILITERS, new String[]{ "testCreateAccount" } );
        //sampleMap.put( BloodDonationLogic.BANK_ID, new String[]{ "create" } );
        sampleMap.put( BloodDonationLogic.RHESUS_FACTOR, new String[]{ "create" } );
        sampleMap.put( BloodDonationLogic.CREATED, new String[]{ "create" } );
        sampleMap.put( BloodDonationLogic.ID, new String[]{ "create" } );

        BloodDonation returnedDonation = logic.createEntity( sampleMap );
        logic.add( returnedDonation );

        returnedDonation = logic.getWithId(returnedDonation.getId());

        //assertEquals( sampleMap.get( BloodDonationLogic.BLOOD_BANK )[ 0 ], returnedDonation.() );
        assertEquals( sampleMap.get( BloodDonationLogic.BLOOD_GROUP )[ 0 ], returnedDonation.getBloodGroup() );
        assertEquals( sampleMap.get( BloodDonationLogic.CREATED )[ 0 ], returnedDonation.getCreated() );
        assertEquals( sampleMap.get( BloodDonationLogic.MILLILITERS )[ 0 ], returnedDonation.getMilliliters() );
        assertEquals( sampleMap.get( BloodDonationLogic.RHESUS_FACTOR )[ 0 ], returnedDonation.getRhd());
        //assertEquals( sampleMap.get( BloodDonationLogic.ID )[ 0 ], returnedDonation.getId() );

        logic.delete( returnedDonation );
    }
    
     @Test
    final void testCreateEntity() {
        Map<String, String[]> sampleMap = new HashMap<>();
        assertEquals( sampleMap.get( BloodDonationLogic.ID )[ 0 ], expectedEntity.getId() );
        assertEquals( sampleMap.get( BloodDonationLogic.BLOOD_GROUP )[ 0 ], expectedEntity.getBloodGroup() );
        assertEquals( sampleMap.get( BloodDonationLogic.CREATED )[ 0 ], expectedEntity.getCreated() );
        assertEquals( sampleMap.get( BloodDonationLogic.MILLILITERS )[ 0 ], expectedEntity.getMilliliters() );
        assertEquals( sampleMap.get( BloodDonationLogic.RHESUS_FACTOR )[ 0 ], expectedEntity.getRhd());

        BloodDonation returnedDonation = logic.createEntity( sampleMap );

        assertBloodDonationEquals( expectedEntity, returnedDonation );
    }
    
    @Test
    final void testCreateEntityNullAndEmpty() {
        Map<String, String[]> testMap = new HashMap<>();
        Consumer<Map<String, String[]>>fillMap = (Map<String,String[]>map) -> {
            
            map.clear();
            testMap.put( BloodDonationLogic.ID, new String[]{ Integer.toString(expectedEntity.getId() ) } );       
            testMap.put( BloodDonationLogic.BLOOD_GROUP, new String[]{String.valueOf(expectedEntity.getBloodGroup()) } );      
            testMap.put( BloodDonationLogic.CREATED, new String[]{logic.convertDateToString(expectedEntity.getCreated())});
            testMap.put( BloodDonationLogic.MILLILITERS, new String[]{ Integer.toString(expectedEntity.getMilliliters() ) } );     
            testMap.put( BloodDonationLogic.RHESUS_FACTOR, new String[]{String.valueOf(expectedEntity.getRhd())} );
        };
        fillMap.accept(testMap);
        testMap.replace(BloodDonationLogic.ID, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(testMap));
        testMap.replace(BloodDonationLogic.ID, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(testMap));
        
        testMap.replace(BloodDonationLogic.BLOOD_GROUP, null);
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(testMap));
 
        testMap.replace(BloodDonationLogic.MILLILITERS, null);
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(testMap));
        
        testMap.replace(BloodDonationLogic.RHESUS_FACTOR, null);
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(testMap));
        
        testMap.replace(BloodDonationLogic.CREATED, null);
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(testMap));
        
    }
    
    @Test
    final void testCreateEntityEdgeValues() {
        IntFunction<String> generateString = ( int length ) -> {
          
            return new Random().ints( 'a', 'z' + 1 ).limit( length )
                    .collect( StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append )
                    .toString();
        };

        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put( BloodDonationLogic.ID, new String[]{ Integer.toString( 1 ) } );
        sampleMap.put( BloodDonationLogic.BLOOD_GROUP, new String[]{ generateString.apply( 1 ) } );
        sampleMap.put( BloodDonationLogic.MILLILITERS, new String[]{ generateString.apply( 1 ) } );
        sampleMap.put( BloodDonationLogic.RHESUS_FACTOR, new String[]{ generateString.apply( 1 ) } );
        sampleMap.put( BloodDonationLogic.CREATED, new String[]{"0001-01-01 01:00:00"});

        //idealy every test should be in its own method
        BloodDonation returnedDonation = logic.createEntity( sampleMap );
        assertEquals(Integer.parseInt(sampleMap.get(BloodDonationLogic .ID)[0]), returnedDonation.getId());
        assertEquals(sampleMap.get(BloodDonationLogic.BLOOD_GROUP)[0], returnedDonation.getBloodGroup());
        assertEquals( sampleMap.get( BloodDonationLogic.RHESUS_FACTOR )[0], returnedDonation.getRhd() );
        assertEquals( sampleMap.get( BloodDonationLogic.MILLILITERS )[0], returnedDonation.getMilliliters() );
        assertEquals( sampleMap.get( BloodDonationLogic.CREATED )[0], returnedDonation.getCreated() );

        sampleMap = new HashMap<>();
        sampleMap.put( BloodDonationLogic.ID, new String[]{ Integer.toString( 1 ) } );
        sampleMap.put( BloodDonationLogic.BLOOD_GROUP, new String[]{ generateString.apply( 45 ) } );
        sampleMap.put( BloodDonationLogic.RHESUS_FACTOR, new String[]{ generateString.apply( 8 ) } );
        sampleMap.put( BloodDonationLogic.MILLILITERS, new String[]{ generateString.apply( 45 ) } );
        sampleMap.put( BloodDonationLogic.CREATED, new String[]{"3456-02-24 21:45:45"});

        //idealy every test should be in its own method
        returnedDonation = logic.createEntity( sampleMap );
        assertEquals( Integer.parseInt( sampleMap.get( BloodDonationLogic.ID )[ 0 ] ), returnedDonation.getId() );
        assertEquals(sampleMap.get(BloodDonationLogic.BLOOD_GROUP)[0], returnedDonation.getBloodGroup());
        assertEquals( sampleMap.get( BloodDonationLogic.RHESUS_FACTOR )[0], returnedDonation.getRhd() );
        assertEquals( sampleMap.get( BloodDonationLogic.MILLILITERS )[0], returnedDonation.getMilliliters() );
        assertEquals( sampleMap.get( BloodDonationLogic.CREATED )[0], returnedDonation.getCreated() );

    }
    
    
    @Test
    final void testGetColumnNames() {
        List<String> list = logic.getColumnNames();
        assertEquals( Arrays.asList("ID", "Milliliters", "Blood Group", "Rhesus Factor", "Bank ID", "Created"), list);
    }

    @Test
    final void testGetColumnCodes() {
        List<String> list = logic.getColumnCodes();
        assertEquals( Arrays.asList( BloodDonationLogic.ID, BloodDonationLogic.MILLILITERS, BloodDonationLogic.BLOOD_GROUP,
                BloodDonationLogic.RHESUS_FACTOR, BloodDonationLogic.BANK_ID, 
                BloodDonationLogic.CREATED ), list );
    }

    @Test
    final void testExtractDataAsList() {
        List<?> list = logic.extractDataAsList( expectedEntity );
        assertEquals( expectedEntity.getId(), list.get( 0 ) );
        assertEquals( expectedEntity.getMilliliters(), list.get( 1 ) );
        assertEquals( expectedEntity.getBloodGroup(), list.get( 2 ) );
        assertEquals( expectedEntity.getRhd(), list.get( 3 ) );
        assertEquals( expectedEntity.getCreated(), list.get( 4 ) );
    }
    
    @Test
    final void testExtractDataAsListInvalid() {
        List<?> list = logic.extractDataAsList(expectedEntity);
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(5));
    }

    
}
