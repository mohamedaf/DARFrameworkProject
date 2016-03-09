package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ViewProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(ViewProvider.class);
    private Map<String, String> stringAttributes;
    private Map<String, List<String>> listAttributes;

    public ViewProvider() {
	stringAttributes = new HashMap<String, String>();
	listAttributes = new HashMap<String, List<String>>();
    }

    public void addStringAttribute(String attribute, String value) {
	stringAttributes.put(attribute, value);
    }

    public void addListAttribute(String attribute, List<String> values) {
	listAttributes.put(attribute, values);
    }

    public String getViewContent(String filePath, String appName) {
	LOGGER.info("Reading view {}", filePath);
	
	StringBuilder viewContent = new StringBuilder();
	try {
	    URL url = new URL("jar:file:" + System.getProperty("user.dir") + 
		    "/apps/" + appName + ".jar!/" + filePath);
	    BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
	    String line;
	    while ((line = br.readLine()) != null) {
		line = treatLine(line);
		viewContent.append(line);
	    }
	} catch (IOException e) {
	    LOGGER.warn("View not found {}", e);
	}
	return viewContent.toString();
	
    }
    
    public String treatLine(String line) {
	
	Pattern pattern = Pattern.compile("\\{([^\\}]+)\\}");
	Matcher matcher = pattern.matcher(line);
	
	while(matcher.find()){
	    String value = stringAttributes.get(matcher.group(1));
	    if(value == null) {
		line = treatListAttribute(line, matcher);
		// We have to rematch cause sentence changed
		pattern.matcher(line);
	    }
	    else {
		line = line.replace("{" + matcher.group(1) + "}", value);
	    }
	}
	return line;
	
    }
    
    public String treatListAttribute(String line, Matcher matcher) {
	
	List<String> values = listAttributes.get(matcher.group(1));
	if(values == null || values.size() == 0) {
	    return line.replace("{" + matcher.group(1) + "}", "");
	}
	
	String res = line;
	for(int i=0; i<values.size(); i++) {
	    if(i == 0)
		res = line.replace("{" + matcher.group(1) + "}", values.get(i));
	    else
		res += "\n" + line.replace("{" + matcher.group(1) + "}", values.get(i));
	}
	return res;
	
    }

}
