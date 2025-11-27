package service;

import entity.Address;
import mapper.AddressMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.SqlSession;
import util.MyBatisUtil;

import java.util.List;

public class AddressService {
    public List<Address> findAllAddresses() {
        try (SqlSession session = MyBatisUtil.getSqlSessionFactory().openSession()) {
            AddressMapper mapper = session.getMapper(AddressMapper.class);
            return mapper.findAllAddresses();
        }
    }

    public Address findAddressById(@Param("addressId") int addressId) {
        try (SqlSession session = MyBatisUtil.getSqlSessionFactory().openSession()) {
            AddressMapper mapper = session.getMapper(AddressMapper.class);
            return mapper.findAddressById(addressId);
        }
    }

    public List<Address> findAddressByCityId(@Param("cityId") int cityId) {
        try (SqlSession session = MyBatisUtil.getSqlSessionFactory().openSession()) {
            AddressMapper mapper = session.getMapper(AddressMapper.class);
            return mapper.findAddressByCityId(cityId);
        }
    }

    public List<Address> findAddressByPostalCode(@Param("postalCode") int postalCode) {
        try (SqlSession session = MyBatisUtil.getSqlSessionFactory().openSession()) {
            AddressMapper mapper = session.getMapper(AddressMapper.class);
            return mapper.findAddressByPostalCode(postalCode);
        }
    }
}
