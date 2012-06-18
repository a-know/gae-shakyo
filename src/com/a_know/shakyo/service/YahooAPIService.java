package com.a_know.shakyo.service;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;


public class YahooAPIService {
    static final String APP_ID = "Av71lDOxg65Z9Pg68ipRHjmfj0qOaIWRifPm1Hs1Ta8xMQj.fWC39ut_YXTa2fcS";

    public static Set<String> parse(String sentence) throws IOException, SAXException, ParserConfigurationException {
        String baseUrl = "http://jlp.yahooapis.jp/MAService/V1/parse";

        HTTPRequest request = new HTTPRequest(new URL(baseUrl + "?appid=" + URLEncoder.encode(APP_ID, "utf-8")), HTTPMethod.POST);
        StringBuilder payload = new StringBuilder().append("filter=9").append("&sentence=").append(URLEncoder.encode(sentence, "utf-8"));
        request.setPayload(payload.toString().getBytes("utf-8"));//ペイロード：宛先アドレスや発信元アドレスなどの管理情報（ヘッダ情報）を除いた正味のデータのこと

        HTTPResponse res = URLFetchServiceFactory.getURLFetchService().fetch(request);
        return extractKeyWords(new StringReader(new String(res.getContent(), "utf-8")));
    }

    static Set<String> extractKeyWords(Reader r) throws SAXException, IOException, ParserConfigurationException{
        Document root = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(r));
        Node resultSet = getNodeByNodeName(root.getChildNodes(), "ResultSet");
        Node maResult = getNodeByNodeName(resultSet.getChildNodes(), "ma_result");
        NodeList wordList = getNodeByNodeName(maResult.getChildNodes(), "word_list").getChildNodes();

        Set<String> sets = new HashSet<String>();

        for(int i = 0, iLength = wordList.getLength(); i < iLength; i++){
            Node word = wordList.item(i);
            if(word.getNodeType() != Node.ELEMENT_NODE){
                continue;
            }
            String surface = null;
            NodeList childrenOfWord = word.getChildNodes();

            for(int j = 0, jLength = childrenOfWord.getLength(); j < jLength; j++){
                Node childOfWord = childrenOfWord.item(j);
                if("surface".equals(childOfWord.getNodeName())){
                    surface = childOfWord.getTextContent();
                    break;
                }
            }
            sets.add(surface);
        }
        return sets;
    }

    static Node getNodeByNodeName(NodeList nodeList, String nodeName){
        for(int i = 0, length = nodeList.getLength(); i < length; i++){
            Node node = nodeList.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE && nodeName.equals(node.getNodeName())){
                return node;
            }
        }
        return null;
    }
}
