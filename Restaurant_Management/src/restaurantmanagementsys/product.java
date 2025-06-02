package restaurantmanagementsys;

/**
 * A simple POJO for one line in an order (cart). 
 * We use an integer `id` to distinguish one order‐line from another.
 */
public class product {
    private Integer id;
    private String productId;
    private String name;
    private String type;
    private Double price;
    private Integer quantity;

    public product(Integer id, String productId, String name, String type, Double price, Integer quantity){
        this.id        = id;
        this.productId = productId;
        this.name      = name;
        this.type      = type;
        this.price     = price;
        this.quantity  = quantity;
    }

    public Integer getId()         { return id;         }
    public String getProductId()   { return productId;  }
    public String getName()        { return name;       }
    public String getType()        { return type;       }
    public Double getPrice()       { return price;      }
    public Integer getQuantity()   { return quantity;   }

    public void setQuantity(Integer q) { this.quantity = q; }
    // (You could add setters for other fields if needed.)
}
