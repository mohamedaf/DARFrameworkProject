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

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GoodDealController implements IHttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoodDealController.class);
    private final AdsProvider adsProvider;
    private final UsersProvider usersProvider;
    private final String navTabLogin = "<ul>\n" +
                                            "<li><a href=\"http://localhost:1024/good-deal/ads\">Ads list</a></li>\n" +
                                            "<li><a href=\"http://localhost:1024/good-deal/new-ad\">New Ad</a></li>\n" +
                                            "<li style=\"float:right\"><a href=\"http://localhost:1024/good-deal/login\">Login</a></li>\n" +
                                	"</ul>\n";
    private final String navTabLogout = "<ul>\n" +
                                            "<li><a href=\"http://localhost:1024/good-deal/ads\">Ads list</a></li>\n" +
                                            "<li><a href=\"http://localhost:1024/good-deal/new-ad\">New Ad</a></li>\n" +
                                            "<li style=\"float:right\"><a href=\"http://localhost:1024/good-deal/logout\">Logout</a></li>\n" +
                                	"</ul>\n";

    public GoodDealController() {

	adsProvider = new AdsProvider();
	usersProvider = new UsersProvider();
	User user = new User("user@upmc.fr", "test");
	usersProvider.addUser(user);
	adsProvider.addAd(new Ad(user, "Restaurant Kebab 'Le royaume des gros bides'", "Bonjour,</br></br>"
		+ "Je met en vente mon restaurant Kebab herite de pere en fils depuis 3 generations ! "
		+ "Nous fournissons le restaurant avec l'ensemble des recettes ancestrale, "
		+ "qu'on maitrise et qui attire beaucoup de clienteles.</br>"
		+ "Raison de vente, a force de travailler AAGA je n'ai plus le temps "
		+ "de m'en occuper, je m'en separe donc avec tristesse.</br>"
		+ "Condition de vente, maitre kebabiste diplome (pas de kababs surgeles).</br></br>"
		+ "Cordialement", 80000));
	adsProvider.addAd(new Ad(user, "Samsung Galaxy S6", "Bonjour,"
		+ "</br></br>Je met en vente mon telephone en excellent etat pour "
		+ "cause d'un passage sur Iphone 6s, le telephone est toujours "
		+ "en cours de garantie facture à l'appui, veuillez me joindre "
		+ "pour plus de details.</br></br>Cordialement", 300));
	
    }

    @Override
    public void doGet(IHttpRequest req, IHttpResponse resp, String call) {

	switch (call.toLowerCase()) {
	case "login":
	    LOGGER.info("GET login");
	    getLogin(req, resp);
	    break;
	case "logout":
	    LOGGER.info("GET logout");
	    getLogout(req, resp);
	    break;
	case "newad":
	    LOGGER.info("GET newAd");
	    getNewAd(req, resp);
	    break;
	case "getadslist":
	    LOGGER.info("GET getAdsList");
	    getAdsList(req, resp);
	    break;
	case "getadsservice":
	    LOGGER.info("GET getAdsService");
	    getAdsService(req, resp);
	    break;
	case "getad":
	    LOGGER.info("GET getAd");
	    getAd(req, resp);
	    break;
	case "getadservice":
	    LOGGER.info("GET getAdService");
	    getAdService(req, resp);
	    break;
	default:
	    LOGGER.info("Http Not found");
	    HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Not_Found);
	}

    }

    private void getLogin(IHttpRequest req, IHttpResponse resp) {
	
	HttpSession session = req.getSession();
	User user = (User) session.getValue("user");

	if (user != null) {
	    doGet(req, resp, "getAdsList");
	    return;
	}
	
	String contentEncoding = resp.getHeaderValue(HeaderResponseField.CONTENT_ENCODING);
	resp.addStringViewAttribute("contentEncoding", contentEncoding);
	LOGGER.info(resp.setViewContent("view/jspr/login.jspr", req.getUrl().getHost()));
	
    }
    
    private void getLogout(IHttpRequest req, IHttpResponse resp) {
	
	HttpSession session = req.getSession();
	User user = (User) session.getValue("user");

	if (user != null) {
	    session.removeValue("user");
	}
	
	String contentEncoding = resp.getHeaderValue(HeaderResponseField.CONTENT_ENCODING);
	resp.addStringViewAttribute("contentEncoding", contentEncoding);
	LOGGER.info(resp.setViewContent("view/jspr/login.jspr", req.getUrl().getHost()));
	
    }
    
    private void getNewAd(IHttpRequest req, IHttpResponse resp) {
	
	HttpSession session = req.getSession();
	User user = (User) session.getValue("user");

	if (user == null) {
	    doGet(req, resp, "login");
	    return;
	}
	
	String contentEncoding = resp.getHeaderValue(HeaderResponseField.CONTENT_ENCODING);
	resp.addStringViewAttribute("contentEncoding", contentEncoding);
	LOGGER.info(resp.setViewContent("view/jspr/newAd.jspr", req.getUrl().getHost()));
	
    }

    private void getAdsList(IHttpRequest req, IHttpResponse resp) {

	String contentEncoding = resp.getHeaderValue(HeaderResponseField.CONTENT_ENCODING);
	ArrayList<String> adsList = new ArrayList<String>();
	for(Ad ad : adsProvider.getAds()) {
	    adsList.add("<a href=\"http://localhost:1024/" + req.getUrl().getHost()
		    	+ "/" + ad.getId() + "/ad\"><h1><span class=\"log-in\">" 
		    	+ ad.getTitle() + "</span></h1></a><p class=\"float\"><b>" 
		    	+ ad.getPrice() + "EUR</b></br><b>Contact: "
		    	+ ad.getUser().getUsername() + "</b></br></p>"
		    	+ "<p class=\"clearfix\"></br>" + ad.getContent() + "</p>");
	}
	
	HttpSession session = req.getSession();
	User user = (User) session.getValue("user");
	if (user == null) {
	    resp.addStringViewAttribute("log", "Login");
	} else {
	    resp.addStringViewAttribute("log", "Logout");
	}
	
	resp.addStringViewAttribute("contentEncoding", contentEncoding);
	resp.addListViewAttribute("list", adsList);
	LOGGER.info(resp.setViewContent("view/jspr/adsList.jspr", req.getUrl().getHost()));
	
    }

    private void getAdsService(IHttpRequest req, IHttpResponse resp) {

	resp.addHeaderValue(HeaderResponseField.CONTENT_TYPE, "application/json");
	JSONObject body = new JSONObject();
	JSONArray ads = new JSONArray();
	body.put("ads", ads);

	for(Ad ad : adsProvider.getAds()) {
	    JSONObject a = new JSONObject();
	    a.put("title", ad.getTitle());
	    a.put("price", ad.getPrice() + " EUR");
	    a.put("contact", ad.getUser().getUsername());
	    a.put("content", ad.getContent());
	    ads.put(a);
	}

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
	
	HttpSession session = req.getSession();
	User user = (User) session.getValue("user");
	if (user == null) {
	    resp.addStringViewAttribute("log", "Login");
	} else {
	    resp.addStringViewAttribute("log", "Logout");
	}
	
	Ad ad = adsProvider.getAd(id);
	resp.addStringViewAttribute("contentEncoding", contentEncoding);
	resp.addStringViewAttribute("title", "Annonce " + ad.getId());
	resp.addStringViewAttribute("adTitle", ad.getTitle());
	resp.addStringViewAttribute("price", String.valueOf(ad.getPrice()));
	resp.addStringViewAttribute("username", ad.getUser().getUsername());
	resp.addStringViewAttribute("content", ad.getContent());
	resp.addStringViewAttribute("adId", String.valueOf(ad.getId()));
	LOGGER.info(resp.setViewContent("view/jspr/ad.jspr", req.getUrl().getHost()));
	
	
    }

    private void getAdService(IHttpRequest req, IHttpResponse resp) {

	resp.addHeaderValue(HeaderResponseField.CONTENT_TYPE, "application/json");
	int id = getInd(req);
	
	if (!adsProvider.isExistingAd(id)) {
	    LOGGER.info("Http Bad request");
	    HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Bad_Request);
	    return;
	}
	
	Ad ad = adsProvider.getAd(id);
	
	JSONObject body = new JSONObject();
	
	body.put("title", ad.getTitle());
	body.put("price", ad.getPrice() + " EUR");
	body.put("contact", ad.getUser().getUsername());
	body.put("content", ad.getContent());

	resp.setBody(body.toString());
	LOGGER.info(resp.getBody());
	
    }

    @Override
    public void doPut(IHttpRequest req, IHttpResponse resp, String call) {

	switch (call.toLowerCase()) {
	case "updatead":
	    LOGGER.info("PUT updateAd");
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
	    resp.setBody(navTabLogin + "<b>Veuillez cliquer sur <a href=\"http://localhost:1024/good-deal/login\">"
		    	+ "ce lien</a> pour vous connecter, puis renouvelez l'action</b>");
	    LOGGER.info(resp.getBody());
	    return;
	}
	
	if (!adsProvider.isExistingAd(id)) {
	    LOGGER.info("Http Bad request");
	    HttpResponseError.setHttpBodyResponseError(resp, HttpResponseStatus.Bad_Request);
	    return;
	} else if (!adsProvider.isAdPocessor(user, id)) {
	    text = "Your are not the ad pocessor, then you cannot delete it";
	} else {
	    try {
		bodyContent = parseBodyContent(req.getBody());
	    } catch (UnsupportedEncodingException e) {
		LOGGER.info("Http Bad request");
		HttpResponseError.setHttpBodyResponseError(resp, HttpResponseStatus.Bad_Request);
		return;
	    }

	    String title = bodyContent.get("title");
	    String content = bodyContent.get("content");
	    String price = bodyContent.get("price");
	    text = update(resp, id, title, content, price);
	    if(text == null)
		return;

	}

	callAdSimpleModelView(req.getUrl().getHost(), "Modification de l'annonce", req.getSession(), resp, contentEncoding, text);

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
		    HttpResponseError.setHttpBodyResponseError(resp, HttpResponseStatus.Bad_Request);
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
	case "login":
	    LOGGER.info("POST login");
	    postLogin(req, resp);
	    break;
	case "newad":
	    LOGGER.info("POST newAd");
	    postNewAd(req, resp);
	    break;
	default:
	    LOGGER.info("Http Not found");
	    HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Not_Found);
	}

    }
    
   private void postLogin(IHttpRequest req, IHttpResponse resp) {
       
       String contentEncoding = resp.getHeaderValue(HeaderResponseField.CONTENT_ENCODING);
       HttpSession session = req.getSession();
       Map<String, String> reqParams = req.getParams();
       String username = reqParams.get("username");
       String password = reqParams.get("password");
       String text = new String();
       
       if(username == null || password == null || username.isEmpty() || password.isEmpty()) {
	   text = "Nom d'utilisateur ou mot de passe vide !";
	   callAdSimpleModelView(req.getUrl().getHost(), "Login", req.getSession(), resp, contentEncoding, text);
	   return;
       }
       
       if(reqParams.containsKey("submitUp")) {
	   if(usersProvider.isUser(username, password)) {
	       text = "Utilisateur déjà exitant ! Veuillez fournir un username différent.";
	       callAdSimpleModelView(req.getUrl().getHost(), "Login", req.getSession(),  resp, contentEncoding, text);
	       return;
	   } else {
	       usersProvider.addUser(new User(username, password));
	       text = "Félicitation vous êtes maintenant inscrits, vous pouvez "
	       	+ "vous connectez en cliquant "
	       	+ "<a href=\"http://localhost:1024/good-deal/login\">ici</a>";
	       callAdSimpleModelView(req.getUrl().getHost(), "Login", req.getSession(), resp, contentEncoding, text);
	       return;
	   }
       }
       
       if(!usersProvider.isUser(username, password)) {
	   text = "Nom d'utilisateur ou mot de passe invalide !";
	   callAdSimpleModelView(req.getUrl().getHost(), "Login", req.getSession(), resp, contentEncoding, text);
	   return;
       }
       
       session.addValue("user", usersProvider.getUser(username, password));
       doGet(req, resp, "getAdsList");
       
   }

    private void postNewAd(IHttpRequest req, IHttpResponse resp) {

	HttpSession session = req.getSession();
	User user = (User) session.getValue("user");

	if (user == null) {
	    doGet(req, resp, "login");
	    return;
	}

	String contentEncoding = resp.getHeaderValue(HeaderResponseField.CONTENT_ENCODING);
	Map<String, String> reqParams = req.getParams();
	String title = reqParams.get("title");
	String content = reqParams.get("content");
	String price = reqParams.get("price");
	String text = "";

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
		return;
	    } catch (NumberFormatException e) {
		text = "Le champs price doit contenir un nombre.";
	    }
	} else {
	    text = "Tous les champs doivent êtres remplis.";
	}

	callAdSimpleModelView(req.getUrl().getHost(), "New Ad", req.getSession(), resp, contentEncoding, text);
    }

    @Override
    public void doDelete(IHttpRequest req, IHttpResponse resp, String call) {

	switch (call.toLowerCase()) {
	case "deletead":
	    LOGGER.info("DELETE deleteAd");
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
	String text = new String();

	if (user == null) {
	    resp.setBody(navTabLogin + "<b>Veuillez cliquer sur <a href=\"http://localhost:1024/good-deal/login\">"
	    	+ "ce lien</a> pour vous connecter, puis renouvelez l'action</b>");
	    LOGGER.info(resp.getBody());
	    return;
	}

	if (!adsProvider.isExistingAd(id)) {
	    LOGGER.info("Http Bad request");
	    HttpResponseError.setHttpBodyResponseError(resp, HttpResponseStatus.Bad_Request);
	    return;
	} else if (!adsProvider.isAdPocessor(user, id)) {
	    Ad ad = adsProvider.getAd(id);
	    text = navTabLogout + "<b>Your are not the ad pocessor, then you cannot delete it</b>\n" 
		    + "<p><h3>" + ad.getTitle() + "</h3><b>Price:" + ad.getPrice() 
		    + " EUR</b><br/>\n<p>" + ad.getContent() + "</p></p>\n";
	} else {
	    adsProvider.removeAd(id);
	    text = navTabLogout + "<b>Ad seccessfully deleted</b>";
	}
	resp.setBody(text);
	LOGGER.info(resp.getBody());

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

    private void callAdSimpleModelView(String appName, String title, HttpSession session, IHttpResponse resp, String contentEncoding, String text) {
	
	User user = (User) session.getValue("user");
	if (user == null) {
	    resp.addStringViewAttribute("log", "Login");
	} else {
	    resp.addStringViewAttribute("log", "Logout");
	}
	
	resp.addStringViewAttribute("contentEncoding", contentEncoding);
	resp.addStringViewAttribute("title", title);
	resp.addStringViewAttribute("text", text);
	LOGGER.info(resp.setViewContent("view/jspr/adSimpleModel.jspr", appName));
	
    }

}
