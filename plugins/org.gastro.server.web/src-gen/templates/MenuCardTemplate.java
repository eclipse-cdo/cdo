package templates;

import org.gastro.inventory.*;
import org.gastro.server.internal.web.*;

public class MenuCardTemplate
{
  protected static String nl;
  public static synchronized MenuCardTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    MenuCardTemplate result = new MenuCardTemplate();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + NL + "<html>" + NL + "  <header>" + NL + "\t  <title>" + NL + "\t\t\t";
  protected final String TEXT_3 = NL + "\t  </title>" + NL + "\t\t<link media=\"screen\" href=\"gastro.css\" type=\"text/css\" rel=\"stylesheet\">" + NL + "\t<header>" + NL + "<body>" + NL + "" + NL + "<h1>";
  protected final String TEXT_4 = "</h1>" + NL + "<table border=\"0\" width=\"400\">";
  protected final String TEXT_5 = NL + "\t<tr><td colspan=\"3\"><h2>";
  protected final String TEXT_6 = "</h2></td></tr>" + NL + "\t<tr><td colspan=\"3\"><h4>";
  protected final String TEXT_7 = "</h4></td></tr>" + NL + "\t";
  protected final String TEXT_8 = NL + "\t\t<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td><td colspan=\"2\"><h3>";
  protected final String TEXT_9 = "</h3></td></tr>" + NL + "\t\t<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td><td>";
  protected final String TEXT_10 = "</td>" + NL + "\t\t\t\t\t<td align=\"right\" valign=\"bottom\" width=\"80\">";
  protected final String TEXT_11 = " </td></tr>" + NL + "\t";
  protected final String TEXT_12 = NL + "</table>" + NL + "" + NL + "</body>" + NL + "</html>";
  protected final String TEXT_13 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
     MenuCard menuCard = (MenuCard)argument; 
    stringBuffer.append(TEXT_2);
    stringBuffer.append(GastroServlet.html(menuCard.getTitle()));
    stringBuffer.append(TEXT_3);
    stringBuffer.append(GastroServlet.html(menuCard.getTitle()));
    stringBuffer.append(TEXT_4);
    for (Section section : menuCard.getSections()) {
    stringBuffer.append(TEXT_5);
    stringBuffer.append(GastroServlet.html(section.getTitle()));
    stringBuffer.append(TEXT_6);
    stringBuffer.append(GastroServlet.html(section.getText()));
    stringBuffer.append(TEXT_7);
    for (Offering offering : section.getOfferings()) {
    stringBuffer.append(TEXT_8);
    stringBuffer.append(GastroServlet.html(offering.getName()));
    stringBuffer.append(TEXT_9);
    stringBuffer.append(GastroServlet.html(offering.getDescription()));
    stringBuffer.append(TEXT_10);
    stringBuffer.append(GastroServlet.html(offering.getPrice()));
    stringBuffer.append(TEXT_11);
    }
    }
    stringBuffer.append(TEXT_12);
    stringBuffer.append(TEXT_13);
    return stringBuffer.toString();
  }
}
