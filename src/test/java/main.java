import java.io.*;
import java.lang.String;
import com.codeborne.selenide.CollectionCondition;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Condition.*;

public  class  main {
    private WebDriver driver;
    String Chrome;
    String login;
    String password;
    String TestUrl;
    String TestResult;
    String loginPage;

    @BeforeSuite // Before Selenium Tests
    public void beforeSeleniumTest() {
        readConfig();
        createDriver();
    }
    @Test// Test id= LP254 , row in sheet  = 1
    public void SeleniumLogin() throws IOException {
        driver.findElement(By.xpath("//div[@class='right-side']/ul/li[1]")).click();
        driver.findElement(By.id("strEmail")).sendKeys(login);
        driver.findElement(By.id("strPassword")).sendKeys(password);
        driver.findElement(By.xpath("//button[@class='mui-btn mui-btn--large mui-btn--accent mui-btn--raised mui-btn--block']")).click();
        String title = driver.getTitle();
        Assert.assertEquals("Route4Me - Account Routes", title);
        if (title.equals("Route4Me - Account Routes")){
            writeResult(1,true);
        } else {
            writeResult(1,false);
        }
    }
    @Test // Test id= LP235 , row in sheet  = 2
    public void SeleniumFindRouteByName() throws IOException {
        int numberOfRoute = driver.findElements(By.xpath("//td[@class='td_route_name']")).size();
        for(int i=0; i<numberOfRoute;i++){
            String nameOfRoute=driver.findElements(By.xpath("//td[@class='td_route_name']")).get(i).getText();
            if(nameOfRoute.equals("Route for searching")){
                writeResult(2,true);
                break;
            }
            if (numberOfRoute==i+1){
                writeResult(2,false);
                break;
            }
        }
    }
    @AfterSuite // After Selenium Test
    public void closedDriver (){
        if(null != driver) {
            driver.close();
            driver.quit();
        }
    }

    @BeforeTest// Before Selenide Tests
    public void beforeSelenideTest(){
        selenideLogin();
    }
    @Test // Test Id  = SE6438 , row in sheet  = 3
    public void findRoutByName() throws IOException{
        $(By.xpath("//div[@class='searchBox old']/label/input")).val("New route 13th of December 2019");
        $$(By.xpath("//td[@class='td_route_name']")).shouldHave(CollectionCondition.size(1));
        String result =  $(By.xpath("//td[@class='td_route_name']")).shouldBe(enabled).getText();
        System.out.println(result);
        if (result.equals("New route 13th of December 2019")){
            writeResult(3,true);
        } else {
            writeResult(3,false);
            //The item below is needed so that the test crashes in case of incorrect results.
            $(By.id("nonexistentIdForFailTest")).getText();


        }
    }
    @Test // Test Id  = SE64548 , row in sheet  = 4
    public void findRouteByAddress() throws IOException {
        $(By.xpath("//div[@class='searchBox old']/label/input")).val("Republic Airport");
        $$(By.xpath("//td[@class='td_route_name']")).shouldHave(CollectionCondition.size(1));
        String result =  $(By.xpath("//td[@class='td_route_name']")).shouldBe(enabled).shouldHave(text("Friday 13th of December 2019 09:59 AM (+02:00)")).getText();
        if (result.equals("Friday 13th of December 2019 09:59 AM (+02:00)")){
            writeResult(4,true);
        } else {
            writeResult(4,false);
            // The item below is needed so that the test crashes in case of incorrect results.
            $(By.id("nonexistentIdForFailTest")).getText();
        }
    }



    public  void writeResult(int rowId, boolean Result) throws IOException {
        InputStream file = new FileInputStream(TestResult);
        XSSFWorkbook wb = new XSSFWorkbook(file);
        Sheet sh = wb.getSheet("TestResultSheet!");
        String openingTestID = sh.getRow(rowId).getCell(0).getRichStringCellValue().getString();
        String openingTestName = sh.getRow(rowId).getCell(1).getRichStringCellValue().getString();
        String openingSteps = sh.getRow(rowId).getCell(2).getRichStringCellValue().getString();
        String openingAttachment = sh.getRow(rowId).getCell(4).getRichStringCellValue().getString();
        String openingNotes = sh.getRow(rowId).getCell(6).getRichStringCellValue().getString();
        Row row = sh.createRow(rowId);
        Cell testID = row.createCell(0);
        Cell testName = row.createCell(1);
        Cell steps = row.createCell(2);
        Cell passCell = row.createCell(3);
        Cell attachment = row.createCell(4);
        Cell dateCell = row.createCell(5);
        Cell notes = row.createCell(6);
        if (Result) {
            passCell.setCellValue("PASS");
            passCell.setCellStyle(cellStyle(wb, true));
        } else {
            passCell.setCellValue("FAIL");
            passCell.setCellStyle(cellStyle(wb, false));
        }
        testID.setCellValue(openingTestID);
        testName.setCellValue(openingTestName);
        steps.setCellValue(openingSteps);
        attachment.setCellValue(openingAttachment);
        notes.setCellValue(openingNotes);
        dateCell.setCellValue(takeData());
        FileOutputStream fileOut = new FileOutputStream(TestResult);
        wb.write(fileOut);
        fileOut.flush();
        fileOut.close();
    }
    public void selenideLogin(){
        open(loginPage);
        $(By.id("strEmail")).val(login);
        $(By.id("strPassword")).val(password);
        $(By.xpath("//button[@class='mui-btn mui-btn--large mui-btn--accent mui-btn--raised mui-btn--block']")).click();
    }
    public String takeData (){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return formatter.format(date);

    }
    public CellStyle cellStyle (XSSFWorkbook wb,boolean Result){
        CellStyle Style = wb.createCellStyle();
        if (Result){
            Style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        } else {
            Style.setFillForegroundColor(IndexedColors.RED.getIndex());
        }
        Style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return Style;
    }
    public void createDriver(){
        System.setProperty("webdriver.chrome.driver", Chrome);
        ChromeOptions options = new ChromeOptions();
        // Any options here

        //options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1080","--ignore-certificate-errors");
        this.driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.get(TestUrl);
    }
    public void readConfig() {
        Properties property = new Properties();
        try {
            FileInputStream configFile = new FileInputStream("D:/JavaProject/Aleksander_Romaniiy_TZ/config.properties");
            property.load(configFile);
            this.loginPage = property.getProperty("Page");
            this.Chrome = property.getProperty("pathToChrome");
            this.login = property.getProperty("login");
            this.password = property.getProperty("password");
            this.TestUrl = property.getProperty("TestUrl");
            this.TestResult = property.getProperty("TestResult");
        } catch (IOException e) {
            System.err.println("[ERROR] Cant find config file");
        }
    }
}
