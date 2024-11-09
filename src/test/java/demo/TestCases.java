package demo;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
// import io.github.bonigarcia.wdm.WebDriverManager;
import demo.wrappers.Wrappers;
//import dev.failsafe.internal.util.Assert;

public class TestCases {
    ChromeDriver driver;

    /*
     * TODO: Write your tests here with testng @Test annotation. 
     * Follow `testCase01` `testCase02`... format or what is provided in instructions
     */

     
    /*
     * Do not change the provided methods unless necessary, they will help in automation and assessment
     */
    @BeforeTest
    public void startBrowser()
    {
        System.setProperty("java.util.logging.config.file", "logging.properties");

        // NOT NEEDED FOR SELENIUM MANAGER
        // WebDriverManager.chromedriver().timeout(30).setup();

        ChromeOptions options = new ChromeOptions();
        LoggingPreferences logs = new LoggingPreferences();

        logs.enable(LogType.BROWSER, Level.ALL);
        logs.enable(LogType.DRIVER, Level.ALL);
        options.setCapability("goog:loggingPrefs", logs);
        options.addArguments("--remote-allow-origins=*");

        System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "build/chromedriver.log"); 

        driver = new ChromeDriver(options);

        driver.manage().window().maximize();
    }
    @Test
    public void testCase01() throws InterruptedException{
        driver.get("https://www.scrapethissite.com/pages/");
        Assert.assertEquals(driver.getCurrentUrl(), "https://www.scrapethissite.com/pages/", "Website URL does not match.");
        System.out.println("Verified URL:https://www.scrapethissite.com/pages/");

        WebElement hockeyTeamsElement=driver.findElement(By.xpath("//a[contains(text(),'Hockey Teams: Forms, Searching and Pagination')]"));
        Wrappers.clickOnElement(hockeyTeamsElement, driver);
        
        //Initialize and declare a hashmap arraylist called datalist
        ArrayList<HashMap<String, Object>> dataList=new ArrayList<>();

        //Locate page no 1
        WebElement clickOnPage=driver.findElement(By.xpath("(//ul[@class='pagination']//li/a)[1]"));
        Wrappers.clickOnElement(clickOnPage, driver);

        //Iterate through 4 pages
        for(int page=1;page<=4;page++){
            List<WebElement> rows=driver.findElements(By.xpath("//tr[@class='team']"));
            for(WebElement row:rows){
                //Extract data from each row
                //Get Text from Teamname locator
                String teamName=row.findElement(By.xpath("./td[@class='name']")).getText();
                int year = Integer.parseInt(row.findElement(By.xpath("./td[@class='year']")).getText());
                double winPercentage = Double.parseDouble(row.findElement(By.xpath("./td[contains(@class,'pct')]")).getText());
                long epoch=System.currentTimeMillis()/1000;
                String epochTime=String.valueOf(epoch);

                //Check if win percentage is less than 40%
                if(winPercentage<0.4){
                    HashMap<String,Object> dataMap=new HashMap<>();
                    dataMap.put("epochTime", epochTime);
                    dataMap.put("teamName", teamName);
                    dataMap.put("year", year);
                    dataMap.put("winPercentage", winPercentage);
                    dataList.add(dataMap);
                }

            }
            if(page<4){
                WebElement nextPagWebElement=driver.findElement(By.xpath("//a[@aria-label='Next']"));
                nextPagWebElement.click();
                Thread.sleep(5000);
            }
        }
        for(HashMap<String,Object> data : dataList){
            System.out.println("Epoch time of Scrape:"+data.get("epochTime")+",TeamName:"+data.get("teamName")+",Year:"+data.get("year")+",Win%:"+data.get("winPercentage"));

        }
        //Store the hashMap data in a json file
        ObjectMapper mapper = new ObjectMapper();
        try{
            String userDir=System.getProperty("user.dir");
            File jsonFile=new File(userDir+"/src/test/resources/hockey-team-data.json");
            mapper.writeValue(jsonFile, dataList);
            System.out.println("JSON data written to:"+jsonFile.getAbsolutePath());
            Assert.assertTrue(jsonFile.length()!=0);

        }catch(IOException e){
            e.printStackTrace();
        }
        


    }

    @Test
    public void testCase02(){
        driver.get("https://www.scrapethissite.com/pages/");
        
        WebElement oscarWiningFilms=driver.findElement(By.xpath("//a[contains(text(),'Oscar')]"));
        Wrappers.clickOnElement(oscarWiningFilms, driver);

        Wrappers.scrape("2015", driver);
        Wrappers.scrape("2014", driver);
        Wrappers.scrape("2013", driver);
        Wrappers.scrape("2012", driver);
        Wrappers.scrape("2011", driver);
        Wrappers.scrape("2010", driver);

    }

    @AfterTest
    public void endTest()
    {
        driver.close();
        driver.quit();

    }
}