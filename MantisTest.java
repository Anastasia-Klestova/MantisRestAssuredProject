package ru.academits.mantis;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class MantisTest {
    private String PHPSESSID;
    private String MANTIS_secure_session;
    private String MANTIS_STRING_COOKIE;
    private Map<String, String> cookies = new HashMap<>();


    @BeforeEach
    public void getCoocies() {
        Response responseLogin = given()
                .contentType("application/x-www-form-urlencoded")
                .body("return=%2Fmantisbt%2Fmy_view_page.php&username=admin&password=admin20&secure_session=on")

                .post("https://academ-it.ru/mantisbt/login.php")
                .andReturn();
        PHPSESSID = responseLogin.cookie("PHPSESSID");
        System.out.println("PHPSESSID" + PHPSESSID);

        MANTIS_secure_session = responseLogin.cookie("MANTIS_secure_session");
        System.out.println("MANTIS_secure_session" + MANTIS_secure_session);

        MANTIS_STRING_COOKIE = responseLogin.cookie("MANTIS_STRING_COOKIE");
        System.out.println("MANTIS_STRING_COOKIE" + MANTIS_STRING_COOKIE);

        cookies.put("PHPSESSID", PHPSESSID);
        cookies.put("MANTIS_secure_session", MANTIS_secure_session);
        cookies.put("MANTIS_STRING_COOKIE", MANTIS_STRING_COOKIE);

    }

    @Test
    public void getAccountInfoTest() {

        Response response = given()
                .cookies(cookies)
                .get("https://academ-it.ru/mantisbt/account_page.php")
                .andReturn();
        RestAssured.baseURI = "https://academ-it.ru/mantisbt/account_page.php";
        assertTrue(response.body().asString().contains("Real Name"));
    }

    @Test
    public void updateRealNameTest() {
        String RealName = "new Real Name";
        Timestamp timestamp = new Timestamp(new Date().getTime());
        String newRealName = "admin1" + timestamp;


        Response responseUpdateName = given()
                .contentType("application/x-www-form-urlencoded")
                .cookies(cookies)
                .body("real_name" + RealName + "password_current=&password=&password_confirm=&email=rovan3014%40mail.ru&realname=" + newRealName)
                .when()
                .post("https://academ-it.ru/mantisbt/account_update.php")
                .andReturn();
        responseUpdateName.prettyPrint();
        assertTrue(responseUpdateName.body().asString().contains("Real name successfully updated"));
        Response response = RestAssured.get("https://academ-it.ru/mantisbt/account_page.php");
        String realName = response.getBody().asString();
        if (!newRealName.equals(realName)) {
            System.out.println("Значения различаются!");
        } else {
            System.out.println("Значения совпадают.");
        }

    }
}
