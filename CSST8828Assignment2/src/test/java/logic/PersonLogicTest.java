package logic;

import common.EMFactory;
import common.TomcatStartUp;
import entity.Person;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    //same as above
    @Test
    final void testAllEdge() {
        List<Person> list = logic.getAll();
        assertEquals(1, list.size());
        logic.delete(personExpected);
        list = logic.getAll();
        assertTrue(list.isEmpty());
    }
     
    private void assertPersonEquals(Person expected, Person actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getAddress(), actual.getAddress());
        assertEquals(expected.getPhone(), actual.getPhone());
        assertEquals(expected.getBirth(), actual.getBirth());
    }
    
    @Test
    final void testGetWithId() {
        Person returnPerson = logic.getWithId(personExpected.getId());
        assertPersonEquals(personExpected, returnPerson);
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
    final void testGetPersonWithPhoneNormal() {
        
    }
    @Test
    final void testGetPersonWithPhonee() {
        personExpected.setPhone("");
        assertNotNull(personExpected);
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
    

}
