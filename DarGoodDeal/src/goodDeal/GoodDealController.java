package goodDeal;

import goodDeal.model.Ad;
import goodDeal.model.AdsProvider;
import goodDeal.model.User;
import goodDeal.model.UsersProvider;
import httpServlet.IHttpServlet;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import model.HttpSession;
import model.request.IHttpRequest;
import model.response.HeaderResponseField;
import model.response.HttpResponseError;
import model.response.HttpResponseStatus;
import model.response.IHttpResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GoodDealController implements IHttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoodDealController.class);
    private final AdsProvider adsProvider;
    private final UsersProvider usersProvider;

    public GoodDealController() {

	adsProvider = new AdsProvider();
	usersProvider = new UsersProvider();
	usersProvider.addUser(new User("testuser1", "test"));
	usersProvider.addUser(new User("testuser2", "test"));
	adsProvider.addAd(new Ad(usersProvider.getUser("testuser1", "test"), "Annonce test 1", "Content 1", 260));
	adsProvider.addAd(new Ad(usersProvider.getUser("testuser2", "test"), "Annonce test 2", "Content 2", 60));
	
    }

    @Override
    public void doGet(IHttpRequest req, IHttpResponse resp, String call) {

	switch (call.toLowerCase()) {
	case "connection":
	    getConnection(req, resp);
	    break;
	case "getadslist":
	    getAdsList(req, resp);
	    break;
	case "getadsservice":
	    getAdsService(req, resp);
	    break;
	case "getad":
	    getAd(req, resp);
	    break;
	case "getadservice":
	    getAdService(req, resp);
	    break;
	default:
	    LOGGER.info("Http Not found");
	    HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Not_Found);
	}

    }

    private void getConnection(IHttpRequest req, IHttpResponse resp) {
	
	String contentEncoding = resp.getHeaderValue(HeaderResponseField.CONTENT_ENCODING);
	resp.addStringViewAttribute("contentEncoding", contentEncoding);
	LOGGER.info(resp.setViewContent("view/connection.jspr", req.getUrl().getHost()));
	
    }

    private void getAdsList(IHttpRequest req, IHttpResponse resp) {

	String contentEncoding = resp.getHeaderValue(HeaderResponseField.CONTENT_ENCODING);
	ArrayList<String> adsList = new ArrayList<String>();
	for(Ad ad : adsProvider.getAds()) {
	    adsList.add("<h3>" + ad.getTitle() + "</h3><b>Prix:" + ad.getPrice() + 
		    "&euro;</b><br/><p>" + ad.getContent() + "</p><br/><br/>");
	}
	resp.addStringViewAttribute("contentEncoding", contentEncoding);
	resp.addListViewAttribute("list", adsList);
	LOGGER.info(resp.setViewContent("view/adsList.jspr", req.getUrl().getHost()));
	
    }

    private void getAdsService(IHttpRequest req, IHttpResponse resp) {

	resp.addHeaderValue(HeaderResponseField.CONTENT_ENCODING, "application/json");
	StringBuilder body = new StringBuilder("{ [");
	for(Ad ad : adsProvider.getAds()) {
	    body.append("{ title : \"" + ad.getTitle() + "\" , price : \"" 
			+ ad.getPrice() + "&euro;\" , content : \"" + ad.getContent() + "\" },");
	}
	body = body.deleteCharAt(body.length()-1);
	body.append("] }");
	resp.setBody(body.toString());
	LOGGER.info(resp.getBody());
	
    }

    private void getAd(IHttpRequest req, IHttpResponse resp) {

	String contentEncoding = resp.getHeaderValue(HeaderResponseField.CONTENT_ENCODING);
	int id = getInd(req);
	
	if (!adsProvider.isExistingAd(id)) {
	    LOGGER.info("Http Bad request");
	    HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Bad_Request);
	    return;
	}
	
	Ad ad = adsProvider.getAd(id);
	String text = ad.getTitle() + "<s/><s/><s/><s/>" + ad.getPrice() + "&euro;<br/>" + ad.getContent();
	callAdMessageView(req.getUrl().getHost(), "Annonce", resp, contentEncoding, text);
	
    }

    private void getAdService(IHttpRequest req, IHttpResponse resp) {

	resp.addHeaderValue(HeaderResponseField.CONTENT_ENCODING, "application/json");
	int id = getInd(req);
	
	if (!adsProvider.isExistingAd(id)) {
	    LOGGER.info("Http Bad request");
	    HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Bad_Request);
	    return;
	}
	
	Ad ad = adsProvider.getAd(id);
	resp.setBody("{ title : \"" + ad.getTitle() + "\" , price : \"" 
		+ ad.getPrice() + "&euro;\" , content : \"" + ad.getContent() + "\" }");
	LOGGER.info(resp.getBody());
	
    }

    @Override
    public void doPut(IHttpRequest req, IHttpResponse resp, String call) {

	switch (call.toLowerCase()) {
	case "updatead":
	    updateAd(req, resp);
	    break;
	default:
	    LOGGER.info("Http Not found");
	    HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Not_Found);
	}

    }

    private void updateAd(IHttpRequest req, IHttpResponse resp) {

	int id = getInd(req);
	Map<String, String> bodyContent;
	HttpSession session = req.getSession();
	User user = (User) session.getValue("user");
	String contentEncoding = resp.getHeaderValue(HeaderResponseField.CONTENT_ENCODING);
	String text = new String();

	if (user == null) {
	    doGet(req, resp, "connection");
	    return;
	}

	if (!adsProvider.isExistingAd(id)) {
	    LOGGER.info("Http Bad request");
	    HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Bad_Request);
	    return;
	} else if (!adsProvider.isAdPocessor(user, id)) {
	    text = "Your are not the ad pocessor, then you cannot delete it";
	} else {
	    try {
		bodyContent = parseBodyContent(req.getBody());
	    } catch (UnsupportedEncodingException e) {
		LOGGER.info("Http Bad request");
		HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Bad_Request);
		return;
	    }

	    String title = bodyContent.get("title");
	    String content = bodyContent.get("content");
	    String price = bodyContent.get("price");
	    
	    text = update(resp, id, title, content, price);
	    if(text == null)
		return;

	}

	callAdMessageView(req.getUrl().getHost(), "Modification de l'annonce", resp, contentEncoding, text);

    }

    private String update(IHttpResponse resp, int id, String title, String content, String price) {

	String text = null;
	if (title != null && content != null && price != null) {
	    if (title.isEmpty() || content.isEmpty() || price.isEmpty()) {
		text = "Title, content and price cannot be empty !";
	    } else {
		try {
		    adsProvider.updateAd(id, title, content, Integer.parseInt(price));
		} catch (NumberFormatException e) {
		    LOGGER.info("Http Bad request");
		    HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Bad_Request);
		    return null;
		}
		text = "Ad seccessfully updated";
	    }
	}
	return text;

    }

    @Override
    public void doPost(IHttpRequest req, IHttpResponse resp, String call) {

	switch (call.toLowerCase()) {
	case "connection":
	    postConnection(req, resp);
	    break;
	case "newad":
	    newAd(req, resp);
	    break;
	default:
	    LOGGER.info("Http Not found");
	    HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Not_Found);
	}

    }
    
   private void postConnection(IHttpRequest req, IHttpResponse resp) {
       
       String contentEncoding = resp.getHeaderValue(HeaderResponseField.CONTENT_ENCODING);
       HttpSession session = req.getSession();
       Map<String, String> reqParams = req.getParams();
       String username = reqParams.get("username");
       String password = reqParams.get("password");
       String text = new String();
       
       if(username == null || password == null || username.isEmpty() || password.isEmpty()) {
	   text = "Nom d'utilisateur ou mot de passe vide !";
	   callAdMessageView(req.getUrl().getHost(), "Connection", resp, contentEncoding, text);
	   return;
       }
       
       if(!usersProvider.isUser(username, password)) {
	   text = "Nom d'utilisateur ou mot de passe invalide !";
	   callAdMessageView(req.getUrl().getHost(), "Connection",  resp, contentEncoding, text);
	   return;
       }
       
       session.addValue("user", usersProvider.getUser(username, password));
       doGet(req, resp, "getAdsList");
       
   }

    private void newAd(IHttpRequest req, IHttpResponse resp) {

	HttpSession session = req.getSession();
	User user = (User) session.getValue("user");

	if (user == null) {
	    doGet(req, resp, "connection");
	    return;
	}

	Map<String, String> reqParams = req.getParams();
	String title = reqParams.get("title");
	String content = reqParams.get("content");
	String price = reqParams.get("price");

	if (title == null || content == null || price == null) {
	    LOGGER.info("Http Bad request");
	    HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Bad_Request);
	    return;
	}

	if (!title.isEmpty() && !content.isEmpty() && !price.isEmpty()) {
	    try {
		Ad ad = new Ad(user, title, content, Integer.parseInt(price));
		adsProvider.addAd(ad);
		doGet(req, resp, "getadslist");
	    } catch (NumberFormatException e) {
		LOGGER.info("Http Bad request");
		HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Bad_Request);
	    }
	}

    }

    @Override
    public void doDelete(IHttpRequest req, IHttpResponse resp, String call) {

	switch (call.toLowerCase()) {
	case "deletead":
	    deleteAd(req, resp);
	    break;
	default:
	    LOGGER.info("Http Not found");
	    HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Not_Found);
	}

    }

    private void deleteAd(IHttpRequest req, IHttpResponse resp) {

	int id = getInd(req);
	HttpSession session = req.getSession();
	User user = (User) session.getValue("user");
	String contentEncoding = resp.getHeaderValue(HeaderResponseField.CONTENT_ENCODING);
	String text = new String();

	if (user == null) {
	    doGet(req, resp, "connection");
	    return;
	}

	if (!adsProvider.isExistingAd(id)) {
	    LOGGER.info("Http Bad request");
	    HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Bad_Request);
	    return;
	} else if (!adsProvider.isAdPocessor(user, id)) {
	    text = "Your are not the ad pocessor, then you cannot delete it";
	} else {
	    adsProvider.removeAd(id);
	    text = "Ad seccessfully deleted";
	}
	callAdMessageView(req.getUrl().getHost(), "Suppression annonce", resp, contentEncoding, text);

    }

    private int getInd(IHttpRequest req) {

	URL url = req.getUrl();
	String path = url.getPath();
	String[] splittedPath = path.split("/");
	return Integer.parseInt(splittedPath[1]);

    }

    private Map<String, String> parseBodyContent(String body) throws UnsupportedEncodingException {

	Map<String, String> query_pairs = new HashMap<String, String>();
	String[] pairs = body.split("&");
	for (String pair : pairs) {
	    int idx = pair.indexOf("=");
	    try {
		query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
				URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
	    } catch (StringIndexOutOfBoundsException e) {
		LOGGER.warn("Query string format is not good");
		continue;
	    }
	}

	return query_pairs;

    }

    private void callAdMessageView(String appName, String title, IHttpResponse resp, String contentEncoding, String text) {
	
	resp.addStringViewAttribute("contentEncoding", contentEncoding);
	resp.addStringViewAttribute("title", title);
	resp.addStringViewAttribute("text", text);
	LOGGER.info(resp.setViewContent("view/adMessage.jspr", appName));
	
    }

}
