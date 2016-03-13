package goodDeal.model;

public class Ad {
    
    private static int idCpt = 0;
    private final User user;
    private int id;
    private String title;
    private String content;
    private int price;
   
    public Ad(User user, String title, String content, int price) {
	
	super();
	id = idCpt++;
	this.user = user;
	this.title = title;
	this.content = content;
	this.price = price;
	
    }
    
    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
    
}
