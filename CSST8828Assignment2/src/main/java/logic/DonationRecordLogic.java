package logic;

import common.ValidationException;
import dal.DonationRecordDAL;
import entity.BloodDonation;
import entity.DonationRecord;
import entity.Person;
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
 * @author sarah
 */
public class DonationRecordLogic extends GenericLogic<DonationRecord, DonationRecordDAL> {
    
    public static final String CREATED = "created";
    public static final String HOSPITAL = "hospital";
    public static final String ADMINSTRATOR = "administrator";
    public static final String TESTED = "tested";
    public static final String DONATION_ID = "donation_id";
    public static final String PERSON_ID = "person_id";
    public static final String RECORD_ID = "recordId";
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat( "yyyy-MM-dd kk:mm:ss" );
      
    int b;
       Person p2= new Person(b);
       
    
    public DonationRecordLogic(DonationRecordDAL dal) {
        super(dal);
    }
    
        @Override
    public List<DonationRecord> getAll() {
          return get( () -> dal().findAll() );
    }

    @Override
    public DonationRecord getWithId(int recordId) {
               return get( () -> dal().findById( recordId ) );    
    
}

    public List<DonationRecord> getDonationRecordWithTested( boolean tested ) {
        return get( () -> dal().findByTested(tested) );
    }
    
    public List<DonationRecord> getDonationRecordWithHospital(String hospital ) {
        return get( () -> dal().findByHospital(hospital));
    }
      
    public List<DonationRecord> getDonationRecordWithAdminstrator(String administrator) {
       return get( () -> dal().findByAdministrator(administrator));
    }
    
    public List<DonationRecord> findByPerson(Person person_id) {
           return get( () -> dal().findByPerson(person_id));
       }
    
    public List<DonationRecord> findByDonation(BloodDonation donation_id) {
           return get( () -> dal().findByDonation(donation_id));
       }
    
       public List<DonationRecord> findByCreated(Date created) {
           return get( () -> dal().findByCreated(created));
       }
       
       
         @Override
    public DonationRecord createEntity( Map<String, String[] > parameterMap ) {
        //do not create any logic classes in this method.

//        return new AccountBuilder().SetData( parameterMap ).build();
        Objects.requireNonNull( parameterMap, "parameterMap cannot be null" );
        //same as if condition below
        if (parameterMap == null) {
            throw new NullPointerException("parameterMap cannot be null");
       }

        //create a new Entity object
        DonationRecord donationRecordEntity = new DonationRecord();

        //ID is generated, so if it exists add it to the entity object
        //otherwise it does not matter as mysql will create an if for it.
        //the only time that we will have id is for update behaviour.
        if( parameterMap.containsKey( RECORD_ID ) ){
            try {
                donationRecordEntity.setId( Integer.parseInt( parameterMap.get( RECORD_ID )[ 0 ] ) );
            } catch( java.lang.NumberFormatException ex ) {
                throw new ValidationException( ex );
            }
        }

        //before using the values in the map, make sure to do error checking.
        //simple lambda to validate a string, this can also be place in another
        //method to be shared amoung all logic classes.
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

       
        //everything in the parameterMap is string so it must first be
        //converted to appropriate type. have in mind that values are
        //stored in an array of String; almost always the value is at
        //index zero unless you have used duplicated key/name somewhere.
        String displayPersonId = null;
        if( parameterMap.containsKey( PERSON_ID )){
           try{
            
            donationRecordEntity.setId( Integer.parseInt( parameterMap.get( PERSON_ID )[ 0 ] ));

            } catch( java.lang.NumberFormatException ex ) {
                throw new ValidationException( ex );
            }
        }
        
         if( parameterMap.containsKey( DONATION_ID )){
           try{
            
             String bD1= parameterMap.get( DONATION_ID )[ 0 ];
              
             int bd1= Integer.parseInt(bD1);
           
             // is that how i should deal with settingBloodDonation?
             BloodDonation bd= new BloodDonation(bd1);
            
            donationRecordEntity.setBloodDonation(bd);

            } catch( java.lang.NumberFormatException ex ) {
                throw new ValidationException( ex );
            }
        }
         
         
         if( parameterMap.containsKey( PERSON_ID )){
           try{
            
             String bD1= parameterMap.get( PERSON_ID )[ 0 ];
              
             //is that a more proper way to do it?
              b = Integer.parseInt(bD1);
       
             
          
            donationRecordEntity.setPerson(p2);

            } catch( java.lang.NumberFormatException ex ) {
                throw new ValidationException( ex );
            }
        }
        
          //extract the date from map first.
        
        String d= parameterMap.get(CREATED)[0];
     
        String hospital = parameterMap.get( HOSPITAL )[ 0 ];
        String administrator = parameterMap.get( ADMINSTRATOR )[ 0 ];
        //need to convert map 
        String tested = parameterMap.get(TESTED)[ 0 ];
        //need to convert
        
           String bd = parameterMap.get(DONATION_ID)[ 0 ];
              int o= Integer.parseInt(bd);
       // convert string to boolean
        boolean testBoolean=Boolean.parseBoolean(tested);  
        
      //convert to date 

        donationRecordEntity.setCreated(convertStringToDate(d));
        //validate the data
        validator.accept( hospital, 100 );
        validator.accept( administrator, 100 );
        
        
     //   validator.accept( tested), 45 );
  
     

        //set values on entity
       donationRecordEntity.setTested( testBoolean);
       donationRecordEntity.setHospital( hospital );
       donationRecordEntity.setAdministrator( administrator );
       

        return donationRecordEntity;
    }
       
       
    @Override
    public List<String> getColumnNames() {
        return Arrays.asList( "RecordId", "Personn_id", "Donation_id", "Tested", "Adminstrator", "Hospital", "Created" );
    }

    @Override
    public List<String> getColumnCodes() {
        return Arrays.asList( RECORD_ID, PERSON_ID, DONATION_ID, TESTED, ADMINSTRATOR, HOSPITAL, CREATED );
    }

    @Override
    public List<?> extractDataAsList(DonationRecord e) {
        return Arrays.asList( e.getId(), e.getPerson(), e.getBloodDonation(), e.getTested(), e.getAdministrator(), 
                e.getHospital(), e.getCreated() );
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
     
      
}