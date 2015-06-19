package kr.ac.korea.embedded.lightapp;

/**
 * 기본적인 url을 가지고 있는 클래스
 */
public class StaticValue {
    private static final String url = "https://fierce-sea-3163-395.herokuapp.com";

    public static String checkUserUrl(int id) {
        return url + "/user/" + id;
    }

    public static String createUserUrl() {
        return url + "/user/";
    }

    public static String uploadLightUrl(int id) { return url + "/user/" + id + "/light"; }
}