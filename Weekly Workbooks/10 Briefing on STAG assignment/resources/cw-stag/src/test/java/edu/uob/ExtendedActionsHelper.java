package edu.uob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExtendedActionsHelper {
    List<HashMap<String, String[]>> actions;

    public ExtendedActionsHelper() {
        this.actions = new ArrayList<>();
        String[] triggers0 = {"open", "unlock"};
        String[] subjects0 = {"trapdoor", "key"};
        String[] consumed0 = {"key"};
        String[] produced0 = {"cellar"};
        String[] narration0 = {"You unlock the door and see steps leading down into a cellar"};
        HashMap<String, String[]> action0 = populateAction(triggers0, subjects0, consumed0, produced0, narration0);
        this.actions.add(action0);
        String[] triggers1 = {"chop", "cut", "cut down"};
        String[] subjects1 = {"tree", "axe"};
        String[] consumed1 = {"tree"};
        String[] produced1 = {"log"};
        String[] narration1 = {"You cut down the tree with the axe"};
        HashMap<String, String[]> action1 = populateAction(triggers1, subjects1, consumed1, produced1, narration1);
        this.actions.add(action1);
        String[] triggers2 = {"drink"};
        String[] subjects2 = {"potion"};
        String[] consumed2 = {"potion"};
        String[] produced2 = {"health"};
        String[] narration2 = {"You drink the potion and your health improves"};
        HashMap<String, String[]> action2 = populateAction(triggers2, subjects2, consumed2, produced2, narration2);
        this.actions.add(action2);
        String[] triggers3 = {"fight", "hit", "attack"};
        String[] subjects3 = {"elf"};
        String[] consumed3 = {"health"};
        String[] produced3 = {};
        String[] narration3 = {"You attack the elf, but he fights back and you lose some health"};
        HashMap<String, String[]> action3 = populateAction(triggers3, subjects3, consumed3, produced3, narration3);
        this.actions.add(action3);
        String[] triggers4 = {"pay"};
        String[] subjects4 = {"elf", "coin"};
        String[] consumed4 = {"coin"};
        String[] produced4 = {"shovel"};
        String[] narration4 = {"You pay the elf your silver coin and he produces a shovel"};
        HashMap<String, String[]> action4 = populateAction(triggers4, subjects4, consumed4, produced4, narration4);
        this.actions.add(action4);
        String[] triggers5 = {"bridge"};
        String[] subjects5 = {"log", "river"};
        String[] consumed5 = {"log"};
        String[] produced5 = {"clearing"};
        String[] narration5 = {"You bridge the river with the log and can now reach the other side"};
        HashMap<String, String[]> action5 = populateAction(triggers5, subjects5, consumed5, produced5, narration5);
        this.actions.add(action5);
        String[] triggers6 = {"dig"};
        String[] subjects6 = {"ground", "shovel"};
        String[] consumed6 = {"ground"};
        String[] produced6 = {"hole", "gold"};
        String[] narration6 = {"You dig into the soft ground and unearth a pot of gold !!!"};
        HashMap<String, String[]> action6 = populateAction(triggers6, subjects6, consumed6, produced6, narration6);
        this.actions.add(action6);
        String[] triggers7 = {"blow"};
        String[] subjects7 = {"horn"};
        String[] consumed7 = {};
        String[] produced7 = {"lumberjack"};
        String[] narration7 = {"You blow the horn and as if by magic, a lumberjack appears !"};
        HashMap<String, String[]> action7 = populateAction(triggers7, subjects7, consumed7, produced7, narration7);
        this.actions.add(action7);
    }

    private HashMap<String, String[]> populateAction(String[] triggers, String[] subjects, String[] consumed, String[] produced, String[] narration) {
        HashMap<String, String[]> action = new HashMap<>();
        action.put("triggers", triggers);
        action.put("subjects", subjects);
        action.put("consumed", consumed);
        action.put("produced", produced);
        action.put("narration", narration);
        return action;
    }

    public int getActionsCount() {
        return this.actions.size();
    }

    public HashMap<String, String[]> getActionByIndex(int index) {
        return this.actions.get(index);
    }

    public String[] getActionAttribute(int actionIndex, String attributeType) {
        return this.actions.get(actionIndex).get(attributeType);
    }
}
