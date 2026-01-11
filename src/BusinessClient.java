import java.io.Serializable;
/**
 * Represents a business client in the order management system.
 * This class stores the business details and tracks the quantities of items ordered.
 * It is designed to be thread-safe for inventory updates.
 */

public class BusinessClient{
    private String name;
    private int BusinessId;
    private int sunglasssesCount = 0;
    private int beltsCount = 0;
    private int scarvesCount = 0;

    /**
     * Constructs a new BusinessClient with a name and a unique ID.
     * * @param name       The official name of the business.
     * @param BusinessId The unique 5-digit identifier for the business.
     */
public BusinessClient(String name, int BusinessId){
    this.name = name;
    this.BusinessId = BusinessId;
}

    /**
     * Returns the name of the business.
     * @return Business name as a String.
     */
public String getName(){
    return name;
}

    /**
     * Returns the unique business ID.
     * @return Business ID as an integer.
     */
public int getBusinessId(){
    return BusinessId;
}
    /**
     * Updates the inventory for a specific item type.
     * This method is synchronized to ensure thread safety when multiple clients
     * update the same business record simultaneously.
     * * @param itemType The type of item (1: Sunglasses, 2: Belts, 3: Scarves).
     * @param amount   The quantity to be added to the current stock.
     */
public synchronized void updateItems(int itemType, int amount){
    switch (itemType){
        case 1:
            sunglasssesCount += amount;
            break;
            case 2:
                beltsCount += amount;
                break;
                case 3:
                    scarvesCount += amount;
                    break;
    }
}
    /**
     * Provides a string representation of the business client and their current order totals.
     * @return A formatted string with business info and item counts.
     */
@Override
    public String toString(){
    return "Business: " + name + " (ID: " + BusinessId + ") - Items: " +
            "Sunglasses: " + sunglasssesCount + ", Belts: " + beltsCount + ", Scarves: " + scarvesCount;
}
}