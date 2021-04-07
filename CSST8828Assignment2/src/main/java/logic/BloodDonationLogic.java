package logic;

import common.ValidationException;
import dal.BloodDonationDAL;
import entity.BloodDonation;
import entity.BloodGroup;
import entity.RhesusFactor;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ObjIntConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author danny
 */
public class BloodDonationLogic extends GenericLogic<BloodDonation, BloodDonationDAL> {
    
    public static final String BANK_ID = "bank_id";
    public static final String MILLILITERS = "milliliters";
    public static final String BLOOD_GROUP = "blood_group";
    public static final String RHESUS_FACTOR = "rhesus_factor";
    public static final String CREATED = "created";
    public static final String ID = "id";
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat( "yyyy-MM-dd kk:mm:ss" );

    BloodDonationLogic() {
        super( new BloodDonationDAL() );
    }
    
    @Override
    public List<BloodDonation> getAll() {
        return get( () -> dal().findAll()); 
    }
    
    @Override
    public BloodDonation getWithId(int id) {
        return get( () -> dal().findById(id)); 
    }
    
    public List<BloodDonation> getBloodDonationWithMilliliters(int milliliters) {
        return get( () -> dal().findByMilliliters(milliliters));
    }
    
    public List<BloodDonation> getBloodDonationWithBloodGroup(BloodGroup bloodGroup) {
        return get( () -> dal().findByBloodGroup(bloodGroup));
    }
    
    public List<BloodDonation> getBloodDonationWithCreated(Date created) {
        return get( () -> dal().findByCreated(created));
    }
    
    public List<BloodDonation> getBloodDonationWithRhd(RhesusFactor rhd) {
        return get( () -> dal().findByRhd(rhd));
    }
    
     public List<BloodDonation> getBloodDonationWithBloodBank(int bankId) {
          return get( () -> dal().findByBloodBank(bankId));
    }
     
    @Override
    public BloodDonation createEntity(Map<String, String[]> parameterMap) {
        Objects.requireNonNull(parameterMap, "ParameterMap cannot be null"); 
        
        //this creates a new entity
        BloodDonation entity = new BloodDonation();
        
        //if statement will perform error checking
        if (parameterMap.containsKey(ID)) {
            try {
                entity.setId(Integer.parseInt(parameterMap.get(ID)[0]));
            }
            catch (java.lang.NumberFormatException ex) {
                throw new ValidationException(ex);
            }
        }
        
        ObjIntConsumer<String> validator = (value, length) -> {

            if (value == null || value.trim().isEmpty() || value.length() > length) {
                String error = "";
                if (value == null || value.trim().isEmpty()) {
                    error = "Value cannot be null or empty: " + value;
                }
                if (value.length() > length) {
                    error = "string length is " + value.length() + " > " + length;
                }
                throw new ValidationException(error);
            }
        };


        
        //String bankId = parameterMap.get(BANK_ID) [ 0 ];
        String milliliters = parameterMap.get(MILLILITERS) [ 0 ];
        String bloodGroup = parameterMap.get(BLOOD_GROUP)[ 0 ];
        //String rhesusFactor = parameterMap.get(RHESUS_FACTOR) [ 0 ];
        //extracting date from map
        String date = parameterMap.get(CREATED) [ 0 ];
        String id = parameterMap.get( ID )[ 0 ];
        
        //called method on RhesusFactorConverter to convert from string to rhesus
        RhesusFactor rhesusFactor = this.convertToEntityAttribute(parameterMap.get(RHESUS_FACTOR)[0]);
        
        //conversion, calling BloodGroup class, storing in a variable of type BloodGroup
        BloodGroup group = BloodGroup.valueOf(bloodGroup);
        
        
        //validating the data
        validator.accept(milliliters, 100);
        //validator.accept(bloodGroup, 8);
        //validator.accept(rhesusFactor, 2);
        validator.accept(id, 45);
        
        //converting date to appropriate format
        entity.setCreated(convertStringToDate(date));
        
        //BloodGroup.valueOf(bloodGroup);
                
        //setting the values on the entity
        entity.setMilliliters(Integer.parseInt(milliliters));
        entity.setRhd(rhesusFactor); 
        entity.setId(Integer.parseInt(id));
        entity.setBloodGroup(group);
          
        return entity;
    }
    
    @Override
    public List<String> getColumnNames() {
        return Arrays.asList ("ID", "Milliliters", "Blood Group", "Rhesus Factor", "Bank ID", "Created");
    }

    @Override
    public List<String> getColumnCodes() {
        return Arrays.asList (BANK_ID, MILLILITERS, BLOOD_GROUP, RHESUS_FACTOR, CREATED, ID); 
    }

    @Override
    public List<?> extractDataAsList(BloodDonation e) {
        return Arrays.asList(e.getBloodBank(), e.getMilliliters(), e.getBloodGroup(), e.getRhd(), e.getCreated(), e.getId()); 
    }
    
    @Override
    public Date convertStringToDate( String date ) {
        try {
            return FORMATTER.parse( date );
        } catch( ParseException ex ) {
            Logger.getLogger( GenericLogic.class.getName() ).log( Level.SEVERE, null, ex );
            throw new ValidationException( "failed to format String=\"" + date + "\" to a date object", ex );
        }
    }
    
    
    public RhesusFactor convertToEntityAttribute( String dbData ) {
        return RhesusFactor.getRhesusFactor( dbData );
    }
    
}
