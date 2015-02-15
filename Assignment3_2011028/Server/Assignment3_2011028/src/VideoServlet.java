

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class VideoServlet
 */
@WebServlet("/VideoServlet")
public class VideoServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private VideoController manager;
    /**
     * Default constructor. 
     */
	
    public VideoServlet() {
        manager = new VideoController();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		String videoID = request.getParameter("id");
		try {
			if(videoID != null && Integer.parseInt(videoID) > 0) {
				out.println(manager.getVideo(videoID, "JSON"));
			} else {
				out.println(manager.getVideos("JSON"));
			}
		} catch (NumberFormatException e) {
			out.println("404");
			response.setStatus(404);
		} catch (NullPointerException e) {
			out.println("404");
			response.setStatus(404);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		HashMap<String, String> videoAttributes = new HashMap<String, String>();
		Integer foundAttributes = 0;
		
		for(String attribute: Video.validAttributes) {
			String curAttribute = request.getParameter(attribute.toLowerCase());
			if(curAttribute != null && !curAttribute.equals("")) {
				videoAttributes.put(attribute, curAttribute);
				foundAttributes++;
			} else {
				response.setStatus(500);
				out.println("{\"status\": \"FAILED\"}");
				return;
			}
		}
		if(foundAttributes == Video.validAttributes.length) {
			Status videoStatus = null;
			try {
				videoStatus = manager.addVideo(videoAttributes);
			} catch(InvalidAttribute ex) {
				response.setStatus(500);
				out.println("{\"status\": \"FAILED\"}");
				return;
			}
			out.println("{\"status\": \"OK\", \"videoID\": \""+videoStatus.getResponseCode()+"\"}");
		} else {
			response.setStatus(500);
			out.println("{\"status\": \"FAILED\"}");
			return;
		}
	}

	/**
	 * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		String videoID = request.getParameter("id");
		HashMap<String, String> videoAttributes = new HashMap<String, String>();
		Integer foundAttributes = 0;
		BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
		StringBuilder temp = new StringBuilder();
		String inputData;
		while((inputData = br.readLine()) != null) {
			temp.append(inputData);
		}
		inputData = temp.toString();
		String[] params = inputData.split("&");
		HashMap<String, String> putData = new HashMap<String, String>();
		for(String param: params) {
			String[] pair = param.split("=");
			try {
				putData.put(pair[0], URLDecoder.decode(pair[1].replaceAll("\\+", " ")));
				if(pair[1] == null || pair[1].equals("")) {
					response.setStatus(500);
					out.println("{\"status\": \"FAILED\"}");
					return;
				}
			} catch (ArrayIndexOutOfBoundsException ex) {
				response.setStatus(500);
				out.println("{\"status\": \"FAILED\"}");
				return;
			}
		}
		for(String attribute: Video.validAttributes) {
			String curAttribute = putData.get(attribute.toLowerCase());
			if(curAttribute != null && !curAttribute.equals("")) {
				videoAttributes.put(attribute, curAttribute);
				foundAttributes++;
			} else {
				response.setStatus(500);
				out.println("{\"status\": \"FAILED\"}");
				return;
			}
		}
		if(foundAttributes == Video.validAttributes.length) {
			try {
				manager.updateVideo(videoID, videoAttributes);
			} catch (InvalidAttribute e) {
				response.setStatus(500);
				out.println("{\"status\": \"FAILED\"}");
				return;
			} catch (NullPointerException e) {
				response.setStatus(404);
				out.println("{\"status\": \"FAILED\"}");
				return;
			}
			out.println("{\"status\": \"OK\", \"videoID\": \""+videoID+"\"}");
		} else {
			response.setStatus(500);
			out.println("{\"status\": \"FAILED\"}");
			return;
		}
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		String videoID = request.getParameter("id");
		if (manager.deleteVideo(videoID).getResponseCode().equals("1")) { 
			out.println("{\"status\": \"OK\", \"videoID\": \""+videoID+"\"}");
		} else {
			response.setStatus(404);
			out.println("{\"status\": \"FAILED\", \"videoID\": \""+videoID+"\"}");
			return;
		}
	}
}