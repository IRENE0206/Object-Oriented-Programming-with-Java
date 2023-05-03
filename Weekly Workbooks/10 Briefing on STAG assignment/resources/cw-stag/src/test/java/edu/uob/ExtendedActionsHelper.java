package edu.uob;

import java.util.*;

public class ExtendedActionsHelper {
    private static final List<HashMap<String, HashSet<String>>> actions;
    private static final List<String> narrations;

    static {
        actions = new ArrayList<>();
        narrations = new ArrayList<>();
        String[] triggers0 = {"open", "unlock"};
        // cabin -> forest -> cabin
        String[] subjects0 = {"trapdoor", "key"};
        String[] consumed0 = {"key"};
        String[] produced0 = {"cellar"};
        HashMap<String, HashSet<String>> action0 = populateAction(triggers0, subjects0, consumed0, produced0);
        narrations.add("You unlock the door and see steps leading down into a cellar");
        actions.add(action0);

        String[] triggers1 = {"chop", "cut", "cut down"};
        // cabin -> forest
        String[] subjects1 = {"tree", "axe"};
        String[] consumed1 = {"tree"};
        String[] produced1 = {"log"};
        narrations.add("You cut down the tree with the axe");
        HashMap<String, HashSet<String>> action1 = populateAction(triggers1, subjects1, consumed1, produced1);
        actions.add(action1);

        String[] triggers2 = {"drink"};
        String[] subjects2 = {"potion"};
        String[] consumed2 = {"potion"};
        String[] produced2 = {"health"};
        narrations.add("You drink the potion and your health improves");
        HashMap<String, HashSet<String>> action2 = populateAction(triggers2, subjects2, consumed2, produced2);
        actions.add(action2);

        String[] triggers3 = {"fight", "hit", "attack"};
        // cellar
        String[] subjects3 = {"elf"};
        String[] consumed3 = {"health"};
        String[] produced3 = {};
        narrations.add("You attack the elf, but he fights back and you lose some health");
        HashMap<String, HashSet<String>> action3 = populateAction(triggers3, subjects3, consumed3, produced3);
        actions.add(action3);

        String[] triggers4 = {"pay"};
        // cellar
        String[] subjects4 = {"elf", "coin"};
        String[] consumed4 = {"coin"};
        String[] produced4 = {"shovel"};
        narrations.add("You pay the elf your silver coin and he produces a shovel");
        HashMap<String, HashSet<String>> action4 = populateAction(triggers4, subjects4, consumed4, produced4);
        actions.add(action4);

        String[] triggers5 = {"bridge"};
        // riverbank
        String[] subjects5 = {"log", "river"};
        String[] consumed5 = {"log"};
        String[] produced5 = {"clearing"};
        narrations.add("You bridge the river with the log and can now reach the other side");
        HashMap<String, HashSet<String>> action5 = populateAction(triggers5, subjects5, consumed5, produced5);
        actions.add(action5);

        String[] triggers6 = {"dig"};
        // clearing
        String[] subjects6 = {"ground", "shovel"};
        String[] consumed6 = {"ground"};
        String[] produced6 = {"hole", "gold"};
        narrations.add("You dig into the soft ground and unearth a pot of gold !!!");
        HashMap<String, HashSet<String>> action6 = populateAction(triggers6, subjects6, consumed6, produced6);
        actions.add(action6);

        String[] triggers7 = {"blow"};
        // riverbank
        String[] subjects7 = {"horn"};
        String[] consumed7 = {};
        String[] produced7 = {"lumberjack"};
        narrations.add("You blow the horn and as if by magic, a lumberjack appears !");
        HashMap<String, HashSet<String>> action7 = populateAction(triggers7, subjects7, consumed7, produced7);
        actions.add(action7);

        String[] triggers8 = {"shut down", "close", "lock"};
        String[] subject8 = {"trapdoor"};
        String[] consumed8 = {"cellar"};
        String[] produced8 = {};
        narrations.add("You lock the door to the cellar");
        HashMap<String, HashSet<String>> action8 = populateAction(triggers8, subject8, consumed8, produced8);
        actions.add(action8);

        String[] triggers9 = {"kill"};
        String[] subject9 = {"elf", "cellar"};
        String[] consumed9 = {"elf", "health"};
        String[] produce9 = {};
        narrations.add("You kill the elf with the axe and lose some health in the fight");
        HashMap<String, HashSet<String>> action9 = populateAction(triggers9, subject9, consumed9, produce9);
        actions.add(action9);

        String[] triggers10 = {"kill", "bury"};
        String[] subject10 = {"lumberjack", "hole", "shovel"};
        String[] consumed10 = {"lumberjack", "hole"};
        String[] produce10 = {};
        narrations.add("You kill the lumberjack with the shovel and bury his body in the hole you dig");
        HashMap<String, HashSet<String>> action10 = populateAction(triggers10, subject10, consumed10, produce10);
        actions.add(action10);
    }

    private static HashMap<String, HashSet<String>> populateAction(String[] triggers, String[] subjects, String[] consumed, String[] produced) {
        HashMap<String, HashSet<String>> action = new HashMap<>();
        action.put("triggers", new HashSet<>(Arrays.asList(triggers)));
        action.put("subjects", new HashSet<>(Arrays.asList(subjects)));
        action.put("consumed", new HashSet<>(Arrays.asList(consumed)));
        action.put("produced", new HashSet<>(Arrays.asList(produced)));
        return action;
    }

    public static int getActionsCount() {
        return actions.size();
    }

    public static HashSet<String> getActionTriggers(int actionIndex) {
        return getActionAttribute(actionIndex, "triggers");
    }

    public static HashSet<String> getActionSubjects(int actionIndex) {
        return getActionAttribute(actionIndex, "subjects");
    }

    public static HashSet<String> getActionProduced(int actionIndex) {
        return getActionAttribute(actionIndex, "produced");
    }

    public static HashSet<String> getActionConsumed(int actionIndex) {
        return getActionAttribute(actionIndex, "consumed");
    }

    private static HashSet<String> getActionAttribute(int actionIndex, String attributeType) {
        return actions.get(actionIndex).get(attributeType);
    }

    public static String getActionNarration(int actionIndex) {
        return narrations.get(actionIndex);
    }

    public static Set<String> getAllTriggers() {
        Set<String> allTriggers = new HashSet<>();
        for (HashMap<String, HashSet<String>> action : actions) {
            allTriggers.addAll(action.get("triggers"));
        }
        return allTriggers;
    }
}
