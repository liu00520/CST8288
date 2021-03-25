package dal;

import entity.DonationRecord;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import entity.Person;
import entity.BloodDonation;
import java.util.Date;

/**
 *
 * @author sarah Kelly
 */
public class DonationRecordDAL extends GenericDAL<DonationRecord> {

    public DonationRecordDAL() {
        super(DonationRecord.class);
    }

    @Override
    public List<DonationRecord> findAll() {

        return findResults("DonationRecord.findAll", null);
    }

    @Override
    public DonationRecord findById(int id) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);

        return findResult("DonationRecord.findByRecordId", map);
    }

    // is hashMap ok for holding boolean 0 or 1?
    public List<DonationRecord> findByTested(boolean test) {

        Map<String, Object> map = new HashMap<>();
        map.put("tested", test);

        return findResults("DonationRecord.findByTested", map);

    }

    public List<DonationRecord> findByAdministrator(String administrator) {

        Map<String, Object> map = new HashMap<>();
        map.put("administrator", administrator);

        return findResults("DonationRecord.findByAdministrator", map);

    }

    public List<DonationRecord> findByHospital(String hospital) {

        Map<String, Object> map = new HashMap<>();
        map.put("hospital", hospital);

        return findResults("DonationRecord.findByHospital", map);

    }

    public List<DonationRecord> findByPerson(Person id) {

        Map<String, Object> map = new HashMap<>();
        map.put("person_id", id);

        return findResults("DonationRecord.findByPerson", map);

    }
    
       //find results for all methods except id because id is teh only unique one 
    public List<DonationRecord> findByDonation(BloodDonation bloodDonation) {

        Map<String, Object> map = new HashMap<>();
        map.put("donation_id", bloodDonation);

        return findResults("DonationRecord.findByDonation", map);

    }

    public List<DonationRecord> findByCreated(Date created) {

        Map<String, Object> map = new HashMap<>();
        map.put("created", created);

        return findResults("DonationRecord.findByCreated", map);

    }

}
