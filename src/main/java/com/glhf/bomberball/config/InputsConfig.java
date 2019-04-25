package com.glhf.bomberball.config;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.glhf.bomberball.utils.Action;
import com.glhf.bomberball.Translator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.glhf.bomberball.config.InputsConfig.KeyPriority.SECONDARY;

/**
 * class InputsConfig
 *
 * Defines all inputs key codes and button codes
 * Used by InputHandler class
 *
 * @author nayala
 */
public class InputsConfig extends Config {

    private String[][] id_list;

    /** Map to convert key codes to actions */
    private transient HashMap<String, List<Action>> map_code_to_action;

    public enum KeyPriority {
        PRIMARY,
        SECONDARY
    }

    /**
     * Default constructor with the default inputs
     */
    public InputsConfig() {
        id_list = new String[Action.values().length][KeyPriority.values().length];
    }

    @Override
    protected Config reset() {
        id_list = new String[Action.values().length][KeyPriority.values().length];

        setInputKeyCode(Action.MOVE_DOWN, Keys.DOWN);
        setInputKeyCode(Action.MOVE_UP, Keys.UP);
        setInputKeyCode(Action.MOVE_LEFT, Keys.LEFT);
        setInputKeyCode(Action.MOVE_RIGHT, Keys.RIGHT);
        setInputKeyCode(Action.DROP_BOMB_LEFT, Keys.NUMPAD_4);
        setInputKeyCode(Action.DROP_BOMB_UP, Keys.NUMPAD_8);
        setInputKeyCode(Action.DROP_BOMB_RIGHT, Keys.NUMPAD_6);
        setInputKeyCode(Action.DROP_BOMB_DOWN, Keys.NUMPAD_2);
        setInputKeyCode(Action.MODE_MOVE, Keys.D);
        setInputKeyCode(Action.MODE_BOMB, Keys.B);
        setInputKeyCode(Action.ENDTURN, Keys.SPACE);
        setInputKeyCode(Action.ENDTURN, Keys.F, SECONDARY);
        setInputKeyCode(Action.DROP_BOMB, Keys.L);
        setInputKeyCode(Action.MENU_GO_BACK, Keys.ESCAPE);


        setInputKeyCode(Action.NEXT_SCREEN, Keys.ENTER);
        setInputButtonCode(Action.NEXT_SCREEN, Buttons.LEFT, SECONDARY);


        setInputButtonCode(Action.DROP_BOMB, Buttons.LEFT);
        setInputButtonCode(Action.DROP_SELECTED_OBJECT, Buttons.LEFT);
        setInputButtonCode(Action.DELETE_OBJECT, Buttons.RIGHT);
        return this;
    }

    public static InputsConfig get() {
        InputsConfig inputsConfig = get("default_inputs", InputsConfig.class);
        inputsConfig.updateReversedMap();
        return inputsConfig;
    }

    public void exportConfig() {
        exportConfig("default_inputs");
    }

    /**
     * Links a key code with a Action
     * @param action
     * @param key_code
     */
    public void setInputKeyCode(Action action, int key_code) {
        setInputKeyCode(action, key_code, KeyPriority.PRIMARY);
    }

    /**
     * Links a button code with a Action
     * @param action
     * @param mouse_button_code
     */
    public void setInputButtonCode(Action action, int mouse_button_code) {
        setInputButtonCode(action, mouse_button_code, KeyPriority.PRIMARY);
    }

    /**
     * Links a key code with a Action depending on KeyPriority
     * @param action
     * @param key_code
     */
    public void setInputKeyCode(Action action, int key_code, KeyPriority priority) {
        setInput(action, priority, getIDForKeyCode(key_code));
    }

    /**
     * Links a button code with a Action depending on KeyPriority
     * @param action
     * @param mouse_button_code
     */
    public void setInputButtonCode(Action action, int mouse_button_code, KeyPriority priority) {
        setInput(action, priority, getIDForMouseButtonCode(mouse_button_code));
    }


    /**
     * Converts key codes to Actions
     * @param key_code
     * @return Action corresponding to key_code
     */
    public List<Action> getKeyCodeActions(int key_code) {
        return getAction(getIDForKeyCode(key_code));
    }

    /**
     * Converts button codes to Actions
     * @param button_code
     * @return Action corresponding to key_code
     */
    public List<Action> getButtonCodeActions(int button_code) {
        return getAction(getIDForMouseButtonCode(button_code));
    }

    /**
     * @param key_code
     * @return key_code is assigned to a Action
     */
    public boolean isKeyCodeAssigned(int key_code) {
        return getKeyCodeActions(key_code).size() > 0;
    }

    /**
     * @param button_code
     * @return button_code is assigned to a ButtonCode
     */
    public boolean isButtonCodeAssigned(int button_code) {
        return getButtonCodeActions(button_code).size() > 0;
    }

    public String[][] getIdList(){
        return id_list;
    }

    public void setInput(Action action, KeyPriority priority, String code_id){
        id_list[action.ordinal()][priority.ordinal()] = code_id;
        updateReversedMap();
        exportConfig();
    }


    public void resetInput(Action action, KeyPriority profile){
        setInput(action, profile, null);

//        //if there is no more action for a key, the key is removed
//        for(Action a : map_code_to_action.get(code_id))
//            if(a!=null)
//                return;
//        map_code_to_action.remove(code_id);
    }

    //private functions =============

    private List<Action> getAction(String key) {
        if(map_code_to_action.containsKey(key)) {
            return map_code_to_action.get(key);
        }
        return new ArrayList<>();
    }

    public static String getIDForKeyCode(int key_code){
        return "K"+key_code;
    }
    public static String getIDForMouseButtonCode(int mouse_button_code){
        return "M"+mouse_button_code;
    }

    public String getInputName(Action action){
        return Translator.getInputName(id_list[action.ordinal()][KeyPriority.PRIMARY.ordinal()]);
    }

    private void updateReversedMap() {
        map_code_to_action = new HashMap<>();
        for(Action a : Action.values()){
            for(KeyPriority p : KeyPriority.values()){
                String id = id_list[a.ordinal()][p.ordinal()];
                if(id==null)continue;
                if(!map_code_to_action.containsKey(id)) {
                    map_code_to_action.put(id, new ArrayList<>());
                }
                map_code_to_action.get(id).add(a);
            }
        }
    }
}
