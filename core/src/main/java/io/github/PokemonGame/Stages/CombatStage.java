package io.github.PokemonGame.Stages;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.PokemonGame.Classes.Battle.Batalha;
import io.github.PokemonGame.Classes.Generators;
import io.github.PokemonGame.Actors.Pokemon;
import io.github.PokemonGame.Classes.PokedexController;

import java.util.Random;

import static com.badlogic.gdx.Gdx.gl;

public class CombatStage extends ApplicationAdapter {
    //Isso é uma textura(basicamente um sprite)
    public Texture ArenaTexture; // Textura da arena

    //Area de desenho de texturas
    public SpriteBatch batch;

    //stage
    public Stage stage;

    //Camera
    public OrthographicCamera Camera;

    //viewport
    private FitViewport viewport;


    //tables
    private Table table = new Table();
    private Table SecondOptionsTable = new Table();
    private int showSecondTable = -1;

    //Pokemons
    private PokedexController PDex = new PokedexController();
    private Pokemon charizard = PDex.getPkm(6);
    private Pokemon venussaur = PDex.getPkm(3);
    public Pokemon currentPokemon = charizard;
    public Pokemon currentEnemyPkm;

    //Skins
    public Skin textSkin = new Skin(Gdx.files.internal("Ui/BattleCore/Buttons/CustomUITextButton.json"));
    public BitmapFont font;//= textSkin.getFont("monogram");
    private FreeTypeFontGenerator generator;
    private FreeTypeFontGenerator.FreeTypeFontParameter parameter;

    //Lógica (parte de Laura e etc)
    private Batalha batalha;
    //Create event happens when the class is created
    @Override
    public void create(){
        //Inicializa um sprite batch (area de desenho de sprites)
        batch = new SpriteBatch();

        //Configurações de Fonte
        generator = new FreeTypeFontGenerator(Gdx.files.internal("Ui/monogram.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.color = Color.BLACK;
        parameter.size = 60;
        font = generator.generateFont(parameter);


        //Load sprites (textures).
        ArenaTexture = new Texture("Ui/BattleCore/battle1.png");

        //set viewport and camera
        Camera = new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        Camera.position.set(0, 0, 10); // Set camera position

        Camera.update(); // Atualiza a posição da camera baseado nas configs acima

        //Cria uma viewPort
        viewport = new FitViewport(Gdx.graphics.getWidth(),Gdx.graphics.getHeight(),Camera);

        textSkin.setScale(24f);

        //Skin
        Skin skin = new Skin(Gdx.files.internal("Ui/buttonStyle.json"));

         // Modifica a escala da fonte(deixar maior ou menor)
        //Scenes management (where all the actors do their things
        stage = new Stage(viewport,batch); // initializes a new stage
        Texture texture = new Texture(Gdx.files.internal("Ui/BattleCore/BlankBoard.png"));

        NinePatch drawable = new NinePatch(texture,8, 8, 8, 8);
        NinePatchDrawable ninePatchDrawable = new NinePatchDrawable(drawable);

        table.setBackground(ninePatchDrawable);

        stage.addActor(table); // add the table to the scene

        //Creates the buttons
        TextButton button = new TextButton("Atacar",textSkin);
        TextButton button2 = new TextButton("Pokemon",textSkin);
        TextButton button3 = new TextButton("Bolsa",textSkin);
        TextButton button4 = new TextButton("Fugir",textSkin);

        //event listener to execute an action when the button is pressed
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                turnTrue();
            }
        });
        //event listener to execute an action when the button is pressed
        button2.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                SwitchPokemon();
            }
        });
        //Event listener to exit game when the button is pressed
        button4.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                Gdx.app.exit();
            }
        });
        table.setPosition(670,0);
        textSkin.setScale(18);
        //Define a table para adicionar os botões na tela
        table.setSize(350,200);
        table.center();
        //table.background("Ui/BattleCore/Bordas.png");
        //table.debug();
        table.add(button).width(150).height(75).pad(15);
        table.add(button2).width(150).height(75).pad(15);
        table.row();
        table.add(button3).width(150).height(75).pad(15);
        table.add(button4).width(150).height(75).pad(15);


        //Configura a table de combate
        SecondOptionsTable = new Table();
        SecondOptionsTable.setSize(500,200);
        SecondOptionsTable.center();
        setupSecondOptionsTable();
        stage.addActor(SecondOptionsTable);


        //define stage como processador de inputs (para comandos futuros)
        Gdx.input.setInputProcessor(stage);

        Generators gen = new Generators();
        currentEnemyPkm = gen.GenPkm(true);

        batalha = new Batalha(this.currentEnemyPkm,this.currentPokemon);
    }
    public void setupSecondOptionsTable(){
        //Limpar os botões
        SecondOptionsTable.clear();
        //Preencher com novos botões
        for(int i=0;i<4;i++){
            if(i == 2){
                SecondOptionsTable.row();
            }
            TextButton btn = new TextButton(currentPokemon.getMoves().get(i).name,textSkin);
            SecondOptionsTable.add(btn).width(200).height(75).pad(30);

            final int moveIndex = i;
            btn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    //currentPokemon.getMoves().get(moveIndex).atack(currentEnemyPkm);
                    batalha.atacarOponente(moveIndex); //Utiliza a lógica de batalha para atacar o pokemon do adversário
                }
            });
        }

    }
    @Override
    public void render(){
        input();
        logic();
        draw();
    }
    public void turnTrue(){
        //this.currentEnemyPkm.Damage(10);
        showSecondTable*=-1;
        Gdx.app.log("ShowSecondTable",""+showSecondTable);
    }
    public void SwitchPokemon(){
        switch (this.currentPokemon.getcIndex()){
            case 6:currentPokemon = venussaur; break;
            case 3:currentPokemon = charizard;  break;
        }
        batalha = new Batalha(this.currentEnemyPkm,this.currentPokemon);
        setupSecondOptionsTable();
        Gdx.app.log("Turning",currentPokemon.getName());
    }
    //where we will read players commands
    public void input(){

    }
    public void YourPokemonIsDead(){
        SwitchPokemon();
    }
    public void EnemyPokemonIsDead(){
        Gdx.app.log("Enemy Pokemon is dead (Switching to a new enemy","Enemy Pokemon is dead (Switching to a new enemy");
        //Use it to create a random pokemon later
        Generators gen = new Generators();
        currentEnemyPkm = gen.GenPkm(true);
        Random rand = new Random();
        batalha = new Batalha(this.currentEnemyPkm,this.currentPokemon);
        int random = rand.nextInt(5)+1;
        ArenaTexture.dispose();
        ArenaTexture = new Texture("Ui/BattleCore/battle"+random+".png");
    }
    public void logic(){
        if(currentPokemon.getLife()<=0){
            YourPokemonIsDead();
        }
        if(currentEnemyPkm.getLife()<=0){
            EnemyPokemonIsDead();
        }

        if(showSecondTable==1){
            SecondOptionsTable.setPosition(100,0);
        }else {
            SecondOptionsTable.setPosition(50,11110);
        }
    }

    public void drawHealthBar(){
        //é meio auto explicativo (desenha a barra de vida dos dois pokemons)
        // Essas variaveis definem o tamanho da barra de vida que é proporcional a vida
        // width = currentHealth / totalHealth * totalBarWidth;

        Skin HealthBarSkin = new Skin(Gdx.files.internal("Ui/BattleCore/Healthbar/Healthbar.json"));
        ProgressBar.ProgressBarStyle healthBarStyle = HealthBarSkin.get("default-horizontal", ProgressBar.ProgressBarStyle.class);
        // Barra de vida do jogador
        ProgressBar playerHealthBar = new ProgressBar(0, currentPokemon.getMaxLife(), 1, false, healthBarStyle);
        playerHealthBar.setValue(currentPokemon.getLife());
        playerHealthBar.setBounds(720, 200, 300, 30); // Posição e tamanho
        stage.addActor(playerHealthBar);

        ProgressBar EnemyHealthBar = new ProgressBar(0, currentEnemyPkm.getMaxLife(), 1, false, healthBarStyle);
        EnemyHealthBar.setValue(currentEnemyPkm.getLife());
        EnemyHealthBar.setBounds(50, 670, 300, 30); // Posição e tamanho
        stage.addActor(EnemyHealthBar);
    }

    public void draw(){
        // Clear the screen
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw the background using SpriteBatch
        Camera.update();
        batch.setProjectionMatrix(Camera.combined);
        //start draw zone
        batch.begin();
        batch.draw(ArenaTexture, 0, 0, Camera.viewportWidth, Camera.viewportHeight);
        batch.draw(currentPokemon.getBackTexture(),100,0,300,300);
        batch.draw(currentEnemyPkm.getFrontTexture(),600,300,300,300);
        //Nome do pokemon
        font.setColor(Color.BLACK);
        font.draw(batch,currentPokemon.getName(),720,250,2f,10,false);
        font.draw(batch,currentEnemyPkm.getName(),50, 720,2f,10,false);
        //end draw zone
        batch.end();

        // Draw health bars
        drawHealthBar();


        // Draw the stage (UI)
        stage.act();
        stage.draw();

    }
    @Override
    public void dispose(){
        generator.dispose();
        ArenaTexture.dispose();
        currentEnemyPkm.disposeTexture();
        currentPokemon.disposeTexture();
        batch.dispose();
        stage.dispose();
    }

    @Override
    public void resize(int width, int height){
        viewport.update(width, height, true); // true centers the camera
    }

}
