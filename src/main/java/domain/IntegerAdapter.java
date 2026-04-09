package domain;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * XML adapter for converting between Integer and String in XML serialization.
 * Used for proper handling of Integer fields in XML representations.
 */
public class IntegerAdapter extends XmlAdapter<String, Integer> {
    
    /**
     * Converts XML string to Integer.
     * 
     * @param s the string representation
     * @return the Integer value
     */
    public Integer unmarshal(String s) {
        return Integer.parseInt(s);
    }
 
    /**
     * Converts Integer to XML string.
     * 
     * @param number the Integer value
     * @return the string representation, empty string if null
     */
    public String marshal(Integer number) {
        if (number == null) return "";
         
        return number.toString();
    }
}