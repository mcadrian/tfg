package model;

import java.util.HashMap;
import java.util.Set;

/**
 * Clase que se crea para poder guardar todos las alarmas que dispara nuestro
 * sistema, y los síntomas que lo han producido
 *
 * @author ALADTO
 */
public class Alarm {

    // la enfermedad que a la que refieren las alarmas
    private final String enfermedad_produce;
    // el mapa donde por cada sintoma que puede producir esa enfermedad, tiene el conjunto de ciclos y valores del mismo
    private final HashMap<String, HashMap<Integer, Double>> mapaSintomas;

    /**
     * Constructor de la Alarma
     *
     * @param enfermedad_produce El nombre de la enfermedad
     */
    public Alarm(String enfermedad_produce) {
        this.enfermedad_produce = enfermedad_produce;
        mapaSintomas = new HashMap<String, HashMap<Integer, Double>>();
    }

    /**
     * Metodo que devuelve el nombre de la enfermedad de la alarma
     *
     * @return El nombre de la enfermedad que produce la alarma
     */
    public String getEnfermedad_produce() {
        return enfermedad_produce;
    }

    /**
     * Metodo que añade un ciclo nuevo al mapa de ciclos que hay creado
     *
     * @param sintoma El sintoma que produjo la enfermedad a la que añadimos el
     * ciclo
     * @param numCiclo El ciclo en el que se produce la alarma
     * @param infoExtra La informacion extra de la alarma
     */
    public void addCiclo(String sintoma, int numCiclo, Double infoExtra) {
        if (mapaSintomas.get(sintoma) == null) {
            mapaSintomas.put(sintoma, new HashMap<Integer, Double>());
            mapaSintomas.get(sintoma).put(numCiclo, infoExtra);
        } else {
            mapaSintomas.get(sintoma).put(numCiclo, infoExtra);
        }
    }

    /**
     * Metodo getter del mapa de sintomas
     *
     * @return Una copia mapa de sintomas para evitar alliasing
     */
    public HashMap<String, HashMap<Integer, Double>> getMapaSintomas() {
        return new HashMap<String, HashMap<Integer, Double>>(mapaSintomas);
    }

    /**
     * Metodo que devuelve las claves del mapa de sintomas
     *
     * @return Las claves del mapa de sintomas
     */
    public Set<String> getClaves() {
        return mapaSintomas.keySet();
    }

    /**
     * Metodo que devuelve las claves del mapa de ciclos correspondientes a las
     * alarmas de un sintoma de una posible enfermedad
     *
     * @param clave String con la clave del conjunto de ciclos que queremos
     * obtener
     * @return El conjunto de claves correspondiente a los ciclos donde se
     * producen las alarmas
     */
    public Set<Integer> getConjuntoClavesCiclos(String clave) {
        return mapaSintomas.get(clave).keySet();
    }

    /**
     * Metodo que devuelve un mapa de ciclos correspondiente a un sintoma
     *
     * @param clave La clave del mapa de ciclos que queremos obtener
     * @return El mapa de ciclos correspondiente a la clave
     */
    public HashMap<Integer, Double> getMapaCiclos(String clave) {
        return new HashMap<Integer, Double>(mapaSintomas.get(clave));
    }

    /**
     * Metodo que devuelve el valor extra real de la causa del sintoma ya sea
     * ms, mV, etc...
     *
     * @param claveSintoma El sintoma de la enfermedad en cuestion
     * @param claveCiclo El ciclo en el que se produjo
     * @return El valor del intervalo, onda, etc... que produjo la alarma
     */
    public Double getValorInfoExtra(String claveSintoma, int claveCiclo) {
        return this.getMapaCiclos(claveSintoma).get(claveCiclo);
    }
}
