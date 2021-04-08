package dal;

import entity.BloodBank;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jia Liu 040992662
 */
public class BloodBankDAL extends GenericDAL<BloodBank> {
    
    public BloodBankDAL(){
        super( BloodBank.class );
    }
    
    @Override
    public List<BloodBank> findAll() {
        //first argument is a name given to a named query defined in appropriate entity
        //second argument is map used for parameter substitution.
        //parameters are names starting with : in named queries, :[name]
        return findResults( "BloodBank.findAll", null );
    }

    @Override
    public BloodBank findById( int bankId ) {
        Map<String, Object> map = new HashMap<>();
        map.put( "bankId", bankId );
        //first argument is a name given to a named query defined in appropriate entity
        //second argument is map used for parameter substitution.
        //parameters are names starting with : in named queries, :[name]
        //in this case the parameter is named "id" and value for it is put in map
        return findResult( "BloodBank.findByBankId", map );
    }
    
    public BloodBank findByName( String name ) {
        Map<String, Object> map = new HashMap<>();
        map.put( "name", name );
        return findResult( "BloodBank.findByName", map );
    }
    
    public List<BloodBank>  findByPrivatelyOwned ( boolean privatelyOwned) {
        Map<String, Object> map = new HashMap<>();
        map.put( "privatelyOwned", privatelyOwned );
        return findResults( "BloodBank.findByPrivatelyOwned", map );
    }
    public List<BloodBank>  findByEstablished ( Date established) {
        Map<String, Object> map = new HashMap<>();
        map.put( "Established", established );
        return findResults( "BloodBank.findByEstablished", map );
    }
    
    public List<BloodBank>  findByEmplyeeCount ( int emplyeeCount) {
        Map<String, Object> map = new HashMap<>();
        map.put( "EmplyeeCount", emplyeeCount );
        return findResults( "BloodBank.findByEmplyeeCount", map );
    }
    public BloodBank findByOwnerID( int ownerId ) {
        Map<String, Object> map = new HashMap<>();
        map.put( "ownerId", ownerId );
        return findResult( "BloodBank.findByOwnerId", map );
    }
    public List<BloodBank>  findContaining ( String search) {
        Map<String, Object> map = new HashMap<>();
        map.put( "containing", search );
        return findResults( "BloodBank.findContaining", map );
    }
    
        
        
    
    
    
    
    
    
}
