package logic;

import common.ValidationException;
import entity.BloodBank;
import dal.BloodBankDAL;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.ObjIntConsumer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jia Liu 040992662
 */
public class BloodBankLogic extends GenericLogic<BloodBank, BloodBankDAL> {
    
    //static string to avoid typos
    public static final String OWNER_ID = "owner_id";
    public static final String PRIVATELY_OWNED = "privately_owned";
    public static final String ESTABLISHED = "established";
    public static final String NAME = "name";
    public static final String EMPLOYEE_COUNT = "employee_count";
    public static final String ID = "bankId";
    
    
    //constructor
    BloodBankLogic(){
        super (new BloodBankDAL());
    }
    
    @Override
    public List<BloodBank> getAll(){
        return get( () -> dal().findAll() );
    }
    
    @Override
    public BloodBank getWithId (int id){
        return get( () -> dal().findById( id ) );
    }
    
    public BloodBank getBloodBankWithName (String name){
        return get( () -> dal().findByName( name ) );
    }
    
    public List<BloodBank> getBloodBankWithPrivatelyOwned (boolean privatelyOwned ){
        return get( () -> dal().findByPrivatelyOwned( privatelyOwned ) );
    }
    
    public List<BloodBank> getBloodBankWithEstablished (Date established ){
        return get( () -> dal().findByEstablished( established ) );
    }
    
    public BloodBank getBloodBanksWithOwner (int ownerId){
        return get( () -> dal().findByOwnerID( ownerId ) );
    }
    
    public List<BloodBank> getBloodBanksWithEmplyeeCount (int count){
        return get( () -> dal().findByEmplyeeCount( count ) );
    }
    
    public BloodBank createEntity(Map<String, String[]> parameterMap){
        Objects.requireNonNull( parameterMap, "parameterMap cannot be null" );
        
        BloodBank entity = new BloodBank();
        
        //error checking
        if( parameterMap.containsKey( ID ) ){
            try {
                entity.setId( Integer.parseInt( parameterMap.get( ID )[ 0 ] ) );
            } catch( java.lang.NumberFormatException ex ) {
                throw new ValidationException( ex );
            }
        }
        
        ObjIntConsumer< String> validator = ( value, length ) -> {
            if( value == null || value.trim().isEmpty() || value.length() > length ){
                String error = "";
                if( value == null || value.trim().isEmpty() ){
                    error = "value cannot be null or empty: " + value;
                }
                if( value.length() > length ){
                    error = "string length is " + value.length() + " > " + length;
                }
                throw new ValidationException( error );
            }
        };
        
        String name = null;
        if( parameterMap.containsKey( NAME ) ){
            name = parameterMap.get( NAME )[ 0 ];
            validator.accept( name, 45 );
        }
        String privatelyOwned = parameterMap.get( PRIVATELY_OWNED )[ 0 ];
        
        String established= parameterMap.get(ESTABLISHED)[0].replace("T", " ");
        
        Date dateOfEstab = this.convertStringToDate(established);
      
        String employeeCount = parameterMap.get( EMPLOYEE_COUNT )[ 0 ];
        name = parameterMap.get( NAME )[ 0 ];
        String id = parameterMap.get( ID )[ 0 ];

        //validate the data
        validator.accept( name, 45 );
        validator.accept( privatelyOwned, 5 );
        validator.accept( established, 45 );
        validator.accept( employeeCount, 45 );
        validator.accept( id, 45 );
   
        //set values on entity
        
        entity.setEstablished(dateOfEstab);
        entity.setName( name );
        entity.setPrivatelyOwned( Boolean.parseBoolean(privatelyOwned) );
        entity.setEmplyeeCount(Integer.parseInt(employeeCount));
        entity.setId(Integer.parseInt(id));
        
    
        try {
            entity.setEstablished (new SimpleDateFormat("dd/MM/yyyy").parse(established));
        } catch (ParseException ex) {
            Logger.getLogger(BloodBankLogic.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        return entity;
        
    }
    @Override
    public List<String> getColumnNames() {
        return Arrays.asList( "BankID", "Name", "Owner ID", "Privately Owned", "Established", "Employee Count" );
    }
    
   @Override
    public List<String> getColumnCodes() {
        return Arrays.asList( ID, NAME, OWNER_ID, PRIVATELY_OWNED, ESTABLISHED, EMPLOYEE_COUNT );
    }
    @Override
    public List<?> extractDataAsList( BloodBank e ) {
        return Arrays.asList( e.getId(), e.getName(), e.getPrivatelyOwned(), e.getEstablished(), e.getEmplyeeCount() );
    }
 
    
}
