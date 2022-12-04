package step;

import com.thoughtworks.gauge.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import driver.Driver;
import methods.Methods;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

public class StepImplementation extends Driver {

    Methods methods;
    Logger logger = LogManager.getLogger(Methods.class);

    public StepImplementation (){
        this.methods = new Methods();
    }

    @Step("<seconds> saniye bekle")
    public void waitElement(long seconds) throws InterruptedException {
        Thread.sleep(seconds * 1000);
        System.out.println(seconds + " saniye beklendi");
        logger.info(seconds + " saniye beklendi");
    }

    @Step("<key> - <keyType> elementine tıklanır")
    public void clickElement(String key, String keyType) {
        WebElement element = methods.getElementByKey(key, keyType);

        if (element == null) {
            System.out.println("!!! HATA ( Element == Null ) HATA !!!");
            logger.error("!!! HATA ( Element == Null ) HATA !!!");
            webDriver.quit();
        }

        if (methods.isDisplayedAndEnabled(element)) {
            methods.clickElement(element);
            System.out.println("( " + key + " ) elementine tiklandi");
            logger.info("( " + key + " ) elementine tiklandi");
        } else {
            System.out.println("( " + key + " ) elementine tiklanamadi");
            logger.info("( " + key + " ) elementine tiklanamadi");
        }

    }

    @Step("<key> - <keyType> elementine <text> değerini yaz")
    public void sendKeysElement(String key, String keyType, String text) {

        WebElement element = methods.getElementByKey(key, keyType);
        text = text.endsWith("KeyValue") ? Driver.TestMap.get(text).toString() : text;

        if (element.isDisplayed() && element.isEnabled()) {
            methods.clickElement(element);
            methods.sendKeys(element, text);
            System.out.println("( " + key + " ) elementine ( " + text + " ) degeri yazildi");
            logger.info("( " + key + " ) elementine ( " + text + " ) degeri yazildi");
        } else {
            System.out.println("( " + key + " ) elementi sayfada goruntulenemedi.");
            logger.info("( " + key + " ) elementi sayfada goruntulenemedi.");
            webDriver.quit();
        }
    }

    @Step("<key> - <keyType> elementinin sayfada görünür olmadığı kontrol edilir")
    public void checkElementVisible(String key, String keyType){
        List<WebElement> elements = methods.getElements(key, keyType);
        int elementSize = elements.size();

        if(elementSize > 0) {
            System.out.println("( " + key + ") elementi gorunur ");
            logger.info("( " + key + ") elementi gorunur ");
            webDriver.quit();
        } else {
            System.out.println("( " + key + ") elementinin sayfada gorunur olmadigi onaylandi ");
            logger.info("( " + key + ") elementinin sayfada gorunur olmadigi onaylandi ");
        }

    }

    @Step("<key> - <keyType> elementinin görünür olması kontrol edilir")
    public void checkVisibleElement(String key, String keyType) {

        WebDriverWait wait = new WebDriverWait(webDriver, 10);
        WebElement element = methods.getElementByKey(key, keyType);
        By locator = null;

        switch (keyType) {

            case "id":
                locator = By.id(key);
                break;

            case "cssSelector":
                locator = By.cssSelector(key);
                break;

            case "xpath":
                locator = By.xpath(key);
                break;

            case "className":
                locator = By.className(key);
                break;

            case "tagName":
                locator = By.tagName(key);
                break;

            case "name":
                locator = By.name(key);
                break;

            default:
                System.out.println("( " + key + " ) elementi icin -> Hatali 'keyType'(" + keyType + ") gonderildi!!!");
                logger.info("( " + key + " ) elementi icin -> Hatali 'keyType'(" + keyType + ") gonderildi!!!");
                break;
        }

        if (locator == null) {
            System.out.println("!!! HATA ( Locator == Null ) HATA !!!");
            logger.error("!!! HATA ( Locator == Null ) HATA !!!");
            webDriver.quit();
        }

        element = wait.until(visibilityOfElementLocated(locator));

        if (element != null && element.isDisplayed()){
            System.out.println("( " + key + " ) elementi sayfada goruntulendi");
            logger.info("( " + key + " ) elementi sayfada goruntulendi");
        } else {
            System.out.println("( " + key + " ) elementi sayfada goruntulenemedi");
            logger.info("( " + key + " ) elementi sayfada goruntulenemedi");
        }

    }

    @Step("<key> - <keyType> seçilen ürünün code değeri ile <key2> - <keyType2> favorilerdeki ürünün code değerini karşılaştır")
    public void productCodeControl(String key, String keyType, String key2, String keyType2){
        WebElement elementInFavoritePage = methods.getElementByKey(key2, keyType2);
        String elementInFavoritePageCode = elementInFavoritePage.getText();
        methods.navigateToBack();
        WebElement elementInProductDetailPage = methods.getElementByKey(key, keyType);
        String elementInProductDetailPageCode = elementInProductDetailPage.getText();
        String[] partOfElementInProductPageCode = elementInProductDetailPageCode.split(" ");
        String newCode = partOfElementInProductPageCode[0];
        methods.navigateToForward();

        if (newCode.equals(elementInFavoritePageCode)){
            System.out.println("( " + newCode + " ) secilen urunun code degeri ile ( " + elementInFavoritePageCode + " ) favorilerdeki urunun code degeri esit");
            logger.info("( " + newCode + " ) secilen urunun code degeri ile ( " + elementInFavoritePageCode + " ) favorilerdeki urunun code degerleri esit");
        } else {
            System.out.println("( " + newCode + " ) secilen urunun code degeri ile ( " + elementInFavoritePageCode + " ) favorilerdeki urunun code degerleri esit degil!!!");
            logger.info("( " + newCode + " ) secilen urunun code degeri ile ( " + elementInFavoritePageCode + " ) favorilerdeki urunun code degerleri esit degil!!!");
            webDriver.quit();
        }

    }

    @Step("<key> - <keyType> listedeki <whichElement>. ürünü seç")
    public void whichElementClick(String key, String keyType, int whichElement){

        List<WebElement> element = methods.getElements(key, keyType);

        long elementListCount = element.stream().count();
        int elementList = (int) elementListCount;
        if (elementList >= (whichElement-1)) {
            methods.clickElement(element.get(whichElement-1));
            System.out.println("( " + key + " ) elementi sayfada ( " + elementList + " ) adet element goruntulendi. ( " + whichElement + " ). elemente tiklandi" );
            logger.info("( " + key + " ) elementi sayfada ( " + elementList + " ) adet element goruntulendi. ( " + whichElement + " ). elemente tiklandi" );
        } else if (elementList > 0 && elementList < (whichElement-1)) {
            methods.clickElement(element.get(0));
            System.out.println("( " + key + " ) elementi sayfada ( " + elementList + " ) adet element goruntulendi. ( 1 ). elemente tıklandi");
            logger.info("( " + key + " ) elementi sayfada ( " + elementList + " ) adet element goruntulendi. ( 1 ). elemente tıklandi");
        }

    }

    @Step("<key> - <keyType> elementine scroll yapılır")
    public void scrollElement(String key, String keyType) throws InterruptedException{

        WebElement element = methods.getElementByKey(key, keyType);

        if (methods.isDisplayedAndEnabled(element)) {
            System.out.println("( " + key + " elementine scroll yapildi ");
            logger.info("( " + key + " elementine scroll yapildi ");
            ((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(true);", element);
            Thread.sleep(500);
        } else {
            System.out.println("( " + key + " ) elementine scroll yapilamadi");
            logger.info("( " + key + " ) elementine scroll yapilamadi");
        }

    }

}
