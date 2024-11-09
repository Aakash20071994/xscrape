package demo.wrappers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.idealized.Javascript;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

public class Wrappers {
    /*
     * Write your selenium wrappers here
     */
    public static boolean clickOnElement(WebElement element, WebDriver driver) {
        if (element.isDisplayed()) {
            try {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].scrollIntoView(true)", element);
                element.click();
                Thread.sleep(2000);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public static boolean enterText(WebElement element, WebDriver driver, String text) {
        try {
            element.click();
            element.clear();
            element.sendKeys(text);
            Thread.sleep(1000);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void scrape(String year,WebDriver driver){

        try{
            WebElement yearLink=driver.findElement(By.id(year));
            String yearLinkText=yearLink.getText();
            Wrappers.clickOnElement(yearLink, driver);

            WebDriverWait wait=new WebDriverWait(driver, Duration.ofSeconds(30));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table[@class='table']")));
        
        ArrayList<HashMap<String,String>> movieList=new ArrayList<>();
        List<WebElement> filmRows=driver.findElements(By.xpath("//tr[contains(@class,'film')]"));
        int count =0;
        for(WebElement filmRow:filmRows){
            String filmTitle=filmRow.findElement(By.xpath("./td[contains(@class,'title')]")).getText();
            String nomination=filmRow.findElement(By.xpath("./td[contains(@class,'film-nominations')]")).getText();
            String awards=filmRow.findElement(By.xpath("./td[contains(@class,'awards')]")).getText();
            boolean isWinnerFlag=count==0;
            String isWinner=String.valueOf(isWinnerFlag);

            long epoch=System.currentTimeMillis()/1000;
            String epochTime=String.valueOf(epoch);

            HashMap<String,String> movieMap=new HashMap<>();
            movieMap.put("epochTime",epochTime);
            movieMap.put("year",yearLinkText);
            movieMap.put("title",filmTitle);
            movieMap.put("nomination",nomination);
            movieMap.put("awards", awards);
            movieMap.put("isWinner", isWinner);

            movieList.add(movieMap);
            count++;


        }
        for(HashMap<String,String> movieData:movieList){
            System.out.println("Epoch time:"+ movieData.get("epochTime")+", Year:"+movieData.get("year")+", Film title:"+movieData.get("title")+", Nomonation:"+movieData.get("nomination")+",Awards:"+movieData.get("awards")+", Best Picture:"+movieData.get("isWinner"));
        }
         ObjectMapper mapper = new ObjectMapper();
        try{
            String userDir=System.getProperty("user.dir");
            File jsonFile=new File(userDir+"/src/test/resources/"+year+"-oscar-winner-data.json");
            mapper.writeValue(jsonFile, movieList);
            System.out.println("JSON data written to:"+jsonFile.getAbsolutePath());
            Assert.assertTrue(jsonFile.length()!=0);

        }catch(IOException e){
            e.printStackTrace();
        }
    }
    catch(Exception e){
        System.out.println("Web Scrap for movies is failed..");
        e.printStackTrace();
    }
    }
}
