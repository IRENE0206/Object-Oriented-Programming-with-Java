package edu.uob;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class ActionsLoader {
    private final GameState gameState;
    private final File actionsFile;

    public ActionsLoader(GameState gameState, File actionsFile) {
        this.gameState = gameState;
        this.actionsFile = actionsFile;
    }
    // the tags (e.g. <subjects> ) will always all be lower case
    // entity names and action triggers are case-insensitive (so could be UPPER lower or MiXeD)
    public void loadActions() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(this.actionsFile);
        Element root = document.getDocumentElement();
        NodeList actions = root.getChildNodes();
        for (int i = 1; i < actions.getLength(); i += 2) {
            Element action = (Element) actions.item(i);
            GameAction gameAction = new GameAction();
            Element consumed = (Element) action.getElementsByTagName("consumed").item(0);
            for (int j = 0; j < consumed.getElementsByTagName("entity").getLength(); j++) {
                String entity = consumed.getElementsByTagName("entity").item(j).getTextContent();
                gameAction.addConsumedEntity(entity.toLowerCase());
            }
            Element subjects = (Element) action.getElementsByTagName("subjects").item(0);
            for (int j = 0; j < subjects.getElementsByTagName("entity").getLength(); j++) {
                String entity = subjects.getElementsByTagName("entity").item(j).getTextContent();
                gameAction.addSubjectEntity(entity.toLowerCase());
            }
            Element produced = (Element) action.getElementsByTagName("produced").item(0);
            for (int j = 0; j < produced.getElementsByTagName("entity").getLength(); j++) {
                String entity = produced.getElementsByTagName("entity").item(j).getTextContent();
                gameAction.addProducedEntity(entity.toLowerCase());
            }
            Element narration = (Element) action.getElementsByTagName("narration").item(0);
            String explanation = narration.getTextContent();
            gameAction.setNarration(explanation);
            Element triggers = (Element) action.getElementsByTagName("triggers").item(0);
            for (int j = 0; j < triggers.getElementsByTagName("keyphrase").getLength(); j++) {
                String phrase = triggers.getElementsByTagName("keyphrase").item(j).getTextContent();
                this.gameState.addAction(phrase.toLowerCase(), gameAction);
            }
        }
    }


}
