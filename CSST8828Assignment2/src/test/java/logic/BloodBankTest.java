package logic;

import common.EMFactory;
import common.TomcatStartUp;
import common.ValidationException;
import entity.BloodBank;
import entity.Person;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import javax.persistence.EntityManager;
import static logic.BloodBankLogic.EMPLOYEE_COUNT;
import static logic.BloodBankLogic.ESTABLISHED;
import static logic.BloodBankLogic.OWNER_ID;
import static logic.BloodBankLogic.PRIVATELY_OWNED;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Disabled;

/**
 *
 * @author Jia Liu 040992662
 */
@Disabled
public class BloodBankTest {
    
    //create instances for testing
    private BloodBankLogic logic;
    private PersonLogic personLogic;
    private BloodBank bloodBankExpected;
    private Person personExpected;
    
    public BloodBankTest() {
    }
    
    @BeforeAll
    public static void setUpBeforeClass() throws Exception{
        TomcatStartUp.createTomcat("/SimpleBloodBank", "common.ServletListener", "simplebloodbank-PU-test");
    }
    
    @AfterAll
    public static void tearDownAfterClass() throws Exception{
        TomcatStartUp.stopAndDestroyTomcat();
    }
    
    @BeforeEach
    public void setUp()throws Exception {
        logic = LogicFactory.getFor("BloodBank");
        personLogic = LogicFactory.getFor("Person");
        EntityManager entity = EMFactory.getEMF().createEntityManager();
        entity.getTransaction().begin();
        
        SimpleDateFormat DateFor = new SimpleDateFormat("dd/MM/yyyy");
        Date dateOfEstab = DateFor.parse("01/01/2000");
  
        BloodBank newBB = new BloodBank();
        newBB.setName("TestBank");
        newBB.setPrivatelyOwned(false);
        newBB.setEstablished(dateOfEstab);
        newBB.setEmplyeeCount(100);
        
        //A Person object needed for owner related testings
        Person person = new Person();
        person.setFirstName("Jia");
        person.setLastName("Liu");
        person.setAddress("JUNIT 5");
        person.setPhone("123456789");
        person.setBirth(personLogic.convertStringToDate("1990-10-10 10:10:10"));
        
        
        personExpected = entity.merge(person);
        bloodBankExpected = entity.merge(newBB);
        //commit the changes
        entity.getTransaction().commit();
        //close EntityManager
        entity.close();
        
    }
   
    
    @AfterEach
    public void tearDown() throws Exception{
        if( bloodBankExpected != null){
            logic.delete( bloodBankExpected );
        }
        if(personExpected != null) {
            personLogic.delete(personExpected);
        }
    }

   /**
     * helper method for later testing all blood bank fields
     *
     * @param expected
     * @param actual
     */
    private void assertBBEquals( BloodBank expected, BloodBank actual ) {
        //assert all field to guarantee they are the same
        assertEquals( expected.getId(), actual.getId() );
        assertEquals( expected.getName(), actual.getName() );
        assertEquals( expected.getPrivatelyOwned(), actual.getPrivatelyOwned() );
        assertEquals( expected.getEstablished(), actual.getEstablished() );
        assertEquals( expected.getEmplyeeCount(), actual.getEmplyeeCount() );
        assertEquals( expected.getOwner(), actual.getOwner() );
        assertEquals( expected.hashCode(), actual.hashCode() );
    }
    
    
    @Test
    final void testGetAll() {
        List<BloodBank> list = logic.getAll();
        int size = list.size();
        assertNotNull(bloodBankExpected);
        assertTrue(size > 0);
        logic.delete(bloodBankExpected);
        list = logic.getAll();
        assertEquals(0, list.size());
    }

    @Test
    final void testGetWithId() {
        BloodBank returneBloodBank = logic.getWithId(bloodBankExpected.getId());
        assertBBEquals(bloodBankExpected, returneBloodBank);
    }
    
    @Test
    final void testGetBloodBankWithName() {
        BloodBank returneBloodBank = logic.getBloodBankWithName(bloodBankExpected.getName());
        assertBBEquals(bloodBankExpected, returneBloodBank);
    }
    
    @Test
    final void testGetBloodBankWithPrivatelyOwned() {
        assertNotNull(bloodBankExpected);
        List<BloodBank> banksEstablishedAt = logic.getBloodBankWithPrivatelyOwned(bloodBankExpected.getPrivatelyOwned());
        for(BloodBank bank : banksEstablishedAt) {
            assertEquals(bloodBankExpected.getPrivatelyOwned(), bank.getPrivatelyOwned());
            if(bank.getId().equals(bloodBankExpected.getId())) {
                assertBBEquals(bloodBankExpected, bank);
            }
        }
    }
 
    @Test
    final void testGetBloodBankWithEstablished() {
        assertNotNull(bloodBankExpected);
        List<BloodBank> banksEstablishedAt = logic.getBloodBankWithEstablished(bloodBankExpected.getEstablished());
        for(BloodBank bank : banksEstablishedAt) {
            assertEquals(bloodBankExpected.getEstablished(), bank.getEstablished());
            if(bank.getId().equals(bloodBankExpected.getId())) {
                assertBBEquals(bloodBankExpected, bank);
            }
        }
    }
  
    @Test
    final void testGetBloodBanksWithOwner() {
        bloodBankExpected.setOwner(personExpected);
        Person returnePerson= bloodBankExpected.getOwner();
        assertEquals(personExpected, returnePerson);
    }
    
    @Test
    final void testGetBloodBanksWithEmplyeeCount() {
        assertNotNull(bloodBankExpected);
        List<BloodBank> banksEstablishedAt = logic.getBloodBanksWithEmplyeeCount(bloodBankExpected.getEmplyeeCount());
        for(BloodBank bank : banksEstablishedAt) {
            assertEquals(bloodBankExpected.getEmplyeeCount(), bank.getEmplyeeCount());
            if(bank.getId().equals(bloodBankExpected.getId())) {
                assertBBEquals(bloodBankExpected, bank);
            }
        }
    }
    
    @Test
    final void testCreateEntity() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(BloodBankLogic.ID, new String[]{Integer.toString(bloodBankExpected.getId())});
        sampleMap.put(BloodBankLogic.NAME, new String[]{bloodBankExpected.getName()});
        sampleMap.put(BloodBankLogic.PRIVATELY_OWNED, new String[]{Boolean.toString(bloodBankExpected.getPrivatelyOwned())});
        sampleMap.put(BloodBankLogic.ESTABLISHED, new String[]{logic.convertDateToString(bloodBankExpected.getEstablished())});
        sampleMap.put(BloodBankLogic.EMPLOYEE_COUNT, new String[]{Integer.toString(bloodBankExpected.getEmplyeeCount())});
       
        BloodBank returneBloodBank = logic.createEntity(sampleMap);
        assertBBEquals(bloodBankExpected, returneBloodBank);
        
    }
    
    @Test
    final void testCreateEntityNullAndEmptyValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            map.put( BloodBankLogic.ID, new String[]{ Integer.toString( bloodBankExpected.getId() ) } );
            map.put(BloodBankLogic.NAME, new String[]{bloodBankExpected.getName()});
            map.put(BloodBankLogic.PRIVATELY_OWNED, new String[]{Boolean.toString(bloodBankExpected.getPrivatelyOwned())});
            map.put(BloodBankLogic.ESTABLISHED, new String[]{logic.convertDateToString(bloodBankExpected.getEstablished())});
            map.put(BloodBankLogic.EMPLOYEE_COUNT, new String[]{Integer.toString(bloodBankExpected.getEmplyeeCount())});
        };

        //Most cannot be null
        fillMap.accept( sampleMap );
        sampleMap.replace( BloodBankLogic.ID, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( BloodBankLogic.ID, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( BloodBankLogic.NAME, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( BloodBankLogic.PRIVATELY_OWNED, new String[]{} );
        assertThrows( ArrayIndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( BloodBankLogic.ESTABLISHED, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        
        fillMap.accept( sampleMap );
        sampleMap.replace( BloodBankLogic.EMPLOYEE_COUNT, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
    }

    @Test
    final void testCreateEntityBadLengthValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            map.put( BloodBankLogic.ID, new String[]{ Integer.toString( bloodBankExpected.getId() ) } );
            map.put(BloodBankLogic.NAME, new String[]{bloodBankExpected.getName()});
            map.put(BloodBankLogic.PRIVATELY_OWNED, new String[]{Boolean.toString(bloodBankExpected.getPrivatelyOwned())});
            map.put(BloodBankLogic.ESTABLISHED, new String[]{logic.convertDateToString(bloodBankExpected.getEstablished())});
            map.put(BloodBankLogic.EMPLOYEE_COUNT, new String[]{Integer.toString(bloodBankExpected.getEmplyeeCount())});
        };
        
          IntFunction<String> generateString = (int e) -> {
            return new Random().ints('a','z'+1).limit(e).collect(
                    StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
        };

        fillMap.accept( sampleMap );
        sampleMap.replace( BloodBankLogic.ID, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( BloodBankLogic.ID, new String[]{ "12b" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );

        sampleMap.replace( BloodBankLogic.NAME, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
         sampleMap.replace(BloodBankLogic.NAME, new String[]{generateString.apply(101)});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
      
        fillMap.accept( sampleMap );
        sampleMap.replace( BloodBankLogic.ESTABLISHED, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( BloodBankLogic.ESTABLISHED, new String[]{ "asdgsdgsdg" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
//        sampleMap.replace( BloodBankLogic.EMPLOYEE_COUNT, new String[]{ "" } );
//        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
//        fillMap.accept( sampleMap );
//        sampleMap.replace( BloodBankLogic.EMPLOYEE_COUNT, new String[]{ "3dfbdfb" } );
//        assertThrows( NumberFormatException.class, () -> logic.createEntity( sampleMap ) );
    }
   
    @Test
    final void testGetColumnNames() {
        List<String> list = logic.getColumnNames();
        assertEquals( Arrays.asList( "BankID","Owner ID","Name", "Privately Owned", "Established", "Employee Count" ), list );
    }
    
     @Test
    final void testGetColumnCodes() {
        List<String> list = logic.getColumnCodes();
        assertEquals( Arrays.asList( BloodBankLogic.ID,BloodBankLogic.OWNER_ID,BloodBankLogic.NAME, BloodBankLogic.PRIVATELY_OWNED, BloodBankLogic.ESTABLISHED, BloodBankLogic.EMPLOYEE_COUNT ), list );
    }
}
