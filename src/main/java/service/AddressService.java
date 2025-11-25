package service;

import entity.Address;
import mapper.AddressMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.SqlSession;
import util.MyBatisUtil;

import java.util.List;

public class AddressService {
    public List<Address> findAllAddresses() {
        SqlSession session = MyBatisUtil.getSqlSessionFactory().openSession();
        try{
            AddressMapper mapper = session.getMapper(AddressMapper.class);
            return mapper.findAllAddresses();
        } finally {
            session.close();
        }
    }

    public Address findAddressById(@Param("addressId") int addressId) {
        SqlSession session = MyBatisUtil.getSqlSessionFactory().openSession();
        try{
            AddressMapper mapper = session.getMapper(AddressMapper.class);
            return mapper.findAddressById(addressId);
        } finally {
            session.close();
        }
    }

    public List<Address> findAddressByCityId(@Param("cityId") int cityId) {
        SqlSession session = MyBatisUtil.getSqlSessionFactory().openSession();
        try{
            AddressMapper mapper = session.getMapper(AddressMapper.class);
            return mapper.findAddressByCityId(cityId);
        } finally {
            session.close();
        }
    }

    public List<Address> findAddressByPostalCode(@Param("postalCode") int postalCode) {
        SqlSession session = MyBatisUtil.getSqlSessionFactory().openSession();
        try{
            AddressMapper mapper = session.getMapper(AddressMapper.class);
            return mapper.findAddressByPostalCode(postalCode);
        } finally {
            session.close();
        }
    }
}
