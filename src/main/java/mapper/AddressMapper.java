package mapper;

import entity.Address;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AddressMapper {
    List<Address> findAllAddresses();

    Address findAddressById(@Param("addressId") int addressId);

    List<Address> findAddressByCityId(@Param("cityId") int cityId);

    List<Address> findAddressByPostalCode(@Param("postalCode") int postalCode);
}
