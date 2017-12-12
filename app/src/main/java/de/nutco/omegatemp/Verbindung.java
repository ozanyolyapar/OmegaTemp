package de.nutco.omegatemp;

/**
 * Created by ozan on 05.12.2017.
 */

public abstract class Verbindung {

    public abstract void setupButtons();

    public Verbindung(){
        setupButtons();
    }

    public void registerButton(){

    }
}
