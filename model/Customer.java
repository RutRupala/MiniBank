package model;

import model.annotation.Id;

public class Customer implements Cloneable, java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private final String customerId;
    private String name;
    private String email;
    private String mobile;
    private Address address;
    private static long customerCounter = 100;

    /** Static nested class: does not need an outer Customer instance. */
    public static class Address implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private final String line, city, pincode;
        public Address(String line, String city, String pincode) {
            this.line = line; this.city = city; this.pincode = pincode;
        }
        public String getLine() { return line; }
        public String getCity() { return city; }
        public String getPincode() { return pincode; }
        @Override
        public String toString() { return line + ", " + city + " - " + pincode; }
    }


    private static String generateCustomerId() {
        customerCounter++;
        return "CUST" + customerCounter;
    }

    public Customer(String name, String email, String mobile) {
        this.customerId = generateCustomerId();
        this.name = name;
        this.email = email;
        this.mobile = mobile;
    }

    public String getCustomerId() { return customerId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getMobile() { return mobile; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    @Override
    public Customer clone() {
        try {
            return (Customer) super.clone();   // shallow copy (Address is immutable)
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }
}
