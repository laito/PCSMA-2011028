import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class VideoServletHttpTest {
	
    private static final String TEST_URL = "http://localhost/Assignment3_2011028/VideoServlet";
    private final CloseableHttpClient httpClient = HttpClients.createDefault();

    /* Test POST and GET */
    @Test
    public void testVideoAddAndList() throws Exception {
        String createdVideoID = createSampleVideo();
        JSONObject receivedVideoJSON = getVideo(createdVideoID);
        JSONObject expectedJSON = new JSONObject("{" +
        	"\"id\": \""+createdVideoID+"\"," +
	        "\"name\": \"My Video\"," + 
	        "\"duration\": \"My Duration\"," + 
	        "\"type\": \"My Type\"," + 
	        "\"rating\": \"2\"," +
	        "\"description\": \"My Description\"," +
        "}");
        assertEquals(expectedJSON.toString(), receivedVideoJSON.toString());
    }

    /* Test GET */
    @Test
    public void testVideosList() throws Exception {
    	HttpGet getVideo = new HttpGet(TEST_URL);
        HttpResponse videoResponse = httpClient.execute(getVideo);
        assertEquals(200, videoResponse.getStatusLine().getStatusCode());
        JSONArray receivedVideoJSON = extractResponseJSONArray(videoResponse);
        assertTrue(receivedVideoJSON.length() >= 0);
    }
    
    /* Test PUT */
    @Test
    public void testVideoUpdate() throws Exception {
        String updatedVideoID = createSampleVideo();
        updateVideo(updatedVideoID);
        JSONObject receivedVideoJSON = getVideo(updatedVideoID);
        JSONObject expectedJSON = new JSONObject("{" +
            	"\"id\": \""+updatedVideoID+"\"," +
    	        "\"name\": \"My Video Updated\"," + 
    	        "\"duration\": \"My Duration Updated\"," + 
    	        "\"type\": \"My Type Updated\"," + 
    	        "\"rating\": \"5\"," +
    	        "\"description\": \"My Description Updated\"," +
            "}");
        assertEquals(expectedJSON.toString(), receivedVideoJSON.toString());
    }
    
    /* Test DELETE */
    @Test
    public void testVideoDelete() throws Exception {
        String updatedVideoID = createSampleVideo();
        JSONObject receivedVideoJSON = getVideo(updatedVideoID);
        JSONObject expectedJSON = new JSONObject("{" +
            	"\"id\": \""+updatedVideoID+"\"," +
    	        "\"name\": \"My Video\"," + 
    	        "\"duration\": \"My Duration\"," + 
    	        "\"type\": \"My Type\"," + 
    	        "\"rating\": \"2\"," +
    	        "\"description\": \"My Description\"," +
            "}");
        assertEquals(expectedJSON.toString(), receivedVideoJSON.toString());
        
        deleteVideo(updatedVideoID);
        HttpGet getVideo = new HttpGet(TEST_URL+"?id="+updatedVideoID);
    	HttpResponse videoResponse = httpClient.execute(getVideo);
    	assertEquals(404, videoResponse.getStatusLine().getStatusCode());
    }
    
    
    /* Test Invalid Attributes on POST */
    @Test
    public void testInvalidAttributesOnCreate() throws ClientProtocolException, IOException, IllegalStateException, JSONException {
    	HttpPost createVideo = new HttpPost(TEST_URL);
    	ArrayList<NameValuePair> newVideo = new ArrayList<NameValuePair>();
        newVideo.add(new BasicNameValuePair("name", "My Video"));
        newVideo.add(new BasicNameValuePair("duration", "My Duration"));
        newVideo.add(new BasicNameValuePair("type", "My Type"));
        newVideo.add(new BasicNameValuePair("rating", "asdf")); /* Invalid Attribute */
        newVideo.add(new BasicNameValuePair("description", "My Description"));
        createVideo.setEntity(new UrlEncodedFormEntity(newVideo));
        HttpResponse createResponse = httpClient.execute(createVideo);
        assertEquals(500, createResponse.getStatusLine().getStatusCode());
        JSONObject responseJSON = extractResponseJSONObject(createResponse);
        String createStatus = responseJSON.getString("status");
        assertEquals("FAILED", createStatus);
    }
    
    /* Test Invalid Attributes on PUT */
    @Test
    public void testInvalidAttributesOnUpdate() throws ClientProtocolException, IOException, IllegalStateException, JSONException {
    	String updatedVideoID = createSampleVideo();
    	HttpPut createVideo = new HttpPut(TEST_URL+"?id="+updatedVideoID);
        ArrayList<NameValuePair> newVideo = new ArrayList<NameValuePair>();
        newVideo.add(new BasicNameValuePair("name", "My Video Updated"));
        newVideo.add(new BasicNameValuePair("duration", "My Duration Updated"));
        newVideo.add(new BasicNameValuePair("type", "My Type Updated"));
        newVideo.add(new BasicNameValuePair("rating", "asdftt")); /* Invalid Attribute */
        newVideo.add(new BasicNameValuePair("description", "My Description Updated"));
        createVideo.setEntity(new UrlEncodedFormEntity(newVideo));
        HttpResponse updateResponse = httpClient.execute(createVideo);
        assertEquals(500, updateResponse.getStatusLine().getStatusCode());
        JSONObject responseJSON = extractResponseJSONObject(updateResponse);
        String createStatus = responseJSON.getString("status");
        assertEquals("FAILED", createStatus);
    }
    
    
    /* Test Missing Attributes on POST */
    @Test
    public void testMissingAttributesOnCreate() throws ClientProtocolException, IOException, IllegalStateException, JSONException {
    	HttpPost createVideo = new HttpPost(TEST_URL);
    	ArrayList<NameValuePair> newVideo = new ArrayList<NameValuePair>();
        newVideo.add(new BasicNameValuePair("name", "My Video"));
        newVideo.add(new BasicNameValuePair("duration", "My Duration"));
        newVideo.add(new BasicNameValuePair("type", "My Type"));
        newVideo.add(new BasicNameValuePair("rating", "2"));
        //newVideo.add(new BasicNameValuePair("description", "My Description")); /* Missing Attribute Description */
        createVideo.setEntity(new UrlEncodedFormEntity(newVideo));
        HttpResponse createResponse = httpClient.execute(createVideo);
        assertEquals(500, createResponse.getStatusLine().getStatusCode());
        JSONObject responseJSON = extractResponseJSONObject(createResponse);
        String createStatus = responseJSON.getString("status");
        assertEquals("FAILED", createStatus);
    }
    
    /* Test Missing Attributes on PUT */
    @Test
    public void testMissingAttributesOnUpdate() throws ClientProtocolException, IOException, IllegalStateException, JSONException {
    	String updatedVideoID = createSampleVideo();
    	HttpPut createVideo = new HttpPut(TEST_URL+"?id="+updatedVideoID);
    	
        ArrayList<NameValuePair> newVideo = new ArrayList<NameValuePair>();
        newVideo.add(new BasicNameValuePair("name", "My Video Updated"));
        newVideo.add(new BasicNameValuePair("duration", "My Duration Updated"));
        newVideo.add(new BasicNameValuePair("type", "My Type Updated"));
        newVideo.add(new BasicNameValuePair("rating", "5"));
 //       newVideo.add(new BasicNameValuePair("description", "My Description Updated")); /* Missing Attribute */
        createVideo.setEntity(new UrlEncodedFormEntity(newVideo));
        HttpResponse updateResponse = httpClient.execute(createVideo);
        assertEquals(500, updateResponse.getStatusLine().getStatusCode());
        JSONObject responseJSON = extractResponseJSONObject(updateResponse);
        String createStatus = responseJSON.getString("status");
        assertEquals("FAILED", createStatus);
    }
    
    /* Test 404 */
    @Test
    public void test404Get() throws ClientProtocolException, IOException {
    	HttpResponse videoResponse;
    	HttpGet getVideo = new HttpGet(TEST_URL+"?id="+"blahblahIdotexit");
    	videoResponse = httpClient.execute(getVideo);
    	assertEquals(404, videoResponse.getStatusLine().getStatusCode());
    }
    
    public void test404Put() throws ClientProtocolException, IOException {
    	HttpPut putVideo = new HttpPut(TEST_URL+"?id="+"blahblahIdotexit");
        ArrayList<NameValuePair> newVideo = new ArrayList<NameValuePair>();
        newVideo.add(new BasicNameValuePair("name", "My Video Updated"));
        newVideo.add(new BasicNameValuePair("duration", "My Duration Updated"));
        newVideo.add(new BasicNameValuePair("type", "My Type Updated"));
        newVideo.add(new BasicNameValuePair("rating", "5"));
        newVideo.add(new BasicNameValuePair("description", "My Description Updated")); /* Missing Attribute */
        putVideo.setEntity(new UrlEncodedFormEntity(newVideo));
    	HttpResponse videoResponse = httpClient.execute(putVideo);
    	assertEquals(404, videoResponse.getStatusLine().getStatusCode());
    }
    
    public void test404Delete() throws ClientProtocolException, IOException {
    	HttpDelete deleteVideo = new HttpDelete(TEST_URL+"?id="+"blahblahIdotexit");
    	HttpResponse videoResponse = httpClient.execute(deleteVideo);
    	assertEquals(404, videoResponse.getStatusLine().getStatusCode());
    }
    
    
    private JSONObject extractResponseJSONObject(HttpResponse response)
            throws IOException, IllegalStateException, JSONException {
        return new JSONObject(IOUtils.toString(response.getEntity().getContent()));
    }
    
    private JSONArray extractResponseJSONArray(HttpResponse response)
            throws IOException, IllegalStateException, JSONException {
        return new JSONArray(IOUtils.toString(response.getEntity().getContent()));
    }
    
    private JSONObject getVideo(String videoID) throws ClientProtocolException, IOException, IllegalStateException, JSONException {
    	HttpGet getVideo = new HttpGet(TEST_URL+"?id="+videoID);
        HttpResponse videoResponse = httpClient.execute(getVideo);
        assertEquals(200, videoResponse.getStatusLine().getStatusCode());
        return extractResponseJSONObject(videoResponse);
    }
    
    private void deleteVideo(String videoID) throws ClientProtocolException, IOException {
    	HttpDelete getVideo = new HttpDelete(TEST_URL+"?id="+videoID);
        HttpResponse videoResponse = httpClient.execute(getVideo);
        assertEquals(200, videoResponse.getStatusLine().getStatusCode());
    }
    
    
    private String updateVideo(String videoID) throws ClientProtocolException, IOException, IllegalStateException, JSONException {
    	HttpPut createVideo = new HttpPut(TEST_URL+"?id="+videoID);
        ArrayList<NameValuePair> newVideo = new ArrayList<NameValuePair>();
        newVideo.add(new BasicNameValuePair("name", "My Video Updated"));
        newVideo.add(new BasicNameValuePair("duration", "My Duration Updated"));
        newVideo.add(new BasicNameValuePair("type", "My Type Updated"));
        newVideo.add(new BasicNameValuePair("rating", "5"));
        newVideo.add(new BasicNameValuePair("description", "My Description Updated"));
        createVideo.setEntity(new UrlEncodedFormEntity(newVideo));
        HttpResponse updateResponse = httpClient.execute(createVideo);
        assertEquals(200, updateResponse.getStatusLine().getStatusCode());
        JSONObject responseJSON = extractResponseJSONObject(updateResponse);
        String createStatus = responseJSON.getString("status");
        assertEquals("OK", createStatus);
        return responseJSON.getString("videoID");
    }
    
    private String createSampleVideo() throws ClientProtocolException, IOException, IllegalStateException, JSONException {
    	HttpPost createVideo = new HttpPost(TEST_URL);
        ArrayList<NameValuePair> newVideo = new ArrayList<NameValuePair>();
        newVideo.add(new BasicNameValuePair("name", "My Video"));
        newVideo.add(new BasicNameValuePair("duration", "My Duration"));
        newVideo.add(new BasicNameValuePair("type", "My Type"));
        newVideo.add(new BasicNameValuePair("rating", "2"));
        newVideo.add(new BasicNameValuePair("description", "My Description"));
        createVideo.setEntity(new UrlEncodedFormEntity(newVideo));
        HttpResponse createResponse = httpClient.execute(createVideo);
        assertEquals(200, createResponse.getStatusLine().getStatusCode());
        JSONObject responseJSON = extractResponseJSONObject(createResponse);
        String createStatus = responseJSON.getString("status");
        assertEquals("OK", createStatus);
        return responseJSON.getString("videoID");
    }

}