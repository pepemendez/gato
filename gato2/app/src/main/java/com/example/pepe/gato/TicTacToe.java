package com.example.pepe.gato;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by Pepe on 08/12/2015.
 */
public class TicTacToe {
    private int tablero[][];
    private int tipo;
    private int movimientos;
    private enum estados {
        espera, turno, perdio, gano, empate, error;

        @Override
        public String toString() {
            switch (this){
                case espera:
                    return "Es su turno";
                case turno:
                    return "Es tu turno";
                case perdio:
                    return "Pidele Revancha!";
                case gano:
                    return "Ganaste!";
                case empate:
                    return "Empate";
                case error:
                    return "Algo paso, intenta de nuevo";
                default:
                    return null;
            }
        }
    };
    private estados estado;
    private String jugador;
    private String rival;

    public TicTacToe(int tipo, String jugador, String rival){
        this.tipo = tipo;
        this.tablero = new int[3][3];
        this.jugador = jugador;
        this.rival = rival;
        if(tipo == 1)
            this.estado = estados.turno;
        if(tipo == 2)
            this.estado = estados.espera;
        this.movimientos = 9;
    }
    public JSONObject getEstado() throws JSONException {
        JSONObject auxiliar = new JSONObject();
        auxiliar.put("estado", this.estado.toString());
        auxiliar.put("jugador", this.jugador);
        auxiliar.put("rival", this.rival);
        auxiliar.put("tablero", Arrays.toString(this.tablero[0]).replace("[","").replace("]",",").replace(" ","") +
                                Arrays.toString(this.tablero[1]).replace("[", "").replace("]", ",").replace(" ","")  +
                                Arrays.toString(this.tablero[2]).replace("[","").replace("]", "").replace(" ","") );

        return auxiliar;
    }
    public boolean mueve(int x, int y, boolean interno){
        int mov;
        if((interno) && (estado != estados.turno))
            return false;
        if(tablero[x][y] != 0)
            return false;
        if(estado == estados.turno) {
            mov = this.tipo;
            estado = estados.espera;

        }
        else if(estado == estados.espera)    //Si no es mi turno
        {
            mov = 2; //Asignamos por default, nos evita un if
            if(mov == this.tipo)
                mov = 1;
            estado = estados.turno;
        }
        else
            return false;

        this.tablero[x][y] = mov;
        this.movimientos--;

        if(movimientos <= 0){
            estado = estados.empate;
        }
        for(int i = 0; i < 3; i++){
            if((tablero[0][i] == mov) && (tablero[1][i] == mov) && (tablero[2][i] == mov) ||
               (tablero[i][0] == mov) && (tablero[i][1] == mov) && (tablero[i][2] == mov)) {
                estado = estados.gano;
                if(mov != this.tipo)
                    estado = estados.perdio;
            }
        }

        if( (tablero[0][0] == mov) && (tablero[1][1] == mov) && (tablero[2][2] == mov) ||
            (tablero[0][2] == mov) && (tablero[1][1] == mov) && (tablero[2][0] == mov)){
            estado = estados.gano;
            if(mov != this.tipo)
                estado = estados.perdio;
        }

        return true;
    }
}
