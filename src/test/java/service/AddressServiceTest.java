package service;

import entity.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AddressServiceTest {

    private AddressService addressService;

    @BeforeEach
    void setUp() { addressService = new AddressService(); }

    /*
        mysql> select count(*) from address;
        +----------+
        | count(*) |
        +----------+
        |      603 |
        +----------+
     */
    @Test
    void testFindAllAddresses() {
        List<Address> addresses = addressService.findAllAddresses();
        assertNotNull(addresses);
        assertFalse(addresses.isEmpty());
        assertTrue(addresses.size() >= 600);
    }

    /*
        mysql> select
                    address_id,
                    address,
                    address2,
                    district,
                    city_id,
                    postal_code,
                    phone,
                    last_update
               from
                    address
               where
                    address_id = 1;
        +------------+-------------------+----------+----------+---------+-------------+-------+---------------------+
        | address_id | address           | address2 | district | city_id | postal_code | phone | last_update         |
        +------------+-------------------+----------+----------+---------+-------------+-------+---------------------+
        |          1 | 47 MySakila Drive | NULL     | Alberta  |     300 |             |       | 2014-09-25 22:30:27 |
        +------------+-------------------+----------+----------+---------+-------------+-------+---------------------+
     */
    @Test
    void testFindAddressById() {
        Address address = addressService.findAddressById(1);
        assertNotNull(address);
        assertEquals("47 MySakila Drive", address.getAddress());
        assertNull(address.getAddress2());
        assertEquals("Alberta", address.getDistrict());
        assertEquals(300, address.getCityId());
    }

    /*
        mysql> select
                    address_id,
                    address,
                    address2,
                    district,
                    city_id,
                    postal_code,
                    phone,
                    last_update
                from
                    address
                where
                    city_id = 300;
        +------------+-------------------+----------+----------+---------+-------------+-------------+---------------------+
        | address_id | address           | address2 | district | city_id | postal_code | phone       | last_update         |
        +------------+-------------------+----------+----------+---------+-------------+-------------+---------------------+
        |          1 | 47 MySakila Drive | NULL     | Alberta  |     300 |             |             | 2014-09-25 22:30:27 |
        |          3 | 23 Workhaven Lane | NULL     | Alberta  |     300 |             | 14033335568 | 2014-09-25 22:30:27 |
        +------------+-------------------+----------+----------+---------+-------------+-------------+---------------------+
     */
    @Test
    void testFindAddressByCityId() {
        List<Address> addresses = addressService.findAddressByCityId(300);
        assertNotNull(addresses);
        assertFalse(addresses.isEmpty());
        assertTrue(addresses.size() >= 2);
    }
}