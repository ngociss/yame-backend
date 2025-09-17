package vn.yame.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.yame.model.Address;
import vn.yame.model.User;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    Optional<Address> findAddressByUserAndIsDefault(User user, boolean isDefault);

    boolean existsAddressByUser(User user);
}
