package lab11;
import java.util.*;

class Restaurant {
	// a restaurant has a name, address, a list of ratings, a list of
	// ratings in number and an average rating
	// We can also have a hidden tag system and search by tag
	String name;
	String address;
	String introduction;
	ArrayList<String> ratings;
	ArrayList<Integer> ratingStar;
	int averageRating;
	
	// class initialization
	Restaurant(String name, String address) {
		this.name = name;
		this.address = address;
		this.ratings = new ArrayList<String>();
		this.ratingStar = new ArrayList<Integer>();
	}
	// initialization with introduction, we have 2 cases because the intro can be long
	Restaurant(String name, String address, String introduction) {
		this.name = name;
		this.address = address;
		this.ratings = new ArrayList<String>();
		this.ratingStar = new ArrayList<Integer>();
		this.introduction = introduction;
	}
	
	void displayRatings() {
		for (String rating: this.ratings) {
			System.out.print("Rating: '");
			System.out.println(rating + "'");
		}
	}
	
	void displayIntroduction() {
		System.out.println(this.introduction);
	}
	
	// change restaurant description
	String changeIntroduction(String newIntroduction) {
		this.introduction = newIntroduction;
		return "Description successfully altered";
	}
	
	// add a rating in words
	String addRating(String rating) {
		this.ratings.add(rating);
		return "Rating successfully received";
	}
	
	// add a rating in number
	int addRating(int rating) {
		this.ratingStar.add(rating);
		
		return rating;
	}
	// calculate average rating (number)
	int calculateAverageRating() {
		if (this.ratingStar.isEmpty()) {
			return -1;
		}
		else {
			int ratingCount = 0;
			int total = 0;
			for (int rating : ratingStar) {
				ratingCount++;
				total += rating;
			}
			this.averageRating = total/ratingCount;
			return 1;
		}
	}
	

	
}

public class Restaurants {
	public static void main(String[] args) {
		// this is our command interface: We read commands from the console 
		Scanner sc = new Scanner(System.in);
		while (sc.hasNextLine()) {
			System.out.println("=== WELCOME TO RESTAURANT MANAGEMENT SYSTEM 3000 ===");
			System.out.println("Input your command: ");
			System.out.println("Input your command: ");
			
            String input = sc.nextLine().trim();
            if (input.equals("#")) {
                System.out.println("System shutting down.");
                break;
            }

            String[] parts = input.split("\\s+");
            if (parts.length == 0) continue;

            String command = parts[0].toLowerCase();
            
            switch(command) {
            	// here, we build cases for our list of command:
            }
		}
	}
}


