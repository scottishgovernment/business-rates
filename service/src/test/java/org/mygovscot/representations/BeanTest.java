package org.mygovscot.representations;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BeanTest  {

    @Test
    public void testLocalAuthority(){
        LocalAuthority la = new LocalAuthority();
        
        la.setId("id");
        assertEquals("id", la.getId());

        la.setCode(1);
        assertEquals(1, la.getCode());

        la.setName("name");
        assertEquals("name", la.getName());
    }
    
    @Test
    public void testProperty(){
        Property p = new Property();
        
        p.setAddress("address");
        assertEquals("address", p.getAddress());
        
        p.setRv("rv");
        assertEquals("rv", p.getRv());

        LocalAuthority la = new LocalAuthority();
        p.setLocalAuthority(la);
        assertEquals(la, p.getLocalAuthority());
    }
    
    @Test
    public void testPostcode(){
        Postcode postcode = new Postcode();

        postcode.setDistrict("district");
        assertEquals("district", postcode.getDistrict());

        postcode.setLatitude(1.0);
        assertEquals(1.00, postcode.getLatitude(), 0.1);

        postcode.setLongitude(2.0);
        assertEquals(2.0, postcode.getLongitude(), 0.1);

        postcode.setPostcode("postcode");
        assertEquals("postcode", postcode.getPostcode());
    }
    
    @Test
    public void testOccupier(){
        Occupier occupier = new Occupier();
        
        occupier.setName("name");
        assertEquals("name", occupier.getName());
        
    }

}