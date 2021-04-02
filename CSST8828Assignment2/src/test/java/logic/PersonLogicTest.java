package logic;

import common.EMFactory;
import common.TomcatStartUp;
import common.ValidationException;
import entity.Person;
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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Mark Newport
 * Testing each PersonLogic method for edge, normal, and error case
 */
public class PersonLogicTest {
    
    private PersonLogic logic;
    private Person personExpected;
    
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
        logic = LogicFactory.getFor("Person");
        EntityManager entity = EMFactory.getEMF().createEntityManager();
        entity.getTransaction().begin();
        
        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Smith");
        person.setAddress("JUNIT 5");
        person.setPhone("123456789");
        person.setBirth(logic.convertStringToDate("1990-10-10 10:10:10"));
        
        personExpected = entity.merge(person);
        entity.getTransaction().commit();
        entity.close();
    }
    
    @AfterEach
    final void tearDown() throws Exception {
        if(personExpected != null) {
            logic.delete(personExpected);
        }    
    }

    @Test
    final void testGetAll() {
        List<Person> list = logic.getAll();
        int originalSize = list.size();
        assertNotNull(personExpected);
        assertTrue(originalSize > 0);
        logic.delete(personExpected);
        list = logic.getAll();
        assertEquals(originalSize-1, list.size());
    }
    
    @Test
    final void testGetWithId() {
        Person returnPerson = logic.getWithId(personExpected.getId());
        assertPersonEquals(personExpected, returnPerson);
    }
    
    @Test
    final void testGetWithIdNormal() {
        Person person = logic.getWithId(personExpected.getId());
        assertNotNull(person);
        person.setId(20);
        assertNotEquals(logic.getWithId(person.getId()), personExpected.getId());
    }
    
    @Test
    final void testGetWithIdInvalid() {
        personExpected.setId(null);
        assertNotNull(personExpected);
        assertThrows(NullPointerException.class,() -> logic.getWithId(personExpected.getId()));
    }
    
    final void testGetPersonWithPhone() {
        List<Person> phonePerson = logic.getPersonWithPhone(personExpected.getPhone());
        assertTrue(phonePerson.contains(personExpected));
        assertEquals(2, phonePerson.size());
    }
    
    @Test
    final void testGetPersonWithFirstName() {
        assertNotNull(personExpected);
        List<Person> firstNamePerson = logic.getPersonWithFirstName(personExpected.getFirstName());
        for(Person person : firstNamePerson) {
            assertEquals(personExpected.getFirstName(), person.getFirstName());
            if(person.getId().equals(personExpected.getId())) {
                assertPersonEquals(personExpected, person);
            }
        }
    }
    
    @Test
    final void testGetPersonWithLastName() {
        assertNotNull(personExpected);
        List<Person> lastNamePerson = logic.getPersonWithLastName(personExpected.getLastName());
        for(Person person : lastNamePerson) {
            assertEquals(personExpected.getLastName(), person.getLastName());
            if(person.getId().equals(personExpected.getId())){
                assertPersonEquals(personExpected, person);
            }
        }
    }
    
    @Test
    final void testGetPersonWithAddress() {
        assertNotNull(personExpected);
        List<Person> address = logic.getPersonWithAddress(personExpected.getAddress());
        for(Person person : address) {
            assertEquals(personExpected.getAddress(), person.getAddress());
            if(person.getId().equals(personExpected.getId())) {
                assertPersonEquals(personExpected, person);
            }
        }
    }
    
    @Test
    final void testCreateEntity() {
        Map<String, String[]> testMap = new HashMap<>();  
        testMap.put(PersonLogic.ID, new String[]{Integer.toString(personExpected.getId())});
        testMap.put(PersonLogic.FIRST_NAME, new String[]{personExpected.getFirstName()});
        testMap.put(PersonLogic.LAST_NAME, new String[]{personExpected.getLastName()});
        testMap.put(PersonLogic.PHONE, new String[]{personExpected.getPhone()});
        testMap.put(PersonLogic.ADDRESS, new String[]{personExpected.getAddress()});
        testMap.put(PersonLogic.BIRTH, new String[]{logic.convertDateToString(personExpected.getBirth())});
        
        Person created = logic.createEntity(testMap);
        assertPersonEquals(personExpected, created);
    }
    
    @Test
    final void testCreateEntityAdd() {
        Map<String, String[]> testMap = new HashMap<>();
        testMap.put(PersonLogic.FIRST_NAME, new String[] {"Test FirstName"});
        testMap.put(PersonLogic.LAST_NAME, new String[] {"TestEntity"});
        testMap.put(PersonLogic.PHONE, new String[] {"9999999999"});
        testMap.put(PersonLogic.ADDRESS, new String[] {"Test"});
        testMap.put(PersonLogic.BIRTH, new String[] {"1990-10-10 10:10:10.0"});
        
        Person returned = logic.createEntity(testMap);
        logic.add(returned);
        returned = logic.getWithId(returned.getId());
        
        assertEquals(testMap.get(PersonLogic.FIRST_NAME)[0], returned.getFirstName());
        assertEquals(testMap.get(PersonLogic.LAST_NAME)[0], returned.getLastName());
        assertEquals(testMap.get(PersonLogic.PHONE)[0], returned.getPhone());
        assertEquals(testMap.get(PersonLogic.ADDRESS)[0], returned.getAddress());
        assertEquals(testMap.get(PersonLogic.BIRTH)[0], returned.getBirth().toString());
 
        logic.delete(returned);
    }
    
    /**
     * Testing the null and empty values of createEntity; Empty and null both throw IndexOutOfBoundsException
     * except for ID which also throws NullPointerException
     */
    @Test
    final void testCreateEntityNullAndEmpty() {
        Map<String, String[]> testMap = new HashMap<>();
        Consumer<Map<String, String[]>>fillMap = (Map<String,String[]>map) -> {
            
            map.clear();
            testMap.put(PersonLogic.ID, new String[]{Integer.toString(personExpected.getId())});      
            testMap.put(PersonLogic.FIRST_NAME, new String[]{personExpected.getFirstName()});     
            testMap.put(PersonLogic.LAST_NAME, new String[]{personExpected.getLastName()});       
            testMap.put(PersonLogic.PHONE, new String[]{personExpected.getPhone()});       
            testMap.put(PersonLogic.ADDRESS, new String[]{personExpected.getAddress()});        
            testMap.put(PersonLogic.BIRTH, new String[]{logic.convertDateToString(personExpected.getBirth())});
        };
        fillMap.accept(testMap);
        testMap.replace(PersonLogic.ID, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(testMap));
        testMap.replace(PersonLogic.ID, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(testMap));
        
        testMap.replace(PersonLogic.FIRST_NAME, null);
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(testMap));
 
        testMap.replace(PersonLogic.LAST_NAME, null);
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(testMap));
        
        testMap.replace(PersonLogic.PHONE, null);
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(testMap));
        
        testMap.replace(PersonLogic.ADDRESS, null);
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(testMap));
        
        testMap.replace(PersonLogic.BIRTH, null);
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(testMap));
    }
    
    @Test
    final void testCreateEntityBadLength() {
        Map<String, String[]> testMap = new HashMap<>();
        Consumer<Map<String, String[]>>fillMap = (Map<String,String[]>map) -> {
            
            map.clear();
            testMap.put(PersonLogic.ID, new String[]{Integer.toString(personExpected.getId())});      
            testMap.put(PersonLogic.FIRST_NAME, new String[]{personExpected.getFirstName()});     
            testMap.put(PersonLogic.LAST_NAME, new String[]{personExpected.getLastName()});       
            testMap.put(PersonLogic.PHONE, new String[]{personExpected.getPhone()});       
            testMap.put(PersonLogic.ADDRESS, new String[]{personExpected.getAddress()});        
            testMap.put(PersonLogic.BIRTH, new String[]{logic.convertDateToString(personExpected.getBirth())});
        };
        
        IntFunction<String> generateString = (int length) -> {
            return new Random().ints('a','z'+1).limit(length).collect(
                    StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
        };
      
        fillMap.accept(testMap);
        testMap.replace(PersonLogic.ID, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(testMap));
        fillMap.accept(testMap);
        testMap.replace(PersonLogic.ID, new String[]{"45g"});
        assertThrows(ValidationException.class, () -> logic.createEntity(testMap));
        
        testMap.replace(PersonLogic.FIRST_NAME, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(testMap));
        fillMap.accept(testMap);
        testMap.replace(PersonLogic.FIRST_NAME, new String[]{generateString.apply(51)});
        assertThrows(ValidationException.class, () -> logic.createEntity(testMap));
        
        testMap.replace(PersonLogic.LAST_NAME, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(testMap));
        fillMap.accept(testMap);
        testMap.replace(PersonLogic.ID, new String[]{generateString.apply(51)});
        assertThrows(ValidationException.class, () -> logic.createEntity(testMap));
        
    }
    
    @Test 
    final void testGetColumnNames() {
        List<String> list = logic.getColumnNames();
        assertEquals(Arrays.asList("ID","First Name","Last Name","Phone", "Address","Birthdate"), list);
    }
  
    @Test
    final void testGetColumnCodes() {
        List<String> list = logic.getColumnCodes();
        assertEquals(Arrays.asList(PersonLogic.ID, PersonLogic.FIRST_NAME, PersonLogic.LAST_NAME,
        PersonLogic.PHONE, PersonLogic.ADDRESS, PersonLogic.BIRTH), list);
    }
    
    @Test
    final void testExtractDataAsList() {
        List<?> list = logic.extractDataAsList(personExpected);
        assertEquals(personExpected.getId(), list.get(0));
        assertEquals(personExpected.getFirstName(), list.get(1));
        assertEquals(personExpected.getLastName(), list.get(2));
        assertEquals(personExpected.getPhone(), list.get(3));
        assertEquals(personExpected.getAddress(), list.get(4));
        assertEquals(personExpected.getBirth(), list.get(5));
    }
   
    @Test
    final void testExtractDataAsListInvalid() {
        List<?> list = logic.extractDataAsList(personExpected);
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(6));
    }
    
        private void assertPersonEquals(Person expected, Person actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getAddress(), actual.getAddress());
        assertEquals(expected.getPhone(), actual.getPhone());
        assertEquals(expected.getBirth(), actual.getBirth());
    }
}
