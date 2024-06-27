import dbTables.PostAddress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dbTables.dbManager.fetchAddress;
import static dbTables.dbManager.fetchAllAddresses;

public class  AddressFinder {
    static Map<String, PostAddress> allAddresses = new HashMap<>();
    public static PostAddress getAddress(String postalCode) {
        if (!allAddresses.isEmpty()) {
            return allAddresses.get(postalCode);
        }
        // Fetch the address from the database
        PostAddress postAddress = fetchAddress(postalCode);
        if (postAddress == null) {
            System.out.println("Address not found");
            return null;
        }
        return postAddress;
    }

    public static List<PostAddress> getAllAddresses() throws Exception {
        if(!allAddresses.isEmpty()) {
            return allAddresses.values().stream().toList();
        }
        // Fetch the addresses from the database
        List<PostAddress> postAddresses = fetchAllAddresses();
        if (postAddresses == null) {
            System.out.println("Addresses not found");
            throw new Exception("Addresses not found");
        }
        for (PostAddress postAddress: postAddresses) {
            allAddresses.put(postAddress.getPostalCode(), postAddress);
        }
        return postAddresses;
    }
}
