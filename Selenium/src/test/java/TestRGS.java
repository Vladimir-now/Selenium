import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;


public class TestRGS {

    private WebDriver driver;
    private WebDriverWait wait;

    @Before
    public void before() {
        System.setProperty("webdriver.chrome.driver", "webdriver/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        wait = new WebDriverWait(driver, 10, 1000);

        String baseUrl = "http://www.rgs.ru";
        driver.get(baseUrl);
    }


    @Test
    public void scenario() {
        //Выбрать Меню
        String menuButtonXPath = "//a[@data-toggle  = \"dropdown\" and @class =\"hidden-xs\"]";
        WebElement menuButton = driver.findElement(By.xpath(menuButtonXPath));
        wait.until(ExpectedConditions.visibilityOf(menuButton));
        menuButton.click();

        //Выбрать категорию - ДМС
        String dmsButtonXPath = "//li/a[contains(text(),\"ДМС\")]";
        WebElement dmsButton = driver.findElement(By.xpath(dmsButtonXPath));
        dmsButton.click();

        //Проверить наличие заголовка - Добровольное медицинское страхование
        String pageTitlePath = "//h1[contains(text(),\"ДМС\")]";
        WebElement pageTitle = driver.findElement(By.xpath(pageTitlePath));
        wait.until(ExpectedConditions.visibilityOf(pageTitle));
        Assert.assertEquals("Заголовок отсутствует/не соответствует требуемому",
                "ДМС — добровольное медицинское страхование",pageTitle.getText());

        //Нажать на кнопку - Отправить заявку
        String sendRequestButtonXpath = "//a[contains(text(),\"Отправить заявку\")]";
        WebElement sendRequestButton = driver.findElement(By.xpath(sendRequestButtonXpath));
        sendRequestButton.click();

        //Проверить, что открылась страница , на которой присутствует текст - Заявка на добровольное медицинское страхование
        String formTitlePath = "//b[contains(@data-bind, \"text: options\")]";
        WebElement formTitle = driver.findElement(By.xpath(formTitlePath));
        wait.until(ExpectedConditions.visibilityOf(formTitle));
        Assert.assertEquals("Заголовок отсутствует/не соответствует требуемому",
                "Заявка на добровольное медицинское страхование",formTitle.getText());

        //Заполнить поля
        //Имя, Фамилия, Отчество, Регион, Телефон,
        //Эл. почта - qwertyqwerty,
        //Комментарии, Я согласен на обработку
        String[] fieldsXpath = {"//input[@name = \"LastName\"]", "//input[@name = \"FirstName\"]",
                "//input[@name = \"MiddleName\"]", "//input[contains(@data-bind, \"value: Phone\")]",
                "//input[contains(@data-bind, \"value: Email\")]", "//textarea[@name = \"Comment\"]"};
        String[] values = {"Пупкин", "Вася", "Мидолович", "9995553322", "qwertyqwerty", "Hi, I am Vasya Pupkin"};
        for (int i = 0; i < fieldsXpath.length; i++) {
            fillInputField(driver.findElement(By.xpath(fieldsXpath[i])),values[i]);
        }
        //Заполняем регион
        String regionXpath = "//select[@name = \"Region\"]";
        WebElement region = driver.findElement(By.xpath(regionXpath));
        wait.until(ExpectedConditions.elementToBeClickable(region));
        Select select = new Select(region);
        select.selectByVisibleText("Москва");

        //Заполняем дату
//        String dateXpath = "//input[contains(@data-bind, \"value: ContactDate\")]";
//        WebElement dateField = driver.findElement(By.xpath(dateXpath));
//        dateField.click();
//        dateField.sendKeys("18092020");

        //Галочка
        String tickButton = "//input[@type = \"checkbox\"]";
        WebElement tickButtonPath = driver.findElement(By.xpath(tickButton));
        tickButtonPath.click();

        //Проверить, что все поля заполнены введенными значениями
        for (String s: fieldsXpath) {
            Assert.assertNotNull(driver.findElement(By.xpath(s)).getText());
        }

        //Нажать Отправить
        String sendXpath = "//button[@id = \"button-m\"]";
        WebElement send = driver.findElement(By.xpath(sendXpath));
        wait.until(ExpectedConditions.elementToBeClickable(send));
        send.click();

        //Проверить, что у Поля - Эл. почта присутствует сообщение об ошибке - Введите корректный email
        WebElement massageErrorEmail = driver.findElement(By.xpath("//label[contains(text(), \"Эл. почта\")]/..//span"));
        Assert.assertEquals("Сообщение не соответствует требуемому", "Введите корректный email", massageErrorEmail.getText());

    }

    @After
    public void close() {
        driver.close();
    }

    
    private void scrollToElementJs(WebElement element) {
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
        javascriptExecutor.executeScript("arguments[0].scrollIntoView(true);", element);
    }

    private void fillInputField(WebElement element, String value) {
        scrollToElementJs(element);
        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.click();
        element.sendKeys(value);
    }

}
