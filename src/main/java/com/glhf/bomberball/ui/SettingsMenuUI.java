package com.glhf.bomberball.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.glhf.bomberball.Bomberball;
import com.glhf.bomberball.Graphics;
import com.glhf.bomberball.Graphics.GUI;
import com.glhf.bomberball.utils.Action;
import com.glhf.bomberball.Translator;
import com.glhf.bomberball.audio.AudioButton;
import com.glhf.bomberball.config.AppConfig;
import com.glhf.bomberball.config.InputsConfig;
import com.glhf.bomberball.screens.MainMenuScreen;
import com.glhf.bomberball.utils.ScreenChangeListener;
import com.glhf.bomberball.screens.SettingsMenuScreen;
import com.glhf.bomberball.utils.Resolutions;

public class SettingsMenuUI extends MenuUI {

    private final Table[] contents;
    private final TextButton[] labels;
    private final ButtonGroup<InputButton> inputsButtonGroup;
    private final AppConfig appConfig;

    //Constructor
    public SettingsMenuUI(SettingsMenuScreen screen) {
        super();
        appConfig = AppConfig.get();
        InputsConfig inputsConfig = InputsConfig.get();
        this.setFillParent(true);

//        input_handler.setSettingsMenuScreen(this);

        final int NB_TABS = 2;
        labels = new AudioButton[NB_TABS];
        Stack stack = new Stack();
        contents = new Table[NB_TABS];

        ClickListener labels_listener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                int selected_index = 0;
                for (int i = 0; i < NB_TABS; i++) {
                    contents[i].setVisible(labels[i].isChecked());
                    if (labels[i].isChecked())
                        selected_index = i;
                }
                stack.swapActor(stack.getChildren().get(NB_TABS - 1), stack.findActor("" + selected_index));
            }
        };

        EventListener button_listener = new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int b) {
                final InputButton button = (InputButton) event.getListenerActor();
                button.setText("?");
                button.setChecked(true);
                screen.listenInputs(button);
                return true;
            }
        };

        labels[0] = new AudioButton(Translator.translate("general"), Graphics.GUI.getSkin());
        labels[0].addListener(labels_listener);

        labels[1] = new AudioButton(Translator.translate("inputs"), Graphics.GUI.getSkin());
        labels[1].addListener(labels_listener);

        contents[0] = new Table();
        contents[0].add(new ParameterScreenSize()).growX().row();
        contents[0].add(new ParameterLanguage()).growX().row();
        contents[0].add(new FullscreenCheckbox()).growX().row();

        //ajout de chaque paramètre pour inputs
        String[][] id_list = inputsConfig.getIdList();
        contents[1] = new Table();
        inputsButtonGroup = new ButtonGroup<>();
        inputsButtonGroup.setMaxCheckCount(1);
        inputsButtonGroup.setMinCheckCount(1);
        contents[1].add(new Label(Translator.translate("Action"), Graphics.GUI.getSkin())).growX();
        contents[1].add(new Label(Translator.translate("Primary"), Graphics.GUI.getSkin())).growX();
        contents[1].add(new Label(Translator.translate("Secondary"), Graphics.GUI.getSkin())).growX().row();
        for(int i=0; i<Action.values().length; i++){
            Action a = Action.values()[i];
            Label label = new Label(a.toString(), Graphics.GUI.getSkin(), "very_small");
            contents[1].add(label).growX();
            for(int j=0; j<id_list[i].length; j++) {
                String id = id_list[i][j];
                InputButton textButton = new InputButton(id, a, j);
                textButton.addListener(button_listener);
                contents[1].add(textButton).growX();
                inputsButtonGroup.add(textButton);
            }
            contents[1].row();
        }

        ButtonGroup<TextButton> labelsButtonGroup = new ButtonGroup<>();
        for(int i=0; i<NB_TABS; i++) {
            this.add(labels[i]).growX();
            labelsButtonGroup.add(labels[i]);
        }
        this.row();
        for(int i=0; i<NB_TABS; i++) {
            ScrollPane actor = new ScrollPane(contents[i]);
            actor.setName(""+i);
            stack.add(actor);
        }
        this.add(stack).colspan(NB_TABS).grow().row();

        TextButton cancelButton = new AudioButton(Translator.translate("Back"), Graphics.GUI.getSkin());
        cancelButton.addListener(new ScreenChangeListener(MainMenuScreen.class));
        this.add(cancelButton).colspan(NB_TABS).growX();

        stack.swapActor(0,1);
        labels[0].setChecked(true);
        contents[1].setVisible(false);
        labelsButtonGroup.setMaxCheckCount(1);
        labelsButtonGroup.setMinCheckCount(1);
    }

    public void uncheckAllInputButtons() {
        inputsButtonGroup.uncheckAll();
    }

    public abstract class Parameter extends HorizontalGroup {
        protected Label label;
        public Parameter(String name) {
            super();
            left();

            label = new Label(name, Graphics.GUI.getSkin());
            addActor(label);
        }
    }

    private class ParameterScreenSize extends Parameter {
        public ParameterScreenSize() {
            super("screen size");
            SelectBox<Resolutions> value = new SelectBox<>(GUI.getSkin());
            value.setItems(Resolutions.values());
            value.setSelected(appConfig.resolution);
            value.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Bomberball.resizeWindow(value.getSelected());
                    appConfig.resolution = value.getSelected();
                    appConfig.exportConfig();
                }
            });
            this.addActor(value);
        }
    }
    public class ParameterString extends Parameter {
        private SelectBox<String> value;
        public ParameterString(String name) {
            super(name);
            value = new SelectBox<>(Graphics.GUI.getSkin());
            value.setItems("test1","test2","test3","test4");
            addActor(value);
        }
    }
    public class ParameterInt extends Parameter {
        private HorizontalGroup value;
        public ParameterInt(String name, float min, float max, float step) {
            super(name);
            value = new HorizontalGroup();
            Slider slider = new Slider(min, max, step, false, GUI.getSkin());
            Label label = new Label("", GUI.getSkin());
            slider.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    label.setText(""+(int)((Slider)actor).getValue());
                }
            });
            value.addActor(label);
            value.addActor(slider);
            addActor(value);
        }
    }

    public class InputButton extends AudioButton {
        public Action action;
        public int priority;
        public String input_id;

        public InputButton(String input_id, Action action, int priority) {
            super(Translator.getInputName(input_id), Graphics.GUI.getSkin(), "input_select");
            this.action = action;
            this.priority = priority;
            this.input_id = input_id;
        }
    }

    private class FullscreenCheckbox extends CheckBox{
        public FullscreenCheckbox() {
            super("fullscreen", GUI.getSkin());
            this.setChecked(appConfig.fullscreen);
            this.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    DisplayMode displayMode = Gdx.graphics.getDisplayMode();
                    if(((CheckBox)actor).isChecked())
                        Gdx.graphics.setFullscreenMode(displayMode);
                    else
                        Bomberball.resizeWindow(appConfig.resolution);
                    appConfig.fullscreen = ((CheckBox)actor).isChecked();
                    appConfig.exportConfig();
                }
            });
        }
    }

    private class ParameterLanguage extends Parameter{
        private SelectBox<String> value;
        public ParameterLanguage() {
            super(Translator.translate("Language"));
            value = new SelectBox<>(Graphics.GUI.getSkin());
            value.setItems("en","fr");
            value.setSelected(appConfig.language);
            value.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    SelectBox<String> s = (SelectBox<String>) actor;
                    appConfig.language = s.getSelected();
                    appConfig.exportConfig();
                    Translator.load(appConfig.language);
                    Bomberball.changeScreen(new SettingsMenuScreen());
                }
            });
            addActor(value);
        }
    }
}