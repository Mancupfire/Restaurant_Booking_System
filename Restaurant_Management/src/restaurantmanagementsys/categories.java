package restaurantmanagementsys;

/**
 * A simple POJO representing one menu item (food or drink).
 */
public class categories {
    private String productId;
    private String name;
    private String type;
    private Double price;
    private String status;

    public categories(String productId, String name, String type, Double price, String status) {
        this.productId = productId;
        this.name      = name;
        this.type      = type;
        this.price     = price;
        this.status    = status;
    }

    public String getProductId() { return productId; }
    public String getName()      { return name;      }
    public String getType()      { return type;      }
    public Double getPrice()     { return price;     }
    public String getStatus()    { return status;    }

    public void setName(String name)       { this.name = name;       }
    public void setType(String type)       { this.type = type;       }
    public void setPrice(Double price)     { this.price = price;     }
    public void setStatus(String status)   { this.status = status;   }
}
