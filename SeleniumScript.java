package selenium;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import java.util.concurrent.TimeUnit;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.*;

public class SeleniumScript {

	static WebDriver driver = new ChromeDriver(); // Chrome driver object
	static int passed = 0; // Global variable that tracks passed test
	
	public static void UserLogin (String name, String pass) {
		
		System.out.println("logging in");
		driver.manage().window().maximize(); // Resizes window into full screen
		driver.get("https://pccc.website/wp-login.php?redirect_to=https%3A%2F%2Fpccc.website%2Fwp-admin%2Fpost-new.php&reauth=1"); // Navigates to the specified web page
		driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS); // Waits for site to load
		driver.findElement(By.name("log")).sendKeys(name);
		driver.findElement(By.name("pwd")).sendKeys(pass);
		driver.findElement(By.name("wp-submit")).click();
		driver.manage().timeouts().implicitlyWait(120, TimeUnit.SECONDS);
		
	}

	public static void CheckImages(String url) throws IOException { // Method to check images on the site
		
		driver.manage().window().maximize(); // Resizes window into full screen
		driver.get(url); // Navigates to the specified web page
		driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS); // Waits for site to load

		List<WebElement> imagesList = driver.findElements(By.tagName("img")); // Get list of all images

		List<WebElement> activeImages = new ArrayList<WebElement>(); // Initializes a new list that tracks the images

		for (int i = 0; i < imagesList.size(); i++) { // Iterates though images list
			if (imagesList.get(i).getAttribute("src") != null) { // Verifies we don't have a null
				activeImages.add(imagesList.get(i)); // Adds images to activeImages list
			}
		}

		for (int j = 0; j < activeImages.size(); j++) { // Iterates through activeImages List
			if ((!imagesList.get(j).getAttribute("src").equals(""))) { // Checks if image attribute is blank
				HttpURLConnection connection = (HttpURLConnection) new URL(activeImages.get(j).getAttribute("src"))
						.openConnection(); // new connection object
				connection.connect(); // Connects to image in current iteration
				String response = connection.getResponseMessage(); // Saves the response message to a string
				connection.disconnect(); // Disconnects from image
				if (imagesList.get(j).getAttribute("title").equals("") &&  !imagesList.get(j).getAttribute("class").equals("leaflet-tile leaflet-tile-loaded")) {
					System.out.println("Image with no at text: " + generateXPATH(imagesList.get(j), ""));
				}
				if (!response.equals("OK") && connection.getResponseCode() != 403) { // If the response is "OK" go to next
					System.out.println(activeImages.get(j).getAttribute("src") + " : " + response); // If response is not "OK" prints response message
				}
			} else {
				System.out.println("Broken Image: " + generateXPATH(imagesList.get(j), "")); // If we find a a src attribute that is "" we call the generateXPATH method and pring the Xpath
			}
			
		}

	}

	public static void CheckLinks(String url) throws MalformedURLException, IOException { // Method to check links on the site
		
		driver.manage().window().maximize(); // Resizes window into full screen
		driver.get(url); // Navigates to the specified web page
		driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS); // Waits for site to load

		List<WebElement> linksList = driver.findElements(By.tagName("a")); // Get list of all links

		List<WebElement> activeLinks = new ArrayList<WebElement>(); // Initializes a new list that tracks the links

		for (int i = 0; i < linksList.size(); i++) { // Iterates though links list
			if (linksList.get(i).getAttribute("href") != null && !linksList.get(i).getAttribute("href").equals("")) { // Verifies we don't have a null and that blank
				activeLinks.add(linksList.get(i)); // Adds images to activeImages list //links are not added to the list
			}
		}

		for (int j = 0; j < activeLinks.size(); j++) { // Iterates through activeLinks List
			try {
				HttpURLConnection connection = (HttpURLConnection) new URL(activeLinks.get(j).getAttribute("href"))
						.openConnection(); // new connection object
				connection.connect(); // Connects to list in current iteration
				System.out.println((j+1) + "/" + activeLinks.size());
				if (connection.getResponseCode() >= 400 && connection.getResponseCode() != 403 && connection.getResponseCode() != 500) {
					System.out.println(activeLinks.get(j).getAttribute("href") + " - " + connection.getResponseMessage()
							+ " is a broken link");
				}
			} catch (Exception e) {
				System.out.println(activeLinks.get(j).getAttribute("href") + " -" + " is a broken link");
			}

		}
	}	

	// Method Written by Scott Izu
	private static String generateXPATH(WebElement childElement, String current) { // Method that generates XPATH when given a WebELement
		String childTag = childElement.getTagName();
		if (childTag.equals("html")) {
			return "/html[1]" + current;
		}
		WebElement parentElement = childElement.findElement(By.xpath(".."));
		List<WebElement> childrenElements = parentElement.findElements(By.xpath("*"));
		int count = 0;
		for (int i = 0; i < childrenElements.size(); i++) {
			WebElement childrenElement = childrenElements.get(i);
			String childrenElementTag = childrenElement.getTagName();
			if (childTag.equals(childrenElementTag)) {
				count++;
			}
			if (childElement.equals(childrenElement)) {
				return generateXPATH(parentElement, "/" + childTag + "[" + count + "]" + current);
			}
		}
		return null;
	}
	
	private static void Search(String keyword) {
		driver.manage().window().maximize(); // Resizes window into full screen
		driver.get("https://pccc.website/?s"); // Navigates to the specified web page
		driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS); // Waits for site to load
		
		WebElement search = driver.findElement(By.name("s"));
		System.out.println("Searcing for: " + keyword);
		search.click();
		search.sendKeys(keyword);
		driver.findElement(By.xpath("/html/body/header/nav/div/form/div/span/button")).click();
		try {
			WebElement result = driver.findElement(By.xpath("//a[contains(@href,"+ keyword +") and contains(@type,'button')]"));
			if(result.getAttribute("href").contains(keyword) && result.getAttribute("href") != "") {
				System.out.println("Keyword: " + keyword + "  found");
			}
		}
		catch(Exception e){
			System.out.println("keyword: " + keyword + " notfound");
		}
	}
	
	private static void Map(String url) {
		
		driver.manage().window().maximize(); // Resizes window into full screen
		driver.get(url); // Navigates to the specified web page
		driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS); // Waits for site to load
		
		List<WebElement> mapsList = driver.findElements(By.xpath("//div[contains(@class,'mapsmarker') or contains(@id, 'lmm_error')]")); // Get list of all links

		List<WebElement> activeMaps = new ArrayList<WebElement>(); // Initializes a new list that tracks the links
		
		for (int i = 0; i < mapsList.size(); i++) { // Iterates though images list
			if (!mapsList.get(i).getAttribute("id").equals("lmm_error")) { // Verifies we don't have a null
				activeMaps.add(mapsList.get(i)); // Adds images to activeImages list
			} else {
				System.out.println("Broken Map at: " + generateXPATH(mapsList.get(i), ""));
			}
		}
		if(activeMaps.size() == 4) {
			System.out.println("All maps working!");
		}
	}
		

	public static void main(String[] args) throws Exception {

		System.setProperty("webdriver.chrome.driver", "C:chromedriver.exe"); // I don't know what this does
		UserLogin("*********", "**********");
		
		System.out.println("Testing the Homepage");
		CheckLinks("https://pccc.website/"); // Calls CheckLinks Method
		CheckImages("https://pccc.website/");
		
		System.out.println("Mental Health");
		Map("https://pccc.website/mental-health/");
		CheckLinks("https://pccc.website/mental-health/"); // Calls CheckLinks Method
		CheckImages("http://pccc.website/mental-health/");
		
		System.out.println("Education");
		Map("https://pccc.website/education/");
		CheckLinks("https://pccc.website/education/");
		CheckImages("https://pccc.website/education/");
		
		System.out.println("Physical");
		Map("https://pccc.website/physical-health/");
		CheckLinks("https://pccc.website/physical-health/");
		CheckImages("https://pccc.website/physical-health/");
		
		System.out.println("Spiritual");
		Map("https://pccc.website/spiritual-health/");
		CheckLinks("https://pccc.website/spiritual-health/");
		CheckImages("https://pccc.website/spiritual-health/");
		
		System.out.println("Economic");
		Map("https://pccc.website/economic-health/");
		CheckLinks("https://pccc.website/economic-health/");
		CheckImages("https://pccc.website/economic-health/");
		
		System.out.println("Soical");
		Map("https://pccc.website/social-services/");
		CheckLinks("https://pccc.website/social-services/");
		CheckImages("https://pccc.website/social-services/");
		
		Search("mental");
		Search("education");
		Search("physical");
		Search("spiritual");
		Search("economic");
		Search("social");
		Search("physical");
		
		System.out.println("Test Completed"); // Notifies user when test is completed	
		
		driver.quit();
	}
}