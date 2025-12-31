package edu.uoc.mii.user;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import edu.uoc.mii.Main;
import edu.uoc.mii.conf.PreferenceManager;

/**
 *
 * @author Marco Rodriguez
 */
public class LoginDialog extends Dialog {

    private final Main game;
    private final TextField usernameField;
    private final TextField passwordField;
    private final TextField emailField;
    private final Label statusLabel;
    private final TextButton actionButton;
    private final TextButton switchModeButton;
    private final TextButton closeButton;
    private final Runnable onSuccessCallback;

    private boolean isRegistering = false;
    private Stage parentStage;

    public LoginDialog(Main game, String title, Skin skin, Runnable onSuccess) {
        super(title, skin);
        this.game = game;
        this.onSuccessCallback = onSuccess;

        usernameField = new TextField("", skin);
        usernameField.setMessageText("Usuario");
        usernameField.setText(PreferenceManager.getInstance().getUsername());
        usernameField.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (Gdx.app.getType() == Application.ApplicationType.WebGL
                        && Gdx.input.isPeripheralAvailable(Input.Peripheral.MultitouchScreen)) {
                    showInputFor(usernameField, false);
                } else {
                    //parentStage.setKeyboardFocus(usernameField);
                    getStage().setKeyboardFocus(usernameField);
                }
                return false;
            }
        });

        passwordField = new TextField("", skin);
        passwordField.setMessageText("Contraseña");
        passwordField.setText(PreferenceManager.getInstance().getPassword());
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        passwordField.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (Gdx.app.getType() == Application.ApplicationType.WebGL
                        && Gdx.input.isPeripheralAvailable(Input.Peripheral.MultitouchScreen)) {
                    showInputFor(passwordField, true);
                } else {
                    getStage().setKeyboardFocus(passwordField);
                }
            }
        });

        emailField = new TextField("", skin);
        emailField.setMessageText("Email");
        emailField.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (Gdx.app.getType() == Application.ApplicationType.WebGL
                        && Gdx.input.isPeripheralAvailable(Input.Peripheral.MultitouchScreen)) {
                    showInputFor(emailField, true);
                } else {
                    getStage().setKeyboardFocus(emailField);
                }
            }
        });        

        statusLabel = new Label("", skin);
        statusLabel.setFontScale(0.8f);
        statusLabel.setColor(Color.RED);
        statusLabel.setAlignment(Align.center);

        actionButton = new TextButton("Entrar", skin);
        switchModeButton = new TextButton("Crear Cuenta", skin);
        switchModeButton.getLabel().setFontScale(0.7f); // Texto botón secundario pequeño
        closeButton = new TextButton("X", skin);

        actionButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.platformInput.hide();
                if (isRegistering) {
                    doRegister();
                } else {
                    doLogin();
                }
            }
        });

        switchModeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.platformInput.hide();
                toggleMode();
            }
        });

        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.platformInput.hide();
                hide();
            }
        });

        Table titleTable = getTitleTable();
        titleTable.add(closeButton).right().padRight(5);

        setModal(true);
        setMovable(false);

        populateTable();
    }

    private void showInputFor(TextField field, boolean password) {
        game.platformInput.showInputFor(field, password,
                value -> {
                    int cursorPos = field.getCursorPosition();
                    field.setText(value);
                    field.setCursorPosition(Math.min(cursorPos + 1, value.length()));
                });
    }

    private void populateTable() {
        Table content = getContentTable();
        content.clearChildren();

        content.defaults().pad(2).width(200).height(25);

        content.add(usernameField).growX().row();
        content.add(passwordField).growX().row();

        if (isRegistering) {
            content.add(emailField).row();
        }

        content.add(statusLabel).height(Value.prefHeight).padTop(5).row();

        Table buttons = getButtonTable();
        buttons.clearChildren();
        buttons.pad(5);

        if (isRegistering) {
            actionButton.setText("Crear");
            switchModeButton.setText("Volver");
        } else {
            actionButton.setText("Entrar");
            switchModeButton.setText("Crear Cuenta");
        }

        buttons.add(actionButton).size(100, 30).padRight(10);
        buttons.add(switchModeButton).height(30);

        pack();

        if (parentStage != null) {
            setPosition(
                    Math.round((parentStage.getWidth() - getWidth()) / 2),
                    Math.round((parentStage.getHeight() - getHeight()) / 2)
            );
        }
    }

    private void toggleMode() {
        isRegistering = !isRegistering;
        statusLabel.setText("");

        populateTable();
    }

    private void doLogin() {
        Gdx.app.log("LoginDialog", "doLogin");
        String user = usernameField.getText();
        String pass = passwordField.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            statusLabel.setText("Rellena todos los campos");
            return;
        }

        actionButton.setDisabled(true);
        statusLabel.setText("Conectando...");
        statusLabel.setColor(Color.YELLOW);

        LoginManager.getInstance().login(user, pass, new LoginManager.Callback() {
            @Override
            public void onSuccess() {
                Gdx.app.postRunnable(() -> {
                    hide();
                    if (onSuccessCallback != null) {
                        onSuccessCallback.run();
                    }
                });
            }

            @Override
            public void onError(String message) {
                Gdx.app.postRunnable(() -> {
                    statusLabel.setText(message);
                    statusLabel.setColor(Color.RED);
                    actionButton.setDisabled(false);
                });
            }
        });
    }

    private void doRegister() {
        String user = usernameField.getText();
        String pass = passwordField.getText();
        String email = emailField.getText();

        if (user.isEmpty() || pass.isEmpty() || email.isEmpty()) {
            statusLabel.setText("Email obligatorio");
            return;
        }

        actionButton.setDisabled(true);
        statusLabel.setText("Registrando...");
        statusLabel.setColor(Color.YELLOW);

        LoginManager.getInstance().register(user, pass, email, new LoginManager.Callback() {
            @Override
            public void onSuccess() {
                Gdx.app.log("LoginDialog", "Register::onSucces");
                Gdx.app.postRunnable(() -> {
                    statusLabel.setText("¡Cuenta creada! Iniciando sesión...");
                    statusLabel.setColor(Color.GREEN);

                    doLogin();
                });
            }

            @Override
            public void onError(String message) {
                Gdx.app.postRunnable(() -> {
                    statusLabel.setText("Error registro: " + message);
                    statusLabel.setColor(Color.RED);
                    actionButton.setDisabled(false);
                });
            }
        });
        Gdx.app.log("LoginDialog", "doRegister:end");
    }

    @Override
    public Dialog show(Stage stage) {
        this.parentStage = stage;
        stage.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Actor target = event.getTarget();
                if (!(target instanceof TextField)) {
                    game.platformInput.hide();
                }
                return false;
            }
        });
        Dialog d = super.show(stage);
        d.setPosition(Math.round((stage.getWidth() - getWidth()) / 2), Math.round((stage.getHeight() - getHeight()) / 2));
        return d;
    }

    @Override
    public void hide() {
        super.hide();
    }
}
