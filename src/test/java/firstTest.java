import com.google.common.io.Resources;
import io.restassured.RestAssured;
import io.restassured.internal.RestAssuredResponseImpl;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class firstTest {
    String userId = "";
    String playlistId = "";
    String tracks = "";
    String authToken = "BQDyxVC0o1Zij0JUHE3VWjlWDIX1-XuK0By72JupMO-1BOzNMhaKBXd_0dELA6q_FUX1XhDTxbtAMOAbNcRXRQjm798qYIYUZayH_U3a_HFGHwImp5WLGZWDM_wJBjCZS7TwPI9E9QGbYTYIFSTcGba8LEoerdeLcxGnp4Quh5Hbvu3HH9x0akm5JsT-vx2MULRhWa-oZUTLArpmZLPmcySOxabX9ACuH4j_EZIynUs7ErktYHHnVkHHBVm4S8L-E5knKVXURVsgcHOt70bkC5s";
    @BeforeMethod
    public void beforeTest() throws IOException {
        RestAssured.baseURI = "https://api.spotify.com/v1";
    }
    @Test
    public void spotifyTest() throws IOException {
        String trackName = "Beat it";
        String newName = "Update Name";
        getUserId();
        createNewPlaylist();
        addItemsToPlaylist(getTrackUri(trackName));
        assertEquals(getTrackUri(trackName),isItemAdded());
        changePlaylistName(newName);
        assertEquals(newName,getPlaylistName());
        deleteTrack(trackName);

    }
    public void getUserId() {
        Response response =
                given()
                        .contentType("application/json; charset=UTF-8")
                        .header("Authorization", "Bearer " + authToken)
                        .when()
                        .get("/me")
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();
//        response.getBody().prettyPeek();
        userId = response.getBody().jsonPath().getString("id");
//
        System.out.println("User ID: " + userId);
//
    }
    public void createNewPlaylist() throws IOException {
        URL file = Resources.getResource("newPlaylist.json");
        String myJson = Resources.toString(file, Charset.defaultCharset());
        JSONObject json = new JSONObject(myJson);
        Response playlistResponse =
                given()
                        .contentType("application/json; charset=UTF-8")
                        .header("Authorization", "Bearer " + authToken)
                        .body(json.toString())
                        .when()
                        .post("users/{userId}/playlists",userId)
                        .then()
                        .statusCode(201)
                        .extract()
                        .response();
//            playlistResponse.getBody().prettyPeek();
        playlistId = playlistResponse.getBody().jsonPath().getString("id");
        System.out.println("PlaylistID: "+  playlistId);
    }
    public String getTrackUri(String trackName){
        Response trackUriResponse =
                given()
                        .contentType("application/json; charset=UTF-8")
                        .header("Authorization", "Bearer " + authToken)
                        .queryParam("q",trackName )
                        .queryParam("type", "track")
                        .queryParam("market", "US")
                        .queryParam("limit","1")
//                        .queryParam("limit","1")
                        .when()
                        .get("search")
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();
        ArrayList arrayList = trackUriResponse.path("tracks.items.uri");
        return arrayList.get(0).toString();
    }
    public void addItemsToPlaylist(String trackUri){
        given()
            .contentType("application/json; charset=UTF-8")
            .header("Authorization", "Bearer " + authToken)
            .queryParam("playlist_id",playlistId)
            .queryParam("uris",trackUri)
        .when()
            .post("playlists/{playlist_id}/tracks",playlistId)
        .then()
             .statusCode(201);
    }
    public String isItemAdded(){
        Response itemResponse =
                given()
                        .contentType("application/json; charset=UTF-8")
                        .header("Authorization", "Bearer " + authToken)
                        .queryParam("playlist_id", playlistId)
                        .queryParam("market", "TR")
                        .queryParam("limit", "1")
                        .when()
                        .get("playlists/{playlist_id}/tracks",playlistId)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();
//            itemResponse.prettyPeek();
        ArrayList arraylist =  itemResponse.path("items.track.uri");
        return arraylist.get(0).toString();

    }
    public void changePlaylistName(String playlistName) throws IOException {
        URL file = Resources.getResource("newPlaylist.json");
        String myJson = Resources.toString(file, Charset.defaultCharset());
        JSONObject json = new JSONObject(myJson);
        json.put("name",playlistName);
        given()
                .contentType("application/json; charset=UTF-8")
                .header("Authorization", "Bearer " + authToken)
                .queryParam("playlist_id", playlistId)
                .body(json.toString())
        .when()
                .put("playlists/{playlist_id}",playlistId)
        .then()
                .statusCode(200);
    }
    public String getPlaylistName(){

        Response nameResponse =
                given()
                        .contentType("application/json; charset=UTF-8")
                        .header("Authorization", "Bearer " + authToken)
                        .queryParam("playlist_id", playlistId)
                .when()
                        .get("playlists/{playlist_id}",playlistId)
                .then()
                        .statusCode(200)
                        .extract()
                        .response();
        return nameResponse.getBody().jsonPath().getString("name");
    }
    public void deleteTrack(String trackName) throws IOException {
        URL file = Resources.getResource("deletetrackBody.json");
        String myJson = Resources.toString(file, Charset.defaultCharset());
        JSONObject json = new JSONObject(myJson);
//         DELETE KISMI ÇALIŞMIYOR!!!!!
//        json.getJSONObject("tracks").put("uri",getTrackUri(trackName));
//        JSONObject hashMap = json.getJSONObject("tracks");
//        HashMap<String, String> hashMap1 = new HashMap<String, String>();
//        hashMap1 = json.getJSONObject("tracks");
//        hashMap1.put(json.getJSONObject("tracks"))
//        System.out.println(json.toString());
        given()
                .contentType("application/json; charset=UTF-8")
                .header("Authorization", "Bearer " + authToken)
                .queryParam("playlist_id", playlistId)
                .body(json.toString())
        .when()
                .delete("playlists/{playlist_id}/tracks",playlistId)
        .then()
                .statusCode(200);

    }
}
/*TODO
    1- Get User ID+
    2- Create New Playlist+
    3- Search for Track *2
    4- Add Items to Playlist
    5- Get a Playlist + Check Track is Add
    6- Update Playlist
    7- Delete a track
    */