package com.glhf.bomberball;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.glhf.bomberball.config.InputsConfig;
import com.glhf.bomberball.utils.Action;

/**
 * class InputHandler
 *
 * Class to handle inputs, use it to register input handlers on actions.
 *
 * Example :
 *
 * inputHandler.registerActionHandler(Action.MOVE_UP, () -> { keyUpHandler() })
 * to register keyUpHandler as an handler on MOVE_UP action
 *
 * inputHandler.registerButtonAction(Action.DROP_BOMB, (x, y) -> { buttonLeftHandler(x, y) })
 * to register buttonLeftHandler as an handler on DROP_BOMB action
 *
 * @author nayala
 */
public class InputHandler extends InputListener {

    /**
     * InputsConfig class to link actions to codes
     */
    private InputsConfig inputs_config;

    /**
     * KeyActions
     * All registrable key actions
     */


    private ActionHandler[] handlers = new ActionHandler[Action.values().length];
    private boolean locked;

    /**
     * Default constructor
     * Creates an InputHandler with default input codes configurations
     */
    public InputHandler() {
//        inputs_config = InputsConfig.defaultConfig();
//        inputs_config.exportConfig("default_inputs");
        inputs_config = InputsConfig.get();

    }

    /**
     * You shouln't use this method
     */
    @Override
    public boolean keyDown(InputEvent event, int keycode) {
        if (!locked && inputs_config.isKeyCodeAssigned(keycode)) {
            for(Action a : inputs_config.getKeyCodeActions(keycode)) {
                ActionHandler handler = this.handlers[a.ordinal()];
                if (handler != null) {
                    handler.handle(event);
                }
            }
        }
        return false;
    }

    /**
     * You shouln't use this method
     */
    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        if (!locked && inputs_config.isButtonCodeAssigned(button)) {
            for(Action a : inputs_config.getButtonCodeActions(button)) {
                ActionHandler handler = this.handlers[a.ordinal()];
                if (handler != null) {
                    handler.handle(event);
                }
            }
        }
        return false;
    }

    /**
     * Registers a key action.
     * @param action action to register
     * @param handler handler to call when action is triggered
     */
    public void registerActionHandler(Action action, KeyActionHandler handler) {
        handlers[action.ordinal()] = new ActionHandler(handler);
    }
    public void registerActionHandler(Action action, ClickActionHandler handler) {
        handlers[action.ordinal()] = new ActionHandler(handler);
    }

    /**
     * Interface to handle key actions.
     * Use lambda functions :
     * ActionHandler key_handler = () -> { handle_key_action(); };
     */
    public interface KeyActionHandler {
        void handle();
    }
    public interface ClickActionHandler{
        void handle(float x, float y);
    }

    public class ActionHandler {
        KeyActionHandler keyActionHandler;
        ClickActionHandler clickActionHandler;
        public ActionHandler(KeyActionHandler keyActionHandler) {
            this.keyActionHandler = keyActionHandler;
        }
        public ActionHandler(ClickActionHandler clickActionHandler) {
            this.clickActionHandler = clickActionHandler;
        }
        public void handle(InputEvent e){
            if(clickActionHandler != null){
                clickActionHandler.handle(e.getStageX(), e.getStageY());
            }
            if(keyActionHandler != null){
                keyActionHandler.handle();
            }
        }
    }

    public void lock(boolean locked) {
        this.locked = locked;
    }
}
