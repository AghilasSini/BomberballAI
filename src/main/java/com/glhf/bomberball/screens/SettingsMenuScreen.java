package com.glhf.bomberball.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.glhf.bomberball.Translator;
import com.glhf.bomberball.config.InputsConfig;
import com.glhf.bomberball.config.InputsConfig.KeyPriority;
import com.glhf.bomberball.ui.SettingsMenuUI;
import com.glhf.bomberball.ui.SettingsMenuUI.InputButton;
import com.glhf.bomberball.utils.WaitNextInput;

public class SettingsMenuScreen extends MenuScreen{

    private final SettingsMenuUI ui;
    private InputProcessor inputProcessorSave;
    private InputButton button;

    public SettingsMenuScreen(){
        super();
        ui = new SettingsMenuUI(this);
        addUI(ui);
    }

    public void setIdReceived(String code) {
        Gdx.input.setInputProcessor(inputProcessorSave);
        ui.uncheckAllInputButtons();
        String esc = InputsConfig.getIDForKeyCode(Input.Keys.ESCAPE);
        InputsConfig inputsConfig = InputsConfig.get();
        //if(code.equals(esc)) code = inputsConfig.getIdList().get(button.action)[button.priority];
        if(code.equals(esc)){
            code = inputsConfig.getIdList()[button.action.ordinal()][button.priority];
            button.setText("");
            inputsConfig.resetInput(button.action, KeyPriority.values()[button.priority]);
        }else {
            button.setText(Translator.getInputName(code));
            inputsConfig.setInput(button.action, KeyPriority.values()[button.priority], code);
        }
        button=null;
        inputsConfig.exportConfig();
    }

    public void listenInputs(InputButton button) {
        this.button=button;
        inputProcessorSave = Gdx.input.getInputProcessor();
        Gdx.input.setInputProcessor(new WaitNextInput(this));
    }
}
