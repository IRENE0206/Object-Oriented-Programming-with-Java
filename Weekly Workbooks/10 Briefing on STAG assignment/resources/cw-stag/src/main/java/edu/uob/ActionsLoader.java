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

public final class ActionsLoader {
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
            this.loadByTagName(action, "consumed", "entity", gameAction);
            this.loadByTagName(action, "subjects", "entity", gameAction);
            this.loadByTagName(action, "produced", "entity", gameAction);
            Element narration = (Element) action.getElementsByTagName("narration").item(0);
            String explanation = narration.getTextContent();
            gameAction.setNarration(explanation);
            this.loadByTagName(action, "triggers", "keyphrase", gameAction);
        }
    }

    private void loadByTagName(Element action, String attributeName, String tagName, GameAction gameAction) {
        Element attribute = (Element) action.getElementsByTagName(attributeName).item(0);
        NodeList nodes = attribute.getElementsByTagName(tagName);
        for (int j = 0; j < nodes.getLength(); j++) {
            String stringToAdd = nodes.item(j).getTextContent().toLowerCase();
            if (attributeName.equalsIgnoreCase("consumed")) {
                gameAction.addConsumedEntityName(stringToAdd);
            } else if (attributeName.equalsIgnoreCase("subjects")) {
                gameAction.addSubjectEntityName(stringToAdd);
            } else if (attributeName.equalsIgnoreCase("produced")) {
                gameAction.addProducedEntityName(stringToAdd);
            } else if (attributeName.equalsIgnoreCase("triggers")) {
                gameAction.addTriggerPhrases(stringToAdd);
                this.gameState.addAction(stringToAdd.toLowerCase(), gameAction);
            }
        }
    }

}
