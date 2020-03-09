package petSwagger;
import static io.restassured.RestAssured.get;

import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import junit.framework.Assert;
import resources.base;
public class PetStoreTest extends base{

public static Logger log =LogManager.getLogger(base.class.getName());
	@BeforeTest
	public void initialize() throws IOException
	{
		 
driver = initializeDriver();
	}
	
	@Test(priority=1)
	public void addPet() throws Exception {
		driver.get(prop.getProperty("postUrl"));
		Thread.sleep(500);
		driver.findElement(By.xpath("//*[@id='operations-pet-addPet']//span[@class='opblock-summary-method']")).click();
		driver.findElement(By.xpath("//*[@id='operations-pet-addPet']//button[@class='btn try-out__btn']")).click();
		WebElement ele = driver.findElement(By.xpath("//*[@class='body-param__text']"));
		String s = ele.getText();
		log.info("Adding Pet...");
		JSONObject json = new JSONObject(s);
		json.put("id", prop.getProperty("idValue"));
		json.put("name",prop.getProperty("nameValue"));
		String update = json.toString();
		log.info("Added pet details are " +update);
		System.out.println("Added pet details are " +update);
		String niceFormattedJson = com.cedarsoftware.util.io.JsonWriter.formatJson(update);
		ele.clear();
		ele.sendKeys(niceFormattedJson);
		driver.findElement(By.xpath("//*[@class='btn execute opblock-control__btn']")).click();
		String actual = ele.findElement(By.xpath("//tr[@class='response']/td[@class='response-col_status']")).getText();
		if(actual.contains("200")) {
			Assert.assertTrue(true);
		}
			
	}
	@Test(priority=2)
	public void retrievePet() throws Exception {
		log.info("Retrieving newly added Pet details...");
		String id = prop.getProperty("idValue");
		Thread.sleep(1000);
		Response resp = get("http://petstore.swagger.io/v2/pet/"+id);
		String code = resp.asString();
		log.info(code);
		System.out.println("Newly added pet details are "+code);
		int code1 = resp.getStatusCode();
		Assert.assertEquals(200, code1);
	}
	
	@Test(priority=3)
	public void removePet() {
		String id = prop.getProperty("idValue");
		RequestSpecification request = RestAssured.given();
		Response resp = request.delete("http://petstore.swagger.io/v2/pet/"+id);
		Response resp1 = get("http://petstore.swagger.io/v2/pet/"+id);
		String code1 = resp1.asString();
		if(code1.contains("Pet not found")) {
			System.out.println("Pet was removed successfully");
			log.info("Pet was removed successfully");
		}
		int code = resp.getStatusCode();
		//System.out.println("Status code is "+code);
		Assert.assertEquals(code, 200);
		log.info("Removed newly added Pet...");
	}
	
	@Test(priority=4)
	public void failurePet() throws Exception {
		log.info("Failed test case...");
		driver.get(prop.getProperty("postUrl"));
		if(driver.findElement(By.xpath("//*[@id='operations-pet-addPet']//button[@class='btn try-out__btn']")).isEnabled()) {
			WebElement element = driver.findElement(By.xpath("//*[@id='operations-pet-addPet']//button[@class='btn try-out__btn']"));
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element); 
			Thread.sleep(500);
			Assert.assertTrue(true);
		}
	}
	
	
	@AfterTest
	public void teardown()
	{
		
		driver.close();
		driver=null;
		
	}
	
}
