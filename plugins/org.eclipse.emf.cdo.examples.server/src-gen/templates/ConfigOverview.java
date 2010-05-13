package templates;

import java.util.*;
import org.eclipse.emf.cdo.examples.server.*;

public class ConfigOverview
{
  protected static String nl;
  public static synchronized ConfigOverview create(String lineSeparator)
  {
    nl = lineSeparator;
    ConfigOverview result = new ConfigOverview();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + NL + "<html>" + NL + "  <header>" + NL + "\t  <title>" + NL + "\t\t\t";
  protected final String TEXT_3 = NL + "\t  </title>" + NL + "\t\t<link media=\"screen\" href=\"gastro.css\" type=\"text/css\" rel=\"stylesheet\">" + NL + "\t<header>" + NL + "<body>" + NL + "" + NL + "<h1>";
  protected final String TEXT_4 = "</h1>" + NL + "" + NL + "<table border=\"0\" width=\"400\">" + NL + "\t<tr><td>Mode:</td><td>";
  protected final String TEXT_5 = "</td></tr>" + NL + "\t<tr><td>User IDs:</td><td>" + NL + "\t";
  protected final String TEXT_6 = NL + "\t\t\t";
  protected final String TEXT_7 = ": ";
  protected final String TEXT_8 = "<br>";
  protected final String TEXT_9 = NL + "\t\t</td></tr>" + NL + "</table>" + NL + "" + NL + "</body>" + NL + "</html>";
  protected final String TEXT_10 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
     DemoConfiguration config = (DemoConfiguration)argument; 
     String title = "Demo Configuration " + AbstractTemplateServlet.html(config.getName()); 
    stringBuffer.append(TEXT_2);
    stringBuffer.append(title);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(title);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(AbstractTemplateServlet.html(config.getMode().toString()));
    stringBuffer.append(TEXT_5);
    
    Map<String, char[]> users = config.getUsers();
    List<String> userIDs = new ArrayList<String>(users.keySet());
    Collections.sort(userIDs);
    for (String userID : userIDs)
    { 
    stringBuffer.append(TEXT_6);
    stringBuffer.append(userID);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(users.get(userID));
    stringBuffer.append(TEXT_8);
     } 
    stringBuffer.append(TEXT_9);
    stringBuffer.append(TEXT_10);
    return stringBuffer.toString();
  }
}
