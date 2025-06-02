package com.hospital.hospital.repo;

import com.hospital.hospital.model.District;
import com.hospital.hospital.model.Doctor;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Integer> {
  List<Doctor> findByIsTherapist(boolean isTherapist);

  List<Doctor> findByIsTherapistAndDistrict(boolean b, District district);
}
