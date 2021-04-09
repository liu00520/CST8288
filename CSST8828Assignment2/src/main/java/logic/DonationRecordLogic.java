package logic;


import common.ValidationException;
import dal.DonationRecordDAL;
import entity.DonationRecord;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ObjIntConsumer;


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
    public static final String ID = "id";
      
    //can i create object of other entities here instead? 
    //so i can use it when use it to set on DonationRecord entity in craete entity
  
    DonationRecordLogic() {
        super(new DonationRecordDAL());
    }

    
    @Override
    public List<DonationRecord> getAll() {
          return get( () -> dal().findAll() );
    }

    @Override
    public DonationRecord getWithId(int id) {
               return get( () -> dal().findById(id));    
    
}

    public List<DonationRecord> getDonationRecordWithTested( boolean tested ) {
        return get( () -> dal().findByTested(tested) );
    }
    
    public List<DonationRecord> getDonationRecordWithHospital(String username ) {
        return get( () -> dal().findByHospital(username));
    }
      
    public List<DonationRecord> getDonationRecordWithAdminstrator(String administrator) {
       return get( () -> dal().findByAdministrator(administrator));
    }
    
    public List<DonationRecord> findByPerson(int personId) {
           return get( () -> dal().findByPerson(personId));
       }
    
    public List<DonationRecord> findByDonation(int donationId) {
           return get( () -> dal().findByDonation(donationId));
       }
    
       public List<DonationRecord> findByCreated(Date created) {
           return get( () -> dal().findByCreated(created));
       }
       
    /**
     * method for search function in JSP
     */
    @Override
    public List<DonationRecord> search(String search) {
        return get(() -> dal().findContaining(search));
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
        if( parameterMap.containsKey(ID) ){
            try {
                donationRecordEntity.setId(Integer.parseInt(parameterMap.get(ID)[0]));
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
        //index zero unless you have used duplicated key/name somewhere
        //extract the date from map .
         
       
        String dd= parameterMap.get(CREATED)[0].replace("T", " ");
        String hospital = parameterMap.get( HOSPITAL )[ 0 ];
        String administrator = parameterMap.get( ADMINSTRATOR )[ 0 ];
        String tested = parameterMap.get(TESTED)[ 0 ];
        
     
       // convert string to boolean
        boolean testBoolean=Boolean.parseBoolean(tested); 
        Date date = this.convertStringToDate(dd);

        
        
        //Validated strings
        validator.accept( hospital, 100 );
        validator.accept( administrator, 100 );
       
      
        
            

       //set values on entity
       donationRecordEntity.setTested(testBoolean);
       donationRecordEntity.setHospital(hospital );
       donationRecordEntity.setAdministrator(administrator );
       donationRecordEntity.setCreated(date);
       


        return donationRecordEntity;
    }
       
       
    @Override
    public List<String> getColumnNames() {
        return Arrays.asList( "RecordId", "Person_id", "Donation_id", "Tested", "Adminstrator", "Hospital", "Created" );
    }

    @Override
    public List<String> getColumnCodes() {
        return Arrays.asList( ID, PERSON_ID, DONATION_ID, TESTED, ADMINSTRATOR, HOSPITAL, CREATED );
    }

    
    @Override
    public List<?> extractDataAsList(DonationRecord e) {
        return Arrays.asList( e.getId(), e.getPerson(), e.getBloodDonation(), e.getTested(), e.getAdministrator(), 
                e.getHospital(), e.getCreated() );
    
     
    }
}