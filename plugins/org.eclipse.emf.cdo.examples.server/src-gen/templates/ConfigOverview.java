package templates;

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
  protected final String TEXT_4 = "</h1>" + NL + "<table border=\"0\" width=\"400\">" + NL + "</table>" + NL + "" + NL + "</body>" + NL + "</html>";
  protected final String TEXT_5 = NL;

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
    stringBuffer.append(TEXT_5);
    return stringBuffer.toString();
  }
}
