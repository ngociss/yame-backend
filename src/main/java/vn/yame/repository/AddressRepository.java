package vn.yame.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.yame.model.Address;
import vn.yame.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    Optional<Address> findByUserAndDefaultAddress(User user, boolean defaultAddress);

    boolean existsAddressByUser(User user);

    List<Address> findByUser(User user);
}
